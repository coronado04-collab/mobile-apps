package com.example.equityspin.model

// Data class to represent the top posts response from Reddit
data class RedditResponse(
    val kind: String,
    val data: RedditData
)

// Data class to represent the "data" field from Reddit's response
data class RedditData(
    val after: String,
    val dist: Int,
    val children: List<RedditPostWrapper>
)

// Data class to represent each post in the "children" array
data class RedditPostWrapper(
    val kind: String,
    val data: RedditPost
)

// Data class to represent the post data
data class RedditPost(
    val title: String,
    val selftext: String,
    val permalink: String
)