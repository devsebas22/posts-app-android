package com.example.postsapp.data.repository

import com.example.postsapp.data.local.dao.CommentDao
import com.example.postsapp.data.local.dao.PostDao
import com.example.postsapp.data.local.entities.CommentEntity
import com.example.postsapp.data.local.entities.PostEntity
import com.example.postsapp.data.remote.api.PostsApi
import com.example.postsapp.data.remote.dto.PostDto
import com.example.postsapp.domain.model.Comment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PostRepositoryTest {

    @Mock
    private lateinit var postsApi: PostsApi

    @Mock
    private lateinit var postDao: PostDao

    @Mock
    private lateinit var commentDao: CommentDao

    private lateinit var repository: PostRepositoryImpl

    private val testPostDto = PostDto(1, 1, "Test Title", "Test Body")
    private val testPostEntity = PostEntity(1, 1, "Test Title", "Test Body")
    private val testCommentEntity = CommentEntity(1, 1, "User", "Comment", 123456789)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = PostRepositoryImpl(postsApi, postDao, commentDao)
    }

    @Test
    fun `getAllPosts should return mapped posts`() = runTest {
        whenever(postDao.getAllPosts()).thenReturn(flowOf(listOf(testPostEntity)))

        val posts = repository.getAllPosts().first()

        assertEquals(1, posts.size)
        assertEquals("Test Title", posts[0].title)
    }

    @Test
    fun `syncPosts should fetch and save posts`() = runTest {
        whenever(postsApi.getPosts()).thenReturn(listOf(testPostDto))

        val result = repository.syncPosts()

        assertTrue(result.isSuccess)
        verify(postDao).insertPosts(listOf(testPostEntity))
    }

    @Test
    fun `addComment should insert comment`() = runTest {
        val comment = Comment(0, 1, "User", "Test comment", System.currentTimeMillis())

        repository.addComment(comment)

        verify(commentDao).insertComment(
            CommentEntity(0, 1, "User", "Test comment", comment.createdAt)
        )
    }

    @Test
    fun `getCommentsByPostId should return mapped comments`() = runTest {
        whenever(commentDao.getCommentsByPostId(1)).thenReturn(flowOf(listOf(testCommentEntity)))

        val comments = repository.getCommentsByPostId(1).first()

        assertEquals(1, comments.size)
        assertEquals("User", comments[0].name)
    }
}