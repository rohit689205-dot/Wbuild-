package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WBuildsViewModel
import com.example.ui.components.BuildThumbnailImage
import com.example.ui.components.DifficultyPill
import com.example.ui.theme.WBuildsBlack
import com.example.ui.theme.WBuildsCard
import com.example.ui.theme.WBuildsCardElevated
import com.example.ui.theme.WBuildsDivider
import com.example.ui.theme.WBuildsGold
import com.example.ui.theme.WBuildsGoldDark
import com.example.ui.theme.WBuildsSuccess
import com.example.ui.theme.WBuildsTextPrimary
import com.example.ui.theme.WBuildsTextSecondary

@Composable
fun BuildDetailsScreen(
  viewModel: WBuildsViewModel,
  buildId: String
) {
  val activeBuild by viewModel.activeBuild.collectAsState()
  val activeProgress by viewModel.activeProgress.collectAsState()
  val userFavorites by viewModel.userFavorites.collectAsState()
  val context = LocalContext.current

  BackHandler {
    viewModel.navigateBack()
  }

  val build = activeBuild

  Scaffold(
    containerColor = WBuildsBlack,
    bottomBar = {
      if (build != null) {
        val currentStep = activeProgress?.currentStep ?: 1L
        val isCompleted = activeProgress?.isCompleted ?: false
        val ctaText = when {
          isCompleted -> "BUILD AGAIN"
          currentStep > 1 -> "RESUME STEP $currentStep"
          else -> "START BUILDING"
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(WBuildsBlack)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Button(
            onClick = {
              viewModel.startBuilding(build.id, startStep = currentStep.toInt())
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("start_building_button")
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              tint = WBuildsBlack,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = ctaText,
              color = WBuildsBlack,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }
        }
      }
    }
  ) { paddingValues ->
    if (build == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        contentAlignment = Alignment.Center
      ) {
        Text("Loading build details...", color = WBuildsTextSecondary)
      }
      return@Scaffold
    }

    val isFav = userFavorites.any { it.buildId == build.id }
    val scrollState = rememberScrollState()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(scrollState)
    ) {
      // Header Image with controls
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1.35f)
      ) {
        BuildThumbnailImage(
          imageUrl = build.thumbnailUrl,
          contentDescription = build.title,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color(0x99000000), Color.Transparent, Color(0xDD000000)),
                startY = 0f
              )
            )
        )

        // Top bar buttons
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { viewModel.navigateBack() },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(WBuildsBlack.copy(alpha = 0.65f))
              .testTag("detail_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = WBuildsGold
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Share Button
            IconButton(
              onClick = {
                val sendIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(Intent.EXTRA_TEXT, "Check out ${build.title} on WBuilds Minecraft Guide!")
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Minecraft Build"))
              },
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(WBuildsBlack.copy(alpha = 0.65f))
                .testTag("share_button")
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = WBuildsTextPrimary
              )
            }

            // Favorite Button
            IconButton(
              onClick = { viewModel.toggleFavorite(build) },
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(WBuildsBlack.copy(alpha = 0.65f))
                .testTag("detail_favorite_button")
            ) {
              Icon(
                imageVector = if (isFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Favorite",
                tint = if (isFav) WBuildsGold else WBuildsTextPrimary
              )
            }
          }
        }

        // Edition & Category on bottom image
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(WBuildsBlack.copy(alpha = 0.85f))
              .border(1.dp, WBuildsGoldDark, RoundedCornerShape(6.dp))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${build.edition.uppercase()} EDITION",
              color = WBuildsGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }

          DifficultyPill(difficulty = build.difficulty)
        }
      }

      // Main Info Content
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        // Title
        Text(
          text = build.title,
          color = WBuildsTextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Specs Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SpecCard(
            modifier = Modifier.weight(1f),
            label = "TOTAL STEPS",
            value = "${build.totalSteps}",
            icon = Icons.Default.DesignServices
          )
          SpecCard(
            modifier = Modifier.weight(1f),
            label = "DIMENSIONS",
            value = build.dimensions.ifBlank { "N/A" },
            icon = Icons.Default.ViewInAr
          )
          SpecCard(
            modifier = Modifier.weight(1f),
            label = "EST. TIME",
            value = build.estimatedTime.ifBlank { "N/A" },
            icon = Icons.Default.Schedule
          )
        }

        // Progress Card if user has progress
        if (activeProgress != null) {
          Spacer(modifier = Modifier.height(14.dp))
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WBuildsCardElevated),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (activeProgress!!.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (activeProgress!!.isCompleted) WBuildsSuccess else WBuildsGold,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = if (activeProgress!!.isCompleted) "Status: Completed!" else "Current Progress: Step ${activeProgress!!.currentStep} of ${build.totalSteps}",
                  color = WBuildsTextPrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Your progress is automatically saved to the cloud.",
                  color = WBuildsTextSecondary,
                  fontSize = 11.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Description Section
        Text(
          text = "OVERVIEW",
          color = WBuildsGold,
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = build.description,
          color = WBuildsTextSecondary,
          fontSize = 14.sp,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Category Tag
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(WBuildsCard)
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Category,
            contentDescription = null,
            tint = WBuildsGold,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Category: ${build.categoryId.replaceFirstChar { it.uppercase() }}",
            color = WBuildsTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }

        if (build.selectedBlocks.isNotEmpty()) {
          Spacer(modifier = Modifier.height(18.dp))
          Text(
            text = "BLOCK CHECKLIST & QUANTITIES",
            color = WBuildsGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, WBuildsDivider, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = WBuildsCard),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              build.selectedBlocks.forEach { blockMap ->
                val bName = (blockMap["name"] as? String) ?: "Minecraft Block"
                val bQty = (blockMap["quantity"] as? Number)?.toLong() ?: 1L
                val bUrl = (blockMap["imageUrl"] as? String) ?: ""
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WBuildsCard)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    com.example.ui.components.MinecraftBlockIcon(
                      blockName = bName,
                      imageUrl = bUrl,
                      modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(bName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                  }
                  Text("x $bQty", color = WBuildsTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }
}

@Composable
fun SpecCard(
  modifier: Modifier = Modifier,
  label: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = WBuildsCard),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, WBuildsDivider)
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = WBuildsGold,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        color = WBuildsTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        maxLines = 1
      )
      Text(
        text = label,
        color = WBuildsTextSecondary,
        fontSize = 8.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}
