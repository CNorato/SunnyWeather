package com.sunnyweather.android.ui.place

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.FragmentPlaceBinding

class PlaceFragment : Fragment(R.layout.fragment_place) {

    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    private val binding: FragmentPlaceBinding by viewBinding()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //onCreateActivity的代替方法
        requireActivity().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                viewModel.placeLiveData.observe(this@PlaceFragment, Observer { result ->
                    val places = result.getOrNull()
                    if (places != null) {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.bgImageView.visibility = View.GONE
                        viewModel.placeList.clear()
                        viewModel.placeList.addAll(places)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                        result.exceptionOrNull()?.printStackTrace()
                    }
                })
                owner.lifecycle.removeObserver(this)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        binding.recyclerView.adapter = adapter
        binding.searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlace(content)
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
    }

}