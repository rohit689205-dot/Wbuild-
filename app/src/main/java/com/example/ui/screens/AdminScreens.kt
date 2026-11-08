package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BuildItem
import com.example.data.model.Category
import com.example.ui.WBuildsViewModel
import com.example.ui.components.BuildThumbnailImage
import com.example.ui.components.WBuildsTopBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.WBuildsBlack
import com.example.ui.theme.WBuildsCard
import com.example.ui.theme.WBuildsCardElevated
import com.example.ui.theme.WBuildsDarkSurface
import com.example.ui.theme.WBuildsDivider
import com.example.ui.theme.WBuildsGold
import com.example.ui.theme.WBuildsGoldDark
import com.example.ui.theme.WBuildsGoldLight
import com.example.ui.theme.WBuildsSuccess
import com.example.ui.theme.WBuildsTextPrimary
import com.example.ui.theme.WBuildsTextSecondary

// --- 1. ADMIN DASHBOARD ---
@Composable
fun AdminDashboardScreen(
  viewModel: WBuildsViewModel
) {
  val adminBuilds by viewModel.adminBuilds.collectAsState()
  val categories by viewModel.categories.collectAsState()

  BackHandler {
    viewModel.navigateTo(Screen.Home)
  }

  val publishedCount = adminBuilds.count { it.status == "published" }
  val draftCount = adminBuilds.count { it.status == "draft" }
  val totalSteps = adminBuilds.sumOf { it.totalSteps }

  Scaffold(
    containerColor = WBuildsBlack,
    topBar = {
      WBuildsTopBar(
        title = "Admin Dashboard",
        showBack = true,
        onBackClick = { viewModel.navigateTo(Screen.Home) },
        showSearch = false,
        showSettings = false
      )
    }
  ) { paddingValues ->
    val scrollState = rememberScrollState()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp)
        .verticalScroll(scrollState),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = "LIVE SYSTEM OVERVIEW",
        color = WBuildsGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )

      // Stats Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        AdminStatCard(
          modifier = Modifier.weight(1f),
          title = "Total Builds",
          value = "${adminBuilds.size}",
          subtitle = "$publishedCount Live"
        )
        AdminStatCard(
          modifier = Modifier.weight(1f),
          title = "Categories",
          value = "${categories.size}",
          subtitle = "Dynamic"
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        AdminStatCard(
          modifier = Modifier.weight(1f),
          title = "Draft Builds",
          value = "$draftCount",
          subtitle = "Unpublished"
        )
        AdminStatCard(
          modifier = Modifier.weight(1f),
          title = "Tutorial Steps",
          value = "$totalSteps",
          subtitle = "Max 1,000 / build"
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "QUICK ACTIONS",
        color = WBuildsGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )

      // Primary Action: Add New Build (6-Step Workflow)
      Button(
        onClick = { viewModel.navigateTo(Screen.AdminCreateBuild) },
        colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("admin_new_build_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = WBuildsBlack)
        Spacer(modifier = Modifier.width(8.dp))
        Text("ADD NEW BUILD (6-STEP WORKFLOW)", color = WBuildsBlack, fontWeight = FontWeight.Black, fontSize = 13.sp)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = { viewModel.navigateTo(Screen.AdminManageCategories) },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .border(1.dp, WBuildsDivider, RoundedCornerShape(12.dp))
        ) {
          Icon(Icons.Default.Category, contentDescription = null, tint = WBuildsGold, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Categories", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = { viewModel.adminTriggerSeed() },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .border(1.dp, WBuildsDivider, RoundedCornerShape(12.dp))
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, tint = WBuildsGold, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Seed Builds", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "MANAGE BUILDS (${adminBuilds.size})",
        color = WBuildsGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )

      adminBuilds.forEach { build ->
        AdminBuildRowCard(
          build = build,
          onEdit = { viewModel.navigateTo(Screen.AdminEditBuild(build.id)) },
          onManageSteps = { viewModel.navigateTo(Screen.AdminManageSteps(build.id)) },
          onDelete = { viewModel.adminDeleteBuild(build.id) }
        )
      }
    }
  }
}

@Composable
fun AdminStatCard(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  subtitle: String
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = WBuildsCard),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, WBuildsDivider)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(text = title, color = WBuildsTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, color = WBuildsGold, fontSize = 24.sp, fontWeight = FontWeight.Black)
      Text(text = subtitle, color = WBuildsTextSecondary, fontSize = 10.sp)
    }
  }
}

@Composable
fun AdminBuildRowCard(
  build: BuildItem,
  onEdit: () -> Unit,
  onManageSteps: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, WBuildsDivider, RoundedCornerShape(12.dp)),
    colors = CardDefaults.cardColors(containerColor = WBuildsCard),
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = build.title,
          color = WBuildsTextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "${build.totalSteps} steps",
            color = WBuildsGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "• ${build.status.uppercase()}",
            color = if (build.status == "published") WBuildsSuccess else Color(0xFFFF9500),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "• ${build.categoryId}",
            color = WBuildsTextSecondary,
            fontSize = 11.sp
          )
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(
          onClick = onManageSteps,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(Icons.Default.Layers, contentDescription = "Manage Steps", tint = WBuildsGold)
        }
        IconButton(
          onClick = onEdit,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(Icons.Default.Edit, contentDescription = "Edit Build", tint = WBuildsTextPrimary)
        }
        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(Icons.Default.Delete, contentDescription = "Delete Build", tint = Color(0xFFFF453A))
        }
      }
    }
  }
}

// --- 2. STREAMLINED 6-STEP ADD NEW BUILD WORKFLOW ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminBuildEditorScreen(
  viewModel: WBuildsViewModel,
  editBuildId: String? = null
) {
  val adminBuilds by viewModel.adminBuilds.collectAsState()
  val categories by viewModel.categories.collectAsState()

  val existing = adminBuilds.find { it.id == editBuildId }

  // Current Wizard Step (1 to 6)
  var wizardStep by remember { mutableIntStateOf(1) }

  // Workflow State Variables
  var selectedCategory by remember { mutableStateOf(existing?.categoryId ?: "farm") }
  var buildTitle by remember { mutableStateOf(existing?.title ?: "") }
  var mainThumbnailUrl by remember { mutableStateOf(existing?.thumbnailUrl ?: "img_minecraft_hero") }

  // Tutorial Images List (Up to 1,000 images per build)
  val tutorialImages = remember {
    mutableStateListOf<String>().apply {
      if (existing != null && existing.totalSteps > 0) {
        for (i in 1..existing.totalSteps.toInt()) {
          add(if (i % 2 == 1) "img_minecraft_hero" else "img_splash_art")
        }
      } else {
        addAll(listOf("img_minecraft_hero", "img_splash_art", "img_minecraft_hero"))
      }
    }
  }

  // Block Checklist (Pre-populated library + quantities)
  val defaultBlocks = listOf(
    Pair("Stone Bricks", 48L),
    Pair("Oak Planks", 32L),
    Pair("Glass Pane", 16L),
    Pair("Spruce Stairs", 24L),
    Pair("Water Bucket", 2L),
    Pair("Lantern", 8L),
    Pair("Dirt / Farmland", 64L),
    Pair("Oak Log", 16L),
    Pair("Cobblestone", 32L),
    Pair("Deepslate", 16L),
    Pair("Netherite Block", 0L),
    Pair("Diamond Block", 0L),
    Pair("Emerald Block", 0L),
    Pair("Gold Block", 0L),
    Pair("Redstone Dust", 0L),
    Pair("Composter", 2L),
    Pair("Barrel", 4L),
    Pair("Cauldron", 1L)
  )

  // Selected Block Checklist Map: blockName -> Pair(isSelected, quantity)
  val blockChecklist = remember {
    mutableStateListOf<Triple<String, Boolean, Long>>().apply {
      defaultBlocks.forEach { (name, defaultQty) ->
        add(Triple(name, defaultQty > 0, if (defaultQty > 0) defaultQty else 10L))
      }
    }
  }

  var blockSearchQuery by remember { mutableStateOf("") }
  var customBlockName by remember { mutableStateOf("") }
  var customBlockQty by remember { mutableStateOf("10") }

  // System Photo Picker Launchers
  val singleStepPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    uri?.let {
      if (tutorialImages.size < 1000) {
        tutorialImages.add(it.toString())
      }
    }
  }

  val multipleStepPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 100)
  ) { uris ->
    uris.forEach { uri ->
      if (tutorialImages.size < 1000) {
        tutorialImages.add(uri.toString())
      }
    }
  }

  val thumbnailPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    uri?.let {
      mainThumbnailUrl = it.toString()
    }
  }

  var replacingStepIndex by remember { mutableIntStateOf(-1) }
  val replaceStepPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    uri?.let {
      if (replacingStepIndex in 0 until tutorialImages.size) {
        tutorialImages[replacingStepIndex] = it.toString()
      }
    }
  }

  BackHandler {
    if (wizardStep > 1) {
      wizardStep--
    } else {
      viewModel.navigateTo(Screen.AdminDashboard)
    }
  }

  Scaffold(
    containerColor = WBuildsBlack,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(WBuildsDarkSurface)
          .padding(bottom = 6.dp)
      ) {
        WBuildsTopBar(
          title = if (editBuildId == null) "Add New Build Guide" else "Edit Build Guide",
          showBack = true,
          onBackClick = {
            if (wizardStep > 1) wizardStep-- else viewModel.navigateTo(Screen.AdminDashboard)
          },
          showSearch = false,
          showSettings = false
        )

        // 6-Step Compact Wizard Indicator
        val stepLabels = listOf(
          "1. Category",
          "2. Title",
          "3. Tutorial (${tutorialImages.size})",
          "4. Blocks",
          "5. Thumbnail",
          "6. Publish"
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          stepLabels.forEachIndexed { idx, label ->
            val stepNum = idx + 1
            val isActive = wizardStep == stepNum
            val isDone = wizardStep > stepNum

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                  when {
                    isActive -> WBuildsGold
                    isDone -> WBuildsGoldDark
                    else -> WBuildsCard
                  }
                )
                .clickable { wizardStep = stepNum }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
                color = if (isActive) WBuildsBlack else WBuildsTextPrimary
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
        .padding(16.dp)
        .verticalScroll(scrollState),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      when (wizardStep) {
        // --- STEP 1: SELECT CATEGORY ---
        1 -> {
          Text(
            text = "STEP 1: SELECT CATEGORY",
            color = WBuildsGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Choose the category where this Minecraft building guide belongs:",
            color = WBuildsTextSecondary,
            fontSize = 12.sp
          )

          val catList = if (categories.isNotEmpty()) categories else listOf(
            Category(id = "farm", name = "Farm"),
            Category(id = "houses", name = "Houses"),
            Category(id = "decoration", name = "Decoration"),
            Category(id = "furniture", name = "Furniture"),
            Category(id = "mansions", name = "Mansions"),
            Category(id = "statues", name = "Statues"),
            Category(id = "animals", name = "Animals"),
            Category(id = "redstone", name = "Redstone"),
            Category(id = "survival", name = "Survival"),
            Category(id = "modern", name = "Modern"),
            Category(id = "medieval", name = "Medieval"),
            Category(id = "beacon", name = "Beacon")
          )

          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            catList.chunked(2).forEach { rowCats ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                rowCats.forEach { cat ->
                  val isSelected = selectedCategory.equals(cat.id, ignoreCase = true)
                  Card(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(12.dp))
                      .clickable { selectedCategory = cat.id }
                      .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) WBuildsGold else WBuildsDivider,
                        shape = RoundedCornerShape(12.dp)
                      ),
                    colors = CardDefaults.cardColors(
                      containerColor = if (isSelected) WBuildsCardElevated else WBuildsCard
                    )
                  ) {
                    Row(
                      modifier = Modifier.padding(14.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Category,
                        contentDescription = null,
                        tint = if (isSelected) WBuildsGold else WBuildsTextSecondary,
                        modifier = Modifier.size(20.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = cat.name,
                        color = if (isSelected) WBuildsGold else WBuildsTextPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                      )
                    }
                  }
                }
                if (rowCats.size == 1) {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          }

          Button(
            onClick = { wizardStep = 2 },
            colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
          ) {
            Text("CONTINUE TO TITLE →", color = WBuildsBlack, fontWeight = FontWeight.Black)
          }
        }

        // --- STEP 2: BUILD TITLE ---
        2 -> {
          Text(
            text = "STEP 2: BUILD TITLE",
            color = WBuildsGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Enter a clear title for this Minecraft building guide. A unique Build ID is generated automatically.",
            color = WBuildsTextSecondary,
            fontSize = 12.sp
          )

          OutlinedTextField(
            value = buildTitle,
            onValueChange = { buildTitle = it },
            label = { Text("Build Title (e.g. Aesthetic Tiered Crop Farm)") },
            placeholder = { Text("Aesthetic Tiered Crop Farm", color = WBuildsTextSecondary) },
            colors = adminTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(WBuildsCard)
              .padding(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = WBuildsSuccess, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Category: ${selectedCategory.uppercase()} • Auto-Generated ID Ready",
                color = WBuildsTextSecondary,
                fontSize = 12.sp
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
              onClick = { wizardStep = 1 },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
            ) {
              Text("← Back", color = WBuildsTextPrimary)
            }

            Button(
              onClick = {
                if (buildTitle.isNotBlank()) wizardStep = 3
              },
              enabled = buildTitle.isNotBlank(),
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
            ) {
              Text("UPLOAD TUTORIAL →", color = WBuildsBlack, fontWeight = FontWeight.Black)
            }
          }
        }

        // --- STEP 3: UPLOAD ALL TUTORIAL IMAGES (UP TO 1,000) ---
        3 -> {
          Text(
            text = "STEP 3: UPLOAD TUTORIAL IMAGES (${tutorialImages.size} / 1000)",
            color = WBuildsGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Upload up to 1,000 step-by-step images for this build. The order of images determines step order. Drag or move to reorder.",
            color = WBuildsTextSecondary,
            fontSize = 12.sp
          )

          // Batch Upload Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                if (tutorialImages.size < 1000) {
                  singleStepPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = WBuildsBlack)
              Spacer(modifier = Modifier.width(6.dp))
              Text("ADD IMAGE", color = WBuildsBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = {
                if (tutorialImages.size < 1000) {
                  multipleStepPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                }
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1.2f)
            ) {
              Icon(Icons.Default.CloudUpload, contentDescription = null, tint = WBuildsGold)
              Spacer(modifier = Modifier.width(6.dp))
              Text("BULK CHOOSE IMAGES", color = WBuildsGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
          }

          // Progress Bar
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Batch Concurrency Status: Ready", color = WBuildsTextSecondary, fontSize = 11.sp)
              Text("${tutorialImages.size} / 1000 Max", color = WBuildsGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
              progress = { (tutorialImages.size.toFloat() / 1000f).coerceIn(0f, 1f) },
              modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
              color = WBuildsGold,
              trackColor = WBuildsDivider
            )
          }

          // Numbered Grid Display (Step 1, Step 2, ... Step 1000)
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            tutorialImages.forEachIndexed { index, imgUrl ->
              val stepNum = index + 1
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .border(1.dp, WBuildsDivider, RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = WBuildsCard),
                shape = RoundedCornerShape(10.dp)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Step Number Pill
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(WBuildsGoldDark),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "$stepNum",
                      color = WBuildsBlack,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.sp
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  // Thumbnail Preview (Clickable to Replace from Phone)
                  Box(
                    modifier = Modifier
                      .size(54.dp)
                      .clip(RoundedCornerShape(6.dp))
                      .background(WBuildsBlack)
                      .clickable {
                        replacingStepIndex = index
                        replaceStepPickerLauncher.launch(
                          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                      }
                  ) {
                    BuildThumbnailImage(imageUrl = imgUrl, contentDescription = "Step $stepNum", modifier = Modifier.fillMaxSize())
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text("Step $stepNum Image", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                      if (imgUrl.startsWith("content:") || imgUrl.startsWith("file:")) "Selected from Phone" else "Ready for upload",
                      color = WBuildsSuccess,
                      fontSize = 10.sp
                    )
                  }

                  // Reorder, Replace, and Remove controls
                  Row {
                    IconButton(
                      onClick = {
                        replacingStepIndex = index
                        replaceStepPickerLauncher.launch(
                          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                      },
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = "Replace Image", tint = WBuildsGold, modifier = Modifier.size(18.dp))
                    }

                    if (index > 0) {
                      IconButton(
                        onClick = {
                          val tmp = tutorialImages[index]
                          tutorialImages[index] = tutorialImages[index - 1]
                          tutorialImages[index - 1] = tmp
                        },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = WBuildsGold, modifier = Modifier.size(18.dp))
                      }
                    }
                    if (index < tutorialImages.size - 1) {
                      IconButton(
                        onClick = {
                          val tmp = tutorialImages[index]
                          tutorialImages[index] = tutorialImages[index + 1]
                          tutorialImages[index + 1] = tmp
                        },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = WBuildsGold, modifier = Modifier.size(18.dp))
                      }
                    }
                    IconButton(
                      onClick = { tutorialImages.removeAt(index) },
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFFF453A), modifier = Modifier.size(18.dp))
                    }
                  }
                }
              }
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
              onClick = { wizardStep = 2 },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
            ) {
              Text("← Back", color = WBuildsTextPrimary)
            }

            Button(
              onClick = {
                if (tutorialImages.isNotEmpty()) wizardStep = 4
              },
              enabled = tutorialImages.isNotEmpty(),
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
            ) {
              Text("BLOCK CHECKLIST →", color = WBuildsBlack, fontWeight = FontWeight.Black)
            }
          }
        }

        // --- STEP 4: BLOCK CHECKLIST ---
        4 -> {
          Text(
            text = "STEP 4: BLOCK CHECKLIST",
            color = WBuildsGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Select required Minecraft blocks and specify quantities needed for this build:",
            color = WBuildsTextSecondary,
            fontSize = 12.sp
          )

          // Search Blocks
          OutlinedTextField(
            value = blockSearchQuery,
            onValueChange = { blockSearchQuery = it },
            placeholder = { Text("Search blocks (e.g. Stone, Glass, Lantern)", color = WBuildsTextSecondary, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WBuildsGold) },
            colors = adminTextFieldColors(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          // Add Custom Block Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = customBlockName,
              onValueChange = { customBlockName = it },
              placeholder = { Text("Custom block name", color = WBuildsTextSecondary, fontSize = 12.sp) },
              colors = adminTextFieldColors(),
              singleLine = true,
              modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
              value = customBlockQty,
              onValueChange = { customBlockQty = it },
              placeholder = { Text("Qty", color = WBuildsTextSecondary, fontSize = 12.sp) },
              colors = adminTextFieldColors(),
              singleLine = true,
              modifier = Modifier.width(70.dp)
            )

            Button(
              onClick = {
                if (customBlockName.isNotBlank()) {
                  val qty = customBlockQty.toLongOrNull() ?: 10L
                  blockChecklist.add(Triple(customBlockName, true, qty))
                  customBlockName = ""
                  customBlockQty = "10"
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(52.dp)
            ) {
              Text("+ ADD", color = WBuildsBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }

          // Block Items Checklist
          val filteredBlocks = blockChecklist.filter {
            blockSearchQuery.isBlank() || it.first.lowercase().contains(blockSearchQuery.trim().lowercase())
          }

          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            filteredBlocks.forEachIndexed { idx, (bName, isSelected, qty) ->
              val realIndex = blockChecklist.indexOfFirst { it.first == bName }
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .border(
                    1.dp,
                    if (isSelected) WBuildsGoldDark else WBuildsDivider,
                    RoundedCornerShape(10.dp)
                  ),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) WBuildsCardElevated else WBuildsCard
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                      checked = isSelected,
                      onCheckedChange = { checked ->
                        if (realIndex >= 0) {
                          blockChecklist[realIndex] = Triple(bName, checked, qty)
                        }
                      },
                      colors = CheckboxDefaults.colors(
                        checkedColor = WBuildsGold,
                        checkmarkColor = WBuildsBlack
                      )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = bName,
                      color = if (isSelected) WBuildsTextPrimary else WBuildsTextSecondary,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      fontSize = 14.sp
                    )
                  }

                  if (isSelected) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text("Qty:", color = WBuildsTextSecondary, fontSize = 11.sp)
                      Spacer(modifier = Modifier.width(4.dp))
                      OutlinedTextField(
                        value = "$qty",
                        onValueChange = { newQtyStr ->
                          val q = newQtyStr.toLongOrNull() ?: 1L
                          if (realIndex >= 0) {
                            blockChecklist[realIndex] = Triple(bName, true, q)
                          }
                        },
                        colors = adminTextFieldColors(),
                        singleLine = true,
                        modifier = Modifier.width(80.dp)
                      )
                    }
                  }
                }
              }
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
              onClick = { wizardStep = 3 },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
            ) {
              Text("← Back", color = WBuildsTextPrimary)
            }

            Button(
              onClick = { wizardStep = 5 },
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
            ) {
              Text("THUMBNAIL →", color = WBuildsBlack, fontWeight = FontWeight.Black)
            }
          }
        }

        // --- STEP 5: UPLOAD BUILD THUMBNAIL ---
        5 -> {
          Text(
            text = "STEP 5: UPLOAD BUILD THUMBNAIL",
            color = WBuildsGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Upload or select the primary thumbnail image that will appear in the user gallery:",
            color = WBuildsTextSecondary,
            fontSize = 12.sp
          )

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(1.4f)
              .clip(RoundedCornerShape(14.dp))
              .border(2.dp, WBuildsGold, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = WBuildsCard)
          ) {
            Box(modifier = Modifier.fillMaxSize()) {
              BuildThumbnailImage(imageUrl = mainThumbnailUrl, contentDescription = "Build Thumbnail", modifier = Modifier.fillMaxSize())

              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(Color.Transparent, Color(0xAA000000)),
                      startY = 80f
                    )
                  )
              )

              Box(
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(12.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(WBuildsGold)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("PRIMARY GALLERY THUMBNAIL", color = WBuildsBlack, fontWeight = FontWeight.Black, fontSize = 10.sp)
              }
            }
          }

          // Select Thumbnail preset or custom photo from phone
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = {
                thumbnailPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
            ) {
              Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = WBuildsBlack)
              Spacer(modifier = Modifier.width(8.dp))
              Text("CHOOSE THUMBNAIL FROM PHONE", color = WBuildsBlack, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }

            Text("Preset Options:", color = WBuildsGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { mainThumbnailUrl = "img_minecraft_hero" },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (mainThumbnailUrl == "img_minecraft_hero") WBuildsGold else WBuildsCard
                ),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  "Preset Hero",
                  color = if (mainThumbnailUrl == "img_minecraft_hero") WBuildsBlack else WBuildsTextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Button(
                onClick = { mainThumbnailUrl = "img_splash_art" },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (mainThumbnailUrl == "img_splash_art") WBuildsGold else WBuildsCard
                ),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  "Preset Splash",
                  color = if (mainThumbnailUrl == "img_splash_art") WBuildsBlack else WBuildsTextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            OutlinedTextField(
              value = mainThumbnailUrl,
              onValueChange = { mainThumbnailUrl = it },
              label = { Text("Custom Image URL") },
              colors = adminTextFieldColors(),
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
              onClick = { wizardStep = 4 },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
            ) {
              Text("← Back", color = WBuildsTextPrimary)
            }

            Button(
              onClick = { wizardStep = 6 },
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
            ) {
              Text("PREVIEW & PUBLISH →", color = WBuildsBlack, fontWeight = FontWeight.Black)
            }
          }
        }

        // --- STEP 6: PUBLISH BUILD ---
        6 -> {
          Text(
            text = "STEP 6: FINAL PREVIEW & PUBLISH",
            color = WBuildsGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Review all details below before saving draft or publishing directly to the user gallery:",
            color = WBuildsTextSecondary,
            fontSize = 12.sp
          )

          // Final Summary Card
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, WBuildsDivider, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = WBuildsCard),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(WBuildsBlack)
                ) {
                  BuildThumbnailImage(imageUrl = mainThumbnailUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(buildTitle.ifBlank { "Untitled Build" }, color = WBuildsTextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                  Text("Category: ${selectedCategory.uppercase()} • ${tutorialImages.size} Steps", color = WBuildsGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }

              HorizontalDivider(color = WBuildsDivider)

              val selectedBlockList = blockChecklist.filter { it.second }
              Text("Selected Blocks (${selectedBlockList.size}):", color = WBuildsTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                selectedBlockList.forEach { (bName, _, qty) ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(WBuildsDarkSurface)
                      .padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Text("$bName ×$qty", color = WBuildsGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          // Action Buttons: Save Draft & Publish Build
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = {
                val selectedBlocksMap = blockChecklist.filter { it.second }.map { (bName, _, qty) ->
                  mapOf("name" to bName, "quantity" to qty)
                }
                val buildPayload = BuildItem(
                  id = editBuildId ?: "",
                  title = buildTitle,
                  categoryId = selectedCategory,
                  thumbnailUrl = mainThumbnailUrl,
                  status = "draft",
                  totalSteps = tutorialImages.size.toLong(),
                  selectedBlocks = selectedBlocksMap
                )
                viewModel.adminPublishWorkflow(
                  build = buildPayload,
                  stepImages = tutorialImages.toList(),
                  selectedBlocks = selectedBlocksMap,
                  isPublished = false
                ) {
                  viewModel.navigateTo(Screen.AdminDashboard)
                }
              },
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .border(1.dp, WBuildsDivider, RoundedCornerShape(10.dp))
            ) {
              Text("SAVE DRAFT", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                val selectedBlocksMap = blockChecklist.filter { it.second }.map { (bName, _, qty) ->
                  mapOf("name" to bName, "quantity" to qty)
                }
                val buildPayload = BuildItem(
                  id = editBuildId ?: "",
                  title = buildTitle,
                  categoryId = selectedCategory,
                  thumbnailUrl = mainThumbnailUrl,
                  status = "published",
                  totalSteps = tutorialImages.size.toLong(),
                  selectedBlocks = selectedBlocksMap
                )
                viewModel.adminPublishWorkflow(
                  build = buildPayload,
                  stepImages = tutorialImages.toList(),
                  selectedBlocks = selectedBlocksMap,
                  isPublished = true
                ) {
                  viewModel.navigateTo(Screen.AdminDashboard)
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
            ) {
              Icon(Icons.Default.Publish, contentDescription = null, tint = WBuildsBlack)
              Spacer(modifier = Modifier.width(6.dp))
              Text("PUBLISH BUILD", color = WBuildsBlack, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
          }
        }
      }
    }
  }
}

// --- 3. STEP MANAGER SCREEN ---
@Composable
fun AdminManageStepsScreen(
  viewModel: WBuildsViewModel,
  buildId: String
) {
  val activeSteps by viewModel.activeSteps.collectAsState()

  BackHandler {
    viewModel.navigateTo(Screen.AdminDashboard)
  }

  var isAddingStep by remember { mutableStateOf(false) }
  var newStepDesc by remember { mutableStateOf("") }
  var newStepImage by remember { mutableStateOf("img_minecraft_hero") }

  Scaffold(
    containerColor = WBuildsBlack,
    topBar = {
      WBuildsTopBar(
        title = "Manage Steps (${activeSteps.size})",
        showBack = true,
        onBackClick = { viewModel.navigateTo(Screen.AdminDashboard) },
        showSearch = false,
        showSettings = false
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Button(
          onClick = { isAddingStep = !isAddingStep },
          colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = WBuildsBlack)
          Spacer(modifier = Modifier.width(6.dp))
          Text("ADD TUTORIAL STEP (MAX 1000)", color = WBuildsBlack, fontWeight = FontWeight.Bold)
        }
      }

      if (isAddingStep) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = WBuildsCardElevated),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text("Create Step ${activeSteps.size + 1}", color = WBuildsGold, fontWeight = FontWeight.Bold)
              OutlinedTextField(
                value = newStepDesc,
                onValueChange = { newStepDesc = it },
                label = { Text("Placement instructions") },
                colors = adminTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
              )
              OutlinedTextField(
                value = newStepImage,
                onValueChange = { newStepImage = it },
                label = { Text("Image URL or Asset Name") },
                colors = adminTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
              )
              Button(
                onClick = {
                  val newStep = com.example.data.model.BuildStep(
                    buildId = buildId,
                    stepNumber = (activeSteps.size + 1).toLong(),
                    sortOrder = (activeSteps.size + 1).toLong(),
                    imageUrl = newStepImage,
                    description = newStepDesc
                  )
                  viewModel.adminSaveStep(newStep) {
                    isAddingStep = false
                    newStepDesc = ""
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("SAVE STEP", color = WBuildsBlack, fontWeight = FontWeight.Black)
              }
            }
          }
        }
      }

      items(activeSteps) { step ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WBuildsDivider, RoundedCornerShape(10.dp)),
          colors = CardDefaults.cardColors(containerColor = WBuildsCard),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Step ${step.stepNumber}", color = WBuildsGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Spacer(modifier = Modifier.height(2.dp))
              Text(step.description.ifBlank { "No description" }, color = WBuildsTextSecondary, fontSize = 12.sp, maxLines = 2)
            }
            IconButton(
              onClick = { viewModel.adminDeleteStep(step.id) }
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete Step", tint = Color(0xFFFF453A))
            }
          }
        }
      }
    }
  }
}

// --- 4. MANAGE CATEGORIES SCREEN ---
@Composable
fun AdminManageCategoriesScreen(
  viewModel: WBuildsViewModel
) {
  val categories by viewModel.categories.collectAsState()

  BackHandler {
    viewModel.navigateTo(Screen.AdminDashboard)
  }

  var newCatName by remember { mutableStateOf("") }
  var newCatSlug by remember { mutableStateOf("") }

  Scaffold(
    containerColor = WBuildsBlack,
    topBar = {
      WBuildsTopBar(
        title = "Manage Categories (${categories.size})",
        showBack = true,
        onBackClick = { viewModel.navigateTo(Screen.AdminDashboard) },
        showSearch = false,
        showSettings = false
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = WBuildsCardElevated),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Create Category", color = WBuildsGold, fontWeight = FontWeight.Bold)
            OutlinedTextField(
              value = newCatName,
              onValueChange = {
                newCatName = it
                newCatSlug = it.lowercase().replace(" ", "-")
              },
              label = { Text("Category Name (e.g. Beacon)") },
              colors = adminTextFieldColors(),
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = newCatSlug,
              onValueChange = { newCatSlug = it },
              label = { Text("Slug Identifier") },
              colors = adminTextFieldColors(),
              modifier = Modifier.fillMaxWidth()
            )
            Button(
              onClick = {
                if (newCatName.isNotBlank()) {
                  val newCat = Category(
                    name = newCatName,
                    slug = newCatSlug,
                    sortOrder = (categories.size + 1).toLong()
                  )
                  viewModel.adminCreateCategory(newCat) {
                    newCatName = ""
                    newCatSlug = ""
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("ADD CATEGORY", color = WBuildsBlack, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      items(categories) { cat ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, WBuildsDivider, RoundedCornerShape(10.dp)),
          colors = CardDefaults.cardColors(containerColor = WBuildsCard),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(cat.name, color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              Text("Slug: ${cat.slug}", color = WBuildsTextSecondary, fontSize = 11.sp)
            }
            IconButton(
              onClick = { viewModel.adminDeleteCategory(cat.id) }
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete Category", tint = Color(0xFFFF453A))
            }
          }
        }
      }
    }
  }
}

@Composable
fun adminTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedBorderColor = WBuildsGold,
  unfocusedBorderColor = WBuildsDivider,
  focusedTextColor = WBuildsTextPrimary,
  unfocusedTextColor = WBuildsTextPrimary,
  focusedLabelColor = WBuildsGold,
  unfocusedLabelColor = WBuildsTextSecondary,
  focusedContainerColor = WBuildsCard,
  unfocusedContainerColor = WBuildsCard
)
