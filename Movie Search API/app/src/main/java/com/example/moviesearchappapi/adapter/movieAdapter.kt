package com.example.moviesearchappapi.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesearchappapi.DetailActivity
import com.example.moviesearchappapi.databinding.ItemMovieBinding
import com.example.moviesearchappapi.model.MovieSearchResult

class MovieAdapter(private var movies: List<MovieSearchResult>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieSearchResult) {
            binding.tvTitle.text = movie.title
            binding.tvYear.text = movie.year
            binding.tvStudio.text = movie.studio
            binding.tvRating.text = movie.rating

            binding.root.setOnClickListener {
                val context: Context = binding.root.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("movieTitle", movie.title)
                    putExtra("movieYear", movie.year)
                    putExtra("movieStudio", movie.studio)
                    putExtra("movieRating", movie.rating)
                    putExtra("moviePosterUrl", movie.posterUrl)
                    putExtra("imdbID", movie.imdbID)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    fun updateMovies(newMovies: List<MovieSearchResult>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}
