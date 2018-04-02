package com.alexstyl.specialdates.person

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.design.widget.TabLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v4.view.ViewPager
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.alexstyl.android.getColorDrawable
import com.alexstyl.android.toBitmap
import com.alexstyl.specialdates.CrashAndErrorTracker
import com.alexstyl.specialdates.ExternalNavigator
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
import com.alexstyl.specialdates.ui.base.ThemedMementoActivity
import com.alexstyl.specialdates.ui.widget.MementoToolbar
import com.novoda.notils.caster.Views
import javax.inject.Inject

class PersonActivity : ThemedMementoActivity(), PersonView, BottomSheetIntentListener {

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
    private var displayNameView: TextView? = null
    private var ageAndStarSignView: TextView? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var adapter: ContactItemsAdapter? = null

    private var displayingContact = Optional.absent<Contact>()
    private var navigator: PersonDetailsNavigator? = null

    private val isVisibleContactOptional = Optional.absent<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postponeEnterTransition()

        window.setBackgroundDrawable(resources.getColorDrawable(R.color.scrim))
        window.setFlags(FLAG_TRANSLUCENT_STATUS, FLAG_TRANSLUCENT_STATUS)
        window.setTitle("")

        setContentView(R.layout.activity_person)

        val applicationModule = (application as MementoApplication).applicationModule
        applicationModule.inject(this)
        analytics!!.trackScreen(Screen.PERSON)

        avatarView = findViewById(R.id.person_avatar)
        displayNameView = findViewById(R.id.person_name)
        ageAndStarSignView = findViewById(R.id.person_age_and_sign)
        viewPager = findViewById(R.id.person_viewpager)

        val toolbar = findViewById<MementoToolbar>(R.id.person_toolbar)
        if (wasCalledFromMemento()) {
            toolbar.displayNavigationIconAsUp()
        } else {
            toolbar.displayNavigationIconAsClose()
        }
        setSupportActionBar(toolbar)
        title = "" // we have a separate view to display the title

        adapter = ContactItemsAdapter(LayoutInflater.from(thisActivity()), EventPressedListener { (action) ->
            try {
                action.run()
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(thisActivity(), R.string.no_app_found, Toast.LENGTH_SHORT).show()
                tracker!!.track(ex)
            }
        })

        viewPager!!.adapter = adapter
        viewPager!!.offscreenPageLimit = 2

        tabLayout = Views.findById(this, R.id.person_tabs)
        tabLayout!!.setupWithViewPager(viewPager, true)

        navigator = PersonDetailsNavigator(ExternalNavigator(this, analytics, tracker))

    }


    private fun baseInterpolator(): TransitionSet =
            TransitionInflater.from(this).inflateTransition(R.transition.base_transition_set) as TransitionSet

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
        displayNameView?.text = viewModel.displayName
        ageAndStarSignView?.text = viewModel.ageAndStarSignlabel


        imageLoader!!.load(viewModel.image)
                .withSize(avatarView!!.width, avatarView!!.height)
                .into(object : ImageLoadedConsumer {
                    override fun onImageLoaded(loadedImage: Bitmap?) {

                        window.sharedElementEnterTransition = baseInterpolator()
                                .addTransition(
                                        RadiusTransition.toSquare(loadedImage!!.width / 2F)
                                )

                        window.sharedElementReturnTransition =
                                baseInterpolator()
                                        .addTransition(
                                                RadiusTransition.toCircle(resources.getDimensionPixelSize(R.dimen.person_avatar_height) / 2F)
                                        )

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
        adapter!!.displayEvents(viewModel)

        updateTabIfNeeded(0, R.drawable.ic_gift)
        updateTabIfNeeded(1, R.drawable.ic_call)
        updateTabIfNeeded(2, R.drawable.ic_message)

        if (tabLayout!!.tabCount <= 1) {
            tabLayout!!.visibility = View.GONE
        } else {
            tabLayout!!.visibility = View.VISIBLE
        }
    }

    private fun updateTabIfNeeded(index: Int, @DrawableRes iconResId: Int) {
        if (tabLayout!!.getTabAt(index) != null) {
            tabLayout!!.getTabAt(index)!!.icon = getTintedDrawable(iconResId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_person_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home && !wasCalledFromMemento()) {
            supportFinishAfterTransition()
            return true
        } else if (itemId == R.id.menu_view_contact) {
            navigator!!.toViewContact(displayingContact)
        } else if (itemId == ID_TOGGLE_VISIBILITY) {
            val isVisible = isVisibleContactOptional.get()
            if (isVisible!!) {
                presenter!!.hideContact(this)
            } else {
                presenter!!.showContact(this)
            }

        }
        return super.onOptionsItemSelected(item)
    }

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

    override fun showPersonAsVisible() {
        TODO("Visibility is not currently available")
    }

    override fun showPersonAsHidden() {
        TODO("Visibility is not currently available")
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

