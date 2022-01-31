package `in`.ac.bits_hyderabad.swd.swd.view.goodies

import `in`.ac.bits_hyderabad.swd.swd.data.Goodie
import `in`.ac.bits_hyderabad.swd.swd.databinding.GoodiesFragmentBinding
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.GoodiesAdapter
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.goodies.GoodiesViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoodiesFragment : Fragment() {

    private lateinit var binding: GoodiesFragmentBinding
    private val viewModel by viewModels<GoodiesViewModel>()

    private val goodiesAdapter = GoodiesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GoodiesFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goodiesAdapter.listener = { goodie ->
            val action =
                GoodiesFragmentDirections.actionGoodiesFragmentToBuyGoodieFragment(goodie)
            findNavController().navigate(action)
        }
        binding.goodiesRecycler.apply {
            edgeEffectFactory = SpringyRecycler.springEdgeEffectFactory<GoodiesAdapter.ViewHolder>()
            adapter = goodiesAdapter
        }

        with(viewModel) {
            isDataLoading.observe(viewLifecycleOwner, isDataLoadingObserver)
            isEmptyList.observe(viewLifecycleOwner, isEmptyListObserver)
            goodie.observe(viewLifecycleOwner, goodiesObserver)
            onMessageError.observe(viewLifecycleOwner, onErrorObserver)
        }
    }

    private val isDataLoadingObserver = Observer<Boolean> { isLoading ->
        with(binding) {
            if (isLoading) {
                goodiesProgress.isVisible = true
                goodiesErrorOccurredView.root.isGone = true
                goodiesRecycler.isGone = true
            } else {
                goodiesProgress.isGone = true
            }
        }
    }

    private val isEmptyListObserver = Observer<Boolean> { isEmpty ->
        with(binding) {
            goodiesRecycler.isVisible = !isEmpty
            noGoodiesImage.isVisible = isEmpty
            noGoodiesText.isVisible = isEmpty
        }
    }

    private val goodiesObserver = Observer<List<Goodie>> { goodies ->
        binding.goodiesRecycler.isVisible = true
        goodiesAdapter.data = goodies
    }

    private val onErrorObserver = Observer<String> { error ->
        with(binding) {
            goodiesErrorOccurredView.root.isVisible = true
            goodiesRecycler.isGone = true
            goodiesErrorOccurredView.errorText.text = error
            goodiesErrorOccurredView.errorRetryButton.setOnClickListener {
                viewModel.loadGoodies()
            }
        }
    }
}