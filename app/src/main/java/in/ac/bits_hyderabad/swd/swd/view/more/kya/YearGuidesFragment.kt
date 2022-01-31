package `in`.ac.bits_hyderabad.swd.swd.view.more.kya

import `in`.ac.bits_hyderabad.swd.swd.databinding.FragmentKyaYearsBinding
import `in`.ac.bits_hyderabad.swd.swd.view.adapter.kya.KyaCourseGuideDataAdapter
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.KyaViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YearGuidesFragment : Fragment() {

    private lateinit var _binding: FragmentKyaYearsBinding
    private val args by navArgs<YearGuidesFragmentArgs>()
    private val viewModel by activityViewModels<KyaViewModel>()

    private val kyaCourseGuideDataAdapter = KyaCourseGuideDataAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKyaYearsBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.kya.value?.kya?.getCourseGuideForYear(args.year)?.let {
            kyaCourseGuideDataAdapter.apply {
                data = it
            }
            _binding.kyaYearsRecycler.adapter = kyaCourseGuideDataAdapter
        }
    }
}
