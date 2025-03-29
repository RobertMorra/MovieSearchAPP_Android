package com.example.moviesearchappapi.model

import org.json.JSONObject

data class MovieDetail(
    val title: String,
    val year: String,
    val rated: String,
    val released: String,
    val runtime: String,
    val genre: String,
    val director: String,
    val writer: String,
    val actors: String,
    val plot: String,
    val language: String,
    val country: String,
    val awards: String,
    val posterUrl: String,
    val imdbID: String,
    val type: String
) {
    companion object {
        fun fromJson(jsonObject: JSONObject): MovieDetail {
            return MovieDetail(
                title = jsonObject.optString("Title"),
                year = jsonObject.optString("Year"),
                rated = jsonObject.optString("Rated"),
                released = jsonObject.optString("Released"),
                runtime = jsonObject.optString("Runtime"),
                genre = jsonObject.optString("Genre"),
                director = jsonObject.optString("Director"),
                writer = jsonObject.optString("Writer"),
                actors = jsonObject.optString("Actors"),
                plot = jsonObject.optString("Plot"),
                language = jsonObject.optString("Language"),
                country = jsonObject.optString("Country"),
                awards = jsonObject.optString("Awards"),
                posterUrl = jsonObject.optString("Poster"),
                imdbID = jsonObject.optString("imdbID"),
                type = jsonObject.optString("Type")
            )
        }
    }
}
