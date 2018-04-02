package com.alexstyl.specialdates.people

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.alexstyl.specialdates.images.ImageLoader
import com.alexstyl.specialdates.ui.widget.ColorImageView

internal class PeopleViewHolder(rowView: View,
                                private val imageLoader: ImageLoader,
                                val avatarView: ColorImageView,
                                private val nameView: TextView) : RecyclerView.ViewHolder(rowView) {

    fun bind(viewModel: PersonViewModel, listener: PeopleViewHolderListener) {
        nameView.text = viewModel.personName
        avatarView.setCircleColorVariant(viewModel.personId.toInt())
        avatarView.setLetter(viewModel.personName, true)
        imageLoader.load(viewModel.avatarURI)
                .asCircle()
                .into(avatarView.imageView)

        itemView.setOnClickListener { listener.onPersonClicked(viewModel.contact, adapterPosition) }
    }
}
