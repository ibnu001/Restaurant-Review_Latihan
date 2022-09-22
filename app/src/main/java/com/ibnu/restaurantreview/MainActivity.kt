package com.ibnu.restaurantreview

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ibnu.restaurantreview.adapter.ReviewAdapter
import com.ibnu.restaurantreview.api.ApiConfig
import com.ibnu.restaurantreview.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "MainActivity"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val layoutmanager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutmanager

        val itemDescriotion = DividerItemDecoration(this, layoutmanager.orientation)
        binding.rvReview.addItemDecoration(itemDescriotion)

        findRestaurant()

        binding.btnSend.setOnClickListener { view ->
            postReview(binding.edReview.text.toString())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun findRestaurant() {
        showLoading(true)

        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)

        client.enqueue(object : Callback<RestaurantResponse> {
            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>,
            ) {
                showLoading(false)

                if (response.isSuccessful) {
                    val responBody = response.body()

                    if (responBody != null) {
                        setRestaurantData(responBody.restaurant)
                        setReviewData(responBody.restaurant.customerReviews)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    private fun setRestaurantData(restaurant: Restaurant) {
        binding.tvTitle.text = restaurant.name
        binding.tvDescription.text = restaurant.description

        Glide.with(this@MainActivity)
            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
            .into(binding.ivPicture)
    }

    private fun setReviewData(consumerReviews: List<CustomerReviewsItem>) {
        val listReview = ArrayList<String>()

        for (review in consumerReviews) {
            listReview.add(
                """
                    ${review.review}
                    - ${review.name}
                """.trimIndent()
            )
        }

        val adapter = ReviewAdapter(listReview)

        binding.rvReview.adapter = adapter
        binding.edReview.setText("")
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    // post review
    private fun postReview(review: String) {
        showLoading(true)

        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Dicoding", review)

        client.enqueue(object : Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse>,
                response: Response<PostReviewResponse>,
            ) {
                showLoading(false)

                val responseBody = response.body()

                if (response.isSuccessful && response != null) {
                    setReviewData(responseBody!!.customerReviews)
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}