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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WBuildsViewModel
import com.example.ui.navigation.Screen
import com.example.ui.theme.WBuildsBlack
import com.example.ui.theme.WBuildsCard
import com.example.ui.theme.WBuildsDivider
import com.example.ui.theme.WBuildsGold
import com.example.ui.theme.WBuildsGoldDark
import com.example.ui.theme.WBuildsSuccess
import com.example.ui.theme.WBuildsTextPrimary
import com.example.ui.theme.WBuildsTextSecondary

@Composable
fun BuildCompletedScreen(
  viewModel: WBuildsViewModel,
  buildId: String
) {
  val activeBuild by viewModel.activeBuild.collectAsState()
  val context = LocalContext.current

  BackHandler {
    viewModel.navigateTo(Screen.Home)
  }

  Scaffold(
    containerColor = WBuildsBlack
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Golden Victory Badge
      Box(
        modifier = Modifier
          .size(100.dp)
          .clip(CircleShape)
          .background(WBuildsGoldDark)
          .border(2.dp, WBuildsGold, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = "Trophy",
          tint = WBuildsBlack,
          modifier = Modifier.size(56.dp)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "BUILD COMPLETED!",
        color = WBuildsGold,
        fontWeight = FontWeight.Black,
        fontSize = 26.sp,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = activeBuild?.title ?: "Minecraft Blueprint",
        color = WBuildsTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Congratulations! You've successfully built all steps of this Minecraft guide. Your achievement has been recorded.",
        color = WBuildsTextSecondary,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Achievement Summary Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, WBuildsDivider, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = WBuildsCard),
        shape = RoundedCornerShape(14.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Total Steps Placed", color = WBuildsTextSecondary, fontSize = 13.sp)
            Text("${activeBuild?.totalSteps ?: 0} Steps", color = WBuildsGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Difficulty", color = WBuildsTextSecondary, fontSize = 13.sp)
            Text(activeBuild?.difficulty ?: "Medium", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Target Edition", color = WBuildsTextSecondary, fontSize = 13.sp)
            Text(activeBuild?.edition ?: "Both", color = WBuildsTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Cloud Sync", color = WBuildsTextSecondary, fontSize = 13.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WBuildsSuccess, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Saved to Profile", color = WBuildsSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Primary Action: Back to Home
      Button(
        onClick = { viewModel.navigateTo(Screen.Home) },
        colors = ButtonDefaults.buttonColors(containerColor = WBuildsGold),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("completion_home_button")
      ) {
        Icon(Icons.Default.Home, contentDescription = null, tint = WBuildsBlack)
        Spacer(modifier = Modifier.width(8.dp))
        Text("BACK TO GALLERY", color = WBuildsBlack, fontWeight = FontWeight.Black, fontSize = 14.sp)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Secondary Action: Share Achievement
      OutlinedButton(
        onClick = {
          val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "I just completed building ${activeBuild?.title} using WBuilds - The Ultimate Minecraft Guide!")
            type = "text/plain"
          }
          context.startActivity(Intent.createChooser(sendIntent, "Share Achievement"))
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .border(1.dp, WBuildsDivider, RoundedCornerShape(12.dp))
          .testTag("completion_share_button")
      ) {
        Icon(Icons.Default.Share, contentDescription = null, tint = WBuildsGold)
        Spacer(modifier = Modifier.width(8.dp))
        Text("SHARE ACHIEVEMENT", color = WBuildsGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }
  }
}
