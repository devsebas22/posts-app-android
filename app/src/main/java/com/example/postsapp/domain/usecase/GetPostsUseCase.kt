package com.example.postsapp.domain.usecase

import com.example.postsapp.domain.model.Post
import com.example.postsapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(): Flow<List<Post>> {
        return repository.getAllPosts()
    }
}