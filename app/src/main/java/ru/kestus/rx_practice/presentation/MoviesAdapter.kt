package ru.kestus.rx_practice.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kestus.rx_practice.databinding.MovieItemBinding
import ru.kestus.rx_practice.domain.MovieItem

class MoviesAdapter: ListAdapter<MovieItem, MoviesAdapter.ViewHolder>(DiffCallback) {

    var onItemClickListener: ((item: MovieItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MovieItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: MovieItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MovieItem) {
            binding.movieName.text = item.name
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(item)
            }
        }
    }

    object DiffCallback: DiffUtil.ItemCallback<MovieItem>() {
        override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem == newItem
        }
    }
}