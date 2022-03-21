package se.staffanljungqvist.revocalize.adapters

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.R
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
        val cardViewLocked = view.findViewById<CardView>(R.id.cardViewLocked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageViewHolder {
        return StageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.stage_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StageViewHolder, position: Int) {

        var isLocked = false
        val stage = fragment.model.stageList[position]

        if (position != 0) {
            if (!fragment.model.stageList[position - 1].isComplete) {
                isLocked = true
            }
        }
            holder.tvStageNumber.text = (position + 1).toString()
            holder.tvStageName.text = stage.name

            val cardColor =
                when (stage.beatenWithRank) {
                    "BRONZE" -> "#FF6C00"
                    "SILVER" -> "#008394"
                    "GOLD" -> "#FFFF58"
                    else -> {
                        "#FFFFFF"
                    }
                }

/*            val rankColor =
                when (stage.beatenWithRank) {
                    "BRONZE" -> "#FF6C00"
                    "SILVER" -> "#008394"
                    "GOLD" -> "#FFFF58"
                    else -> {
                        "#4BEBFF"
                    }
                }*/

            if (stage.isComplete) {
                holder.tvStageBeatenRank.isVisible = true
                holder.tvStageBeatenRank.text = stage.beatenWithRank
              //  holder.tvStageBeatenRank.setTextColor(Color.parseColor(rankColor))
                holder.tvStageComplete.text = "COMPLETED!"
                holder.cardView.setCardBackgroundColor(Color.parseColor(cardColor))
            }



            holder.cardView.setOnClickListener {
                passData(position, stage.pointRecord)
            }

        if (isLocked) {
            holder.cardViewLocked.isVisible = true
        }
    }

    fun passData(stage: Int, score : Int) {
        val bundle = Bundle()
        bundle.putInt("stage", stage)
        bundle.putInt("score", score)
        val ingameFragment = InGameFragment()
        ingameFragment.arguments = bundle
        fragment.requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, ingameFragment).commit()
    }

    override fun getItemCount(): Int {
        return fragment.model.stageList.size
    }

}