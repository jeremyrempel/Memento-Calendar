package com.alexstyl.specialdates.upcoming

import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.View
import android.widget.TextView
import com.alexstyl.specialdates.images.ImageLoader
import com.alexstyl.specialdates.ui.widget.ColorImageView
import com.alexstyl.specialdates.upcoming.view.OnUpcomingEventClickedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class ContactEventViewHolder(view: View,
                             val avatarView: ColorImageView,
                             private val contactName: TextView,
                             private val eventLabel: TextView,
                             private val imageLoader: ImageLoader)
    : UpcomingRowViewHolder<UpcomingContactEventViewModel>(view) {

    override fun bind(viewModel: UpcomingContactEventViewModel, listener: OnUpcomingEventClickedListener) {
        avatarView.setCircleColorVariant(viewModel.backgroundVariant)
        avatarView.setLetter(viewModel.contactName)

        contactName.text = viewModel.contactName
        eventLabel.text = viewModel.eventLabel
        eventLabel.setTextColor(viewModel.eventColor)

        Glide.with(avatarView.context)
                .asBitmap()
                .load(viewModel.contactImagePath)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val drawable = RoundedBitmapDrawableFactory.create(avatarView.resources, resource)
                        drawable.cornerRadius = 1000F
                        avatarView.imageView.setImageDrawable(drawable)
                    }
                })

//        imageLoader.load(viewModel.contactImagePath)
//                .asCircle()
//                .into(avatarView.imageView)
        itemView.setOnClickListener { listener.onContactClicked(viewModel.contact, adapterPosition) }
    }
}
