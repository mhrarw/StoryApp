package com.example.storyapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ItemLoadingStateBinding
import com.example.storyapp.databinding.ItemStoryBinding

class StoryAdapter(
    private val clickListener: (String) -> Unit,
    private val retry: () -> Unit
) : PagingDataAdapter<Story, RecyclerView.ViewHolder>(StoryDiffCallback()) {

    var loadState: LoadState = LoadState.Loading

    companion object {
        const val STORY_VIEW_TYPE = 1
        const val LOAD_STATE_VIEW_TYPE = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            STORY_VIEW_TYPE -> {
                val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StoryViewHolder(binding)
            }
            LOAD_STATE_VIEW_TYPE -> {
                val binding = ItemLoadingStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadStateViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StoryViewHolder -> {
                val story = getItem(position) as Story
                holder.bind(story)
            }
            is LoadStateViewHolder -> {
                holder.bind(loadState)
            }
        }
    }



    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount) {
            STORY_VIEW_TYPE
        } else {
            LOAD_STATE_VIEW_TYPE
        }
    }

    inner class StoryViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.tvItemName.text = story.name
            Glide.with(binding.ivItemPhoto)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            itemView.setOnClickListener {
                clickListener.invoke(story.id)
            }
        }
    }

    inner class LoadStateViewHolder(val binding: ItemLoadingStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.tvError.isVisible = loadState is LoadState.Error

            if (loadState is LoadState.Error) {
                binding.tvError.text = loadState.error.localizedMessage
            }
        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }
    }

}



