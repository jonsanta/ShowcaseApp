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
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.showcaseApp.R
import com.example.showcaseApp.adapters.GalleryAdapter
import com.example.showcaseApp.databinding.GalleryActivityBinding

class GalleryAnimations(private val galleryActivityBinding: GalleryActivityBinding){

    /**
     * Animates selected photo --> expand/shrink
     *
     * @param holder : RecyclerView holder which contains selected photo
     * @param photo : Selected photo instance
     * @param position : Photo position on RecyclerView & ViewPager (both should be the same)
     * @param viewPager : ViewPager2 View
     * @param center : View you want your photo be centered to
     */
    fun animate(holder: GalleryAdapter.ViewHolder, photo: Photo, position: Int, viewPager: View, center : View){
        val thumbView = holder.photo

        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()

        holder.photo.getGlobalVisibleRect(startBoundsInt)
        center.getGlobalVisibleRect(finalBoundsInt, globalOffset)
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

        viewPager.visibility = View.VISIBLE

        viewPager.pivotX = 0f
        viewPager.pivotY = 0f

        //If photo is not showing --> Expand -- Else --> Shrink
        if(!photo.isShowing()){
            expand(viewPager, startBounds, finalBounds, startScale)
            (viewPager as ViewPager2).setCurrentItem(position, false)
            viewPager.setBackgroundColor(ActivityCompat.getColor(galleryActivityBinding.root.context, R.color.appBG))
            Gallery.setSelection(photo, position, galleryActivityBinding)
            photo.setShowing(true)
        }else {
            viewPager.setBackgroundColor(ActivityCompat.getColor(galleryActivityBinding.root.context, android.R.color.transparent))
            shrink(viewPager, startBounds, startScale)
            photo.setShowing(false)
            Gallery.clearSelection()
            thumbView.alpha = 1f
        }
    }

    //Animates Gallery photo open event
    private fun expand(expandedImageView : View, startBounds : RectF, finalBounds : RectF, startScale : Float){
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
            duration = 250
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    expandedImageView.visibility = View.VISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {
                    //Not used
                }
            })
            start()
        }
    }


    //Animates Gallery photo closing event
    private fun shrink(expandedImageView : View, startBounds : RectF, startScale : Float){
        AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).apply {
                with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale))
            }
            duration = 150
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    expandedImageView.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator) {
                    expandedImageView.visibility = View.GONE
                }
            })
            start()
        }
    }
}