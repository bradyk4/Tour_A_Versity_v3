package com.example.tour_a_versity_v3

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tour_a_versity_v3.dto.Buildings
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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var firestore: FirebaseFirestore

    private lateinit var map: GoogleMap
    private lateinit var tucMarker: Marker
    private lateinit var cechMarker: Marker
    private lateinit var pavilionMarker: Marker
    private lateinit var lawMarker: Marker
    private lateinit var belgenMarker: Marker
    private lateinit var mcmickenMarker: Marker
    private lateinit var braunsteinMarker: Marker
    private lateinit var daapMarker: Marker
    private val zoomLevel = 17f
    private val baseLat = 39.13175
    private val baseLng = -84.51774
    private val baseLatLng = LatLng(baseLat, baseLng)

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
                        // building.buildingID = it.id
                        val marker = LatLng(building.latitude.toDouble(), building.longitude.toDouble())
                        map.addMarker(MarkerOptions().position(marker).title(building.buildingName).snippet(building.info))

                    }
                }
            }
        }

        /*
        // marker for TUC
        val tucLat = 39.13175
        val tucLng = -84.51774
        val zoomLevel = 17f
        //create LatLng object for TUC
        val tucLatLng = LatLng(tucLat, tucLng)
        //move camera to TUC & add marker
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(tucLatLng, zoomLevel))
        tucMarker = map.addMarker(MarkerOptions()
                .position(tucLatLng)
                .title("TUC")
                .snippet("Tangeman University Center")
        )

        // marker for CECH
        val cechLat = 39.130315
        val cechLng = -84.518680
        val cechLatLng = LatLng(cechLat, cechLng)
        cechMarker = map.addMarker(MarkerOptions()
                .position(cechLatLng)
                .title("CECH")
                .snippet("College of Education, Criminal Justice, Human Services, and IT")
        )
        // marker for UC pavilion
        val pavilionLat = 39.131050
        val pavilionLng = -84.518669
        val pavilionLatLng = LatLng(pavilionLat, pavilionLng)
        pavilionMarker = map.addMarker(MarkerOptions()
            .position(pavilionLatLng)
            .title("Uc Pavilion")
            .snippet("University of Cincinnati Pavilion")
        )

        // marker for UC college of Law
        val lawLat = 39.129200
        val lawLng = -84.520150
        val lawLatLng = LatLng(lawLat, lawLng)
        lawMarker = map.addMarker(MarkerOptions()
            .position(lawLatLng)
            .title("College of Law")
            .snippet("University of Cincinnati College of Law")
        )

        // marker for Belgen library
        val belgenLat = 39.129420
        val belgenLng = -84.519322
        val belgenLatLng = LatLng(belgenLat, belgenLng)
        belgenMarker = map.addMarker(MarkerOptions()
            .position(belgenLatLng)
            .title("Belgen Library")
            .snippet("University of Cincinnati College of Law")
        )

        // marker for McMicken
        val mcmickenLat = 39.131939
        val mcmickenLng = -84.519152
        val mcmickenLatLng = LatLng(mcmickenLat, mcmickenLng)
        mcmickenMarker = map.addMarker(MarkerOptions()
            .position(mcmickenLatLng)
            .title("McMicken Hall")
            .snippet("University of Cincinnati McMicken Hall")
        )

        // marker for Braunstein Hall
        val braunsteinLat = 39.132900
        val braunsteinLng = -84.518562
        val braunsteinLatLng = LatLng(braunsteinLat, braunsteinLng)
        braunsteinMarker = map.addMarker(MarkerOptions()
            .position(braunsteinLatLng)
            .title("Braunstein Hall")
            .snippet("University of Cincinnati Braunstein Hall")
        )

        // marker for DAAP Hall
        val daapLat = 39.134422
        val daapLng = -84.518636
        val daapLatLng = LatLng(daapLat, daapLng)
        daapMarker = map.addMarker(MarkerOptions()
            .position(daapLatLng)
            .title("DAAP")
            .snippet("College of Design, Art, Architecture, and Planning")
        )

         */

    }

    override fun onInfoWindowClick(p0: Marker?) {
        if (p0 == tucMarker){
            val intent = Intent(this, TUC_Info::class.java)
            startActivity(intent)
        }
        else if (p0 == cechMarker){
            val intent = Intent(this, CECH_Info::class.java)
            startActivity(intent)
        }
        else if (p0 == pavilionMarker){
            val intent = Intent(this, CECH_Info::class.java)
            startActivity(intent)
        }
        else if (p0 == lawMarker){
            val intent = Intent(this, CECH_Info::class.java)
            startActivity(intent)
        }
        else if (p0 == belgenMarker){
            val intent = Intent(this, CECH_Info::class.java)
            startActivity(intent)
        }
        else if (p0 == mcmickenMarker){
            val intent = Intent(this, CECH_Info::class.java)
            startActivity(intent)
        }
        else if (p0 == braunsteinMarker){
            val intent = Intent(this, CECH_Info::class.java)
            startActivity(intent)
        }
        else if (p0 == daapMarker){
            val intent = Intent(this, CECH_Info::class.java)
            startActivity(intent)
        }
    }

}