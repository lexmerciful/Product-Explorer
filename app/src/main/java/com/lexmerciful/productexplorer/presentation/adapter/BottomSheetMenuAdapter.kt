package com.lexmerciful.productexplorer.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lexmerciful.productexplorer.databinding.ItemBottomSheetMenuBinding
import com.lexmerciful.productexplorer.domain.model.BottomSheetMenuItem

class BottomSheetMenuAdapter(private val bottomSheetDialog: BottomSheetDialog?, private val items: List<BottomSheetMenuItem>) : RecyclerView.Adapter<BottomSheetMenuAdapter.BottomSheetMenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetMenuViewHolder {
        return BottomSheetMenuViewHolder(ItemBottomSheetMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BottomSheetMenuViewHolder, position: Int) {
        holder.bind(items[position], holder)
    }

    inner class BottomSheetMenuViewHolder(val binding: ItemBottomSheetMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BottomSheetMenuItem, holder: BottomSheetMenuViewHolder) {
            binding.bsMenuTextView.text = item.name

            // Show Check
            binding.checkImageView.isVisible = item.showChecked

            // Set action click listener
            holder.itemView.setOnClickListener {
                item.action()
                bottomSheetDialog?.dismiss()
            }
        }
    }

}