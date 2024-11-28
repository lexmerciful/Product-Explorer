package com.lexmerciful.productexplorer.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexmerciful.productexplorer.common.Resource
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.domain.repository.ProductRepository
import com.lexmerciful.productexplorer.presentation.ui.ProductsFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {

    private val _productListFlow = MutableStateFlow<Resource<List<Product>>>(Resource.loading())
    val productListFlow: StateFlow<Resource<List<Product>>> = _productListFlow

    private val _productFilterMutableStateFlow = MutableStateFlow<String?>(null)

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        //Log.d("ProductsFragmentViemodel", "Load")
        viewModelScope.launch {
            repository.getProducts().collect { resource ->
                _productListFlow.value = resource
                //Log.d("ProductsFragmentViemodel", "Error ${ "hgfh" } ")
            }
        }
    }

    val filteredProductsFlow: StateFlow<List<Product>> = combine(
        _productListFlow,
        _productFilterMutableStateFlow
    ) { productResource, filter ->
        if (productResource.data?.isNotEmpty() == true) {
            val products = productResource.data
            if (filter.isNullOrEmpty()) products
            else products.filter { it.category.equals(filter, ignoreCase = true) }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(category: String?) {
        _productFilterMutableStateFlow.value = category
    }

    fun refreshProduct() {
        fetchProducts()
    }

}