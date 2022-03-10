package se.staffanljungqvist.revocalize.adapters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.builders.Stages
import se.staffanljungqvist.revocalize.models.Stage
import se.staffanljungqvist.revocalize.ui.InGameFragment
import se.staffanljungqvist.revocalize.ui.StartFragment

class StageRecAdapter : RecyclerView.Adapter<StageRecAdapter.StageViewHolder>() {

    lateinit var fragment: StartFragment

    inner class StageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<CardView>(R.id.cvStageCard)
        val tvStageName = view.findViewById<TextView>(R.id.tvStageName)
        val tvStageBeatenRank = view.findViewById<TextView>(R.id.tvStageRank)
        val tvStageComplete = view.findViewById<TextView>(R.id.tvStageComplete)
        val tvStageNumber = view.findViewById<TextView>(R.id.tvStageNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageViewHolder {
        return StageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.stage_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StageViewHolder, position: Int) {

        val stage = fragment.model.stageList[position]

        holder.tvStageNumber.text = (position + 1).toString()
        holder.tvStageName.text = stage.name

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

        holder.cardView.setOnClickListener {
            passData(position)

        }

    }

         fun passData(stage : Int) {
        val bundle = Bundle()
        bundle.putInt("stage", stage)

        val ingameFragment = InGameFragment()
        ingameFragment.arguments = bundle


             fragment.requireActivity().supportFragmentManager.beginTransaction()
                 .replace(R.id.fragmentContainerView, ingameFragment).commit()

    }

    override fun getItemCount(): Int {
        return fragment.model.stageList.size
    }

}