package com.group11.healthtrackerapp.FoodTracker

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group11.healthtrackerapp.FoodTracker.Schema.FoodOption
import com.group11.healthtrackerapp.FoodTracker.adapters.SuggestFoodDetailsRecViewAdapter
import com.group11.healthtrackerapp.FoodTracker.viewmodels.FoodTrackerViewModel
import com.group11.healthtrackerapp.R
import java.time.LocalDate
import kotlin.collections.ArrayList

class FoodLoggingActivity : AppCompatActivity() {
    private var mRecView : RecyclerView? = null
    private var mEmptyView : RelativeLayout? = null
    private var mRecViewAdapter: SuggestFoodDetailsRecViewAdapter? = null
    private var mAutoCompleteTextView : AutoCompleteTextView? = null
    private var mAutoCompleteArrayAdapter : ArrayAdapter<String>? = null
    private var mBackButton : ImageButton? = null
    private var mModelView : FoodTrackerViewModel? = null
    private var mList : MutableList<String>? = null
    private var date : LocalDate? = null //get from calling activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        mModelView = ViewModelProvider(this)[FoodTrackerViewModel::class.java]
        mEmptyView = findViewById<RelativeLayout>(R.id.emptyLayout)
        setContentView(R.layout.activity_food_logging)
        date = LocalDate.parse(intent.getStringExtra("date"))
        mModelView!!.mMonth = date!!.monthValue
        mModelView!!.mDay = date!!.dayOfMonth
        mModelView!!.mYear = date!!.year
        mBackButton = findViewById(R.id.back_btn)
        mBackButton?.setOnClickListener{
            val intent = Intent(this, DailyFoodPage::class.java)
            intent.putExtra("date", date.toString())
            startActivity(intent)
        }
        initRecyclerView()
        val foodOptionObserver = Observer<ArrayList<FoodOption>> {
            mRecViewAdapter?.notifyDataSetChanged()
            if(mRecViewAdapter?.itemCount!! > 0) {
                mRecView?.visibility = View.VISIBLE
                findViewById<TextView>(R.id.emptyTextView).visibility = View.INVISIBLE
                findViewById<ImageView>(R.id.addImg).visibility = View.INVISIBLE
            }
        }
        mModelView!!.getFoodOptions().observe(this, foodOptionObserver )
        initAutoCompleteTextView()
        val autoCompleteOptionObserver = Observer<MutableList<String>> {
            mAutoCompleteArrayAdapter?.clear()
            mAutoCompleteArrayAdapter?.addAll(mModelView?.getAutoSuggestOptions()?.value!!)
            mAutoCompleteArrayAdapter?.notifyDataSetChanged()
            mAutoCompleteArrayAdapter?.filter?.filter(mAutoCompleteTextView!!.text.toString())
        }
        mModelView!!.getAutoSuggestOptions().observe(this, autoCompleteOptionObserver)
        val foodChangedObserver = Observer<Boolean> {
                if(mModelView!!.mFoodAddedName == "" ) return@Observer
                val builder = AlertDialog.Builder(this)
                builder.setMessage("${mModelView!!.mFoodAddedName} has been added to log!");
                builder.setTitle("Alert!")
                builder.setCancelable(false)
                builder.setNegativeButton("Close",
                    DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.cancel() } as DialogInterface.OnClickListener)
                val alertDialog  = builder.create()
                alertDialog.show()
        }
        mModelView!!.getFoodAdded().observe(this, foodChangedObserver)
    }
    private fun initRecyclerView() {
        val newFoods = ArrayList<FoodOption>()
        mRecView = findViewById<RecyclerView>(R.id.FoodDetailsRecView)
        val foodList = mModelView?.getFoodOptions()?.value
        mRecViewAdapter = SuggestFoodDetailsRecViewAdapter(this, foodList!!, mModelView!!)
        mRecView!!.adapter = mRecViewAdapter
        mRecView!!.layoutManager = LinearLayoutManager(this)
    }

    private fun initAutoCompleteTextView() {
        mList = (mModelView!!.getAutoSuggestOptions().value)!!
        mList!!.add("pizza")
        mAutoCompleteTextView  = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        mAutoCompleteArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList!!)
        mAutoCompleteTextView!!.setAdapter(mAutoCompleteArrayAdapter)
        mAutoCompleteTextView!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            @SuppressLint("SuspiciousIndentation")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val curFoodInput = mAutoCompleteTextView!!.text.toString()
                    mModelView?.updateAutoSuggestOptionList(curFoodInput)
            }
        })
        mAutoCompleteTextView!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event -> false }) //on enter pressed
        mAutoCompleteTextView!!.setOnItemClickListener(OnItemClickListener { arg0, arg1, arg2, arg3 -> //option selected
            val view: View? = currentFocus
            if (view != null) {
                val inputManager: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                mModelView?.updateFoodOptionList(mAutoCompleteTextView!!.text.toString())
            }
        })
    }
}

