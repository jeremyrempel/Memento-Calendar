package com.alexstyl.specialdates.dailyreminder.actions

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_SENDTO
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.alexstyl.specialdates.CrashAndErrorTracker
import com.alexstyl.specialdates.MementoApplication
import com.alexstyl.specialdates.R
import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.contact.ContactIntentExtractor
import com.alexstyl.specialdates.dailyreminder.ActionType
import com.alexstyl.specialdates.images.ImageLoader
import com.alexstyl.specialdates.person.AndroidContactActions
import com.alexstyl.specialdates.person.ContactActionsAdapter
import com.alexstyl.specialdates.ui.base.ThemedMementoActivity
import javax.inject.Inject

class PersonActionsActivity : ThemedMementoActivity() {

    lateinit var presenter: ContactActionsPresenter
        @Inject set
    lateinit var extractor: ContactIntentExtractor
        @Inject set
    lateinit var errorTracker: CrashAndErrorTracker
        @Inject set
    lateinit var imageLoader: ImageLoader
        @Inject set

    lateinit var view: ContactActionsView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_actions)

        window.statusBarColor = Color.TRANSPARENT
        window.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.scrim)))

        (application as MementoApplication).applicationModule.inject(this)

        val recyclerView = findViewById<RecyclerView>(R.id.actions_list)!!
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = ContactActionsAdapter {
            it.action.run()
            finish()
        }
        recyclerView.adapter = adapter

        val contact = extractor.getContactExtra(intent)
        val actionType = intent.actionType

        if (contact.isPresent && actionType != null) {
            view = AndroidContactActionsView(
                    findViewById(R.id.actions_avatar),
                    findViewById(R.id.actions_prompt),
                    adapter,
                    imageLoader,
                    contact.get(), actionType)
        } else {
            errorTracker.track(RuntimeException("Tried to load the actions for a contact from $intent"))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.startPresentingInto(view, AndroidContactActions(this))
    }

    override fun onStop() {
        super.onStop()
        presenter.stopPresenting()
    }


    companion object {

        fun buildCallIntentFor(context: Context, contact: Contact): Intent {
            return Intent(context, PersonActionsActivity::class.java)
                    .setAction(ACTION_CALL)
                    .putContactExtra(contact)
        }

        fun buildSendIntentFor(context: Context, contact: Contact): Intent {
            return Intent(context, PersonActionsActivity::class.java)
                    .setAction(ACTION_SENDTO)
                    .putContactExtra(contact)
        }

        private fun Intent.putContactExtra(contact: Contact): Intent {
            return putExtra(ContactIntentExtractor.EXTRA_CONTACT_ID, contact.contactID)
                    .putExtra(ContactIntentExtractor.EXTRA_CONTACT_SOURCE, contact.source)
        }
    }

    private val Intent.actionType: ActionType?
        get() =
            when (this.action) {
                ACTION_CALL -> ActionType.CALL
                ACTION_SENDTO -> ActionType.SEND_WISH
                else -> {
                    null
                }
            }
}


