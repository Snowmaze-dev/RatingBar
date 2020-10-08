package ru.snowmaze.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.snowmaze.example.R
import ru.snowmaze.ratingbar.RatingBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ratingBar.onRatingChangeListener = RatingBar.OnRatingChangeListener {
            Toast.makeText(this, "Selected rating: $it", Toast.LENGTH_SHORT).show()
        }
    }
}