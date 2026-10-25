package com.example.mygpslocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mygpslocation.ui.theme.MyGPSLocationTheme

class MainActivity : ComponentActivity() {

    private lateinit var locationManager: LocationManager

    private var locationText by mutableStateOf("Not Requested")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        savedInstanceState?.getString(KEY_LOCATION_TEXT)?.let { locationText = it }

        enableEdgeToEdge()
        setContent {
            MyGPSLocationTheme {
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { result ->
                    when {
                        result[Manifest.permission.ACCESS_FINE_LOCATION] == true -> requestGpsLocation()
                        result[Manifest.permission.ACCESS_COARSE_LOCATION] == true ->
                            locationText = "Precise location required for GPS"
                        else -> locationText = "Permission denied"
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GpsScreen(
                        locationText = locationText,
                        onRequestClick = {
                            if (hasLocationPermission()) {
                                requestGpsLocation()
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_LOCATION_TEXT, locationText)
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    private fun requestGpsLocation() {
        if (!hasLocationPermission()) return

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationText = "GPS is turned off"
            return
        }

        locationText = "Locating..."
        try {
            locationManager.getCurrentLocation(
                LocationManager.GPS_PROVIDER,
                null,
                mainExecutor
            ) { location ->
                locationText = if (location != null) {
                    "Lat: %.6f\nLon: %.6f".format(location.latitude, location.longitude)
                } else {
                    "Location unavailable"
                }
            }
        } catch (e: SecurityException) {
            locationText = "Permission denied"
        }
    }

    companion object {
        private const val KEY_LOCATION_TEXT = "location_text"
    }
}

@Composable
fun GpsScreen(locationText: String, onRequestClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "GPS Location", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = locationText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestClick) {
            Text(text = "Request / Refresh")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GpsScreenPreview() {
    MyGPSLocationTheme {
        GpsScreen(locationText = "Not Requested", onRequestClick = {})
    }
}
