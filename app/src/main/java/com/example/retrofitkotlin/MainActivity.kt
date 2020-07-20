package com.example.retrofitkotlin

import android.app.Dialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.example.retrofitkotlin.PojoModel.Language
import com.example.retrofitkotlin.PojoModel.LanguageStore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_store.*
import kotlinx.android.synthetic.main.language_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.LinearLayout
import android.widget.EditText




class MainActivity : AppCompatActivity(), MyAdapter.Listener {
    private var myAdapter: MyAdapter? = null
    private var myCompositeDisposable: CompositeDisposable? = null
    private var myLanguageArrayList: ArrayList<Language>? = null
    private val BASE_URL = "http://10.0.2.2:8000/api/"
    lateinit var requestInterface: RetrofitApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myCompositeDisposable = CompositeDisposable()
        initRecyclerView()
        loadData()

        fab.setOnClickListener{
            Toast.makeText(this, "YES", Toast.LENGTH_LONG).show()
            newDialog()
        }

    }

    private fun newDialog(){
        var dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.dialog_store)

        dialog.push_language.setOnClickListener {
            if (!dialog.edit_language.text.toString().isEmpty()) {
                Toast.makeText(this, "!!!!!", Toast.LENGTH_LONG).show()
                StoreLanguage(this, dialog.edit_language.text.toString()).execute()
            }
        }

        dialog.show()
    }

    private fun initRecyclerView() {
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this@MainActivity)
        recycler_view.layoutManager = layoutManager
    }

    private fun loadData() {
        requestInterface = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(RetrofitApiInterface::class.java)


        myCompositeDisposable?.add(requestInterface.getData() //Add all RxJava disposables to a CompositeDisposable//
            .observeOn(AndroidSchedulers.mainThread()) //Send the Observable’s notifications to the main UI thread//
            .subscribeOn(Schedulers.io())              //Subscribe to the Observer away from the main UI thread//
            .subscribe(this::handleResponse))

    }


    private inner class StoreLanguage(private var context: MainActivity,private var newlanguage: String) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {

            val languageStore = LanguageStore(newlanguage)
            requestInterface!!.storelanguage(languageStore).enqueue(object : Callback<Language> {
                override fun onResponse(call: Call<Language>, response: Response<Language>) {
                    Log.i("", "post submitted to API." + response.body()!!)


                    if (response.isSuccessful()) {
                        val language = response.body()
                        Log.i("", "post registration to API" + response.body()!!.toString())
                        Log.i("", "name" + response.body()!!.name)
                        Log.i("", "created_at" + response.body()!!.created_at)

                        if (language != null) {
                            myLanguageArrayList?.add(language)
                        }
                        recycler_view.adapter?.notifyDataSetChanged()
                    }
                }
                override fun onFailure(call: Call<Language>, t: Throwable) {
                }
            })
            return true
        }

        override fun onPostExecute(bool: Boolean?) {
            if (bool!!) {
                Toast.makeText(context, "New Language stored succesfully", Toast.LENGTH_LONG).show()
            }
        }
    }

    private inner class DeleteLanguage(private var context: MainActivity,private var position: Int) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {

            requestInterface!!.deleteLanguage(position).enqueue(object : Callback<Language> {
                override fun onResponse(call: Call<Language>, response: Response<Language>) {
                    Log.i("", "post submitted to API." + response.body()!!)
                    if (response.isSuccessful()) {
                        Log.i("", "post registration to API" + response.body()!!.toString())
                        Log.i("", "name" + response.body()!!.name)
                        Log.i("", "created_at" + response.body()!!.created_at)
                    }

                    myLanguageArrayList?.removeAt(position)
                    recycler_view.adapter?.notifyItemRemoved(position)
                }
                override fun onFailure(call: Call<Language>, t: Throwable) {
                }
            })
            return true
        }

        override fun onPostExecute(bool: Boolean?) {
            if (bool!!) {
                Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show()
            }
        }
    }

    private inner class UpdateLanguage(private var context: MainActivity,private var position: Int,private var updatetext:String,private var pos_id:Int): AsyncTask<Void,Void,Boolean>(){
        override fun doInBackground(vararg params: Void?): Boolean {
            val languageStore = LanguageStore(updatetext)

            requestInterface!!.updateLanguage(pos_id,languageStore).enqueue(object : Callback<Language> {
                override fun onResponse(call: Call<Language>, response: Response<Language>) {
                    Log.i("", "post submitted to API." + response.body()!!)
                    myLanguageArrayList?.removeAt(position)
                    if (response.isSuccessful()) {

                        val language = response.body()
                        Log.i("", "post registration to API" + response.body()!!.toString())
                        Log.i("", "name" + response.body()!!.name)
                        Log.i("", "created_at" + response.body()!!.created_at)

                        if (language != null) {
                            myLanguageArrayList?.add(position,language)
                        }
                        recycler_view.adapter?.notifyDataSetChanged()
                    }
                }
                override fun onFailure(call: Call<Language>, t: Throwable) {
                }
            })
            return true
        }

        override fun onPostExecute(bool: Boolean?) {
            if (bool!!) {
                Toast.makeText(context, "Updated", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun handleResponse(languageList: List<Language>) {
        myLanguageArrayList = ArrayList(languageList)
        myAdapter = MyAdapter(myLanguageArrayList!!, this)
        recycler_view.adapter = myAdapter
    }

    override fun onItemClick(language: Language, position: Int) {
        var dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.language_detail)

        dialog.textView.text = language.id.toString()
        dialog.textView2.text = language.name
        dialog.textView3.text = language.created_at
        dialog.textView4.text = language.updated_at

        dialog.cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.update.setOnClickListener {

            val view = layoutInflater.inflate(R.layout.edit_text, null)
            val editText = view.findViewById(R.id.edit) as EditText
            val builder = AlertDialog.Builder(this)

            builder.setTitle(language.id.toString())
            builder.setMessage(language.name)
            builder.setView(view)

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                Toast.makeText(applicationContext, android.R.string.yes, Toast.LENGTH_SHORT).show()
                UpdateLanguage(this,position,editText.text.toString(),language.id).execute()
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
            }

            builder.show()
        }

        dialog.delete.setOnClickListener {
            DeleteLanguage(this, position).execute()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        myCompositeDisposable?.clear()
    }

}
