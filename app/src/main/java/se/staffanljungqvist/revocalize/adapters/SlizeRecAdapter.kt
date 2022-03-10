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
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.models.Slize
import se.staffanljungqvist.revocalize.ui.InGameFragment


class SlizeRecAdapter() : RecyclerView.Adapter<SlizeRecAdapter.MyViewHolder>() {

    var blinknumber = -1
    set(value) {
        field = value
        Log.d(TAG, "slizeadapterns blinknummervariabel sättes till $value")
    }

    var hasChecked = MutableLiveData<Boolean>()
    var slizes = listOf<Slize>()
    lateinit var fragment: InGameFragment
    val blinkHandler = Handler(Looper.getMainLooper())

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<CardView>(R.id.cardView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.slize_item, parent, false)
        )
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Log.d(TAG, "Läser in viewholder $position")
        val slice = slizes[position]

        holder.cardView.setCardBackgroundColor(Color.parseColor(slice.color))

        if (position == blinknumber) {
            holder.cardView.setCardBackgroundColor(Color.WHITE)
            Log.d(TAG, "slizepositionen $position är vit")
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor(slice.color))
        }


        holder.itemView.setOnTouchListener { view, event ->

            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                (fragment as InGameFragment).startDragging(holder)
                Log.d(TAG, "Tryckte ner knappen")

                //    fragment.audioAdapter.playAudio(slice)

            }

            return@setOnTouchListener true
        }

    }

    override fun getItemCount(): Int {
        return slizes.size
    }

    fun runLight2(slizeNumber: Int) {

    }


/*    fun runLight(slizes: List<Slize>) {

        blinknumber = -1
        blinkHandler.post(object : Runnable {

            override fun run() {

                if (blinknumber + 1 <= (slizes.size)) {
                    blinknumber += 1
                    notifyItemChanged(blinknumber)
                    notifyItemChanged(blinknumber - 1)
                    blinkHandler.postDelayed(this, slizes[0].length)
                } else {
                    Log.d(TAG, "har kollat färdigt")
                    hasChecked.value = true
                    blinknumber = -1

                }
            }
        })
    }*/
}