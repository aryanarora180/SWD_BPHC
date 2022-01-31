package `in`.ac.bits_hyderabad.swd.swd.view.more.kya

import `in`.ac.bits_hyderabad.swd.swd.databinding.FragmentKyaDepartmentsBinding
import `in`.ac.bits_hyderabad.swd.swd.view.SpringyRecycler
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya.KyaCourseYearAdapter
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.KyaViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KyaDepartmentsFragment : Fragment() {

    private lateinit var _binding: FragmentKyaDepartmentsBinding
    private val viewModel by activityViewModels<KyaViewModel>()

    private val kyaCourseYearAdapter = KyaCourseYearAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKyaDepartmentsBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.kya.value?.kya?.getDepartmentNames()?.let {
            kyaCourseYearAdapter.apply {
                data = it
                listener = {
                    val action =
                        KyaDepartmentsFragmentDirections.actionKyaDepartmentsFragmentToKyaDepartmentsDataFragment(
                            it
                        )
                    findNavController().navigate(action)
                }
            }
            _binding.kyaDeptRecycler.apply {
                edgeEffectFactory =
                    SpringyRecycler.springEdgeEffectFactory<KyaCourseYearAdapter.ViewHolder>()
                adapter = kyaCourseYearAdapter
            }
        }
    }
}
