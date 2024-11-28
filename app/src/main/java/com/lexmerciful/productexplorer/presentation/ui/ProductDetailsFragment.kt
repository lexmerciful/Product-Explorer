package com.lexmerciful.productexplorer.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.gson.Gson
import com.lexmerciful.productexplorer.R
import com.lexmerciful.productexplorer.common.Constants.PRODUCT
import com.lexmerciful.productexplorer.common.Constants.PRODUCT_LIST
import com.lexmerciful.productexplorer.common.fromJsonWithType
import com.lexmerciful.productexplorer.databinding.FragmentProductDetailsBinding
import com.lexmerciful.productexplorer.domain.model.Product
import com.lexmerciful.productexplorer.presentation.adapter.ProductAdapter


class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var product: Product
    private var productList: List<Product>? = emptyList()
    private lateinit var navController: NavController
    private var relatedProductList: List<Product>? = emptyList()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFragmentBundle()

        setupToolbar()

        populateData()

        setupRelatedProductsRecyclerView()

        setupRelatedProductList()

    }

    private fun getFragmentBundle() {
        arguments?.apply {
            getString(PRODUCT)?.let { productJson ->
                product = Gson().fromJsonWithType<Product>(productJson)
            }
            getString(PRODUCT_LIST)?.let { productListJson ->
                productList = Gson().fromJsonWithType<List<Product>>(productListJson)
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.productDetailsToolbar)

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.product_details)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun populateData() {
        binding.productImageview.load(product.image){
            placeholder(R.drawable.ic_shopping_product)
            error(R.drawable.ic_shopping_product)
            crossfade(true)
        }

        binding.productTitleTextView.text = product.title
        binding.productPriceTextView.text = getString(R.string.product_price_format, product.price.toString())
        binding.categoryTextView.text = product.category
        binding.productRatingRatingbar.rating = product.rating.rate.toFloat()
        binding.ratingCountTextView.text = getString(R.string.product_rating_count_format, product.rating.count.toString())
        binding.productDescriptionTextView.text = product.description
    }

    private fun setupRelatedProductsRecyclerView() {
        productAdapter = ProductAdapter()
        binding.relatedProductsRecyclerview.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        productAdapter.onItemClick = { product ->
            navController = findNavController()
            val bundle = bundleOf(PRODUCT to Gson().toJson(product), PRODUCT_LIST to Gson().toJson(productList))
            navController.navigate(R.id.action_productDetailsFragment_self, bundle)

        }
    }

    private fun setupRelatedProductList() {
        relatedProductList = productList?.filter { it.category == product.category  && it.title != product.title }
        productAdapter.differ.submitList(relatedProductList)
    }

}