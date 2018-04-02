package com.alexstyl.specialdates.upcoming

import android.graphics.Bitmap
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.View
import android.widget.TextView
import com.alexstyl.android.toBitmap
import com.alexstyl.specialdates.R
import com.alexstyl.specialdates.images.ImageLoadedConsumer
import com.alexstyl.specialdates.images.ImageLoader
import com.alexstyl.specialdates.ui.widget.ColorImageView
import com.alexstyl.specialdates.upcoming.view.OnUpcomingEventClickedListener

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

        val resources = avatarView.resources
        imageLoader.load(viewModel.contactImagePath)
                .withSize(resources.getDimensionPixelSize(R.dimen.upcoming_contact_avatar_width),
                        resources.getDimensionPixelSize(R.dimen.upcoming_contact_avatar_height))
                .into(object : ImageLoadedConsumer {
                    override fun onImageLoaded(loadedImage: Bitmap?) {
                        val drawable = RoundedBitmapDrawableFactory.create(resources, loadedImage)
                        drawable.cornerRadius = 1000F
                        avatarView.imageView.setImageDrawable(drawable)
                    }

                    override fun onLoadingFailed() {
                        // TODO
                        val bitmap = ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)?.toBitmap()
                        val drawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
                        drawable.cornerRadius = 0F
                        avatarView.setImageDrawable(drawable)
                    }

                })

        itemView.setOnClickListener { listener.onContactClicked(viewModel.contact, adapterPosition) }
    }
}
