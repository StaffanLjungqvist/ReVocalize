package se.staffanljungqvist.revocalize.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.adapters.StageRecAdapter
import se.staffanljungqvist.revocalize.databinding.FragmentStageSelectBinding
import se.staffanljungqvist.revocalize.viewmodels.StageSelectViewModel


class StageSelectFragment : Fragment() {

    val model: StageSelectViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentStageSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStageSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.loadStages(requireContext())

        model.userDataLoaded.observe(viewLifecycleOwner) {
            recyclerView = binding.rvStageRecyclerView
            val stageAdapter = StageRecAdapter()
            stageAdapter.fragment = this
            recyclerView.adapter = stageAdapter
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            if (model.hasBeatenGame != 0) {
                showStageBeaten(model.hasBeatenGame)
            }
        }


        model.theGameWasBeaten.observe(viewLifecycleOwner) {
            if (it) {
                val bundle = Bundle()
                bundle.putInt("rank", model.hasBeatenGame)
                val theFragment = GameBeatFragment()
                theFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, theFragment).addToBackStack(null)
                    .commit()
            }
        }
    }

    fun showStageBeaten(rankBeaten : Int) {

            binding.tvGameBeatenWith.isVisible = true
            binding.tvGameBeatenWithRank.isVisible = true

            when (rankBeaten) {
                1 -> {
                    binding.tvGameBeatenWithRank.text = "BRONZE"
                    binding.tvGameBeatenWithRank.setTextColor((Color.parseColor("#FF6C00")))
                }
                2 -> {
                    binding.tvGameBeatenWithRank.text = "SILVER"
                    binding.tvGameBeatenWithRank.setTextColor((Color.parseColor("#4BEBFF")))
            }
                3 -> {
                    binding.tvGameBeatenWithRank.text = "GOLD"
                    binding.tvGameBeatenWithRank.setTextColor((Color.parseColor("#FFFF58")))
                }
    }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}