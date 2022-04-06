package se.staffanljungqvist.revocalize.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentGameOverBinding


class GameOverFragment : Fragment() {

    private var _binding: FragmentGameOverBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGameOverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val gameOverPlayer = MediaPlayer.create(context, R.raw.fail)
        gameOverPlayer.start()

        binding.btnBackToMain.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, StageSelectFragment()).commit()
            requireActivity().supportFragmentManager.popBackStack()
            activity?.viewModelStore?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}