package com.example.postsapp.domain.repository

import com.example.postsapp.domain.model.Comment
import com.example.postsapp.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getAllPosts(): Flow<List<Post>>
    fun searchPosts(query: String): Flow<List<Post>>
    suspend fun getPostById(postId: Int): Post?
    suspend fun syncPosts(): Result<Unit>

    fun getCommentsByPostId(postId: Int): Flow<List<Comment>>
    suspend fun addComment(comment: Comment)
}