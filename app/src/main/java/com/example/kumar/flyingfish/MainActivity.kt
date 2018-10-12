package com.example.kumar.flyingfish

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var gameView: FlyingFishView
    private val handler = Handler()
    private lateinit var runnable: Runnable
    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // just defining here to pass to FlyFishView to send intent when game over
        val intent = Intent(this, GameOverActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        // just defining here to pass to FlyingFishView to get high score while playing
        val sp = getSharedPreferences("flyingFish", MODE_PRIVATE)

        // runnable is callback to be passed into handler
        runnable = object : Runnable {
            override fun run() {
                gameView.invalidate()

                // run this task again after 30ms (lead to infinite loop) until not removed callbacks
                handler.postDelayed(this, 30)
                // remove all previous callbacks
                handler.removeCallbacksAndMessages(this)
            }
        }

        // setContentView to custom created view FlyingFishView
        gameView = FlyingFishView(this, intent, sp)
        setContentView(gameView)

        if (sp.getBoolean("soundOn", true)) {
            player = MediaPlayer.create(this,  R.raw.music)
        }
        player?.isLooping = true
        player?.setVolume(100f, 100f)
    }

    override fun onResume() {
        super.onResume()
        // instantly run the callback
        handler.post(runnable)
        player?.start()
    }

    override fun onPause() {
        super.onPause()
        // remove callback if activity paused to prevent data leak
        handler.removeCallbacks(runnable)
        player?.stop()
        player?.release()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // remove callback on back pressed
        handler.removeCallbacks(runnable)

        // send user to splash activity on pressing back button
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
