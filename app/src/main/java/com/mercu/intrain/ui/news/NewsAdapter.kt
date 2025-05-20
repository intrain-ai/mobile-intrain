package com.mercu.intrain.ui.news
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mercu.intrain.API.Article
import com.mercu.intrain.R
import com.mercu.intrain.databinding.ItemNewsBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class NewsAdapter : ListAdapter<Article, NewsAdapter.NewsViewHolder>(ArticleDiffCallback()) {

    var onItemClick: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(article: Article) {
            with(binding) {
                // Load image with Glide
                Glide.with(itemView)
                    .load(article.urlToImage)
                    .placeholder(R.drawable.ic_pdf_placeholder)
                    .error(R.drawable.ic_warning)
                    .into(newsImage)

                newsHeadline.text = article.title ?: "No Title"
                newsSource.text = article.source.name ?: "Unknown Source"
                newsDate.text = formatDate(article.publishedAt)
                newsSummary.text = article.description ?: "No description available"

                // Handle missing content
                if (article.description.isNullOrEmpty()) {
                    newsSummary.visibility = View.GONE
                } else {
                    newsSummary.visibility = View.VISIBLE
                }
            }
        }

        private fun formatDate(isoDate: String?): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                val outputFormat = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
                val date = inputFormat.parse(isoDate.orEmpty())
                outputFormat.format(date ?: return "Invalid")
            } catch (e: Exception) {
                "Invalid date"
            }
        }

    }

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}