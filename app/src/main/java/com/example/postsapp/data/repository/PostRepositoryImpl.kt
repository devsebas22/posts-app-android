package com.example.postsapp.data.repository

import com.example.postsapp.data.local.dao.CommentDao
import com.example.postsapp.data.local.dao.PostDao
import com.example.postsapp.data.local.entities.CommentEntity
import com.example.postsapp.data.local.entities.PostEntity
import com.example.postsapp.data.remote.api.PostsApi
import com.example.postsapp.domain.model.Comment
import com.example.postsapp.domain.model.Post
import com.example.postsapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postsApi: PostsApi,
    private val postDao: PostDao,
    private val commentDao: CommentDao
) : PostRepository {

    override fun getAllPosts(): Flow<List<Post>> {
        return postDao.getAllPosts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchPosts(query: String): Flow<List<Post>> {
        return postDao.searchPosts(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPostById(postId: Int): Post? {
        return postDao.getPostById(postId)?.toDomain()
    }

    override suspend fun syncPosts(): Result<Unit> {
        return try {
            val posts = postsApi.getPosts()
            val entities = posts.map { it.toEntity() }
            postDao.insertPosts(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCommentsByPostId(postId: Int): Flow<List<Comment>> {
        return commentDao.getCommentsByPostId(postId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addComment(comment: Comment) {
        commentDao.insertComment(comment.toEntity())
    }

    private fun PostEntity.toDomain() = Post(
        id = id,
        userId = userId,
        title = title,
        body = body
    )

    private fun com.example.postsapp.data.remote.dto.PostDto.toEntity() = PostEntity(
        id = id,
        userId = userId,
        title = title,
        body = body
    )

    private fun CommentEntity.toDomain() = Comment(
        id = id,
        postId = postId,
        name = name,
        body = body,
        createdAt = createdAt
    )

    private fun Comment.toEntity() = CommentEntity(
        id = id,
        postId = postId,
        name = name,
        body = body,
        createdAt = createdAt
    )
}