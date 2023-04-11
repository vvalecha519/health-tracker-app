package com.group11.healthtrackerapp.FoodTracker.viewmodels;

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.group11.healthtrackerapp.FoodTracker.Schema.FoodOption
import com.group11.healthtrackerapp.FoodTracker.repository.NutritionRepository


public class FoodTrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val mApplication = application
    private var mFoodOptions : MutableLiveData<ArrayList<FoodOption>> = MutableLiveData(ArrayList<FoodOption>())
    private var mFoodAdded : MutableLiveData<Boolean> = MutableLiveData(false)
    private var mAutoSuggestOptions : MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf<String>())
    private var mFoodRepo : NutritionRepository = NutritionRepository()
    public var mFoodAddedName : String = ""
    public var mMonth: Int? = null;
    public var mDay: Int? = null;
    public var mYear: Int? = null;


    public fun getFoodOptions() : LiveData<ArrayList<FoodOption>> {
      return mFoodOptions
    }
    public fun getAutoSuggestOptions() : LiveData<MutableList<String>> {
        return mAutoSuggestOptions
    }

    public fun getFoodAdded() : LiveData<Boolean> {
        return mFoodAdded
    }

    public fun updateFoodOptionList(foodName : String) {
        val modifyFoodOptions : (ArrayList<FoodOption>) -> Unit = {newFoodOptions : ArrayList<FoodOption> ->
            mFoodOptions.value?.clear()
            val currentFoods = mFoodOptions.value
            currentFoods?.addAll(newFoodOptions)
            mFoodOptions.postValue(currentFoods)
        }
        mFoodRepo.getFoodOptions(foodName, mApplication.applicationContext, modifyFoodOptions)
    }

    public fun updateAutoSuggestOptionList(foodName : String) {
        if(foodName.length < 3) return
        val modifyAutoSuggestOptions : (MutableList<String>) -> Unit = {newFoodOptions : MutableList<String> ->
            mAutoSuggestOptions.value?.clear()
            mAutoSuggestOptions.value?.addAll(newFoodOptions)
            mAutoSuggestOptions.postValue( mAutoSuggestOptions.value)
        }
        mFoodRepo.getAutoSuggestOptions(foodName, mApplication.applicationContext, modifyAutoSuggestOptions)
    }

    public fun logFood(quantity: String, units: String, foodName: String) {
        val changeFoodAdded : (String) -> Unit = {response : String ->
            mFoodAddedName = foodName
            mFoodAdded.postValue(true)
            addFoodDatabase(response)
        }
        mFoodRepo.getFoodNutrition("$quantity$units $foodName", mApplication.applicationContext, changeFoodAdded)
    }

    public fun addFoodDatabase(response: String) {
        if(response != null || response != "") {
            mFoodRepo.addFoodDatabase(response, mDay!!, mMonth!!, mYear!!, mApplication.applicationContext)
        }
    }

}
