package com.example.tour_a_versity_v3.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tour_a_versity_v3.R
import com.example.tour_a_versity_v3.dto.Buildings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity2.*

class InfoActivity : AppCompatActivity(){

    private lateinit var firestore: FirebaseFirestore
    private lateinit var name: String
    private lateinit var info: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get ID of which building is clicked
        val bID = intent.getStringExtra("ID")

        // initialize firestore
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        firestore.collection("buildings").addSnapshotListener{
                snapshot, e ->
            // if there is an exception we want to play
            if (e != null){
                Log.w(ContentValues.TAG, "Listen Failed", e)
                return@addSnapshotListener
            }
            // if we are here, we did not encounter an exception
            if (snapshot != null){
                // now, we have a populated snapshot
                val documents = snapshot.documents
                documents.forEach {

                    val building = it.toObject(Buildings::class.java)
                    if (building != null && building.buildingID == bID?.toInt()) {
                        txtTitle.text = building.buildingName
                        txtInfo.text = building.info
                    }
                }
            }
        }


        setContentView(R.layout.activity2)

        val button = findViewById<Button>(R.id.TucPhotoButton)
        button.setOnClickListener {
            val intent = Intent(this, PhotosActivity::class.java)
            startActivity(intent)
        }
    }
}