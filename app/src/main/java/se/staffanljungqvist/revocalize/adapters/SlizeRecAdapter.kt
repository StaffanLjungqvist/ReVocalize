package se.staffanljungqvist.revocalize.adapters

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.models.Slize
import se.staffanljungqvist.revocalize.ui.InGameFragment
import se.staffanljungqvist.revocalize.ui.TAG


class SlizeRecAdapter() : RecyclerView.Adapter<SlizeRecAdapter.MyViewHolder>() {

    var blinknumber = -1
    var slizes = listOf<Slize>()
    lateinit var fragment: InGameFragment
    val blinkHandler = Handler(Looper.getMainLooper())

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<CardView>(R.id.cardView)
        val highLight = view.findViewById<CardView>(R.id.cvWhite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.slize_item, parent, false)
        )
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val slice = slizes[position]

        holder.cardView.apply {
            animate()
                .alpha(1f)
                .setDuration(200.toLong())
                .setListener(null)
        }


        holder.cardView.setCardBackgroundColor(Color.parseColor(slice.color))

        val slizeLength = fragment.model.slices?.get(0)?.length

        if (position == blinknumber) {
            holder.highLight.isVisible = true
            holder.highLight.apply {
                if (slizeLength != null) {
                    holder.highLight.isVisible = true
                    //           animate()
                    //                .scaleX(1f)
                    //                  .alpha(0f)
                    //                 .setDuration(2000.toLong())
                    //                .setListener(null)
                }
            }
            Log.d(TAG, "slizepositionen $position är vit")
        } else {
            holder.highLight.isVisible = false
        }


        holder.itemView.setOnTouchListener { view, event ->

            if (fragment.model.doneIterating.value!!) {

                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    (fragment as InGameFragment).startDragging(holder)
                    Log.d(TAG, "Tryckte ner knappen")
                }
            }
            return@setOnTouchListener true
        }

    }

    override fun getItemCount(): Int {
        return slizes.size
    }
}