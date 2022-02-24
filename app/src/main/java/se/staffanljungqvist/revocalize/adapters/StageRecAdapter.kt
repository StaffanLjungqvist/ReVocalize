package se.staffanljungqvist.revocalize.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.builders.Stages
import se.staffanljungqvist.revocalize.ui.InGameFragment
import se.staffanljungqvist.revocalize.ui.StartFragment

class StageRecAdapter : RecyclerView.Adapter<StageRecAdapter.StageViewHolder>() {

    lateinit var fragment: StartFragment

    inner class StageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<CardView>(R.id.cvStageCard)
        val tvStageName = view.findViewById<TextView>(R.id.tvStageName)
        val tvStageDifficulty = view.findViewById<TextView>(R.id.tvStageDifficulty)
        val tvStageBeatenRank = view.findViewById<TextView>(R.id.tvStageRank)
        val tvStageComplete = view.findViewById<TextView>(R.id.tvStageComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageViewHolder {
        return StageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.stage_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StageViewHolder, position: Int) {

        val stage = Stages.StageList[position]

        val cardColor =
        when (stage.difficulty) {
            "EASY" -> "#4BEBFF"
            "MEDIUM" -> "#38FF75"
            "HARD" -> "#FF4BF8"
            else -> {
                 "#4BEBFF"
            }
        }

        val rankColor =
            when (stage.beatenWithRank) {
                "BRONZE" -> "#FF6C00"
                "SILVER" -> "#00E3FF"
                "GOLD" -> "#FFFF58"
                else -> {
                    "#4BEBFF"
                }
            }







        if (stage.isComplete) {
            holder.tvStageBeatenRank.isVisible = true
            holder.tvStageBeatenRank.text = stage.beatenWithRank
            holder.tvStageBeatenRank.setTextColor(Color.parseColor(rankColor))
            holder.tvStageComplete.text = "COMPLETED!"
        }

        holder.cardView.setCardBackgroundColor(Color.parseColor(cardColor))
        holder.tvStageName.text = stage.name
        holder.tvStageDifficulty.text = stage.difficulty

        holder.cardView.setOnClickListener {
            fragment.model.currentStage = Stages.StageList[position]
            fragment.requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, InGameFragment()).commit()
        }


    }

    override fun getItemCount(): Int {
        return Stages.StageList.size
    }

}