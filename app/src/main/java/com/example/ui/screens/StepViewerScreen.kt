package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.BuildStep
import com.example.ui.WBuildsViewModel
import com.example.ui.components.BuildThumbnailImage
import com.example.ui.theme.WBuildsBlack
import com.example.ui.theme.WBuildsCard
import com.example.ui.theme.WBuildsCardElevated
import com.example.ui.theme.WBuildsDarkSurface
import com.example.ui.theme.WBuildsDivider
import com.example.ui.theme.WBuildsGold
import com.example.ui.theme.WBuildsGoldDark
import com.example.ui.theme.WBuildsSuccess
import com.example.ui.theme.WBuildsTextPrimary
import com.example.ui.theme.WBuildsTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepViewerScreen(
  viewModel: WBuildsViewModel,
  buildId: String,
  startStep: Int = 1
) {
  val activeBuild by viewModel.activeBuild.collectAsState()
  val activeSteps by viewModel.activeSteps.collectAsState()

  val totalStepsCount = if (activeSteps.isNotEmpty()) {
    activeSteps.size
  } else {
    (activeBuild?.totalSteps?.toInt() ?: 1).coerceAtLeast(1)
  }

  var currentStepIndex by remember { mutableIntStateOf(startStep.coerceIn(1, totalStepsCount)) }
  var isFullScreenOpen by remember { mutableStateOf(false) }
  var isReportDialogOpen by remember { mutableStateOf(false) }
  var reportDescription by remember { mutableStateOf("") }

  // Save progress when stepping
  LaunchedEffect(currentStepIndex) {
    viewModel.saveProgress(
      buildId = buildId,
      currentStep = currentStepIndex.toLong(),
      totalSteps = totalStepsCount.toLong()
    )
  }

  BackHandler {
    viewModel.navigateBack()
  }

  // Find step data if available
  val currentStepData: BuildStep? = activeSteps.find { it.stepNumber == currentStepIndex.toLong() }
    ?: activeSteps.getOrNull(currentStepIndex - 1)

  val stepImageUrl = currentStepData?.imageUrl?.ifBlank { null }
    ?: if (currentStepIndex % 2 == 1) "img_minecraft_hero" else "img_splash_art"

  val progressPercent = (currentStepIndex.toFloat() / totalStepsCount.toFloat()).coerceIn(0f, 1f)

  Scaffold(
    containerColor = WBuildsBlack,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(WBuildsDarkSurface)
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { viewModel.navigateBack() },
              modifier = Modifier.testTag("step_viewer_back_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Step Viewer",
                tint = WBuildsGold
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = activeBuild?.title ?: "Building Tutorial",
                color = WBuildsTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
              Text(
                text = "STEP $currentStepIndex OF $totalStepsCount",
                color = WBuildsGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Restart button
            IconButton(
              onClick = { currentStepIndex = 1 },
              modifier = Modifier.testTag("restart_button")
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Restart Build",
                tint = WBuildsTextSecondary
              )
            }
            // Report button
            IconButton(
              onClick = { isReportDialogOpen = true },
              modifier = Modifier.testTag("report_button")
            ) {
              Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = "Report Step",
                tint = WBuildsTextSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Linear Progress Bar
        LinearProgressIndicator(
          progress = { progressPercent },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = WBuildsGold,
          trackColor = WBuildsDivider
        )
      }
    },
    bottomBar = {
      // Step Navigation Controls
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(WBuildsDarkSurface)
          .navigationBarsPadding()
          .border(1.dp, WBuildsDivider, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Previous button (White Pill Button)
          Button(
            onClick = {
              if (currentStepIndex > 1) {
                currentStepIndex--
              }
            },
            enabled = currentStepIndex > 1,
            colors = ButtonDefaults.buttonColors(
              containerColor = Color.White,
              disabledContainerColor = Color.White.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
              .width(130.dp)
              .height(48.dp)
              .testTag("prev_step_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = null,
              tint = WBuildsBlack,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Back",
              color = WBuildsBlack,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }

          // Next or Finish button (White Pill Button)
          if (currentStepIndex < totalStepsCount) {
            Button(
              onClick = {
                currentStepIndex++
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color.White),
              shape = RoundedCornerShape(24.dp),
              modifier = Modifier
                .width(130.dp)
                .height(48.dp)
                .testTag("next_step_button")
            ) {
              Text(
                text = "Next",
                color = WBuildsBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = null,
                tint = WBuildsBlack,
                modifier = Modifier.size(20.dp)
              )
            }
          } else {
            Button(
              onClick = {
                viewModel.completeBuild(buildId, totalStepsCount.toLong())
              },
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsSuccess),
              shape = RoundedCornerShape(24.dp),
              modifier = Modifier
                .width(130.dp)
                .height(48.dp)
                .testTag("complete_build_button")
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = WBuildsBlack,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "FINISH",
                color = WBuildsBlack,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  ) { paddingValues ->
    val scrollState = rememberScrollState()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(scrollState)
    ) {
      // Step Image with Zoom / Fullscreen trigger
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1.35f)
          .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
          .background(WBuildsCard)
          .clickable { isFullScreenOpen = true }
          .testTag("step_image_container")
      ) {
        BuildThumbnailImage(
          imageUrl = stepImageUrl,
          contentDescription = "Step $currentStepIndex image",
          modifier = Modifier.fillMaxSize()
        )

        // Overlay zoom button
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(12.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(WBuildsBlack.copy(alpha = 0.75f))
            .clickable { isFullScreenOpen = true },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Fullscreen,
            contentDescription = "Full-screen Zoom",
            tint = WBuildsGold,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      // Guidance & Step Description
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = "INSTRUCTIONS",
          color = WBuildsGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = currentStepData?.description?.ifBlank { null }
            ?: "Follow the blueprint image carefully. Align blocks precisely with the coordinate markings and layer boundaries shown in this step.",
          color = WBuildsTextPrimary,
          fontSize = 15.sp,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Block Requirements Section
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WBuildsDivider, RoundedCornerShape(12.dp)),
          colors = CardDefaults.cardColors(containerColor = WBuildsCard),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                tint = WBuildsGold,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "BLOCK REQUIREMENTS",
                color = WBuildsGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val blocks = currentStepData?.blockRequirements ?: emptyList()
            if (blocks.isNotEmpty()) {
              FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                blocks.forEach { bMap ->
                  val name = (bMap["name"] as? String) ?: "Minecraft Block"
                  val qty = (bMap["quantity"] as? Number)?.toLong() ?: 1L
                  BlockRequirementChip(name = name, quantity = qty)
                }
              }
            } else {
              // Standard materials for default demo step
              FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                BlockRequirementChip(name = "Stone Bricks", quantity = 8L)
                BlockRequirementChip(name = "Oak Planks", quantity = 16L)
                BlockRequirementChip(name = "Lantern", quantity = 2L)
                BlockRequirementChip(name = "Glass Pane", quantity = 4L)
              }
            }
          }
        }
      }
    }
  }

  // Full-screen Zoomable Dialog
  if (isFullScreenOpen) {
    Dialog(
      onDismissRequest = { isFullScreenOpen = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      var scale by remember { mutableFloatStateOf(1f) }
      val transformableState = rememberTransformableState { zoomChange, _, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
      }

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(WBuildsBlack)
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .transformable(state = transformableState),
          contentAlignment = Alignment.Center
        ) {
          BuildThumbnailImage(
            imageUrl = stepImageUrl,
            contentDescription = "Full-screen Step $currentStepIndex",
            modifier = Modifier
              .fillMaxWidth()
              .graphicsLayer(
                scaleX = scale,
                scaleY = scale
              )
          )
        }

        // Top bar overlay
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "STEP $currentStepIndex / $totalStepsCount (Pinch to Zoom)",
            color = WBuildsGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )

          IconButton(
            onClick = { isFullScreenOpen = false },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(WBuildsDarkSurface)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Full Screen",
              tint = WBuildsTextPrimary
            )
          }
        }
      }
    }
  }

  // Report Step Modal
  if (isReportDialogOpen) {
    AlertDialog(
      onDismissRequest = { isReportDialogOpen = false },
      containerColor = WBuildsDarkSurface,
      title = {
        Text("Report Incorrect Step", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold)
      },
      text = {
        Column {
          Text(
            text = "Notice a missing block, misaligned angle, or wrong count on Step $currentStepIndex? Let the builders know:",
            color = WBuildsTextSecondary,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = reportDescription,
            onValueChange = { reportDescription = it },
            placeholder = { Text("Describe the issue...", color = WBuildsTextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = WBuildsGold,
              unfocusedBorderColor = WBuildsDivider,
              focusedTextColor = WBuildsTextPrimary,
              unfocusedTextColor = WBuildsTextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.showMessage("Report submitted. Thank you for helping keep WBuilds accurate!")
            isReportDialogOpen = false
            reportDescription = ""
          },
          colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold)
        ) {
          Text("Submit", color = WBuildsBlack, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { isReportDialogOpen = false }) {
          Text("Cancel", color = WBuildsTextSecondary)
        }
      }
    )
  }
}

@Composable
fun BlockRequirementChip(name: String, quantity: Long, imageUrl: String = "") {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(horizontal = 6.dp)
  ) {
    com.example.ui.components.MinecraftBlockIcon(
      blockName = name,
      imageUrl = imageUrl,
      modifier = Modifier.size(42.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "x$quantity",
      color = WBuildsTextSecondary,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )
  }
}
