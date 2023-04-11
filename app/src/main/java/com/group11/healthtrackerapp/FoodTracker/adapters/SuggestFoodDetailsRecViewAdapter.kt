package com.group11.healthtrackerapp.FoodTracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group11.healthtrackerapp.FoodTracker.Schema.FoodOption
import com.group11.healthtrackerapp.FoodTracker.viewmodels.FoodTrackerViewModel
import com.group11.healthtrackerapp.R
import com.squareup.picasso.Picasso



class SuggestFoodDetailsRecViewAdapter(private val context: Context, mFoods : ArrayList<FoodOption>, mViewModel : FoodTrackerViewModel) : RecyclerView.Adapter<SuggestFoodDetailsRecViewAdapter.ViewHolder>() {
    private var foods = mFoods
    private var mFoodTrackerViewModel : FoodTrackerViewModel = mViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.foodsuggest_list_item, parent, false)
        val mViewHolder = ViewHolder(view)
        mViewHolder.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mViewHolder.spinner.adapter = mViewHolder.adapter
        return mViewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.foodName.text = foods[position].foodName
        Picasso.get().load(foods[position].imageUrl).into(holder.foodImg)

        holder.logBtn.setOnClickListener{
            mFoodTrackerViewModel.logFood(holder.quantityText.text.toString(),holder.spinner.selectedItem.toString(), holder.foodName.text.toString())
        }
    }
    override fun getItemCount(): Int {
        return foods.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var spinner : Spinner = itemView.findViewById<Spinner>(R.id.unit_spinner)
        var logBtn : Button = itemView.findViewById(R.id.logBtn)
        var quantityText : EditText = itemView.findViewById(R.id.quantity)
        var adapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            itemView.context, R.array.unit_options, android.R.layout.simple_spinner_item)

        var foodName : TextView = itemView.findViewById(R.id.foodName)
        var foodImg : ImageView = itemView.findViewById(R.id.foodImg)
    }
}
