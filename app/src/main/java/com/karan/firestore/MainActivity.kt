package com.karan.firestore

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.karan.firestore.databinding.ActivityMainBinding
import com.karan.firestore.databinding.CustomDialogboxBinding

class MainActivity : AppCompatActivity(), Recycler_btn {
    lateinit var binding: ActivityMainBinding
    var array = ArrayList<Items>()
    var recyclerAdapter = recyclerAdapter(array, this)
    var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerList.layoutManager = linearLayoutManager
        binding.recyclerList.adapter = recyclerAdapter
        binding.recyclerList.setHasFixedSize(true)

        binding.btnFab.setOnClickListener {
            var dialogboxBinding = CustomDialogboxBinding.inflate(layoutInflater)
            Dialog(this).apply {
                setContentView(dialogboxBinding.root)
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialogboxBinding.btnSave.setOnClickListener {
                    val data = Items(
                        "",
                        dialogboxBinding.etEmail.text.toString(),
                        dialogboxBinding.etClass.text.toString(),
                        dialogboxBinding.etNumber.text.toString().toInt()
                    )

                    db.collection("firestoreCollection").add(data).addOnCompleteListener {
                        if (it.isSuccessful) {
                            println("Data Saved: ${it.result}")

                        }
                        recyclerAdapter.notifyDataSetChanged()

                    }
                    dismiss()
                }

                show()
            }

        }
        db.collection("firestoreCollection").addSnapshotListener { snapshots, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            for (snapshot in snapshots!!.documentChanges) {
                val userModel = convertObject(snapshot.document)
                when (snapshot.type) {
                    DocumentChange.Type.ADDED -> {
                        userModel?.let { array.add(it) }
                        Log.e("", "userModelList ${array.size}")
                        Log.e("", "userModelList ${array}")

                    }

                    DocumentChange.Type.MODIFIED -> {
                        userModel?.let {
                            val index = getIndex(userModel)
                            if (index > -1)
                                array.set(index, it)
                        }
                    }

                    DocumentChange.Type.REMOVED -> {
                        userModel?.let {
                            val index = getIndex(userModel)
                            if (index > -1)
                                array.removeAt(index)
                        }
                    }
                }
            }
        }


    }

    private fun convertObject(snapshot: QueryDocumentSnapshot): Items? {
        val ItemModel: Items? = snapshot.toObject(Items::class.java)
        ItemModel?.id = snapshot.id ?: ""
        return ItemModel

    }

    fun getIndex(ItemModelL: Items): Int {
        var index = -1
        index = array.indexOfFirst { element ->
            element.id?.equals(ItemModelL.id) == true
        }
        return index
    }

    override fun update_data(tems: Items, position: Int) {

    }

    override fun delete_data(items: Items, position: Int) {
        AlertDialog.Builder(this).apply {
            setTitle("Are you sure")
            setPositiveButton("Yes")
            { _, _ ->
                db.collection("firestoreCollection")
                    .document(array[position].id ?: "")
                    .delete()
            }
            setNegativeButton("NO")
            { _, _ ->

            }
            show()
        }
    }
}




