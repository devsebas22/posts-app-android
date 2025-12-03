package com.example.postsapp.presentation.posts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postsapp.domain.model.Post
import com.example.postsapp.domain.usecase.GetPostsUseCase
import com.example.postsapp.domain.usecase.SearchPostsUseCase
import com.example.postsapp.domain.usecase.SyncPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
    private val searchPostsUseCase: SearchPostsUseCase,
    private val syncPostsUseCase: SyncPostsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PostsState())
    val state: StateFlow<PostsState> = _state.asStateFlow()

    init {
        Log.d("PostsApp", "===== PostsViewModel INIT =====")
        loadPosts()
        syncPosts()
    }

    private fun loadPosts() {
        Log.d("PostsApp", "PostsViewModel: loadPosts() called")
        getPostsUseCase()
            .catch { e ->
                Log.e("PostsApp", "PostsViewModel: Error loading posts", e)
                _state.value = _state.value.copy(isLoading = false)
            }
            .onEach { posts ->
                Log.d("PostsApp", "PostsViewModel: Received ${posts.size} posts")
                _state.value = _state.value.copy(
                    posts = posts,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        Log.d("PostsApp", "PostsViewModel: search query = '$query'")
        _state.value = _state.value.copy(searchQuery = query)

        if (query.isBlank()) {
            loadPosts()
        } else {
            searchPostsUseCase(query)
                .catch { e ->
                    Log.e("PostsApp", "PostsViewModel: Error searching", e)
                }
                .onEach { posts ->
                    Log.d("PostsApp", "PostsViewModel: Search found ${posts.size} posts")
                    _state.value = _state.value.copy(posts = posts)
                }
                .launchIn(viewModelScope)
        }
    }

    private fun syncPosts() {
        Log.d("PostsApp", "PostsViewModel: syncPosts() started")
        viewModelScope.launch {
            _state.value = _state.value.copy(isSyncing = true)
            try {
                val result = syncPostsUseCase()
                if (result.isSuccess) {
                    Log.d("PostsApp", "PostsViewModel: Sync SUCCESS")
                } else {
                    Log.e("PostsApp", "PostsViewModel: Sync FAILED: ${result.exceptionOrNull()}")
                }
            } catch (e: Exception) {
                Log.e("PostsApp", "PostsViewModel: Sync exception", e)
            }
            _state.value = _state.value.copy(isSyncing = false)
        }
    }

    fun refresh() {
        Log.d("PostsApp", "PostsViewModel: refresh() called")
        syncPosts()
    }
}

data class PostsState(
    val posts: List<Post> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false
)