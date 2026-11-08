package com.example.sensorlab

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var temperatureSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null

    // Changing observable state makes Compose redraw the affected readings.
    private var temperature by mutableStateOf<Float?>(null)
    private var gyroscope by mutableStateOf<GyroscopeReading?>(null)
    private var temperatureAvailable by mutableStateOf(false)
    private var gyroscopeAvailable by mutableStateOf(false)
    private var temperatureRegistrationFailed by mutableStateOf(false)
    private var gyroscopeRegistrationFailed by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        temperatureAvailable = temperatureSensor != null
        gyroscopeAvailable = gyroscopeSensor != null

        setContent {
            SensorLabTheme {
                SensorScreen(
                    temperature = temperature,
                    temperatureAvailable = temperatureAvailable,
                    gyroscope = gyroscope,
                    gyroscopeAvailable = gyroscopeAvailable,
                    temperatureRegistrationFailed = temperatureRegistrationFailed,
                    gyroscopeRegistrationFailed = gyroscopeRegistrationFailed,
                    onReset = { gyroscope = GyroscopeReading(0f, 0f, 0f) },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Do not show readings from a previous foreground session as live data.
        temperature = null
        gyroscope = null
        temperatureRegistrationFailed = temperatureSensor?.let {
            !sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: false
        gyroscopeRegistrationFailed = gyroscopeSensor?.let {
            !sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: false
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                if (event.values.isNotEmpty()) temperature = event.values[0]
            }
            Sensor.TYPE_GYROSCOPE -> {
                if (event.values.size >= 3) {
                    // Copy values: Android may reuse the event's underlying array.
                    gyroscope = GyroscopeReading(event.values[0], event.values[1], event.values[2])
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No accuracy-dependent behavior is needed for this lab.
    }
}

data class GyroscopeReading(val x: Float, val y: Float, val z: Float)

@Composable
private fun SensorLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(
            primary = Color(0xFF225FA6),
            background = Color(0xFFF7F9FC),
            surface = Color(0xFFF7F9FC),
        ),
        content = content,
    )
}

@Composable
fun SensorScreen(
    temperature: Float?,
    temperatureAvailable: Boolean,
    gyroscope: GyroscopeReading?,
    gyroscopeAvailable: Boolean,
    temperatureRegistrationFailed: Boolean = false,
    gyroscopeRegistrationFailed: Boolean = false,
    onReset: () -> Unit = {},
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("SENSOR LAB", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)
            Text("Android Sensor Monitor", style = MaterialTheme.typography.headlineMedium)
            Text("Live readings from your device", style = MaterialTheme.typography.bodyLarge)

            SensorValueCard("Ambient Temperature") {
                when {
                    !temperatureAvailable -> Text("Temperature sensor is not available.")
                    temperatureRegistrationFailed -> Text("Unable to start the temperature sensor. Reopen the app to retry.")
                    temperature == null -> Text("Waiting for sensor reading...")
                    else -> Text(String.format(Locale.getDefault(), "%.1f °C", temperature),
                        style = MaterialTheme.typography.headlineLarge)
                }
                Text("Ambient air temperature", style = MaterialTheme.typography.bodySmall)
            }

            SensorValueCard("Gyroscope") {
                when {
                    !gyroscopeAvailable -> Text("Gyroscope is not available.")
                    gyroscopeRegistrationFailed -> Text("Unable to start the gyroscope. Reopen the app to retry.")
                    gyroscope == null -> Text("Waiting for sensor reading...")
                    else -> {
                        AxisReading("X", gyroscope.x)
                        AxisReading("Y", gyroscope.y)
                        AxisReading("Z", gyroscope.z)
                    }
                }
                Text("Angular velocity · radians per second", style = MaterialTheme.typography.bodySmall)
                OutlinedButton(
                    onClick = onReset,
                    enabled = gyroscopeAvailable && !gyroscopeRegistrationFailed && gyroscope != null,
                ) { Text("Reset gyroscope") }
                Text("Reset shows zero until the next reading arrives.", style = MaterialTheme.typography.bodySmall)
            }

            Text("Rotate your phone around one axis at a time. When it is still, gyroscope readings should stay near zero.",
                style = MaterialTheme.typography.bodyMedium)
            Text("A missing sensor is a valid hardware result. Many phones do not have an ambient temperature sensor.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SensorValueCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            content()
        }
    }
}

@Composable
private fun AxisReading(axis: String, value: Float) {
    Text(
        text = String.format(Locale.getDefault(), "%s: % .3f rad/s", axis, value),
        style = MaterialTheme.typography.titleMedium,
        fontFamily = FontFamily.Monospace,
    )
}

@Preview(showBackground = true)
@Composable
private fun LiveReadingsPreview() {
    SensorLabTheme { SensorScreen(22.5f, true, GyroscopeReading(0.012f, -0.104f, 0.003f), true) }
}

@Preview(showBackground = true)
@Composable
private fun MissingSensorsPreview() {
    SensorLabTheme { SensorScreen(null, false, null, false) }
}

@Preview(showBackground = true)
@Composable
private fun WaitingForReadingsPreview() {
    SensorLabTheme { SensorScreen(null, true, null, true) }
}
