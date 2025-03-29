package com.example.moviesearchappapi.viewmodel

import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviesearchappapi.model.MovieDetail
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MovieDetailViewModel : ViewModel() {

    val movieDetailLiveData = MutableLiveData<MovieDetail>()

    fun fetchMovieDetail(imdbID: String) {
        @Suppress("DEPRECATION")
        FetchMovieDetailTask { detail ->
            detail?.let {
                movieDetailLiveData.postValue(it)
            }
        }.execute(imdbID)
    }

    @Suppress("DEPRECATION")
    private class FetchMovieDetailTask(val callback: (MovieDetail?) -> Unit) :
        AsyncTask<String, Void, MovieDetail?>() {

        override fun doInBackground(vararg params: String?): MovieDetail? {
            val imdbID = params[0] ?: return null
            val apiKey = "ac1aac88"
            val urlString = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbID"
            val connection = URL(urlString).openConnection() as HttpURLConnection
            return try {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = buildString {
                    var line = reader.readLine()
                    while (line != null) {
                        append(line)
                        line = reader.readLine()
                    }
                }
                reader.close()
                val jsonResponse = JSONObject(response)
                if (jsonResponse.optString("Response") == "True") {
                    // Assuming you have a MovieDetail.fromJson() method to parse the JSON.
                    MovieDetail.fromJson(jsonResponse)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                connection.disconnect()
            }
        }

        override fun onPostExecute(result: MovieDetail?) {
            super.onPostExecute(result)
            callback(result)
        }
    }
}
