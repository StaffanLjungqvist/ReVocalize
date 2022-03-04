package se.staffanljungqvist.revocalize.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.adapters.StageRecAdapter
import se.staffanljungqvist.revocalize.builders.Stages
import se.staffanljungqvist.revocalize.databinding.FragmentStartBinding
import se.staffanljungqvist.revocalize.viewmodels.ViewModel


class StartFragment : Fragment() {

    val model : ViewModel by activityViewModels()

    private lateinit var recyclerView : RecyclerView

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        recyclerView = binding.rvStageRecyclerView
        var stageAdapter = StageRecAdapter()
        stageAdapter.fragment = this
        recyclerView.adapter = stageAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false )

        model.loadUserData(requireContext())

        model.userDataLoaded.observe(requireActivity(), Observer {
            stageAdapter.notifyDataSetChanged()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}