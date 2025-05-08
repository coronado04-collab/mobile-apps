package com.example.equityspin.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.equityspin.MyViewModel
import com.example.equityspin.R

//Jetpack composable for the NewsScreen
@Composable
fun NewsScreen(modifier: Modifier = Modifier, viewModel: MyViewModel) {
//    Articles are collected as redditNews variables. (included in the viewModel)
    val articles by viewModel.redditNews.collectAsState()

//    Use the viewModel function to get the top posts in the wall street bets subreddit
    LaunchedEffect(Unit) {
        viewModel.fetchTopWallStreetBetsPosts()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
//        Title at the top of the page (shows WallstreetBets Top 20 posts)
        Text(
            text = stringResource(id = R.string.news_screen_title),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )
// Allows us to move it up and down
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
//            Articles are in a list, we can get them and iterate
//            For each article we create a NewsArticleCard (the next jetpack composable)
            items(articles) { article ->
                NewsArticleCard(article = article)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

//Jetpack composable for creating a News article card
@Composable
fun NewsArticleCard(article: NewsArticle) { // Each article is NewsArticle (includes title description and link)
    val context = LocalContext.current
// Create a card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = article.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            if (article.description.isNotBlank()) {
                Text(
                    text = article.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))
            }

            // "View on Reddit" link
            Text(
                text = stringResource(id = R.string.view_on_reddit),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0077CC),
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.link))
                    context.startActivity(intent)
                }
            )
        }
    }
}


//Our news article class has a title, description and link (to the reddit post)
data class NewsArticle(
    val title: String,
    val description: String,
    val link: String
)

