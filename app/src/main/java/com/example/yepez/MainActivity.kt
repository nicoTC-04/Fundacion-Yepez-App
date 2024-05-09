package com.example.yepez

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.VectorDrawable
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.yepez.databinding.ActivityMainBinding
import android.view.View
import androidx.cardview.widget.CardView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import kotlinx.coroutines.withContext


class Store(
    val id: Int,
    val name: String,
    val cityName: String,
    val coordinates: LatLng,
    val imageName: String,
    val pH: Double
)


// Lista de tiendas
val stores = listOf(
    Store(
        1,
        "Río Pánuco",
        "Norte de Veracruz",
        LatLng(22.0667, -98.1833),
        "panuco",
        8.0
    ),
    Store(
        2,
        "Río Tuxpan",
        "Norte de Veracruz",
        LatLng(20.9500, -97.4078),
        "tuxpan",
        6.0
    ),
    Store(
        3,
        "Río Cazones",
        "Norte de Veracruz",
        LatLng(20.7000, -97.5000),
        "cazones",
        7.0
    ),
    Store(
        4,
        "Río Tecolutla",
        "Norte de Veracruz",
        LatLng(20.4833, -97.0167),
        "tecolutla",
        9.0
    ),
    Store(
        5,
        "Río Papaloapan",
        "Sur de Veracruz",
        LatLng(18.0833, -95.9000),
        "papaloapan",
        6.0
    ),
    Store(
        6,
        "Río Coatzacoalcos",
        "Sur de Veracruz",
        LatLng(18.1500, -94.4167),
        "coatzacoalcos",
        7.0
    ),
    Store(
        7,
        "Laguna de Tamiahua",
        "Norte de Veracruz",
        LatLng(21.2667, -97.4500),
        "tamiahua",
        8.5
    ),
    Store(
        8,
        "Laguna Catemaco",
        "Sur de Veracruz",
        LatLng(18.4167, -95.1167),
        "catemaco",
        7.5
    ),
    Store(
        9,
        "Laguna Mandinga",
        "Centro de Veracruz",
        LatLng(19.1000, -96.0333),
        "mandinga",
        6.5
    ),
    Store(
        10,
        "Presa Tuxpango",
        "Sur de Veracruz",
        LatLng(18.7000, -97.0000),
        "tuxpango",
        7.5
    ),
    Store(
        11,
        "Presa Paso de Piedras (Chicayán)",
        "Sur de Veracruz",
        LatLng(18.2333, -95.8500),
        "pasodepiedras",
        8.0
    ),
    Store(
        12,
        "Laguna Pueblo Viejo",
        "Norte de Veracruz",
        LatLng(21.9167, -97.8000),
        "puebloviejo",
        9.0
    ),
    Store(
        13,
        "Laguna Sontecomapan",
        "Sur de Veracruz",
        LatLng(18.5833, -95.2000),
        "sontecomapan",
        7.0
    ),
    Store(
        14,
        "Laguna de Alvarado",
        "Centro de Veracruz",
        LatLng(18.7667, -95.7667),
        "alvarado",
        6.5
    ),
    Store(
        15,
        "Laguna La Mancha",
        "Centro de Veracruz",
        LatLng(19.5667, -96.3833),
        "lamancha",
        7.0
    )
)


class MainActivity : AppCompatActivity(), OnMapReadyCallback,LoginListener {

    private var mGoogleMap: GoogleMap? = null
    private var selectedLocationMap: GoogleMap? = null

    private lateinit var lastLocation: Location
    private lateinit var fusedfLocationClient: FusedLocationProviderClient


    private lateinit var imageView: ImageView



    companion object {
        //Obtener ubicacion
        private const val LOCATION_REQUEST_CODE = 1
    }

    private lateinit var tiendaTextView: TextView
    private lateinit var ubicacionTextView: TextView
    private lateinit var direccion: TextView
    private lateinit var imagenSeleccionada:ImageView
    private lateinit var tiendaSeleccionada:TextView
    private lateinit var correoUsuario:TextView

    private lateinit var firstName:String
    private lateinit var lastName:String
    private lateinit var email:String
    var points: Int=0
    var userid:Int=0

    var storeid:Int =0


    private lateinit var binding:ActivityMainBinding;


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Login())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.map ->replaceFragment(Map())
                R.id.leaderboard -> replaceFragment(Leaderboard())
                R.id.settings -> replaceFragment(Settings())
                else -> {

                }
            }
            true
        }

        //texto card principal
        tiendaTextView = findViewById(R.id.tienda)
        ubicacionTextView = findViewById(R.id.ubicacion)
        direccion = findViewById(R.id.adress)
        correoUsuario=findViewById(R.id.leaderboardCorreo)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)



        //obtener ubicacion por parte del usuario
        fusedfLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val logOutButton:Button=findViewById(R.id.logOut)

        logOutButton.setOnClickListener{
            logOut()
            replaceFragment(Login())
        }

        val loginFragment = Login()
        // Asigna esta actividad como el oyente del evento de inicio de sesión
        loginFragment.setLoginListener(this)

        // Agrega el fragmento de Login al contenedor
        replaceFragment(loginFragment)
    }

    //LogOut
    private fun logOut(){
        val mensajeDespedida = "¡Hasta luego, $firstName!"
        Toast.makeText(this, mensajeDespedida, Toast.LENGTH_SHORT).show()

        firstName = ""
        lastName = ""
        email = ""
        points = 0
        userid = 0
    }


    override fun onLoginButtonClicked(correo: String) {
        validarCorreo(correo)
        
    }



 // Acceso a la app, ademas de recibir los datos necesarios por usuario
 private fun validarCorreo(correo: String) {
     if (correo == "nico") {
         // Crear e inicializar el fragmento de Map
         val mapFragment = Map()
         replaceFragment(mapFragment)
     } else {
         // Mostrar mensaje de correo incorrecto
         Toast.makeText(this, "Correo incorrecto", Toast.LENGTH_SHORT).show()
     }
 }

private fun updatePHTextView(pH: Double) {
    val phTextView: TextView = findViewById(R.id.textpH)
    val anomalyResource = getAnomalyStringResource(pH.toString())
    phTextView.setText(anomalyResource)
}

    private fun updateStoreTexts(name: String, cityName: String, imageName: String, storeId: Int) {
        val tiendaTextView: TextView = findViewById(R.id.tienda)
        val ubicacionTextView: TextView = findViewById(R.id.ubicacion)

        val imagenSeleccionada:ImageView=findViewById(R.id.imagenTienda)
        val imageResourceId = getImageResourceId(imageName)

        if(storeId==1 || storeId==2 || storeId==3 || storeId==4 || storeId==5 || storeId==6){
            imagenSeleccionada.setImageResource(R.drawable.rio)
        } else if(storeId==7 || storeId==8 || storeId==9 || storeId==12 || storeId==13 || storeId==14 || storeId==15){
            imagenSeleccionada.setImageResource(R.drawable.laguna)
        } else if(storeId==10 || storeId==11){
            imagenSeleccionada.setImageResource(R.drawable.tuxpango)
        } else{
            imagenSeleccionada.setImageResource(imageResourceId)
        }

        tiendaTextView.setText(name)
        ubicacionTextView.setText(cityName)
    }


    //Lugar de inicio de los Mapas
    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap
        mGoogleMap!!.uiSettings.isZoomControlsEnabled = true

        // Agregar marcadores para cada tienda
        stores.forEach { store ->
            val marker = mGoogleMap?.addMarker(
                MarkerOptions().position(store.coordinates)
                    .title(store.name)
            )

            marker?.tag = store.id
            mGoogleMap?.setOnMarkerClickListener { clickedMarker ->
                storeid  = clickedMarker.tag as Int
                val clickedStore = stores.find { it.id == storeid  }

                if (clickedStore != null) {
                    selectedLocationMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedStore.coordinates, 13f))

                    updateStoreTexts(clickedStore.name, clickedStore.cityName, clickedStore.imageName, clickedStore.id)

                    updatePHTextView(clickedStore.pH)
                }

                false
            }
        }

        setUpMap()
    }

    //Creacion de los Mapas
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mGoogleMap?.isMyLocationEnabled = true
        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = true
        mGoogleMap?.uiSettings?.isRotateGesturesEnabled = true
        mGoogleMap?.uiSettings?.isScrollGesturesEnabled = true

        fusedfLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))


                // Calcula la distancia a cada tienda y encuentra la más cercana
                val nearestStore = stores.minByOrNull { store ->
                    val storeLocation = Location("").apply {
                        latitude = store.coordinates.latitude
                        longitude = store.coordinates.longitude
                    }
                    location.distanceTo(storeLocation)
                }

                nearestStore?.let { store ->
                    // Navega hacia la tienda más cercana en el mapa
                    val nearestStoreLatLng = LatLng(store.coordinates.latitude, store.coordinates.longitude)
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(nearestStoreLatLng, 12f))

                    // Actualiza los TextViews
                    tiendaTextView.text = store.name
                    ubicacionTextView.text = store.cityName
                    direccion.text = "${store.name}, ${store.cityName}"
                    storeid=store.id
                }

                // Habilita los controles de zoom una vez que se ha obtenido la ubicación
                mGoogleMap?.uiSettings?.isZoomControlsEnabled = true
            }
        }
    }


    //Cambio de imagen de acuerdo a ubicacion
    private fun getImageResourceId(imageName: String?): Int {
        val lowerCaseName = imageName?.lowercase()

        return when {
            lowerCaseName?.contains("rio") == true -> R.drawable.rio
            lowerCaseName?.contains("laguna") == true -> R.drawable.laguna
            lowerCaseName?.contains("presa") == true -> R.drawable.tuxpango
            else -> R.drawable.laguna
        }
    }


    fun getAnomalyStringResource(ph: String): Int {
        return when (ph) {
            "6.5" -> R.string.capturar_y_reportar_anomalia_65
            "7.0" -> R.string.capturar_y_reportar_anomalia_70
            "7.5" -> R.string.capturar_y_reportar_anomalia_75
            "8.0" -> R.string.capturar_y_reportar_anomalia_80
            "8.5" -> R.string.capturar_y_reportar_anomalia_85
            "9.0" -> R.string.capturar_y_reportar_anomalia_90
            else -> R.string.capturar_y_reportar_anomalia
        }
    }


    //Cambio de actividad de acuerdo a los eventos de la navBar
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Ocultar los componentes específicos al cambiar al fragmento Leaderboard
        when (fragment) {
            is Map -> {
                binding.imagenTienda.visibility = View.VISIBLE
                binding.tienda.visibility = View.VISIBLE
                binding.ubicacion.visibility = View.VISIBLE
                binding.adress.visibility = View.VISIBLE
                binding.storeData.visibility=View.VISIBLE
                binding.mapFragment.visibility=View.VISIBLE
                binding.guideline.visibility=View.VISIBLE
                binding.bottomNavigationView.visibility=View.VISIBLE
                binding.logOut.visibility=View.GONE
                binding.cardLeaderboard.visibility=View.GONE
            }
            is Leaderboard -> {
                binding.imagenTienda.visibility = View.GONE
                binding.tienda.visibility = View.GONE
                binding.ubicacion.visibility = View.GONE
                binding.adress.visibility = View.GONE
                binding.cardView.visibility = View.GONE
                binding.storeData.visibility=View.GONE
                binding.mapFragment.visibility=View.GONE
                binding.guideline.visibility=View.GONE
                binding.logOut.visibility=View.GONE
                binding.cardLeaderboard.visibility=View.VISIBLE


            }
            is Login -> {
                binding.imagenTienda.visibility = View.GONE
                binding.tienda.visibility = View.GONE
                binding.ubicacion.visibility = View.GONE
//                binding.adress.visibility = View.GONE
//                binding.cardView.visibility = View.GONE
                binding.storeData.visibility=View.GONE
                binding.mapFragment.visibility=View.GONE
                binding.guideline.visibility=View.GONE
                binding.bottomNavigationView.visibility=View.GONE
                binding.logOut.visibility=View.GONE
                binding.cardLeaderboard.visibility=View.GONE
            }

            is Settings->{
                binding.imagenTienda.visibility = View.GONE
                binding.tienda.visibility = View.GONE
                binding.ubicacion.visibility = View.GONE
//                binding.adress.visibility = View.GONE
//                binding.cardView.visibility = View.GONE
                binding.storeData.visibility=View.GONE
                binding.mapFragment.visibility=View.GONE
                binding.guideline.visibility=View.GONE
                binding.logOut.visibility=View.VISIBLE
                binding.cardLeaderboard.visibility=View.GONE
            }


        }

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}




