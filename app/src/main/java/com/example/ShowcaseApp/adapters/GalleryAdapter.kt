package com.example.showcaseApp.adapters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.showcaseApp.R
import com.example.showcaseApp.activities.GalleryActivity
import com.example.showcaseApp.classes.Gallery
import com.example.showcaseApp.classes.Photo
import com.example.showcaseApp.classes.Utils
import com.squareup.picasso.Picasso


class GalleryAdapter(private val list: List<Photo>, private val activity : GalleryActivity) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val photo = list[position]

        Picasso.get().load(Uri.fromFile(photo.getThumbnail())).noFade().fit().centerCrop().into(holder.photo)

        holder.photo.adjustViewBounds = true

        // EDITMODE CHECK FOR PORTRAIT <--> LAND SWAP REDRAW
        holder.checkBox.isClickable = Gallery.isEditMode()
        holder.checkBox.isVisible = Gallery.isEditMode()
        holder.checkBox.isChecked = Gallery.isSelected(photo)

        // Click Listener
        onClick(holder, photo, position)
        photo.setView(holder)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    private fun onClick(holder : ViewHolder, photo : Photo, position : Int){

        holder.photo.setOnLongClickListener{
            if(!Gallery.isEditMode()) { // LONG CLICK EVENT - while !editMode
                Gallery.setEditMode(true, activity)// Enables editMode
                select(holder, photo, position)
            }
            else select(holder, photo, position)
            true
        }

        var currentAnimator: Animator? = null

        // The system "short" animation time duration, in milliseconds. This
        // duration is ideal for subtle animations or animations that occur
        // very frequently.
        var shortAnimationDuration: Int = 150

        holder.photo.setOnClickListener { view ->
            Utils.preventTwoClick(view)
            if(Gallery.isEditMode()) select(holder, photo, position) // CLICK EVENT - while editMode
            else{
                val thumbView = holder.photo
                // If there's an animation in progress, cancel it
                // immediately and proceed with this one.
                currentAnimator?.cancel()

                // Load the high-resolution "zoomed-in" image.
                val expandedImageView: ImageView = activity.findViewById(R.id.ac4_imagepreview)
                expandedImageView.setImageURI(photo.getFile().toUri())

                // Calculate the starting and ending bounds for the zoomed-in image.
                // This step involves lots of math. Yay, math.
                val startBoundsInt = Rect()
                val finalBoundsInt = Rect()
                val globalOffset = Point()

                // The start bounds are the global visible rectangle of the thumbnail,
                // and the final bounds are the global visible rectangle of the container
                // view. Also set the container view's offset as the origin for the
                // bounds, since that's the origin for the positioning animation
                // properties (X, Y).
                thumbView.getGlobalVisibleRect(startBoundsInt)
                activity.findViewById<View>(R.id.ac4_recyclerView)
                    .getGlobalVisibleRect(finalBoundsInt, globalOffset)
                startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
                finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

                val startBounds = RectF(startBoundsInt)
                val finalBounds = RectF(finalBoundsInt)

                // Adjust the start bounds to be the same aspect ratio as the final
                // bounds using the "center crop" technique. This prevents undesirable
                // stretching during the animation. Also calculate the start scaling
                // factor (the end scaling factor is always 1.0).
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

                // Hide the thumbnail and show the zoomed-in view. When the animation
                // begins, it will position the zoomed-in view in the place of the
                // thumbnail.
                thumbView.alpha = 0f
                expandedImageView.visibility = View.VISIBLE

                // Set the pivot point for SCALE_X and SCALE_Y transformations
                // to the top-left corner of the zoomed-in view (the default
                // is the center of the view).
                expandedImageView.pivotX = 0f
                expandedImageView.pivotY = 0f

                // Construct and run the parallel animation of the four translation and
                // scale properties (X, Y, SCALE_X, and SCALE_Y).
                currentAnimator = AnimatorSet().apply {
                    play(ObjectAnimator.ofFloat(
                        expandedImageView,
                        View.X,
                        startBounds.left,
                        finalBounds.left)
                    ).apply {
                        with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                        with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                        with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
                    }
                    duration = shortAnimationDuration.toLong()
                    interpolator = DecelerateInterpolator()
                    addListener(object : AnimatorListenerAdapter() {

                        override fun onAnimationEnd(animation: Animator) {
                            currentAnimator = null
                            expandedImageView.visibility = View.VISIBLE
                            Gallery.setViewVisibility(activity.findViewById<ImageButton>(R.id.ac4_remove), true)
                            Gallery.setSelected(photo, position, activity)
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            currentAnimator = null
                        }
                    })
                    start()
                }

                // Upon clicking the zoomed-in image, it should zoom back down
                // to the original bounds and show the thumbnail instead of
                // the expanded image.
                activity.findViewById<ImageButton>(R.id.ac4_btn_discard).setOnClickListener {
                    currentAnimator?.cancel()

                    // Animate the four positioning/sizing properties in parallel,
                    // back to their original values.
                    currentAnimator = AnimatorSet().apply {
                        play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).apply {
                            with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                            with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale))
                            with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale))
                        }
                        duration = shortAnimationDuration.toLong()
                        interpolator = DecelerateInterpolator()
                        addListener(object : AnimatorListenerAdapter() {

                            override fun onAnimationEnd(animation: Animator) {
                                thumbView.alpha = 1f
                                expandedImageView.visibility = View.GONE
                                currentAnimator = null
                                activity.closeImage()
                            }

                            override fun onAnimationCancel(animation: Animator) {
                                thumbView.alpha = 1f
                                expandedImageView.visibility = View.GONE
                                currentAnimator = null
                                activity.closeImage()
                            }
                        })
                        start()
                    }
                }

                //Prevent touch through ImageView
            }
        }
    }

    private fun select(holder : ViewHolder, photo : Photo, position: Int){
        Gallery.setSelected(photo, position, activity)
        holder.checkBox.isChecked = !holder.checkBox.isChecked
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageButton = itemView.findViewById(R.id.icv_imageBtn_imagen)
        val checkBox : CheckBox = itemView.findViewById(R.id.icv_checkbox)
    }
}