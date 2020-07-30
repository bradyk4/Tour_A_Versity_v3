package com.example.tour_a_versity_v3

import android.content.ContentValues
import android.content.Intent
import android.os.Build.ID
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tour_a_versity_v3.dto.Buildings
import com.example.tour_a_versity_v3.ui.InfoActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var firestore: FirebaseFirestore

    private lateinit var map: GoogleMap
    private val zoomLevel = 17f
    private val baseLat = 39.13175
    private val baseLng = -84.51774
    private val baseLatLng = LatLng(baseLat, baseLng)
    lateinit var bID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.setOnInfoWindowClickListener(this)
        //move camera to TUC
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(baseLatLng, zoomLevel))
        //initialize firestore
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
                    if (building != null && building.latitude.isNotEmpty() && building.longitude.isNotEmpty()) {
                        val marker = LatLng(building.latitude.toDouble(), building.longitude.toDouble())
                        map.addMarker(MarkerOptions().position(marker).title(building.buildingName).snippet(building.buildingID.toString()))

                    }
                }
            }
        }

    }

    override fun onInfoWindowClick(p0: Marker?) {
        if (p0 != null) {
            // set ID of building to a value
            bID = p0.snippet
        }
        val intent = Intent(this, InfoActivity::class.java)
        // pass the ID value to the InfoActivity
        intent.putExtra("ID", bID)
        startActivity(intent)
    }

}