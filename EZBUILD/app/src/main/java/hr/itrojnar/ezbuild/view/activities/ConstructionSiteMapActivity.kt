package hr.itrojnar.ezbuild.view.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import hr.itrojnar.ezbuild.R
import hr.itrojnar.ezbuild.model.viewModels.ConstructionSite
import hr.itrojnar.ezbuild.utils.Constants


class ConstructionSiteMapActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mCurrentLocation: Location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var mConstructionSite: ConstructionSite? = null
    private var mConstructionSitesList: ArrayList<ConstructionSite>? = null

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_construction_site_map)

        val toolbar: Toolbar = findViewById(R.id.toolbar_cs_map)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(Constants.EXTRA_CS_DETAILS)) {
            mConstructionSite = intent.getParcelableExtra(Constants.EXTRA_CS_DETAILS)!!
            supportActionBar!!.title = getString(R.string.lbl_cs_location)
        }
        else if (intent.hasExtra(Constants.EXTRA_ALL_CS_DETAILS)) {
            mConstructionSitesList = intent.getParcelableArrayListExtra(Constants.EXTRA_ALL_CS_DETAILS)
            supportActionBar!!.title = getString(R.string.lbl_cs_locations)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mGoogleMap.isMyLocationEnabled = true
        }

        mGoogleMap.uiSettings.isZoomControlsEnabled = true

        if (mConstructionSite != null) {

            val position = LatLng(mConstructionSite?.latitude!!, mConstructionSite?.longitude!!)
            val csStatus = when (mConstructionSite?.isActive) {
                true -> getString(R.string.lbl_cs_details_status_active)
                else -> getString(R.string.lbl_cs_details_status_inactive)
            }
            mGoogleMap.addMarker(MarkerOptions().position(position).title(mConstructionSite?.fullAddress).snippet(getString(R.string.lbl_cs_manager) + " "
                    + mConstructionSite!!.constructionSiteManager?.fullName + "\n" + getString(R.string.lbl_cs_number_of_workers) + " " + mConstructionSite!!.employees.count() +
                    "\n" + getString(R.string.lbl_cs_details_cs_status) + " " + csStatus))
            mGoogleMap.setInfoWindowAdapter(object : InfoWindowAdapter {

                override fun getInfoWindow(marker: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View {
                    val info = LinearLayout(this@ConstructionSiteMapActivity)
                    info.orientation = LinearLayout.VERTICAL
                    info.background = ContextCompat.getDrawable(this@ConstructionSiteMapActivity, R.drawable.custom_background_rv_item)
                    info.setPadding(20)

                    val title = TextView(this@ConstructionSiteMapActivity)
                    title.setTextColor(Color.BLACK)
                    title.typeface = Typeface.createFromAsset(this@ConstructionSiteMapActivity.assets, "Montserrat-Bold.ttf")
                    title.textSize = 15f
                    title.text = marker.title

                    val snippet = TextView(this@ConstructionSiteMapActivity)
                    snippet.setTextColor(Color.BLACK)
                    snippet.typeface = Typeface.createFromAsset(this@ConstructionSiteMapActivity.assets, "Montserrat-Regular.ttf")
                    snippet.text = marker.snippet
                    snippet.textSize = 14f

                    info.addView(title)
                    info.addView(snippet)

                    return info
                }
            })
            val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 16f)
            mGoogleMap.animateCamera(newLatLngZoom)
        }
        else if (mConstructionSitesList != null) {

            if (::mCurrentLocation.isInitialized) {
                val latLng = LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            } else {
                val latLng = LatLng(45.81, 15.98)
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
            }

            mConstructionSitesList?.forEach {
                val position = LatLng(it.latitude, it.longitude)
                val csStatus = when (it.isActive) {
                    true -> getString(R.string.lbl_cs_details_status_active)
                    else -> getString(R.string.lbl_cs_details_status_inactive)
                }
                mGoogleMap.addMarker(MarkerOptions().position(position).title(it.fullAddress).icon(BitmapFromVector(applicationContext, R.drawable.ic_location_cs)).snippet(getString(R.string.lbl_cs_manager) + " "
                        + it.constructionSiteManager?.fullName + "\n" + getString(R.string.lbl_cs_number_of_workers) + " " + it.employees.count() +
                        "\n" + getString(R.string.lbl_cs_details_cs_status) + " " + csStatus))
                mGoogleMap.setInfoWindowAdapter(object : InfoWindowAdapter {

                    override fun getInfoWindow(marker: Marker): View? {
                        return null
                    }

                    override fun getInfoContents(marker: Marker): View? {
                        val info = LinearLayout(this@ConstructionSiteMapActivity)
                        info.orientation = LinearLayout.VERTICAL
                        info.background = ContextCompat.getDrawable(this@ConstructionSiteMapActivity, R.drawable.custom_background_rv_item)
                        info.setPadding(20)

                        val title = TextView(this@ConstructionSiteMapActivity)
                        title.setTextColor(Color.BLACK)
                        title.typeface = Typeface.createFromAsset(this@ConstructionSiteMapActivity.assets, "Montserrat-Bold.ttf")
                        title.textSize = 15f
                        title.text = marker.title

                        val snippet = TextView(this@ConstructionSiteMapActivity)
                        snippet.setTextColor(Color.BLACK)
                        snippet.typeface = Typeface.createFromAsset(this@ConstructionSiteMapActivity.assets, "Montserrat-Regular.ttf")
                        snippet.text = marker.snippet
                        snippet.textSize = 14f

                        info.addView(title)
                        info.addView(snippet)

                        return info
                    }
                })
            }
        }
    }

    private fun fetchLocation() {

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }

        val supportMapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.construction_site_map) as SupportMapFragment

        val task = mFusedLocationClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                mCurrentLocation = location
                supportMapFragment.getMapAsync(this)
            }
        }
        task.addOnFailureListener {
            supportMapFragment.getMapAsync(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }

    override fun onMarkerClick(marker: Marker) = false

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}