package com.example.postsapp.domain.usecase

import com.example.postsapp.domain.repository.PostRepository
import javax.inject.Inject

class SyncPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.syncPosts()
    }
}