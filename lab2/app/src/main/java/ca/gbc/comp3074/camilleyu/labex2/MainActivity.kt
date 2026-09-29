package ca.gbc.comp3074.camilleyu.labex2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val countOutput= findViewById<TextView>(R.id.countOutput)

        val subButton =findViewById<Button>(R.id.subtractionButton)

        val addButton=findViewById<Button>(R.id.addButton)

        val resetbutton =findViewById<Button>(R.id.resetButton)

        val stepButton =findViewById<Button>(R.id.stepButton)

        val resetstepButton =findViewById<Button>(R.id.resetstepbutton)

        var count =0
        var stepsize=1




        subButton.setOnClickListener {
            count =count -stepsize
            countOutput.text=count.toString()
        }


        addButton.setOnClickListener {
            count =count +stepsize
            countOutput.text=count.toString()
        }

        stepButton.setOnClickListener {
           stepsize=stepsize+1
        }

        resetbutton.setOnClickListener {
            count=0
            countOutput.text=count.toString()
        }

        resetstepButton.setOnClickListener {
            stepsize=1
        }



    }
}