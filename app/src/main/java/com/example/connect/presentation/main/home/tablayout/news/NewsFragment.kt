package com.example.connect.presentation.main.home.tablayout.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.connect.databinding.NewsFragmentBinding
import com.example.connect.presentation.main.home.HomeFragmentDirections
import com.example.connect.utilites.app.SharedPreferences
import com.kennyc.view.MultiStateView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class NewsFragment : Fragment() {
    @Inject
    lateinit var pref: SharedPreferences
    lateinit var binding: NewsFragmentBinding
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewsFragmentBinding.inflate(inflater, container, false)

        binding.fabNews.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddNewsFragment2())
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.berita()
        observe()
        Log.v("Token", pref.getToken())
        binding.rvNews.adapter = NewsAdapter(
            NewsAdapter.OnclickListener {

            }
        )
    }

    private fun observe() {
        viewModel.state.flowWithLifecycle(lifecycle)
            .onEach { state -> HandleState(state) }
            .launchIn(lifecycleScope)
    }

    private fun HandleState(state: NewsState) {

        when (state) {
            is NewsState.Loading -> {
                binding.apply {
                    msvListNews.viewState = MultiStateView.ViewState.LOADING
                }

            }
            is NewsState.Success -> {
                binding.apply {
                    msvListNews.viewState =
                        if (state.kirimanEntity.isEmpty()) MultiStateView.ViewState.EMPTY
                        else MultiStateView.ViewState.CONTENT
                }


            }
            else -> {}
        }

    }

}