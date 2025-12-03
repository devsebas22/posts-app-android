package com.example.postsapp.domain.usecase

import com.example.postsapp.domain.model.Post
import com.example.postsapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(query: String): Flow<List<Post>> {
        return repository.searchPosts(query)
    }
}