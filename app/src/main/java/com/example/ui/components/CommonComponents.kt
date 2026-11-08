package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.BuildItem
import com.example.data.model.Category
import com.example.ui.theme.WBuildsBadgeBg
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

@Composable
fun WBuildsTopBar(
  title: String? = null,
  showBack: Boolean = false,
  onBackClick: () -> Unit = {},
  showSearch: Boolean = true,
  onSearchClick: () -> Unit = {},
  showSettings: Boolean = true,
  onSettingsClick: () -> Unit = {},
  isAdmin: Boolean = false,
  onAdminClick: () -> Unit = {}
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(WBuildsBlack)
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    // Left action
    Row(
      modifier = Modifier.align(Alignment.CenterStart),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (showBack) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = WBuildsGold
          )
        }
      } else if (showSettings) {
        IconButton(
          onClick = onSettingsClick,
          modifier = Modifier.testTag("settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = WBuildsTextSecondary
          )
        }
      }
    }

    // Center Branding / Title
    Row(
      modifier = Modifier.align(Alignment.Center),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (title == null) {
        // WBuilds Golden Logo
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(WBuildsGoldDark),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Construction,
            contentDescription = null,
            tint = WBuildsBlack,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "WBUILDS",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = WBuildsGold,
            letterSpacing = 1.sp
          )
          Text(
            text = "MINECRAFT GUIDE",
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp,
            color = WBuildsTextSecondary,
            letterSpacing = 1.5.sp
          )
        }
      } else {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = WBuildsTextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    // Right actions
    Row(
      modifier = Modifier.align(Alignment.CenterEnd),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (isAdmin) {
        IconButton(
          onClick = onAdminClick,
          modifier = Modifier.testTag("admin_panel_button")
        ) {
          Icon(
            imageVector = Icons.Default.AdminPanelSettings,
            contentDescription = "Admin Panel",
            tint = WBuildsGold
          )
        }
      }
      if (showSearch) {
        IconButton(
          onClick = onSearchClick,
          modifier = Modifier.testTag("search_icon_button")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Builds",
            tint = WBuildsTextPrimary
          )
        }
      }
    }
  }
}

@Composable
fun CategoryChipsRow(
  categories: List<Category>,
  selectedCategoryId: String,
  onCategorySelected: (String) -> Unit
) {
  val scrollState = rememberScrollState()

  // Standard category options from reference
  val allCategories = listOf(
    Pair("all", "All"),
    Pair("favorites", "Favorites"),
    Pair("farm", "Farm"),
    Pair("medieval", "Medieval"),
    Pair("houses", "Houses"),
    Pair("mansions", "Mansions"),
    Pair("statues", "Statues"),
    Pair("animals", "Animals"),
    Pair("decoration", "Decoration"),
    Pair("furniture", "Furniture"),
    Pair("beacon", "Beacon"),
    Pair("redstone", "Redstone"),
    Pair("survival", "Survival"),
    Pair("modern", "Modern")
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState)
      .padding(horizontal = 12.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    allCategories.forEach { (catId, catName) ->
      val isSelected = selectedCategoryId.equals(catId, ignoreCase = true)
      val bg = if (isSelected) WBuildsGold else WBuildsCard
      val textColor = if (isSelected) WBuildsBlack else WBuildsTextSecondary
      val border = if (isSelected) WBuildsGoldLight else WBuildsDivider

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(bg)
          .border(1.dp, border, RoundedCornerShape(20.dp))
          .clickable { onCategorySelected(catId) }
          .padding(horizontal = 14.dp, vertical = 7.dp)
          .testTag("category_chip_$catId"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = catName,
          fontSize = 13.sp,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          color = textColor
        )
      }
    }
  }
}

@Composable
fun BuildCard(
  build: BuildItem,
  isFavorite: Boolean,
  onBuildClick: () -> Unit,
  onFavoriteClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onBuildClick)
      .border(1.dp, WBuildsDivider, RoundedCornerShape(12.dp))
      .testTag("build_card_${build.id}"),
    colors = CardDefaults.cardColors(containerColor = WBuildsCard)
  ) {
    Column {
      // Thumbnail Box with badges & favorite button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1.25f)
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
                colors = listOf(Color.Transparent, Color(0xAA000000)),
                startY = 100f
              )
            )
        )

        // Edition badge (top left)
        Row(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(8.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(WBuildsBlack.copy(alpha = 0.8f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = build.edition.uppercase(),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = WBuildsGold
            )
          }

          if (build.difficulty.isNotBlank()) {
            DifficultyPill(difficulty = build.difficulty)
          }
        }

        // Favorite Button (top right)
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(6.dp)
            .size(32.dp)
            .clip(CircleShape)
            .background(WBuildsBlack.copy(alpha = 0.65f))
            .clickable(onClick = onFavoriteClick)
            .testTag("fav_button_${build.id}"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) WBuildsGold else WBuildsTextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }

        // Steps badge (bottom left)
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(WBuildsGoldDark.copy(alpha = 0.9f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${build.totalSteps} STEPS",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = WBuildsBlack
          )
        }
      }

      // Title & Specifications
      Column(
        modifier = Modifier.padding(10.dp)
      ) {
        Text(
          text = build.title,
          color = WBuildsTextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
        if (build.dimensions.isNotBlank() || build.estimatedTime.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (build.dimensions.isNotBlank()) {
              Text(
                text = build.dimensions,
                color = WBuildsTextSecondary,
                fontSize = 11.sp
              )
            }
            if (build.dimensions.isNotBlank() && build.estimatedTime.isNotBlank()) {
              Text(
                text = " • ",
                color = WBuildsTextSecondary,
                fontSize = 11.sp
              )
            }
            if (build.estimatedTime.isNotBlank()) {
              Text(
                text = build.estimatedTime,
                color = WBuildsGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun DifficultyPill(difficulty: String) {
  val (color, bg) = when (difficulty.lowercase()) {
    "easy" -> Pair(WBuildsSuccess, WBuildsBlack.copy(alpha = 0.8f))
    "medium" -> Pair(WBuildsGold, WBuildsBlack.copy(alpha = 0.8f))
    "hard" -> Pair(Color(0xFFFF9500), WBuildsBlack.copy(alpha = 0.8f))
    "expert" -> Pair(Color(0xFFFF453A), WBuildsBlack.copy(alpha = 0.8f))
    else -> Pair(WBuildsTextSecondary, WBuildsBlack.copy(alpha = 0.8f))
  }
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(bg)
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = difficulty.uppercase(),
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      color = color
    )
  }
}

@Composable
fun BuildThumbnailImage(
  imageUrl: String,
  contentDescription: String?,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  when {
    imageUrl == "img_minecraft_hero" -> {
      Image(
        painter = painterResource(id = R.drawable.img_minecraft_hero),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    imageUrl == "img_splash_art" -> {
      Image(
        painter = painterResource(id = R.drawable.img_splash_art),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    imageUrl == "img_app_icon_fg" -> {
      Image(
        painter = painterResource(id = R.drawable.img_app_icon_fg),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    imageUrl.startsWith("http") || imageUrl.startsWith("content:") || imageUrl.startsWith("file:") -> {
      AsyncImage(
        model = ImageRequest.Builder(context)
          .data(imageUrl)
          .crossfade(true)
          .error(R.drawable.img_minecraft_hero)
          .placeholder(R.drawable.img_minecraft_hero)
          .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    imageUrl.isNotBlank() && !imageUrl.startsWith("img_") -> {
      AsyncImage(
        model = ImageRequest.Builder(context)
          .data(imageUrl)
          .crossfade(true)
          .error(R.drawable.img_minecraft_hero)
          .placeholder(R.drawable.img_minecraft_hero)
          .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
    else -> {
      Image(
        painter = painterResource(id = R.drawable.img_minecraft_hero),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
      )
    }
  }
}
