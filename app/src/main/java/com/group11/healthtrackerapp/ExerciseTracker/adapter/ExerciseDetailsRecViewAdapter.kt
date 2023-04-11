package com.group11.healthtrackerapp.ExerciseTracker.adapter

import android.content.Context
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.group11.healthtrackerapp.ExerciseTracker.repository.Exercise
import com.group11.healthtrackerapp.R

class ExerciseDetailsRecViewAdapter(private val context: Context) : RecyclerView.Adapter<ExerciseDetailsRecViewAdapter.ViewHolder>() {
    private var exercises = ArrayList<Exercise>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.details_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.exerciseType.text = exercises[position].exerciseType.toString()
        holder.duration.text = "${exercises[position].duration.hours}h ${exercises[position].duration.minutes}m"
        holder.caloriesBurned.text = exercises[position].caloriesBurned.toString()
        holder.dateTime.text = exercises[position].dateTime.toString().replace("T", " ")
        holder.parent.setOnClickListener {
            Toast.makeText(context, "${exercises[position].exerciseType} Selected", Toast.LENGTH_SHORT).show()
        }

        holder.description.text = exercises[position].description

        if (exercises[position].isExpanded) {
            TransitionManager.beginDelayedTransition(holder.parent)
            holder.expandedRelLayout.visibility = View.VISIBLE
            holder.downArrow.visibility = View.GONE
        } else {
            TransitionManager.beginDelayedTransition(holder.parent)
            holder.expandedRelLayout.visibility = View.GONE
            holder.downArrow.visibility = View.VISIBLE
        }

        holder.downArrow.setOnClickListener {
            val exercise = exercises[position]
            exercise.isExpanded = !exercise.isExpanded
            notifyItemChanged(position)
        }

        holder.upArrow.setOnClickListener {
            val exercise = exercises[position]
            exercise.isExpanded = !exercise.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }


    fun setExercises(exercises: ArrayList<Exercise>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var exerciseType: TextView = itemView.findViewById(R.id.exerciseType)
        var parent: CardView = itemView.findViewById(R.id.parent)
        var duration: TextView = itemView.findViewById(R.id.duration)
        var caloriesBurned: TextView = itemView.findViewById(R.id.caloriesBurned)
        var dateTime: TextView = itemView.findViewById(R.id.dateTime)

        var downArrow: ImageView = itemView.findViewById(R.id.btnDownArrow)
        var upArrow: ImageView = itemView.findViewById(R.id.btnUpArrow)
        var expandedRelLayout: RelativeLayout = itemView.findViewById(R.id.expandedRelLayout)
        var description: TextView = itemView.findViewById(R.id.txtShortDesc)


    }

}