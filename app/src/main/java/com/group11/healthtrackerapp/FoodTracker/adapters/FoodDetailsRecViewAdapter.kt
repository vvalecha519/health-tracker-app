package com.group11.healthtrackerapp.FoodTracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group11.healthtrackerapp.FoodTracker.repository.FoodNutrition
import com.group11.healthtrackerapp.R


class FoodDetailsRecViewAdapter(private val context: Context) : RecyclerView.Adapter<FoodDetailsRecViewAdapter.ViewHolder>() {
    private var food = ArrayList<FoodNutrition>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.foodName)
        val qty: TextView = itemView.findViewById(R.id.qty)
        val caloriesIntake: TextView = itemView.findViewById(R.id.caloriesIntake)
        val fatIntake: TextView = itemView.findViewById(R.id.fatIntake)
        val proteinIntake: TextView = itemView.findViewById(R.id.proteinIntake)
        val carbohydrateIntake: TextView = itemView.findViewById(R.id.carbohydrateIntake)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.details_food_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFood = food[position]
        holder.foodName.text = currentFood.food_name
        holder.qty.text = currentFood.serving_qty.toString()
        holder.caloriesIntake.text = currentFood.nf_calories.toString()
        holder.fatIntake.text = currentFood.nf_total_fat.toString()
        holder.proteinIntake.text = currentFood.nf_protein.toString()
        holder.carbohydrateIntake.text = currentFood.nf_total_carbohydrate.toString()


    }

    override fun getItemCount(): Int {
        return food.size
    }

    fun setFood(foods: ArrayList<FoodNutrition>) {
        this.food = foods
        notifyDataSetChanged()
    }
}