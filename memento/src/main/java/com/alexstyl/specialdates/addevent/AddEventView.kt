package com.alexstyl.specialdates.addevent

import com.alexstyl.specialdates.contact.Contact
import java.net.URI

interface AddEventView {

    fun display(uri: URI)
    fun display(viewModels: List<AddEventContactEventViewModel>)
    fun displayContact(contact: Contact)

    fun allowImagePick()
    fun preventImagePick()

    fun allowSave()
    fun preventSave()
    
    fun clearAvatar()
    fun displayUpdatedContact(updatedContact: Contact)
}
