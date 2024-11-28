package com.lexmerciful.productexplorer.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lexmerciful.productexplorer.R
import com.lexmerciful.productexplorer.databinding.ItemProductBinding
import com.lexmerciful.productexplorer.domain.model.Product

class ProductAdapter() : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    lateinit var onItemClick: ((Product) -> Unit?)

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val product = differ.currentList[position]

        holder.binding.productImageview.load(product.image) {
            placeholder(R.drawable.ic_shopping_product)
            error(R.drawable.ic_shopping_product)
            crossfade(true)
        }

        holder.binding.productTitleTextview.text = product.title
        holder.binding.priceTextview.text = context.getString(R.string.item_price_format, product.price.toString())
        holder.binding.ratingTextview.text = "${product.rating.rate}"
        holder.binding.ratingCountTextview.text =
            context.getString(R.string.item_rating_format, product.rating.count.toString())

        holder.itemView.setOnClickListener {
            onItemClick.invoke(product)
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this@ProductAdapter, diffUtil)
}