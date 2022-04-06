package se.staffanljungqvist.revocalize.ui

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import se.staffanljungqvist.revocalize.R


class GameBeatFragment : Fragment() {

    var gameRank = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        gameRank = arguments?.getInt("rank")!!
        return inflater.inflate(R.layout.fragment_game_beat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            val stageCompletePlayer = MediaPlayer.create(context, R.raw.perfect3)
        stageCompletePlayer.start()

        val tvRank = view.findViewById<TextView>(R.id.tvGameBeatenWithRank_GameBeat)


        var rankColor = "#FF6C00"

        when (gameRank) {
            1 -> {
                tvRank.text = "BRONZE"
                rankColor = "#FF6C00"

            }
            2 -> {
                tvRank.text = "SILVER"
                rankColor = "#00E3FF"
            }
            3 -> {
                tvRank.text = "GOLD"
                rankColor = "#FFFF58"
                view.findViewById<TextView>(R.id.tvDoBetter).text = "YOU ARE THE ULTIMATE CHAMPION!"
            }
        }

        tvRank.setTextColor(Color.parseColor(rankColor))

        view.findViewById<Button>(R.id.btnBackToStageSelect_BeatenGame).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }
}