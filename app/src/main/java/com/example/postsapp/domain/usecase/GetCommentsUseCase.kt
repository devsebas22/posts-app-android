package com.example.postsapp.domain.usecase

import com.example.postsapp.domain.model.Comment
import com.example.postsapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(postId: Int): Flow<List<Comment>> {
        return repository.getCommentsByPostId(postId)
    }
}