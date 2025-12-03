package com.example.postsapp.data.remote.api

import com.example.postsapp.data.remote.dto.PostDto
import retrofit2.http.GET

interface PostsApi {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}