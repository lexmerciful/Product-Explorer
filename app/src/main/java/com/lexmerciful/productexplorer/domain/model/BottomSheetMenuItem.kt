package com.lexmerciful.productexplorer.domain.model

data class BottomSheetMenuItem(
    val name: String,
    var showChecked: Boolean = false,
    val action: () -> Unit
)