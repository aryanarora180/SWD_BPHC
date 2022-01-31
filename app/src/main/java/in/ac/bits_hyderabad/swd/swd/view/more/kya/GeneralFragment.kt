package `in`.ac.bits_hyderabad.swd.swd.view.more.kya

import `in`.ac.bits_hyderabad.swd.swd.databinding.FragmentKyaGeneralBinding
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya.KyaGeneralAdapter
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.KyaViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralFragment : Fragment() {

    private lateinit var _binding: FragmentKyaGeneralBinding
    private val viewModel by activityViewModels<KyaViewModel>()

    private val kyaGeneralAdapter = KyaGeneralAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKyaGeneralBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This fragment will only be opened when the value is not null anyway
        viewModel.kya.value?.kya?.generalElectives.let {
            if (it?.isNotEmpty() == true) {
                kyaGeneralAdapter.data = it
                _binding.kyaGeneralRecycler.adapter = kyaGeneralAdapter
            } else {
                with(_binding) {
                    kyaGeneralRecycler.visibility = View.GONE
                    noInfoImage.visibility = View.VISIBLE
                    noInfoText.visibility = View.VISIBLE
                }
            }
        }
    }
}