package ca.gbc.comp3074.camilleyu.assignment1
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_about)
        val btnGoBack = findViewById<Button>(R.id.goBackbutton)

        btnGoBack.setOnClickListener {
            finish()



    }

    }


}

