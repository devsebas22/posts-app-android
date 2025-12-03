package com.example.postsapp.domain.usecase

import com.example.postsapp.domain.model.Comment
import com.example.postsapp.domain.repository.PostRepository
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(comment: Comment) {
        repository.addComment(comment)
    }
}