package com.example.postsapp.domain.model

data class Comment(
    val id: Int,
    val postId: Int,
    val name: String,
    val body: String,
    val createdAt: Long
)