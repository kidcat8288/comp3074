package ca.gbc.comp3074.camilleyu.assignment1;

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val editHours = findViewById<EditText>(R.id.editHours)
        val editHoursRate = findViewById<EditText>(R.id.editHoursRate)
        val editTaxRate = findViewById<EditText>(R.id.editTaxRate)

        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        val btnAbout = findViewById<Button>(R.id.btnAbout)

        val textPay = findViewById<TextView>(R.id.textPay)
        val textOvertime = findViewById<TextView>(R.id.textOvertime)
        val textTotalPay = findViewById<TextView>(R.id.textTotalPay)
        val textTax = findViewById<TextView>(R.id.textTax)






        btnCalculate.setOnClickListener {

           val hoursText = editHours.text.toString()

           val hourlyRateText = editHoursRate.text.toString()

           val taxRateText = editTaxRate.text.toString()


           val hours = hoursText.toDoubleOrNull()
           val hourlyRate = hourlyRateText.toDoubleOrNull()
           val taxRate = taxRateText.toDoubleOrNull()


           if (hours == null || hourlyRate == null || taxRate == null) {
               Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
           }


           if (hours < 0 || hourlyRate < 0 || taxRate < 0 || taxRate > 100) {
               Toast.makeText(this, "Please check your input values", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
           }


           var pay: Double
           var overtimePay: Double

           if (hours <= 40) {
               pay = hours * hourlyRate
               overtimePay = 0.0
           } else {
               pay = 40 * hourlyRate
               overtimePay = (hours - 40) * hourlyRate * 1.5
           }

           val totalPay = pay + overtimePay
           val tax = pay * (taxRate / 100)



           textPay.text = "Pay: \$%.2f".format(pay)

           textOvertime.text = "Overtime Pay: \$%.2f".format(overtimePay)

           textTotalPay.text = "Total Pay: \$%.2f".format(totalPay)

           textTax.text = "Tax: \$%.2f".format(tax)

        }


        btnAbout.setOnClickListener {

            val intent = Intent(this, AboutActivity::class.java)

            startActivity(intent)

        }




        }
    }
