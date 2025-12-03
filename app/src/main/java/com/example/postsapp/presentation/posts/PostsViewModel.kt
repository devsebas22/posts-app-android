package com.example.postsapp.presentation.posts

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
        loadPosts()
        syncPosts()
    }

    private fun loadPosts() {
        getPostsUseCase()
            .onEach { posts ->
                _state.value = _state.value.copy(
                    posts = posts,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)

        if (query.isBlank()) {
            loadPosts()
        } else {
            searchPostsUseCase(query)
                .onEach { posts ->
                    _state.value = _state.value.copy(posts = posts)
                }
                .launchIn(viewModelScope)
        }
    }

    private fun syncPosts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSyncing = true)
            syncPostsUseCase()
            _state.value = _state.value.copy(isSyncing = false)
        }
    }

    fun refresh() {
        syncPosts()
    }
}

data class PostsState(
    val posts: List<Post> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false
)