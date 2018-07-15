package com.example.kumar.flyingfish

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.view.MotionEvent
import android.view.View

@SuppressLint("ViewConstructor")
class FlyingFishView(context: Context, intent1: Intent, sp1: SharedPreferences) : View(context) {
    private val intent = intent1
    private val sp = sp1

    private var fishX = 10
    private var fishY = 0
    private var fishSpeed = 0
    private var canvasWidth = 0
    private var canvasHeight = 0

    private var yellowX = 0
    private var yellowY = 0
    private var yellowSpeed = 16 // speed of yellow ball
    private var yellowPaint = Paint()

    private var greenX = 0
    private var greenY = 0
    private var greenSpeed = 25 // speed of green ball
    private var greenPaint = Paint()

    private var redX = 0
    private var redY = 0
    private var redSpeed = 20 // speed of red ball
    private var redPaint = Paint()

    private var score = 0
    private var lifeCount = 3
    private var touch = false

    private var fish = arrayOfNulls<Bitmap>(2)
    private var backgroundImage: Bitmap
    private var life = arrayOfNulls<Bitmap>(2)
    private var scorePaint: Paint = Paint()

    init {
        fish[0] = BitmapFactory.decodeResource(resources, R.drawable.fish1)
        fish[1] = BitmapFactory.decodeResource(resources, R.drawable.fish2)

        backgroundImage = BitmapFactory.decodeResource(resources, R.drawable.background)

        // setting up yellow ball
        yellowPaint.color = Color.YELLOW
        yellowPaint.isAntiAlias = false

        // setting up green ball
        greenPaint.color = Color.GREEN
        greenPaint.isAntiAlias = false

        // setting up red ball
        redPaint.color = Color.RED
        redPaint.isAntiAlias = false

        // setting up text properties for score
        scorePaint.color = Color.WHITE
        scorePaint.textSize = 70f
        scorePaint.typeface = Typeface.DEFAULT_BOLD
        scorePaint.isAntiAlias = true

        life[0] = BitmapFactory.decodeResource(resources, R.drawable.hearts)
        life[1] = BitmapFactory.decodeResource(resources, R.drawable.heart_grey)

        // initial height of fish in y direction
        fishY = 500
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // getting width and height of canvas
        canvasWidth = canvas!!.width
        canvasHeight = canvas.height

        // setting background image
        canvas.drawBitmap(backgroundImage, 0f, 0f, null)

        // text to show score
        canvas.drawText("Score: $score", 20f, 60f, scorePaint)

        // fish cannot above a point on screen which is available at height of fish from top of canvas
        val minFishY = fish[0]!!.height

        // fish cannot go down to a point which is available at height of fish from bottom of canvas
        val maxFishY = canvasHeight - fish[0]!!.height

        // fish height from bottom should be decreasing constantly by its speed
        // which means it should be increasing from top
        fishY += fishSpeed

        // fishSpeed is height by which fish should fall if user not touching screen
        // fishSpeed should be increasing constantly if fish is falling freely (gravity)
        // so falling speed of fish will be least when it just starts falling
        // and increases as it looses its height from bottom
        fishSpeed += 2

        // if height from top goes less than its minHeight from top then stp fish right there don't allow it to go above than that
        if (fishY < minFishY) fishY = minFishY

        // if height from top goes more than its maxHeight then drown the fish & GAME OVER
        else if (fishY > maxFishY) {
            lifeCount = 0
        }

        // if user touch the screen use flying fish image
        if (touch) {
            canvas.drawBitmap(fish[1], fishX.toFloat(), fishY.toFloat(), null)
            touch = false
        } else { // else use normal fish image
            canvas.drawBitmap(fish[0], fishX.toFloat(), fishY.toFloat(), null)
        }

        // subtract speed of yellow ball from x distance of ball to move ball in left direction
        yellowX -= yellowSpeed

        // if fish hits yellow ball increase score by 10
        if (hitBallChecker(yellowX, yellowY)) {
            score += 10
            // remove yellow ball by making x distance negative (it hides ball inside left corner of screen)
            yellowX = -100
        }

        // if x distance of ball is negative then ball will be hidden
        // so throw this ball again from right of screen towards fish
        // to throw from right of screen make x distance greater than canvasWidth
        // throw ball at random y heights between min & max y height
        if (yellowX < 0) {
            yellowX = canvasWidth + 21
            yellowY = Math.floor(Math.random() * (maxFishY - minFishY)).toInt() + minFishY
        }
        // actually draw the circle with yellow paint properties on canvas to show that
        canvas.drawCircle(yellowX.toFloat(), yellowY.toFloat(), 25f, yellowPaint)


        // things will be same for green & red ball also except the score
        // if we hit green ball score increase by 20
        // and if we hit red ball score doesn't increases life count decrease by 1

        greenX -= greenSpeed

        // if fish hits green ball increase score by 20
        if (hitBallChecker(greenX, greenY)) {
            score += 20
            greenX = -100
        }

        if (greenX < 0) {
            greenX = canvasWidth + 21
            greenY = Math.floor(Math.random() * (maxFishY - minFishY)).toInt() + minFishY
        }
        canvas.drawCircle(greenX.toFloat(), greenY.toFloat(), 25f, greenPaint)


        // things are same as yellow ball except hitting result
        redX -= redSpeed

        // if fish hits red ball decrease life count by 1
        if (hitBallChecker(redX, redY)) {
            redX = -100
            lifeCount--
        }

        if (redX < 0) {
            redX = canvasWidth + 21
            redY = Math.floor(Math.random() * (maxFishY - minFishY)).toInt() + minFishY
        }
        canvas.drawCircle(redX.toFloat(), redY.toFloat(), 30f, redPaint)

        // now if life count reach to 0 then just over the current game and user to GameOverActivity
        if (lifeCount == 0) {
            // if score is greater than high score then save current score as high score in sp
            if (score > sp.getInt("highScore", 0)) {
                sp.edit().putInt("highScore", score).apply()
            }
            // send current score to next activity to show score there
            intent.putExtra("score", score)
            context.startActivity(intent)
        }

        // to show 3 hearts for fish life
        for (i in 0..2) {
            val x = canvasWidth - 70 - 1.5 * (2 - i) * life[0]!!.width
            val y = 30f

            if (i < lifeCount) { // show red heart for life available
                canvas.drawBitmap(life[0], x.toFloat(), y, null)
            } else { // show gray heart for life lost
                canvas.drawBitmap(life[1], x.toFloat(), y, null)
            }
        }
    }

    // to check whether fish hit the ball or not
    private fun hitBallChecker(x: Int, y: Int): Boolean {
        return when {
        // if x distance of fish is less than x distance of ball
        // and x distance of ball is less than x distance of fish + fish width
        // it means ball is crossing fish in x direction
        // same for y direction

        // now if both happen simultaneously i.e fish & ball crossing each other in x and y simultaneously
        // then it is only possible when they hit each other so return true
            fishX < x && x < fishX + fish[0]!!.width && fishY < y && y < fishY + fish[0]!!.height -> true

        // else return false they are not hitting each other
            else -> false
        }
    }

    // to handle touch events on screen
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // when user touch screen(touch down) make speed of fish negative to move fish up
        // we are adding fish speed to y height of fish if fish speed is negative y height of fish wi;; decrease
        // which means fish moves up
        if (event?.action == MotionEvent.ACTION_DOWN) {
            touch = true
            fishSpeed = -22
        }
        return true
    }
}
