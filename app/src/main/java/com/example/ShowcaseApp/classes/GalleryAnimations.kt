package com.example.showcaseApp.classes

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.GalleryActivity

class GalleryAnimations(private val activity: GalleryActivity) {

    fun animate(photo: Photo, duration : Int){
        val holder = photo.getView()!!

        val thumbView = photo.getView()!!.photo

        val expandedImageView: ImageView = activity.findViewById(R.id.ac4_imagepreview)
        expandedImageView.setImageURI(photo.getFile().toUri())

        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()

        thumbView.getGlobalVisibleRect(startBoundsInt)
        activity.findViewById<View>(R.id.ac4_recyclerView)
            .getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

        val startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)

        val startScale: Float
        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            // Extend start bounds horizontally
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        thumbView.alpha = 0f
        expandedImageView.visibility = View.VISIBLE

        expandedImageView.pivotX = 0f
        expandedImageView.pivotY = 0f

        if(!holder.expanded){
            expand(expandedImageView, startBounds, finalBounds, startScale, duration, photo)
            holder.expanded = true
        }else {
            shrink(expandedImageView, startBounds, startScale, duration, thumbView)
            holder.expanded = false
        }

        activity.findViewById<ImageButton>(R.id.ac4_remove).setOnClickListener{
            Utils.preventTwoClick(it)
            Gallery.setViewVisibility(activity.findViewById(R.id.ac4_remove), false)
            thumbView.alpha = 1f
            activity.removePhoto()
            holder.expanded = false
        }
    }

    private fun expand(expandedImageView : ImageView, startBounds : RectF, finalBounds : RectF, startScale : Float, animDuration: Int, photo: Photo){
        AnimatorSet().apply {
            play(
                ObjectAnimator.ofFloat(
                expandedImageView,
                View.X,
                startBounds.left,
                finalBounds.left)
            ).apply {
                with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
            }
            duration = animDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    expandedImageView.visibility = View.VISIBLE
                    expandedImageView.background = activity.resources.getColor(R.color.white, activity.theme).toDrawable()
                    Gallery.setViewVisibility(activity.findViewById<ImageButton>(R.id.ac4_remove), true)
                    Gallery.setSelected(photo, photo.getPosition(), activity)
                }

                override fun onAnimationCancel(animation: Animator) {
                }
            })
            start()
        }
    }

    private fun shrink(expandedImageView : ImageView, startBounds : RectF, startScale : Float, animDuration: Int, thumbView : ImageView){
        Gallery.setViewVisibility(activity.findViewById(R.id.ac4_remove), false)

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).apply {
                with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale))
            }
            duration = animDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    thumbView.alpha = 1f
                    expandedImageView.visibility = View.GONE
                    activity.closeImage()
                }

                override fun onAnimationCancel(animation: Animator) {
                    thumbView.alpha = 1f
                    expandedImageView.visibility = View.GONE
                    activity.closeImage()
                }
            })
            start()
        }
    }
}