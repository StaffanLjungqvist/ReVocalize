package se.staffanljungqvist.revocalize.adapters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.ui.InGameFragment
import se.staffanljungqvist.revocalize.ui.StageSelectFragment

class StageRecAdapter : RecyclerView.Adapter<StageRecAdapter.StageViewHolder>() {

    lateinit var fragment: StageSelectFragment

    inner class StageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cvStageCard)
        val tvStageName: TextView = view.findViewById(R.id.tvStageName)
        val tvStageBeatenRank: TextView = view.findViewById(R.id.tvStageRank)
        val tvStageNumber: TextView = view.findViewById(R.id.tvStageNumber)
        val cardViewLocked: CardView = view.findViewById(R.id.cardViewLocked)
        val llPlay: LinearLayout = view.findViewById(R.id.llPlay)
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
                "SILVER" -> "#4BEBFF"
                "GOLD" -> "#FFFF58"
                else -> {
                    "#FFFFFF"
                }
            }

        if (stage.isComplete) {
            holder.tvStageBeatenRank.text = stage.beatenWithRank
            holder.cardView.setCardBackgroundColor(Color.parseColor(cardColor))
        } else {
            holder.tvStageBeatenRank.text = "NOT COMPLETE"
        }


        holder.llPlay.setOnClickListener {
            passData(position)
        }

        //Läser nivåer som inte är avklarade
        if (isLocked) {
            holder.cardViewLocked.isVisible = true
            holder.llPlay.isVisible = false
        }
    }

    private fun passData(stage: Int) {

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