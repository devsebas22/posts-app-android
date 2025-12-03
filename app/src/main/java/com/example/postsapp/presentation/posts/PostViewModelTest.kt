package com.example.postsapp.presentation.posts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.postsapp.domain.model.Post
import com.example.postsapp.domain.usecase.GetPostsUseCase
import com.example.postsapp.domain.usecase.SearchPostsUseCase
import com.example.postsapp.domain.usecase.SyncPostsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@ExperimentalCoroutinesApi
class PostsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getPostsUseCase: GetPostsUseCase

    @Mock
    private lateinit var searchPostsUseCase: SearchPostsUseCase

    @Mock
    private lateinit var syncPostsUseCase: SyncPostsUseCase

    private lateinit var viewModel: PostsViewModel

    private val testPosts = listOf(
        Post(1, 1, "Title 1", "Body 1"),
        Post(2, 1, "Title 2", "Body 2")
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        whenever(getPostsUseCase()).thenReturn(flowOf(testPosts))
        whenever(syncPostsUseCase()).thenReturn(Result.success(Unit))

        viewModel = PostsViewModel(getPostsUseCase, searchPostsUseCase, syncPostsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load posts`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(testPosts, state.posts)
        assertFalse(state.isLoading)
    }

    @Test
    fun `search query should trigger search`() = runTest {
        val searchResults = listOf(testPosts[0])
        whenever(searchPostsUseCase("Title 1")).thenReturn(flowOf(searchResults))

        viewModel.onSearchQueryChange("Title 1")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(searchPostsUseCase).invoke("Title 1")
        assertEquals("Title 1", viewModel.state.value.searchQuery)
    }

    @Test
    fun `refresh should sync posts`() = runTest {
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(syncPostsUseCase).invoke()
    }
}