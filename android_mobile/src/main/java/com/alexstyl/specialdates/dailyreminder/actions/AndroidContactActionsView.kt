package com.alexstyl.specialdates.dailyreminder.actions

import android.widget.ImageView
import android.widget.TextView
import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.dailyreminder.ActionType
import com.alexstyl.specialdates.images.ImageLoader
import com.alexstyl.specialdates.person.ContactActionViewModel
import com.alexstyl.specialdates.person.ContactActionsAdapter

class AndroidContactActionsView(private val avatar: ImageView,
                                private val label: TextView,
                                private val adapter: ContactActionsAdapter,
                                private val imageLoader: ImageLoader,
                                override val contact: Contact,
                                override val actionType: ActionType
) : ContactActionsView {

    override fun display(viewModel: PersonActionsViewModel) {
        imageLoader.load(viewModel.avatarURI)
                .asCircle()
                .into(avatar)
        label.text = viewModel.label
    }

    override fun display(viewModels: List<ContactActionViewModel>) {
        adapter.display(viewModels)
    }

}
