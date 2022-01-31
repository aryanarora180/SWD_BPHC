package `in`.ac.bits_hyderabad.swd.swd.view.more.kya

import `in`.ac.bits_hyderabad.swd.swd.databinding.FragmentKyaYearsBinding
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
class CourseYearsFragment : Fragment() {

    private lateinit var _binding: FragmentKyaYearsBinding
    private val viewModel by activityViewModels<KyaViewModel>()

    private val kyaCourseYearAdapter = KyaCourseYearAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKyaYearsBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.kya.value?.kya?.getYears()?.let {
            kyaCourseYearAdapter.apply {
                data = it
                listener = {
                    val action =
                        CourseYearsFragmentDirections.actionCourseYearsFragmentToYearGuidesFragment(
                            it
                        )
                    findNavController().navigate(action)
                }
            }
            _binding.kyaYearsRecycler.apply {
                edgeEffectFactory =
                    SpringyRecycler.springEdgeEffectFactory<KyaCourseYearAdapter.ViewHolder>()
                adapter = kyaCourseYearAdapter
            }
        }
    }
}