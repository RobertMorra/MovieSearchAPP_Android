package com.example.moviesearchappapi

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesearchappapi.adapter.MovieAdapter
import com.example.moviesearchappapi.databinding.ActivityMainBinding
import com.example.moviesearchappapi.viewmodel.MovieViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MovieAdapter
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MovieAdapter(emptyList())
        binding.rvMovies.layoutManager = LinearLayoutManager(this)
        binding.rvMovies.adapter = adapter

        viewModel.movies.observe(this, Observer { movies ->
            adapter.updateMovies(movies)
        })

        // Set up Search button click and touch animations
        binding.btnSearch.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val year = binding.etYear.text.toString().trim()
            val studio = binding.etStudio.text.toString().trim()
            val rating = binding.etRating.text.toString().trim()
            viewModel.searchMovies(title, year, studio, rating)
        }
        addScaleAnimation(binding.btnSearch)

        // Set up Scroll Down button click and touch animations
        binding.btnScrollDown.setOnClickListener {
            if (adapter.itemCount > 0) {
                binding.rvMovies.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }
        addScaleAnimation(binding.btnScrollDown)
    }

    // Function to add scale animation on touch (grow slightly when pressed)
    private fun addScaleAnimation(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Scale up to 1.1x the original size
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Return to original size
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                }
            }
            false  // allow other click events to be processed
        }
    }
}
