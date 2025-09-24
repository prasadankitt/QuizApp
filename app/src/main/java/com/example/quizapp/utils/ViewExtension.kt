package com.example.quizapp.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator

fun View.scaleAnimation(
    fromScale: Float = 0.8f,
    toScale: Float = 1.0f,
    duration: Long = 300,
    overshoot: Boolean = false
) {
    val scaleX = ObjectAnimator.ofFloat(this, "scaleX", fromScale, toScale)
    val scaleY = ObjectAnimator.ofFloat(this, "scaleY", fromScale, toScale)

    val animatorSet = AnimatorSet().apply {
        playTogether(scaleX, scaleY)
        this.duration = duration
        interpolator = if (overshoot) OvershootInterpolator() else AccelerateDecelerateInterpolator()
    }

    animatorSet.start()
}

fun View.fadeInAnimation(duration: Long = 300) {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(duration)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .start()
}

fun View.slideInFromBottom(duration: Long = 400) {
    translationY = height.toFloat()
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .translationY(0f)
        .setDuration(duration)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .start()
}

fun View.pulseAnimation(duration: Long = 1000) {
    val scaleUp = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.1f)
    val scaleUpY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.1f)
    val scaleDown = ObjectAnimator.ofFloat(this, "scaleX", 1.1f, 1f)
    val scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", 1.1f, 1f)

    val animatorSet = AnimatorSet().apply {
        play(scaleUp).with(scaleUpY)
        play(scaleDown).with(scaleDownY).after(scaleUp)
        this.duration = duration / 2
        interpolator = AccelerateDecelerateInterpolator()
    }

    animatorSet.start()
}