package com.example.moviesearchappapi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moviesearchappapi.databinding.ActivityDetailBinding
import com.example.moviesearchappapi.model.MovieDetail
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("movieTitle") ?: "No Title Available"
        val year = intent.getStringExtra("movieYear") ?: "No Year Available"
        val studio = intent.getStringExtra("movieStudio") ?: "No Studio Available"
        val rating = intent.getStringExtra("movieRating") ?: "No Rating Available"
        val posterUrl = intent.getStringExtra("moviePosterUrl") ?: ""
        val imdbID = intent.getStringExtra("imdbID") ?: ""

        // Initially set basic details
        binding.tvDetailTitle.text = title
        binding.tvDetailYear.text = "Year: $year"
        binding.tvDetailStudio.text = "Studio: $studio"
        binding.tvDetailRating.text = "Rating: $rating"

        // Load poster from search result if available
        if (posterUrl.isNotEmpty()) {
            ImageLoadTask(posterUrl, binding.ivPoster).execute()
        }


        if (imdbID.isNotEmpty()) {
            FetchMovieDetailTask(imdbID) { movieDetail ->
                movieDetail?.let {
                    binding.tvDetailTitle.text = it.title
                    binding.tvDetailYear.text = "Year: ${it.year}"
                    binding.tvDetailStudio.text = "Director: ${it.director}"
                    binding.tvDetailRating.text = "Rated: ${it.rated}"
                    binding.tvDetailPlot.text = it.plot
                    if (it.posterUrl.isNotEmpty()) {
                        ImageLoadTask(it.posterUrl, binding.ivPoster).execute()
                    }
                }
            }.execute()
        }

        // Back button to finish the activity
        binding.btnBack.setOnClickListener { finish() }
    }

    @Suppress("DEPRECATION")
    private class ImageLoadTask(val url: String, val imageView: android.widget.ImageView) :
        AsyncTask<Void, Void, Bitmap?>() {
        override fun doInBackground(vararg params: Void?): Bitmap? {
            return try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            result?.let { imageView.setImageBitmap(it) }
        }
    }

    @Suppress("DEPRECATION")
    private class FetchMovieDetailTask(
        val imdbID: String,
        val callback: (MovieDetail?) -> Unit
    ) : AsyncTask<Void, Void, MovieDetail?>() {
        override fun doInBackground(vararg params: Void?): MovieDetail? {
            val apiKey = "ac1aac88"
            val urlString = "https://www.omdbapi.com/?apikey=$apiKey&i=$imdbID"
            return try {
                val connection = URL(urlString).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line = reader.readLine()
                while (line != null) {
                    response.append(line)
                    line = reader.readLine()
                }
                reader.close()
                val jsonResponse = JSONObject(response.toString())
                if (jsonResponse.optString("Response") == "True") {
                    MovieDetail.fromJson(jsonResponse)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: MovieDetail?) {
            super.onPostExecute(result)
            callback(result)
        }
    }
}
