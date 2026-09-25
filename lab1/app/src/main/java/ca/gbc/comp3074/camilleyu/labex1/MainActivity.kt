package ca.gbc.comp3074.camilleyu.labex1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val message = findViewById<TextView>(R.id.countOutput)
        val button = findViewById<Button>(R.id.subtractionButton)

        button.setOnClickListener {
            message.text = "Button clicked!"
        }
    }
}