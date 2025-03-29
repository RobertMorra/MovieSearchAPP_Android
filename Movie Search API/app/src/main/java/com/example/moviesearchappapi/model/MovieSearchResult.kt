package com.example.moviesearchappapi.model

data class MovieSearchResult(
    val title: String,
    val year: String,
    val imdbID: String,
    val studio: String,
    val rating: String,
    val posterUrl: String
)
