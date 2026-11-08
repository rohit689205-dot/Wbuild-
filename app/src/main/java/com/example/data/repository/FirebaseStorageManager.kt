package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class FirebaseStorageManager(private val context: Context) {

  private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
  private val storageRef by lazy { storage.reference }

  /**
   * Uploads a single image Uri to Firebase Cloud Storage.
   * Returns a permanent HTTPS download URL or a persistent internal storage file URL.
   */
  suspend fun uploadImageToStorage(
    localUriString: String,
    destinationPath: String,
    onProgress: (Float) -> Unit = {}
  ): String {
    // 1. If already an HTTP/HTTPS URL, return as-is
    if (localUriString.startsWith("http://") || localUriString.startsWith("https://")) {
      return localUriString
    }

    // 2. If blank, return fallback
    if (localUriString.isBlank()) {
      return "img_minecraft_hero"
    }

    val uri = try {
      Uri.parse(localUriString)
    } catch (e: Exception) {
      return localUriString
    }

    val imageRef = storageRef.child(destinationPath)

    return try {
      val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open stream for Uri: $localUriString")

      val bytes = inputStream.use { it.readBytes() }
      val uploadTask = imageRef.putBytes(bytes)

      uploadTask.addOnProgressListener { taskSnapshot ->
        if (taskSnapshot.totalByteCount > 0) {
          val progress = taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount.toFloat()
          onProgress(progress)
        }
      }

      uploadTask.await()
      val downloadUrl = imageRef.downloadUrl.await().toString()
      Log.d("FirebaseStorage", "Successfully uploaded $destinationPath -> $downloadUrl")
      downloadUrl
    } catch (e: Exception) {
      Log.w("FirebaseStorage", "Cloud Storage upload failed for $destinationPath (using persistent local file fallback)", e)
      copyUriToPersistentInternalStorage(uri, destinationPath)
    }
  }

  /**
   * Controlled bulk upload pipeline for up to 1,000 tutorial step images.
   * Uses Semaphore(4) for controlled concurrency (4 parallel uploads at a time).
   */
  suspend fun batchUploadStepImages(
    buildId: String,
    localImageUris: List<String>,
    onProgress: (completedCount: Int, totalCount: Int, percent: Int) -> Unit = { _, _, _ -> }
  ): List<String> {
    if (localImageUris.isEmpty()) return emptyList()

    val uploadedUrls = ConcurrentHashMap<Int, String>()
    val semaphore = Semaphore(4) // Controlled concurrency of 4
    val total = localImageUris.size
    val completedCount = AtomicInteger(0)

    coroutineScope {
      val jobs = localImageUris.mapIndexed { index, uriStr ->
        async(Dispatchers.IO) {
          semaphore.withPermit {
            val stepNum = index + 1
            val destPath = "builds/$buildId/steps/step_${stepNum}_${UUID.randomUUID().toString().take(6)}.jpg"
            val finalUrl = uploadImageToStorage(uriStr, destPath)
            uploadedUrls[index] = finalUrl

            val currentDone = completedCount.incrementAndGet()
            val percent = ((currentDone.toFloat() / total.toFloat()) * 100).toInt()
            onProgress(currentDone, total, percent)
          }
        }
      }
      jobs.awaitAll()
    }

    // Preserve exact step order 0..total-1
    return (0 until total).map { idx ->
      uploadedUrls[idx] ?: localImageUris.getOrElse(idx) { "img_minecraft_hero" }
    }
  }

  /**
   * Fallback copy to internal persistent storage (`filesDir/app_uploaded_images/`)
   * so clearing cache or Android settings data will NEVER destroy the uploaded image reference.
   */
  private fun copyUriToPersistentInternalStorage(uri: Uri, destinationPath: String): String {
    return try {
      val storageDir = File(context.filesDir, "app_uploaded_images").apply { mkdirs() }
      val sanitizedFileName = destinationPath.replace("/", "_").replace(" ", "_")
      val targetFile = File(storageDir, sanitizedFileName)

      context.contentResolver.openInputStream(uri)?.use { input ->
        targetFile.outputStream().use { output ->
          input.copyTo(output)
        }
      }
      Uri.fromFile(targetFile).toString()
    } catch (e: Exception) {
      Log.e("FirebaseStorage", "Internal storage copy failed", e)
      uri.toString()
    }
  }
}
