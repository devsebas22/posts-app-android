package com.example.postsapp.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.postsapp.presentation.comments.CommentsScreen
import com.example.postsapp.presentation.posts.PostsScreen

sealed class Screen(val route: String) {
    object Posts : Screen("posts")
    object Comments : Screen("comments/{postId}") {
        fun createRoute(postId: Int) = "comments/$postId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Posts.route
    ) {
        composable(Screen.Posts.route) {
            PostsScreen(
                onPostClick = { postId ->
                    navController.navigate(Screen.Comments.createRoute(postId))
                }
            )
        }

        composable(
            route = Screen.Comments.route,
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.IntType
                }
            )
        ) {
            CommentsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}