package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel

class ExitFragment : Fragment() {

    val model: IngameViewModel by activityViewModels()

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

            model.calculateScore(requireContext())

            requireActivity().supportFragmentManager.popBackStack()

            val bundle = Bundle()
            model.numberOfphrasesDone.value?.let { it1 -> bundle.putInt("score", it1) }
            bundle.putBoolean("isRecord", model.newRecord)
            val theFragment = GameOverFragment()
            theFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, theFragment).addToBackStack(null)
                .commit()



            activity?.viewModelStore?.clear()
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}