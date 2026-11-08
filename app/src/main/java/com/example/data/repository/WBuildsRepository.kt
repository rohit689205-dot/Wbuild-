package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.BlockAsset
import com.example.data.model.BuildItem
import com.example.data.model.BuildStep
import com.example.data.model.BuildingProgress
import com.example.data.model.Category
import com.example.data.model.FavoriteItem
import com.example.data.model.UserProfile
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class WBuildsRepository(private val db: FirebaseFirestore) {

  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth
  private val BOOTSTRAP_ADMIN_EMAIL = "rajkumari69920@gmail.com"

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  fun currentUserId(): String? = auth.currentUser?.uid

  fun currentUserEmail(): String? = auth.currentUser?.email

  fun isCurrentUserAdmin(): Boolean {
    val user = auth.currentUser ?: return false
    return user.email.equals(BOOTSTRAP_ADMIN_EMAIL, ignoreCase = true)
  }

  // --- Categories ---
  fun observeCategories(): Flow<List<Category>> = flow {
    val path = "categories"
    emitAll(
      db.collection(path)
        .orderBy("sortOrder", Query.Direction.ASCENDING)
        .snapshots()
        .map { snapshot -> snapshot.toObjects(Category::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
          emit(emptyList())
        }
    )
  }

  suspend fun createCategory(category: Category): String {
    require(isCurrentUserAdmin()) { "Only administrators can create categories." }
    val path = "categories"
    val docRef = if (category.slug.isNotBlank()) db.collection(path).document(category.slug) else db.collection(path).document()
    val payload = mapOf(
      "name" to category.name,
      "slug" to if (category.slug.isNotBlank()) category.slug else docRef.id,
      "thumbnailUrl" to category.thumbnailUrl,
      "sortOrder" to category.sortOrder,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.set(payload).await()
      return docRef.id
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun updateCategory(category: Category) {
    require(isCurrentUserAdmin()) { "Only administrators can update categories." }
    require(category.id.isNotBlank()) { "Category ID cannot be blank" }
    val docRef = db.collection("categories").document(category.id)
    val payload = mapOf(
      "name" to category.name,
      "slug" to category.slug,
      "thumbnailUrl" to category.thumbnailUrl,
      "sortOrder" to category.sortOrder,
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.update(payload).await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteCategory(categoryId: String) {
    require(isCurrentUserAdmin()) { "Only administrators can delete categories." }
    val docRef = db.collection("categories").document(categoryId)
    try {
      docRef.delete().await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- Builds ---
  fun observeBuilds(
    categoryId: String? = null,
    searchQuery: String? = null,
    includeDrafts: Boolean = false
  ): Flow<List<BuildItem>> = flow {
    val path = "builds"
    var query: Query = db.collection(path)

    if (!includeDrafts || !isCurrentUserAdmin()) {
      query = query.whereEqualTo("status", "published")
    }

    if (!categoryId.isNullOrBlank() && categoryId != "all" && categoryId != "favorites") {
      query = query.whereEqualTo("categoryId", categoryId)
    }

    emitAll(
      query.snapshots()
        .map { snapshot ->
          val items = snapshot.toObjects(BuildItem::class.java)
          if (!searchQuery.isNullOrBlank()) {
            val q = searchQuery.trim().lowercase()
            items.filter {
              it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.difficulty.lowercase().contains(q) ||
                it.edition.lowercase().contains(q)
            }
          } else {
            items
          }
        }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
          emit(emptyList())
        }
    )
  }

  suspend fun getBuildById(buildId: String): BuildItem? {
    val doc = db.collection("builds").document(buildId).get().await()
    return doc.toObject(BuildItem::class.java)
  }

  suspend fun createBuild(build: BuildItem): String {
    require(isCurrentUserAdmin()) { "Only administrators can create builds." }
    val docRef = db.collection("builds").document()
    val uid = requireUserId()
    val payload = mapOf(
      "title" to build.title,
      "slug" to if (build.slug.isNotBlank()) build.slug else docRef.id,
      "description" to build.description,
      "categoryId" to build.categoryId,
      "difficulty" to build.difficulty,
      "edition" to build.edition,
      "dimensions" to build.dimensions,
      "estimatedTime" to build.estimatedTime,
      "thumbnailUrl" to build.thumbnailUrl,
      "featured" to build.featured,
      "status" to build.status,
      "totalSteps" to build.totalSteps,
      "selectedBlocks" to build.selectedBlocks,
      "createdBy" to uid,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.set(payload).await()
      return docRef.id
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun updateBuild(build: BuildItem) {
    require(isCurrentUserAdmin()) { "Only administrators can update builds." }
    require(build.id.isNotBlank()) { "Build ID cannot be blank" }
    val docRef = db.collection("builds").document(build.id)
    val payload = mutableMapOf<String, Any>(
      "title" to build.title,
      "description" to build.description,
      "categoryId" to build.categoryId,
      "difficulty" to build.difficulty,
      "edition" to build.edition,
      "dimensions" to build.dimensions,
      "estimatedTime" to build.estimatedTime,
      "thumbnailUrl" to build.thumbnailUrl,
      "featured" to build.featured,
      "status" to build.status,
      "totalSteps" to build.totalSteps,
      "selectedBlocks" to build.selectedBlocks,
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.update(payload).await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun publishBuildWorkflow(
    build: BuildItem,
    stepImages: List<String>,
    selectedBlocks: List<Map<String, Any>>,
    isPublished: Boolean
  ): String {
    require(isCurrentUserAdmin()) { "Only administrators can publish builds." }
    val uid = requireUserId()
    val isNew = build.id.isBlank()
    val docRef = if (isNew) db.collection("builds").document() else db.collection("builds").document(build.id)
    val buildId = docRef.id

    val statusStr = if (isPublished) "published" else "draft"
    val totalStepsCount = stepImages.size.toLong()

    val payload = mutableMapOf<String, Any>(
      "title" to build.title.ifBlank { "Untitled Build" },
      "slug" to if (build.slug.isNotBlank()) build.slug else buildId,
      "description" to build.description,
      "categoryId" to build.categoryId.ifBlank { "farm" },
      "difficulty" to build.difficulty.ifBlank { "Medium" },
      "edition" to build.edition.ifBlank { "Both" },
      "dimensions" to build.dimensions,
      "estimatedTime" to build.estimatedTime,
      "thumbnailUrl" to build.thumbnailUrl.ifBlank { stepImages.firstOrNull() ?: "img_minecraft_hero" },
      "featured" to build.featured,
      "status" to statusStr,
      "totalSteps" to totalStepsCount,
      "selectedBlocks" to selectedBlocks,
      "updatedAt" to FieldValue.serverTimestamp()
    )

    if (isNew) {
      payload["createdBy"] = uid
      payload["createdAt"] = FieldValue.serverTimestamp()
      docRef.set(payload).await()
    } else {
      docRef.update(payload).await()
    }

    // Save/update tutorial step records in batches of 400
    if (stepImages.isNotEmpty()) {
      val chunks = stepImages.chunked(400)
      var stepNum = 1L
      for (chunk in chunks) {
        val batch = db.batch()
        for (imgUrl in chunk) {
          val stepDocId = "${buildId}_step_$stepNum"
          val stepRef = db.collection("buildSteps").document(stepDocId)
          val stepData = mapOf(
            "buildId" to buildId,
            "stepNumber" to stepNum,
            "sortOrder" to stepNum,
            "imageUrl" to imgUrl,
            "description" to "Step $stepNum: Place blocks according to the blueprint image.",
            "status" to statusStr,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
          batch.set(stepRef, stepData)
          stepNum++
        }
        batch.commit().await()
      }
    }

    return buildId
  }

  suspend fun deleteBuild(buildId: String) {
    require(isCurrentUserAdmin()) { "Only administrators can delete builds." }
    val docRef = db.collection("builds").document(buildId)
    try {
      docRef.delete().await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- Build Steps ---
  fun observeBuildSteps(buildId: String): Flow<List<BuildStep>> = flow {
    val path = "buildSteps"
    emitAll(
      db.collection(path)
        .whereEqualTo("buildId", buildId)
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(BuildStep::class.java).sortedBy { it.stepNumber }
        }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
          emit(emptyList())
        }
    )
  }

  suspend fun saveBuildStep(step: BuildStep): String {
    require(isCurrentUserAdmin()) { "Only administrators can save build steps." }
    val docRef = if (step.id.isNotBlank()) db.collection("buildSteps").document(step.id) else db.collection("buildSteps").document()
    val isNew = step.id.isBlank()
    val payload = mutableMapOf<String, Any>(
      "buildId" to step.buildId,
      "stepNumber" to step.stepNumber,
      "sortOrder" to step.sortOrder,
      "imageUrl" to step.imageUrl,
      "description" to step.description,
      "blockRequirements" to step.blockRequirements,
      "status" to step.status,
      "updatedAt" to FieldValue.serverTimestamp()
    )
    if (isNew) {
      payload["createdAt"] = FieldValue.serverTimestamp()
    }
    try {
      if (isNew) {
        docRef.set(payload).await()
      } else {
        docRef.update(payload).await()
      }
      return docRef.id
    } catch (e: Exception) {
      handleFirestoreError(e, if (isNew) OperationType.CREATE else OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteBuildStep(stepId: String) {
    require(isCurrentUserAdmin()) { "Only administrators can delete build steps." }
    val docRef = db.collection("buildSteps").document(stepId)
    try {
      docRef.delete().await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- Favorites ---
  fun observeFavorites(userId: String): Flow<List<FavoriteItem>> = flow {
    val path = "users/$userId/favorites"
    emitAll(
      db.collection("users").document(userId).collection("favorites")
        .snapshots()
        .map { snapshot -> snapshot.toObjects(FavoriteItem::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          handleFirestoreError(firestoreError ?: error, OperationType.LIST, path)
          emit(emptyList())
        }
    )
  }

  suspend fun toggleFavorite(userId: String, build: BuildItem, isFavorite: Boolean) {
    val docRef = db.collection("users").document(userId).collection("favorites").document(build.id)
    try {
      if (isFavorite) {
        docRef.delete().await()
      } else {
        val payload = mapOf(
          "buildId" to build.id,
          "buildTitle" to build.title,
          "thumbnailUrl" to build.thumbnailUrl,
          "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
      }
    } catch (e: Exception) {
      handleFirestoreError(e, if (isFavorite) OperationType.DELETE else OperationType.CREATE, docRef.path)
      throw e
    }
  }

  // --- Progress ---
  fun observeProgress(userId: String, buildId: String): Flow<BuildingProgress?> = flow {
    val path = "users/$userId/progress/$buildId"
    emitAll(
      db.collection("users").document(userId).collection("progress").document(buildId)
        .snapshots()
        .map { snapshot ->
          if (snapshot.exists()) snapshot.toObject(BuildingProgress::class.java) else null
        }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          handleFirestoreError(firestoreError ?: error, OperationType.GET, path)
          emit(null)
        }
    )
  }

  suspend fun saveProgress(userId: String, buildId: String, currentStep: Long, completedSteps: Long, isCompleted: Boolean) {
    val docRef = db.collection("users").document(userId).collection("progress").document(buildId)
    val payload = mapOf(
      "buildId" to buildId,
      "currentStep" to currentStep,
      "completedSteps" to completedSteps,
      "isCompleted" to isCompleted,
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.set(payload).await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  // --- User Profiles ---
  fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
    val path = "users/$userId"
    emitAll(
      db.collection("users").document(userId)
        .snapshots()
        .map { snapshot ->
          if (snapshot.exists()) snapshot.toObject(UserProfile::class.java) else null
        }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          handleFirestoreError(firestoreError ?: error, OperationType.GET, path)
          emit(null)
        }
    )
  }

  suspend fun syncUserProfile(userId: String, displayName: String, email: String, photoURL: String) {
    val docRef = db.collection("users").document(userId)
    val existingDoc = docRef.get().await()
    val isAdminUser = email.equals(BOOTSTRAP_ADMIN_EMAIL, ignoreCase = true)
    if (!existingDoc.exists()) {
      val payload = mapOf(
        "userId" to userId,
        "displayName" to displayName.ifBlank { "Minecraft Architect" },
        "email" to email,
        "photoURL" to photoURL,
        "role" to if (isAdminUser) "admin" else "user",
        "accountStatus" to "active",
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      docRef.set(payload).await()
    } else {
      val updates = mutableMapOf<String, Any>(
        "displayName" to displayName.ifBlank { "Minecraft Architect" },
        "photoURL" to photoURL,
        "updatedAt" to FieldValue.serverTimestamp()
      )
      if (isAdminUser) {
        updates["role"] = "admin"
      }
      docRef.update(updates).await()
    }
  }

  // --- Initial Data Seeder (Ensures user gallery has instant rich data) ---
  suspend fun seedInitialDataIfEmpty() {
    try {
      val categoriesCheck = db.collection("categories").limit(1).get().await()
      if (!categoriesCheck.isEmpty) {
        return // Already seeded
      }
    } catch (e: Exception) {
      Log.w("WBuildsRepo", "Category check exception (might be rules or empty)", e)
    }

    if (!isCurrentUserAdmin()) {
      return
    }

    try {
      // Seed categories
      val defaultCategories = listOf(
        Category(id = "farm", name = "Farm", slug = "farm", sortOrder = 1),
        Category(id = "medieval", name = "Medieval", slug = "medieval", sortOrder = 2),
        Category(id = "houses", name = "Houses", slug = "houses", sortOrder = 3),
        Category(id = "mansions", name = "Mansions", slug = "mansions", sortOrder = 4),
        Category(id = "statues", name = "Statues", slug = "statues", sortOrder = 5),
        Category(id = "animals", name = "Animals", slug = "animals", sortOrder = 6),
        Category(id = "decoration", name = "Decoration", slug = "decoration", sortOrder = 7),
        Category(id = "furniture", name = "Furniture", slug = "furniture", sortOrder = 8),
        Category(id = "beacon", name = "Beacon", slug = "beacon", sortOrder = 9),
        Category(id = "redstone", name = "Redstone", slug = "redstone", sortOrder = 10),
        Category(id = "survival", name = "Survival", slug = "survival", sortOrder = 11),
        Category(id = "modern", name = "Modern", slug = "modern", sortOrder = 12)
      )

      for (cat in defaultCategories) {
        val docRef = db.collection("categories").document(cat.id)
        docRef.set(
          mapOf(
            "name" to cat.name,
            "slug" to cat.slug,
            "sortOrder" to cat.sortOrder,
            "thumbnailUrl" to "",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        ).await()
      }

      val adminUid = requireUserId()

      // Seed 8 prominent reference builds
      val sampleBuilds = listOf(
        BuildItem(
          id = "build_tiered_farm",
          title = "Aesthetic Tiered Crop Farm",
          slug = "aesthetic-tiered-crop-farm",
          description = "A multi-level aesthetic wheat and carrot farm complete with integrated water irrigation, rustic lanterns, and wooden scaffolding fences. Highly efficient and visually stunning for survival bases.",
          categoryId = "farm",
          difficulty = "Easy",
          edition = "Both",
          dimensions = "16x16x8",
          estimatedTime = "45 mins",
          thumbnailUrl = "img_minecraft_hero",
          featured = true,
          status = "published",
          totalSteps = 6,
          createdBy = adminUid
        ),
        BuildItem(
          id = "build_medieval_tavern",
          title = "Medieval Tavern",
          slug = "medieval-tavern",
          description = "A cozy timber-frame tavern featuring an open hearth fireplace, barrel storage cellar, upstairs bedrooms, and intricate cobblestone foundation.",
          categoryId = "medieval",
          difficulty = "Medium",
          edition = "Both",
          dimensions = "18x22x14",
          estimatedTime = "60 mins",
          thumbnailUrl = "img_splash_art",
          featured = true,
          status = "published",
          totalSteps = 5,
          createdBy = adminUid
        ),
        BuildItem(
          id = "build_woodland_mansion",
          title = "Woodland Grand Manor",
          slug = "woodland-grand-manor",
          description = "Grand multi-wing mansion built with dark oak, polished deepslate trims, glass dome atriums, and royal master suites.",
          categoryId = "mansions",
          difficulty = "Expert",
          edition = "Java",
          dimensions = "38x42x24",
          estimatedTime = "120 mins",
          thumbnailUrl = "img_minecraft_hero",
          featured = false,
          status = "published",
          totalSteps = 4,
          createdBy = adminUid
        ),
        BuildItem(
          id = "build_cherry_farm",
          title = "Cherry Blossom Farm",
          slug = "cherry-blossom-farm",
          description = "Japanese-inspired terraced garden surrounded by blooming cherry blossom leaves, bamboo water chutes, and stone lantern shrines.",
          categoryId = "farm",
          difficulty = "Easy",
          edition = "Both",
          dimensions = "14x14x6",
          estimatedTime = "30 mins",
          thumbnailUrl = "img_splash_art",
          featured = false,
          status = "published",
          totalSteps = 4,
          createdBy = adminUid
        ),
        BuildItem(
          id = "build_dragon_statue",
          title = "Ender Dragon Statue",
          slug = "ender-dragon-statue",
          description = "Imposing obsidian and blackstone monument depicting the mythical winged dragon perched upon an ancient gateway.",
          categoryId = "statues",
          difficulty = "Hard",
          edition = "Both",
          dimensions = "20x30x22",
          estimatedTime = "90 mins",
          thumbnailUrl = "img_minecraft_hero",
          featured = false,
          status = "published",
          totalSteps = 4,
          createdBy = adminUid
        ),
        BuildItem(
          id = "build_elephant_statue",
          title = "Desert Elephant Monument",
          slug = "desert-elephant-monument",
          description = "Majestic sandstone elephant with ornate emerald and gold ceremonial tapestries, perfect for desert temple courtyards.",
          categoryId = "animals",
          difficulty = "Medium",
          edition = "Both",
          dimensions = "12x18x15",
          estimatedTime = "45 mins",
          thumbnailUrl = "img_splash_art",
          featured = false,
          status = "published",
          totalSteps = 3,
          createdBy = adminUid
        ),
        BuildItem(
          id = "build_lamp_post",
          title = "Medieval Hanging Lamp Post",
          slug = "medieval-lamp-post",
          description = "Intricate cobblestone and dark oak street lamp with wrought-iron chains holding an amber soul lantern.",
          categoryId = "decoration",
          difficulty = "Easy",
          edition = "Both",
          dimensions = "3x3x7",
          estimatedTime = "10 mins",
          thumbnailUrl = "img_minecraft_hero",
          featured = false,
          status = "published",
          totalSteps = 3,
          createdBy = adminUid
        ),
        BuildItem(
          id = "build_medieval_ship",
          title = "Royal Merchant Galleon",
          slug = "royal-merchant-galleon",
          description = "Full three-mast sea vessel with billowing wool sails, crow's nest, captain's cabin, and cargo hold cannons.",
          categoryId = "medieval",
          difficulty = "Hard",
          edition = "Both",
          dimensions = "25x40x35",
          estimatedTime = "120 mins",
          thumbnailUrl = "img_splash_art",
          featured = false,
          status = "published",
          totalSteps = 4,
          createdBy = adminUid
        )
      )

      for (b in sampleBuilds) {
        val bRef = db.collection("builds").document(b.id)
        bRef.set(
          mapOf(
            "title" to b.title,
            "slug" to b.slug,
            "description" to b.description,
            "categoryId" to b.categoryId,
            "difficulty" to b.difficulty,
            "edition" to b.edition,
            "dimensions" to b.dimensions,
            "estimatedTime" to b.estimatedTime,
            "thumbnailUrl" to b.thumbnailUrl,
            "featured" to b.featured,
            "status" to b.status,
            "totalSteps" to b.totalSteps,
            "createdBy" to b.createdBy,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        ).await()
      }

      // Seed detailed image-by-image steps for Aesthetic Tiered Crop Farm
      val farmSteps = listOf(
        BuildStep(
          id = "step_farm_1",
          buildId = "build_tiered_farm",
          stepNumber = 1,
          sortOrder = 1,
          imageUrl = "img_minecraft_hero",
          description = "Lay out the stone foundation grid. Create a 16x16 perimeter using Stone Bricks and place corner support pillars.",
          blockRequirements = listOf(
            mapOf("name" to "Stone Bricks", "quantity" to 48L),
            mapOf("name" to "Chiseled Stone Bricks", "quantity" to 4L),
            mapOf("name" to "Oak Log", "quantity" to 16L)
          ),
          status = "published"
        ),
        BuildStep(
          id = "step_farm_2",
          buildId = "build_tiered_farm",
          stepNumber = 2,
          sortOrder = 2,
          imageUrl = "img_splash_art",
          description = "Construct the bottom tier soil bed. Place water hydration channels at the center and enclose with Oak Trapdoors.",
          blockRequirements = listOf(
            mapOf("name" to "Dirt", "quantity" to 64L),
            mapOf("name" to "Oak Trapdoor", "quantity" to 12L),
            mapOf("name" to "Water Bucket", "quantity" to 2L)
          ),
          status = "published"
        ),
        BuildStep(
          id = "step_farm_3",
          buildId = "build_tiered_farm",
          stepNumber = 3,
          sortOrder = 3,
          imageUrl = "img_minecraft_hero",
          description = "Build the second tiered tier staircase and support arches using Spruce Planks and Cobblestone Stairs.",
          blockRequirements = listOf(
            mapOf("name" to "Cobblestone Stairs", "quantity" to 24L),
            mapOf("name" to "Spruce Planks", "quantity" to 32L),
            mapOf("name" to "Oak Fence", "quantity" to 16L)
          ),
          status = "published"
        ),
        BuildStep(
          id = "step_farm_4",
          buildId = "build_tiered_farm",
          stepNumber = 4,
          sortOrder = 4,
          imageUrl = "img_splash_art",
          description = "Till the farmland blocks with your hoe and plant the crop seeds: wheat on the lower level, carrots on the upper level.",
          blockRequirements = listOf(
            mapOf("name" to "Wheat Seeds", "quantity" to 32L),
            mapOf("name" to "Carrots", "quantity" to 32L),
            mapOf("name" to "Bone Meal", "quantity" to 16L)
          ),
          status = "published"
        ),
        BuildStep(
          id = "step_farm_5",
          buildId = "build_tiered_farm",
          stepNumber = 5,
          sortOrder = 5,
          imageUrl = "img_minecraft_hero",
          description = "Add atmospheric lighting and roof overhang. Hang Lanterns from Iron Chains under the wooden pergolas.",
          blockRequirements = listOf(
            mapOf("name" to "Lantern", "quantity" to 8L),
            mapOf("name" to "Chain", "quantity" to 8L),
            mapOf("name" to "Spruce Slab", "quantity" to 28L)
          ),
          status = "published"
        ),
        BuildStep(
          id = "step_farm_6",
          buildId = "build_tiered_farm",
          stepNumber = 6,
          sortOrder = 6,
          imageUrl = "img_splash_art",
          description = "Finishing touches: Add storage barrels, composting bins, and water troughs for harvesting tools.",
          blockRequirements = listOf(
            mapOf("name" to "Barrel", "quantity" to 4L),
            mapOf("name" to "Composter", "quantity" to 2L),
            mapOf("name" to "Cauldron", "quantity" to 1L)
          ),
          status = "published"
        )
      )

      for (s in farmSteps) {
        val sRef = db.collection("buildSteps").document(s.id)
        sRef.set(
          mapOf(
            "buildId" to s.buildId,
            "stepNumber" to s.stepNumber,
            "sortOrder" to s.sortOrder,
            "imageUrl" to s.imageUrl,
            "description" to s.description,
            "blockRequirements" to s.blockRequirements,
            "status" to s.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        ).await()
      }

    } catch (e: Exception) {
      Log.e("WBuildsRepo", "Failed to seed default content", e)
    }
  }
}
