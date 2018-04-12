package com.alexstyl.specialdates.dailyreminder.actions

import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.dailyreminder.ActionType
import com.alexstyl.specialdates.person.ContactActionViewModel

interface ContactActionsView {

    val contact: Contact
    val actionType: ActionType
    fun display(viewModel: PersonActionsViewModel)
    fun display(viewModels: List<ContactActionViewModel>)

}
