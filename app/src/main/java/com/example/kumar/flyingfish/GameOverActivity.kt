package com.example.kumar.flyingfish

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_game_over.*

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val sp = getSharedPreferences("flyingFish", MODE_PRIVATE)
        highScoreView.append(sp.getInt("highScore", 0).toString())

        scoreView.append(intent.getIntExtra("score", 0).toString())

        playAgainBtn.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }
}
