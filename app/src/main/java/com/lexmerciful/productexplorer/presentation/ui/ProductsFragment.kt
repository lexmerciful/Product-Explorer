package com.lexmerciful.productexplorer.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.lexmerciful.productexplorer.R
import com.lexmerciful.productexplorer.common.BottomSheetMenu
import com.lexmerciful.productexplorer.common.Constants.PRODUCT
import com.lexmerciful.productexplorer.common.Constants.PRODUCT_LIST
import com.lexmerciful.productexplorer.common.Resource
import com.lexmerciful.productexplorer.data.repository.ProductRepositoryImpl
import com.lexmerciful.productexplorer.databinding.FragmentProductsBinding
import com.lexmerciful.productexplorer.domain.model.BottomSheetMenuItem
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.presentation.adapter.ProductAdapter
import com.lexmerciful.productexplorer.presentation.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private lateinit var productAdapter: ProductAdapter
    private val productViewModel by viewModels<ProductViewModel>()
    private lateinit var navController: NavController
    private var hasBeenHandled = false
    private var productList: List<Product>? = emptyList()
    private var filtering = "All"

    companion object {
        private val TAG = ProductsFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        setupProductRecyclerView()

        getProducts()

        productsFlowSetup()

        setupSwipeRefresh()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.homeToolbar)

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.apply {
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.product_store)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.product_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        true
                    }

                    R.id.menu_filter -> {
                        showFilterBottomSheet()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter()
        binding.accountRecyclerView.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        productAdapter.onItemClick = { product ->

            navController = findNavController()
            val bundle = bundleOf(
                PRODUCT to Gson().toJson(product),
                PRODUCT_LIST to Gson().toJson(productList)
            )
            navController.navigate(R.id.action_productsFragment_to_productDetailsFragment, bundle)

        }
    }

    private fun getProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productListFlow.collectLatest { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            Log.d(TAG, "Success ${resource.data}")
                            setupSuccessState()
                        }

                        Resource.Status.ERROR -> {
                            Log.d(TAG, "Error ${resource.message} ${resource.data}")
                            showErrorState(resource.data, resource.message)
                        }

                        Resource.Status.LOADING -> {
                            Log.d(TAG, "Loading ${resource.data}")
                            showLoadingState()
                        }
                    }
                    productList = resource.data
                }
            }
        }
    }

    private fun showLoadingState() {
        binding.loadingProgressBar.isVisible = true
        binding.errorStateLinearLayout.isVisible = false
    }

    private fun showErrorState(productList: List<Product>?, errorMessage: String?) {
        binding.loadingProgressBar.isVisible = false
        if (productList?.isEmpty() == true) {
            binding.errorStateLinearLayout.isVisible = true
        } else {
            binding.errorStateLinearLayout.isVisible = false
            if (!hasBeenHandled) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                hasBeenHandled = true
            }

        }
    }

    private fun setupSuccessState() {
        binding.loadingProgressBar.isVisible = false
        binding.errorStateLinearLayout.isVisible = false
    }


    private fun productsFlowSetup() {
        lifecycleScope.launch {
            productViewModel.filteredProductsFlow.collect {
                productAdapter.differ.submitList(it)
            }
        }
    }

    private fun showFilterBottomSheet() {
        val items = arrayListOf<BottomSheetMenuItem>()
        val bottomSheetMenu = BottomSheetMenu(requireContext(), "Filter by:", items)

        val categoryList = productList?.map { it.category }?.distinct()?.toMutableList()
        categoryList?.add(0, "All")
        categoryList?.forEach { category ->
            items.add(
                BottomSheetMenuItem(
                    name = category,
                    showChecked = filtering == category
                ) {
                    setProductFilter(category)
                }
            )
        }
        bottomSheetMenu.show()
    }

    private fun setProductFilter(category: String) {
        filtering = category
        val categoryText = if ((category.equals("All", ignoreCase = true))) "" else category
        productViewModel.setFilter(categoryText)
    }

    private fun setupSwipeRefresh() {
        binding.productSwipeRefreshLayout.setOnRefreshListener {
            binding.productSwipeRefreshLayout.isRefreshing = false
            hasBeenHandled = false
            productViewModel.refreshProduct()
        }
    }
}