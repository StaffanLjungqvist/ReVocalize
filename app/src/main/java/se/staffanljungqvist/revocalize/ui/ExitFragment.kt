package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel

class ExitFragment : Fragment() {

    lateinit var model: IngameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, StartFragment()).commit()
            activity?.viewModelStore?.clear()
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            model.calculateScore(requireContext())
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}