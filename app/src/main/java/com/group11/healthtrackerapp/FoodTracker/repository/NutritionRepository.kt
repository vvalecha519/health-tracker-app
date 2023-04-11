package com.group11.healthtrackerapp.FoodTracker.repository;

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.group11.healthtrackerapp.AppDatabase
import com.group11.healthtrackerapp.FoodTracker.Schema.FoodOption
import com.group11.healthtrackerapp.R
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import kotlin.math.min


public class NutritionRepository {
    private val url = "https://trackapi.nutritionix.com/v2"
    private var  mRequestQueue : RequestQueue? = null

    fun getFoodOptions(foodName: String, context : Context, callback : (ArrayList<FoodOption>) ->  Unit) {
        val suffixUrl = "/search/instant?query=$foodName"
        mRequestQueue = Volley.newRequestQueue(context)
        val getRequest: StringRequest = object : StringRequest(Method.GET, url + suffixUrl,
            Response.Listener { response ->
                val commonFoodArray = JSONObject(response.toString()).getJSONArray("common")
                val foodOptions = ArrayList<FoodOption>()
                for (i in 1..min(commonFoodArray.length(), 10)) {
                    val foodItem = commonFoodArray[i-1].toString()
                    val jsonFoodItem = JSONObject(foodItem)
                    val foodName = jsonFoodItem.getString("food_name")
                    val image = jsonFoodItem.getJSONObject("photo").getString("thumb")
                    foodOptions.add(FoodOption(foodName, image))
                }
                callback(foodOptions)
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "error => $error")
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["x-app-id"] = context.resources.getString(R.string.api_id)
                params["x-app-key"] = context.resources.getString(R.string.api_key)
                return params
            }
        }
        mRequestQueue!!.add(getRequest)
    }

    fun getAutoSuggestOptions(foodName: String, context : Context, callback : (MutableList<String>) ->  Unit) {
        val suffixUrl = "/search/instant?query=$foodName"
        mRequestQueue = Volley.newRequestQueue(context)
        val getRequest: StringRequest = object : StringRequest(Method.GET, url + suffixUrl,
            Response.Listener { response ->
                val commonFoodArray = JSONObject(response.toString()).getJSONArray("common")
                val foodOptions = mutableListOf<String>()
                for (i in 1..min(commonFoodArray.length(), 5)) {
                    val foodItem = commonFoodArray[i-1].toString()
                    val jsonFoodItem = JSONObject(foodItem)
                    foodOptions.add(jsonFoodItem.getString("food_name"))
                }
                callback(foodOptions)
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "error => $error")
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["x-app-id"] = context.resources.getString(R.string.api_id)
                params["x-app-key"] = context.resources.getString(R.string.api_key)
                return params
            }
        }
        mRequestQueue!!.add(getRequest)
    }

    fun getFoodNutrition(queryString: String, context: Context, callback : (String) ->  Unit) {
        val suffixUrl = "/natural/nutrients"
        var jsonBody : JSONObject = JSONObject()
        jsonBody.put("query", queryString)
        val mRequestBody = jsonBody.toString()
        Log.i("debug", mRequestBody)
        mRequestQueue = Volley.newRequestQueue(context)
        val postRequest: StringRequest = object : StringRequest(Method.POST, url + suffixUrl,
            Response.Listener { response ->
            Log.d("debug", response.toString())
                callback(response.toString())
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "error => $error")
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["x-app-id"] = context.resources.getString(R.string.api_id)
                params["x-app-key"] = context.resources.getString(R.string.api_key)
                params["Content-Type"] = "application/json"
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray? {
                return try {
                    if(mRequestBody == null) {return null}
                    else return mRequestBody.encodeToByteArray()
                } catch (uee: UnsupportedEncodingException) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8")
                    null
                }
            }

        }
        mRequestQueue!!.add(postRequest)
    }

    private fun checkValidValue(s: String) : Double {
        return if(s != "null") s.toDouble() else 0.0
    }

    fun addFoodDatabase(response : String, day : Int, month: Int, year: Int, context: Context) {

        Log.d("logging food", "Here")
        //parse string, insert to database
        var foodNutritionJson = JSONObject(response)
        foodNutritionJson = JSONObject(foodNutritionJson.getJSONArray("foods")[0].toString())

        println("asdakdjasdja" + foodNutritionJson.getString("nf_sugars"))
        val newFoodEntry = FoodNutrition(0,
            if(foodNutritionJson.getString("food_name") != "null") foodNutritionJson.getString("food_name") else "",
            year, month, day,
            checkValidValue(foodNutritionJson.getString("serving_qty")),
            if(foodNutritionJson.getString("serving_unit") == "null") foodNutritionJson.getString("serving_unit") else "" ,
                checkValidValue(foodNutritionJson.getString("nf_calories")),
                checkValidValue(foodNutritionJson.getString("nf_total_fat")),
                checkValidValue(foodNutritionJson.getString("nf_saturated_fat")),
                checkValidValue(foodNutritionJson.getString("nf_cholesterol")),
                checkValidValue(foodNutritionJson.getString("nf_sodium")),
                checkValidValue(foodNutritionJson.getString("nf_total_carbohydrate")),
                checkValidValue(foodNutritionJson.getString("nf_dietary_fiber")),
                checkValidValue(foodNutritionJson.getString("nf_sugars")),
                checkValidValue(foodNutritionJson.getString("nf_protein")),
                checkValidValue(foodNutritionJson.getString("nf_potassium")),
        )
        val foodNutritionDao = AppDatabase.getDatabase(context).FoodNutritionDao()
        foodNutritionDao.insert(newFoodEntry)
        Log.v("debug", foodNutritionDao.getAll().toString())
    }
}
