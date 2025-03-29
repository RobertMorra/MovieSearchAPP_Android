package com.example.moviesearchappapi.viewmodel

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviesearchappapi.model.MovieSearchResult
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MovieViewModel : ViewModel() {

    private val _movies = MutableLiveData<List<MovieSearchResult>>()
    val movies: LiveData<List<MovieSearchResult>>
        get() = _movies

    fun searchMovies(title: String, year: String, studio: String, rating: String) {
        Log.d("MovieViewModel", "searchMovies called with title: '$title', year: '$year'")
        if (title.isEmpty()) {
            _movies.postValue(emptyList())
            return
        }
        SearchMoviesTask { moviesList ->
            Log.d("MovieViewModel", "API returned ${moviesList.size} results")
            _movies.postValue(moviesList)
        }.execute(title, year)
    }

    @Suppress("DEPRECATION")
    private class SearchMoviesTask(val callback: (List<MovieSearchResult>) -> Unit) :
        AsyncTask<String, Void, List<MovieSearchResult>>() {

        override fun doInBackground(vararg params: String?): List<MovieSearchResult> {
            val title = params[0] ?: return emptyList()
            val year = params[1] ?: ""
            val apiKey = "ac1aac88"
            val urlString = if (year.isNotEmpty()) {
                "https://www.omdbapi.com/?apikey=$apiKey&s=${title.replace(" ", "+")}&y=$year"
            } else {
                "https://www.omdbapi.com/?apikey=$apiKey&s=${title.replace(" ", "+")}"
            }
            Log.d("SearchMoviesTask", "API URL: $urlString")
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            val movies = mutableListOf<MovieSearchResult>()
            try {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String? = reader.readLine()
                while (line != null) {
                    response.append(line)
                    line = reader.readLine()
                }
                reader.close()
                Log.d("SearchMoviesTask", "Response: ${response.toString()}")
                val jsonResponse = JSONObject(response.toString())
                if (jsonResponse.optString("Response") == "True") {
                    val jsonArray = jsonResponse.getJSONArray("Search")
                    for (i in 0 until jsonArray.length()) {
                        val movieObj = jsonArray.getJSONObject(i)
                        val movie = MovieSearchResult(
                            title = movieObj.optString("Title"),
                            year = movieObj.optString("Year"),
                            imdbID = movieObj.optString("imdbID"),
                            studio = "N/A",
                            rating = "N/A",
                            posterUrl = movieObj.optString("Poster", "")
                        )
                        movies.add(movie)
                    }
                } else {
                    Log.e("SearchMoviesTask", "API error: ${jsonResponse.optString("Error")}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SearchMoviesTask", "Exception during API call", e)
            } finally {
                connection.disconnect()
            }
            return movies
        }

        override fun onPostExecute(result: List<MovieSearchResult>) {
            super.onPostExecute(result)
            callback(result)
        }
    }
}
