package com.alexstyl.specialdates.person

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.alexstyl.android.toBitmap
import com.alexstyl.specialdates.CrashAndErrorTracker
import com.alexstyl.specialdates.MementoApplication
import com.alexstyl.specialdates.Optional
import com.alexstyl.specialdates.R
import com.alexstyl.specialdates.analytics.Analytics
import com.alexstyl.specialdates.analytics.Screen
import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.contact.ContactNotFoundException
import com.alexstyl.specialdates.contact.ContactSource
import com.alexstyl.specialdates.contact.ContactSource.SOURCE_DEVICE
import com.alexstyl.specialdates.contact.ContactsProvider
import com.alexstyl.specialdates.images.ImageLoadedConsumer
import com.alexstyl.specialdates.images.ImageLoader
import com.alexstyl.specialdates.transition.RadiusTransition
import com.alexstyl.specialdates.ui.base.MementoActivity
import com.novoda.notils.caster.Views
import javax.inject.Inject

class PersonActivity : MementoActivity(), PersonView, BottomSheetIntentListener {

    var analytics: Analytics? = null
        @Inject set
    var imageLoader: ImageLoader? = null
        @Inject set
    var contactsProvider: ContactsProvider? = null
        @Inject set
    var tracker: CrashAndErrorTracker? = null
        @Inject set
    var presenter: PersonPresenter? = null
        @Inject set

    private var avatarView: ImageView? = null
    private var displayingContact = Optional.absent<Contact>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postponeEnterTransition()

        setContentView(R.layout.activity_person)

        val radiusSize = resources.getDimensionPixelSize(R.dimen.person_avatar_height) / 2F
        window.sharedElementEnterTransition = baseInterpolator()
                .addTransition(RadiusTransition.toSquare(radiusSize))

        window.sharedElementReturnTransition =
                baseInterpolator()
                        .addTransition(RadiusTransition.toCircle(radiusSize))


        val applicationModule = (application as MementoApplication).applicationModule
        applicationModule.inject(this)
        analytics!!.trackScreen(Screen.PERSON)
        avatarView = Views.findById(this, R.id.person_avatar)
    }

    private fun baseInterpolator(): TransitionSet =
            TransitionInflater.from(this).inflateTransition(R.transition.base_transition_set) as TransitionSet


    override fun onResume() {
        super.onResume()
        displayingContact = extractContactFrom(intent)
        if (displayingContact.isPresent) {
            presenter!!.startPresentingInto(this, displayingContact.get(), AndroidContactActions(this))
        } else {
            tracker!!.track(IllegalArgumentException("No contact to display"))
            finish()
        }
    }

    private fun wasCalledFromMemento(): Boolean {
        val extras = intent.extras
        return extras != null && intent.extras!!.containsKey(EXTRA_CONTACT_ID)
    }

    private fun extractContactFrom(intent: Intent): Optional<Contact> {
        val data = intent.data
        if (data != null) {
            val contactId = java.lang.Long.valueOf(data.lastPathSegment)
            return contactFor(contactId!!, SOURCE_DEVICE)
        }

        val contactID = intent.getLongExtra(EXTRA_CONTACT_ID, -1)
        if (contactID == -1L) {
            return Optional.absent()
        }
        @ContactSource val contactSource = intent.getIntExtra(EXTRA_CONTACT_SOURCE, -1)

        return if (contactSource == -1) {
            Optional.absent()
        } else contactFor(contactID, contactSource)
    }

    private fun contactFor(contactID: Long, contactSource: Int): Optional<Contact> {
        return try {
            Optional(contactsProvider!!.getContact(contactID, contactSource))
        } catch (e: ContactNotFoundException) {
            tracker!!.track(e)
            Optional.absent()
        }

    }

    override fun displayPersonInfo(viewModel: PersonInfoViewModel) {
        imageLoader!!.load(viewModel.image)
                .withSize(avatarView!!.width, avatarView!!.height)
                .into(object : ImageLoadedConsumer {
                    override fun onImageLoaded(loadedImage: Bitmap?) {
                        val drawable = RoundedBitmapDrawableFactory.create(resources, loadedImage)
                        drawable.cornerRadius = 0F
                        avatarView?.setImageDrawable(drawable)
                        startPostponedEnterTransition()
                    }

                    override fun onLoadingFailed() {
                        // TODO
                        val bitmap = ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)?.toBitmap()

                        val drawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
                        drawable.cornerRadius = 0F
                        avatarView?.setImageDrawable(drawable)
                        startPostponedEnterTransition()
                    }
                })
    }

    override fun displayAvailableActions(viewModel: PersonAvailableActionsViewModel) {
    }

    override fun showPersonAsVisible() {
        throw UnsupportedOperationException("Visibility is not currently available")
    }

    override fun showPersonAsHidden() {
        throw UnsupportedOperationException("Visibility is not currently available")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_person_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home && !wasCalledFromMemento()) {
            finishAfterTransition()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        presenter!!.stopPresenting()
    }

    override fun onActivitySelected(intent: Intent) {
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_app_found, Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        private const val EXTRA_CONTACT_SOURCE = "extra:source"
        private const val EXTRA_CONTACT_ID = "extra:id"
        private const val ANIMATION_DURATION = 400
        private const val ID_TOGGLE_VISIBILITY = 1023

        fun buildIntentFor(context: Context, contact: Contact): Intent {
            val intent = Intent(context, PersonActivity::class.java)
            intent.putExtra(EXTRA_CONTACT_ID, contact.contactID)
            intent.putExtra(EXTRA_CONTACT_SOURCE, contact.source)
            return intent
        }
    }
}

private fun Resources.getDrawableCompat(@DrawableRes drawableResId: Int): Drawable? =
        ResourcesCompat.getDrawable(this, drawableResId, null)

private fun Resources.getColorDrawable(@ColorRes colorRes: Int): Drawable? =
        ColorDrawable(ResourcesCompat.getColor(this, colorRes, null))
