package com.lexmerciful.productexplorer.common

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lexmerciful.productexplorer.databinding.BottomSheetMenuBinding
import com.lexmerciful.productexplorer.domain.model.BottomSheetMenuItem
import com.lexmerciful.productexplorer.presentation.adapter.BottomSheetMenuAdapter

class BottomSheetMenu(
    context: Context,
    private val title: String = "",
    private val items: List<BottomSheetMenuItem>
)  {

    private val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context)
    private var binding: BottomSheetMenuBinding

    init {
        binding = BottomSheetMenuBinding.inflate(LayoutInflater.from(context))
        val view = binding.root
        bottomSheetDialog.setContentView(view)

        binding.titleTextView.text = title

        binding.bottomSheetMenuRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.bottomSheetMenuRecyclerView.adapter = BottomSheetMenuAdapter(bottomSheetDialog, items)
    }

    fun show() {
        bottomSheetDialog.show()
    }

    fun dismiss() {
        bottomSheetDialog.dismiss()
    }

}