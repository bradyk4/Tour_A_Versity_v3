package com.example.tour_a_versity_v3.ui

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.tour_a_versity_v3.R
import com.example.tour_a_versity_v3.dto.Buildings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity3.*

class PhotosActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE: Int = 1998
    private val CAMERA_PERMISSION_REQUEST_CODE = 1997
    private lateinit var firestore: FirebaseFirestore


    @RequiresApi(Build.VERSION_CODES.M)
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
                        val uriString: String = building.remoteURI
                        val uri = uriString.toUri()
                        imageView.setImageURI(null)
                        Glide
                            .with(this)
                            .load(uri) // the uri from Firebase
                            .into(imageView) // imageView variable
                    }
                }
            }
        }
        setContentView(R.layout.activity3)

        button.setOnClickListener {
            prepTakePhoto()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun prepTakePhoto() {
        if (ContextCompat.checkSelfPermission(
                applicationContext!!,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            takePhoto()
        } else {
            val permissionRequest = arrayOf(android.Manifest.permission.CAMERA)
            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto()
                } else {
                    Toast.makeText(applicationContext, "Unable to use camera", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    //checks for camera permissions
    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(applicationContext!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                val imageBitMap = data!!.extras!!.get("data") as Bitmap
                imgCamera.setImageBitmap(imageBitMap)
            }
        }
    }



}