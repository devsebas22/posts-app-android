package com.example.postsapp.presentation.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postsapp.domain.model.Comment
import com.example.postsapp.domain.model.Post
import com.example.postsapp.domain.repository.PostRepository
import com.example.postsapp.domain.usecase.AddCommentUseCase
import com.example.postsapp.domain.usecase.GetCommentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val repository: PostRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Int = checkNotNull(savedStateHandle["postId"])

    private val _state = MutableStateFlow(CommentsState())
    val state: StateFlow<CommentsState> = _state.asStateFlow()

    init {
        loadPost()
        loadComments()
    }

    private fun loadPost() {
        viewModelScope.launch {
            val post = repository.getPostById(postId)
            _state.value = _state.value.copy(post = post)
        }
    }

    private fun loadComments() {
        getCommentsUseCase(postId)
            .onEach { comments ->
                _state.value = _state.value.copy(
                    comments = comments,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

    fun onCommentNameChange(name: String) {
        _state.value = _state.value.copy(commentName = name)
    }

    fun onCommentBodyChange(body: String) {
        _state.value = _state.value.copy(commentBody = body)
    }

    fun addComment() {
        val name = _state.value.commentName.trim()
        val body = _state.value.commentBody.trim()

        if (name.isBlank() || body.isBlank()) {
            _state.value = _state.value.copy(error = "Nombre y comentario son requeridos")
            return
        }

        viewModelScope.launch {
            val comment = Comment(
                id = 0,
                postId = postId,
                name = name,
                body = body,
                createdAt = System.currentTimeMillis()
            )

            addCommentUseCase(comment)

            _state.value = _state.value.copy(
                commentName = "",
                commentBody = "",
                error = null
            )
        }
    }

    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class CommentsState(
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val commentName: String = "",
    val commentBody: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)