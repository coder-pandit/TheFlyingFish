package com.example.kumar.flyingfish

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private lateinit var fromBottom: Animation
    private lateinit var fromTop: Animation
    private lateinit var fromRight: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // show highScore stored in sp otherwise show 0
        val sp = getSharedPreferences("flyingFish", MODE_PRIVATE)
        highScoreView.append(sp.getInt("highScore", 0).toString())

        // if activity is started from launcher i.e not started from intent sent by other activity then only show animation
        if (intent.action == Intent.ACTION_MAIN && intent.categories.contains(Intent.CATEGORY_LAUNCHER)) {
            // pull play button from the bottom of screen
            fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom)
            playBtn.animation = fromBottom

            // pull high score view from top of screen
            fromTop = AnimationUtils.loadAnimation(this, R.anim.from_top)
            highScoreView.animation = fromTop

            // fish will come from right of screen
            fromRight = AnimationUtils.loadAnimation(this, R.anim.from_right)
            imageView.animation = fromRight
        }

        // on clicking play button let user play the game by sending user to MainActivity
        playBtn.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        if (sp.getBoolean("soundOn", true)) {
            soundSetting.setImageResource(R.drawable.ic_sound_on)
        } else {
            soundSetting.setImageResource(R.drawable.ic_sound_off)
        }

        soundSetting.setOnClickListener {
            if (sp.getBoolean("soundOn", true)) {
                sp.edit().putBoolean("soundOn", false).apply()
                soundSetting.setImageResource(R.drawable.ic_sound_off)
            } else {
                sp.edit().putBoolean("soundOn", true).apply()
                soundSetting.setImageResource(R.drawable.ic_sound_on)
            }
        }
    }
}
