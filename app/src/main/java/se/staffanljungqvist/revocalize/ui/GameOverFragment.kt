package se.staffanljungqvist.revocalize.ui

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.databinding.FragmentGameOverBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel


class GameOverFragment : Fragment() {

    val model: IngameViewModel by activityViewModels()

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



        Log.d(TAG, "Gameoverfragment läser gamecomplete som ${model.gameComplete}")





        val isBeat = arguments?.getBoolean("gameBeat")
        val score = arguments?.getInt("score")
        val userRecord = arguments?.getInt("userRecord")
        val isRecord = arguments?.getBoolean("isRecord")

        if (isBeat!!) {
            val beatGamePlayer = MediaPlayer.create(context, R.raw.perfect3)
            beatGamePlayer.start()
            binding.tvGameOverMessage.setTextColor(Color.parseColor("#38FF75"))
            binding.tvGameOverMessage.text = "CONGRATULATIONS!"
            binding.tvYouBeatGame.isVisible = true
        } else {
            val gameOverPlayer = MediaPlayer.create(context, R.raw.game_over)
            gameOverPlayer.start()
        }



        if (score != null) {
            view.findViewById<TextView>(R.id.tvFinalScore).text = (score).toString()
        }



        if (isRecord == true) {
            binding.tvRecord.setTextColor(Color.parseColor("#38FF75"))
            binding.tvRecord.text = "NEW RECORD!"
            binding.tvRecord.textSize = 24f
        } else {
            binding.tvRecord.text = "YOUR RECORD IS " + userRecord
        }

        binding.btnBackToMain.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, StartFragment()).commit()
            requireActivity().supportFragmentManager.popBackStack()
            activity?.viewModelStore?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}