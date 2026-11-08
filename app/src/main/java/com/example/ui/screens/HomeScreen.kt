package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BuildItem
import com.example.ui.WBuildsViewModel
import com.example.ui.components.BuildCard
import com.example.ui.components.BuildThumbnailImage
import com.example.ui.components.CategoryChipsRow
import com.example.ui.components.DifficultyPill
import com.example.ui.components.WBuildsTopBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.WBuildsBlack
import com.example.ui.theme.WBuildsCard
import com.example.ui.theme.WBuildsDarkSurface
import com.example.ui.theme.WBuildsDivider
import com.example.ui.theme.WBuildsGold
import com.example.ui.theme.WBuildsGoldDark
import com.example.ui.theme.WBuildsTextPrimary
import com.example.ui.theme.WBuildsTextSecondary

@Composable
fun HomeScreen(
  viewModel: WBuildsViewModel
) {
  val categories by viewModel.categories.collectAsState()
  val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
  val builds by viewModel.builds.collectAsState()
  val userFavorites by viewModel.userFavorites.collectAsState()
  val isAdmin = viewModel.isAdmin

  val featuredBuild = builds.firstOrNull { it.featured } ?: builds.firstOrNull()

  Scaffold(
    topBar = {
      WBuildsTopBar(
        showSearch = true,
        onSearchClick = { viewModel.navigateTo(Screen.Search) },
        showSettings = true,
        onSettingsClick = { viewModel.navigateTo(Screen.Settings) },
        isAdmin = isAdmin,
        onAdminClick = { viewModel.navigateTo(Screen.AdminDashboard) }
      )
    },
    bottomBar = {
      WBuildsBottomNav(
        currentScreen = Screen.Home,
        onNavigate = { screen -> viewModel.navigateTo(screen) }
      )
    },
    containerColor = WBuildsBlack
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Horizontal category selector
      CategoryChipsRow(
        categories = categories,
        selectedCategoryId = selectedCategoryId,
        onCategorySelected = { catId -> viewModel.selectCategory(catId) }
      )

      // Builds Gallery (2-column grid with Featured Hero header)
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 10.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Hero Featured Card on "all" category
        if (selectedCategoryId == "all" && featuredBuild != null) {
          item(span = { GridItemSpan(2) }) {
            FeaturedHeroCard(
              build = featuredBuild,
              onBuildClick = { viewModel.openBuild(featuredBuild.id) },
              onStartBuilding = { viewModel.startBuilding(featuredBuild.id) }
            )
          }
        }

        // Section Title
        item(span = { GridItemSpan(2) }) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = when (selectedCategoryId) {
                "all" -> "POPULAR BUILDS"
                "favorites" -> "MY FAVORITES"
                else -> "${selectedCategoryId.uppercase()} BUILDS"
              },
              color = WBuildsGold,
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp
            )
            Text(
              text = "${builds.size} guides",
              color = WBuildsTextSecondary,
              fontSize = 12.sp
            )
          }
        }

        // Builds Grid Items
        if (builds.isEmpty()) {
          item(span = { GridItemSpan(2) }) {
            EmptyGalleryState(
              category = selectedCategoryId,
              isAdmin = isAdmin,
              onSeedClick = { viewModel.adminTriggerSeed() }
            )
          }
        } else {
          items(builds, key = { it.id }) { build ->
            val isFav = userFavorites.any { it.buildId == build.id }
            BuildCard(
              build = build,
              isFavorite = isFav,
              onBuildClick = { viewModel.openBuild(build.id) },
              onFavoriteClick = { viewModel.toggleFavorite(build) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun FeaturedHeroCard(
  build: BuildItem,
  onBuildClick: () -> Unit,
  onStartBuilding: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onBuildClick)
      .border(1.dp, WBuildsGoldDark, RoundedCornerShape(16.dp))
      .testTag("featured_hero_card"),
    colors = CardDefaults.cardColors(containerColor = WBuildsCard)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1.7f)
    ) {
      BuildThumbnailImage(
        imageUrl = build.thumbnailUrl,
        contentDescription = build.title,
        modifier = Modifier.fillMaxSize()
      )

      // Dark gradient overlay
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color(0x33000000), Color(0xEE000000)),
              startY = 40f
            )
          )
      )

      // Tag & Content
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(WBuildsGold)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "FEATURED BUILD",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = WBuildsBlack,
              letterSpacing = 1.sp
            )
          }

          DifficultyPill(difficulty = build.difficulty)
        }

        Column {
          Text(
            text = build.title,
            color = WBuildsTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = build.description,
            color = WBuildsTextSecondary,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${build.totalSteps} STEPS • ${build.edition}",
              color = WBuildsGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )

            Button(
              onClick = onStartBuilding,
              colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
              modifier = Modifier.testTag("start_building_hero_button")
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = WBuildsBlack,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "START",
                color = WBuildsBlack,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun EmptyGalleryState(
  category: String,
  isAdmin: Boolean,
  onSeedClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 40.dp, horizontal = 20.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = Icons.Default.Construction,
        contentDescription = null,
        tint = WBuildsGold,
        modifier = Modifier.size(48.dp)
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "No builds found in '$category'",
        color = WBuildsTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Try switching categories or search for another Minecraft blueprint.",
        color = WBuildsTextSecondary,
        fontSize = 12.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )

      if (isAdmin) {
        Spacer(modifier = Modifier.height(16.dp))
        Button(
          onClick = onSeedClick,
          colors = ButtonDefaults.buttonColors(containerColor = WBuildsGoldDark)
        ) {
          Text("Seed Initial Builds into Database", color = WBuildsBlack, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun WBuildsBottomNav(
  currentScreen: Screen,
  onNavigate: (Screen) -> Unit
) {
  NavigationBar(
    containerColor = WBuildsDarkSurface,
    contentColor = WBuildsGold,
    modifier = Modifier
      .navigationBarsPadding()
      .border(1.dp, WBuildsDivider, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
  ) {
    NavigationBarItem(
      selected = currentScreen is Screen.Home,
      onClick = { onNavigate(Screen.Home) },
      icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
      label = { Text("Home", fontSize = 10.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WBuildsBlack,
        selectedTextColor = WBuildsGold,
        indicatorColor = WBuildsGold,
        unselectedIconColor = WBuildsTextSecondary,
        unselectedTextColor = WBuildsTextSecondary
      )
    )

    NavigationBarItem(
      selected = currentScreen is Screen.Search,
      onClick = { onNavigate(Screen.Search) },
      icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
      label = { Text("Search", fontSize = 10.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WBuildsBlack,
        selectedTextColor = WBuildsGold,
        indicatorColor = WBuildsGold,
        unselectedIconColor = WBuildsTextSecondary,
        unselectedTextColor = WBuildsTextSecondary
      )
    )

    NavigationBarItem(
      selected = currentScreen is Screen.Favorites,
      onClick = { onNavigate(Screen.Favorites) },
      icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
      label = { Text("Favorites", fontSize = 10.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WBuildsBlack,
        selectedTextColor = WBuildsGold,
        indicatorColor = WBuildsGold,
        unselectedIconColor = WBuildsTextSecondary,
        unselectedTextColor = WBuildsTextSecondary
      )
    )

    NavigationBarItem(
      selected = currentScreen is Screen.Profile,
      onClick = { onNavigate(Screen.Profile) },
      icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
      label = { Text("Profile", fontSize = 10.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WBuildsBlack,
        selectedTextColor = WBuildsGold,
        indicatorColor = WBuildsGold,
        unselectedIconColor = WBuildsTextSecondary,
        unselectedTextColor = WBuildsTextSecondary
      )
    )
  }
}
