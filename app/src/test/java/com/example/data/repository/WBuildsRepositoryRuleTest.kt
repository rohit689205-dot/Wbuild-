package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.BuildItem
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class WBuildsRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun observeBuilds_authenticatedUser_receivesPublishedBuilds() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val testBuildId = "test_build_" + UUID.randomUUID().toString()
    firestore.collection("builds").document(testBuildId).set(
      mapOf(
        "title" to "Observed Farm",
        "categoryId" to "farm",
        "difficulty" to "Easy",
        "edition" to "Both",
        "status" to "published",
        "totalSteps" to 10L,
        "createdBy" to "creator_1",
        "createdAt" to com.google.firebase.Timestamp.now(),
        "updatedAt" to com.google.firebase.Timestamp.now()
      )
    ).await()

    val repository = WBuildsRepository(firestore)
    val builds = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeBuilds().first { list -> list.any { it.id == testBuildId } }
    }
    assertTrue(builds.any { it.id == testBuildId })
  }

  @Test
  fun toggleFavorite_userCanAddAndRemoveFavorite() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = WBuildsRepository(firestore)
    val sampleBuild = BuildItem(
      id = "fav_test_" + UUID.randomUUID().toString(),
      title = "Test Favorite Build"
    )

    // Add to favorites
    withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.toggleFavorite(uid, sampleBuild, isFavorite = false)
    }

    // Verify added
    val favs = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeFavorites(uid).first { list -> list.any { it.buildId == sampleBuild.id } }
    }
    assertEquals(1, favs.filter { it.buildId == sampleBuild.id }.size)

    // Remove from favorites
    withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.toggleFavorite(uid, sampleBuild, isFavorite = true)
    }

    val favsAfter = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeFavorites(uid).first { list -> list.none { it.buildId == sampleBuild.id } }
    }
    assertTrue(favsAfter.none { it.buildId == sampleBuild.id })
  }

  @Test
  fun nonAdmin_cannotCreateBuildDirectly() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = WBuildsRepository(firestore)
    try {
      repository.createBuild(
        BuildItem(
          title = "Malicious Build",
          categoryId = "houses",
          difficulty = "Easy",
          edition = "Both",
          status = "published",
          totalSteps = 10L
        )
      )
      fail("Non-admin user should not be able to create builds")
    } catch (e: IllegalArgumentException) {
      // Expected precondition failure
      assertTrue(e.message?.contains("Only administrators") == true)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice@wbuilds.test"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 4000L
  }
}
