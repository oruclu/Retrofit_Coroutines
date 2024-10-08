package com.example.retrofitlearn.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitlearn.adapter.RecyclerViewAdapter
import com.example.retrofitlearn.databinding.ActivityMainBinding
import com.example.retrofitlearn.model.PostModel
import com.example.retrofitlearn.service.PostAPI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.schedulers.RxThreadFactory
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private var postModels: ArrayList<PostModel>? = null

    private var adapter: RecyclerViewAdapter? = null

    private var compositeDisposable: CompositeDisposable? = null

    private var job: Job? = null

    private lateinit var exceptionHandler : CoroutineExceptionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        compositeDisposable = CompositeDisposable()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("exxxx: ${throwable.localizedMessage}")
        }

        loadData()
    }

    //----      REQUEST WITH RxJava       -------//
    private fun loadData() {
        lifecycleScope.launch(exceptionHandler) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build()
                    .create(PostAPI::class.java)

            //-- supervisorScope : scope içindəki scope-ların biri çökəndə
            // digərlərinə təsir etməsin, digər hamısı işləməyə davam etsin
            supervisorScope {
                job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                    val response = retrofit.fetchData()

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                postModels = ArrayList(it)
                                postModels?.let {
                                    adapter = RecyclerViewAdapter(it)
                                    binding.recyclerView.adapter = adapter
                                }
                            }
                        }
                    }
                }
            }

        }

//        compositeDisposable?.addAll(
//            retrofit.fetchData()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::handleResponse)
//        )


        /*

        //----      REQUEST WITH CALL       -------//
        val service = retrofit.create(PostAPI::class.java)

        val call = service.fetchData()

         call.enqueue(object : Callback<List<PostModel>> {
            override fun onResponse(
                call: Call<List<PostModel>>,
                response: Response<List<PostModel>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        postModels = ArrayList(it)

                        postModels?.let {

                            adapter = RecyclerViewAdapter(it)
                            binding.recyclerView.adapter = adapter
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<PostModel>>, t: Throwable) {
                println("in onFailure : ${t.message}")
            }

        }) */

    }

//    fun handleResponse(postList: List<PostModel>) {
//        postModels = ArrayList(postList)
//
//        postModels?.let {
//            adapter = RecyclerViewAdapter(it)
//            binding.recyclerView.adapter = adapter
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }
}