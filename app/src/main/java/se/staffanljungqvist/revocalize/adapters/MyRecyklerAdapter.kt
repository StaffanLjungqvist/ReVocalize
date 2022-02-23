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
import se.staffanljungqvist.revocalize.ui.InGameFragment
import se.staffanljungqvist.revocalize.R
import se.staffanljungqvist.revocalize.models.Slize


class SlizeRecAdapter() : RecyclerView.Adapter<SlizeRecAdapter.MyViewHolder>() {

    var blinknumber = -1
    var hasChecked = MutableLiveData<Boolean>()
    var slizes = listOf<Slize>()
    lateinit var fragment : InGameFragment

    inner class MyViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val cardView = view.findViewById<CardView>(R.id.cardView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.slize_item, parent, false))
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val slice = slizes[position]

        if (position == blinknumber) {
            holder.cardView.setCardBackgroundColor(Color.WHITE)
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


    fun runLight(slizes : List<Slize>) {
        val mainHandler = Handler(Looper.getMainLooper())
        blinknumber = 0
        mainHandler.post(object : Runnable {

            override fun run() {

                Log.d(TAG, "blinknummer : ${blinknumber}")

                if (blinknumber < (slizes.size )) {
                    notifyItemChanged(blinknumber)
                    blinknumber += 1

                    mainHandler.postDelayed(this, slizes[0].length)
                } else {
                    hasChecked.value = true
                    blinknumber = -1
                    Log.d(TAG, "blinknummer : $blinknumber")

                }
            }
        })
    }
}