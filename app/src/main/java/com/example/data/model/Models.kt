package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Category(
  @DocumentId val id: String = "",
  val name: String = "",
  val slug: String = "",
  val thumbnailUrl: String = "",
  val sortOrder: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BlockRequirement(
  val name: String = "",
  val quantity: Long = 1L,
  val imageUrl: String = ""
)

@IgnoreExtraProperties
data class BuildItem(
  @DocumentId val id: String = "",
  val title: String = "",
  val slug: String = "",
  val description: String = "",
  val categoryId: String = "",
  val difficulty: String = "Easy", // Easy, Medium, Hard, Expert
  val edition: String = "Both", // Java, Bedrock, Both
  val dimensions: String = "", // e.g. 15x20x12
  val estimatedTime: String = "", // e.g. 45 mins
  val thumbnailUrl: String = "",
  val featured: Boolean = false,
  val status: String = "published", // published, draft
  val totalSteps: Long = 0L,
  val selectedBlocks: List<Map<String, Any>> = emptyList(),
  val createdBy: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BuildStep(
  @DocumentId val id: String = "",
  val buildId: String = "",
  val stepNumber: Long = 1L,
  val sortOrder: Long = 1L,
  val imageUrl: String = "",
  val description: String = "",
  val blockRequirements: List<Map<String, Any>> = emptyList(),
  val status: String = "published", // published, draft
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BlockAsset(
  @DocumentId val id: String = "",
  val blockName: String = "",
  val normalizedName: String = "",
  val imageUrl: String = "",
  val filename: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FavoriteItem(
  @DocumentId val id: String = "",
  val buildId: String = "",
  val buildTitle: String = "",
  val thumbnailUrl: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BuildingProgress(
  @DocumentId val id: String = "",
  val buildId: String = "",
  val currentStep: Long = 1L,
  val completedSteps: Long = 0L,
  val isCompleted: Boolean = false,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserProfile(
  @DocumentId val id: String = "",
  val userId: String = "",
  val displayName: String = "",
  val email: String = "",
  val photoURL: String = "",
  val role: String = "user", // user, admin
  val accountStatus: String = "active", // active, suspended
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)
