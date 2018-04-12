package com.alexstyl.specialdates.dailyreminder.actions

import com.alexstyl.specialdates.Strings
import com.alexstyl.specialdates.dailyreminder.ActionType
import com.alexstyl.specialdates.person.ContactActions
import com.alexstyl.specialdates.person.ContactActionsProvider
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class ContactActionsPresenter(
        private val personActionsProvider: ContactActionsProvider,
        private val strings: Strings,
        private val workScheduler: Scheduler,
        private val resultScheduler: Scheduler) {

    private var disposable = CompositeDisposable()

    fun startPresentingInto(view: ContactActionsView, contactActions: ContactActions) {
        disposable.add(
                headerFor(view)
                        .observeOn(resultScheduler)
                        .subscribeOn(workScheduler)
                        .subscribe { view.display(it) }
        )

        disposable.add(
                actionsFor(view, contactActions)
                        .observeOn(resultScheduler)
                        .subscribeOn(workScheduler)
                        .subscribe { viewModels ->
                            view.display(viewModels)
                        })


    }

    private fun headerFor(view: ContactActionsView) = Observable.fromCallable {
        PersonActionsViewModel("How would you like to ${view.actionType.actionLabel()} ${view.contact.givenName}?", view.contact.imagePath)
    }

    private fun actionsFor(view: ContactActionsView, contactActions: ContactActions) =
            when (view.actionType) {
                ActionType.CALL -> Observable.fromCallable { personActionsProvider.callActionsFor(view.contact, contactActions) }
                ActionType.SEND_WISH -> Observable.fromCallable { personActionsProvider.messagingActionsFor(view.contact, contactActions) }
            }

    fun stopPresenting() {
        disposable.dispose()
    }

    private fun ActionType.actionLabel() =
            when (this) {
                ActionType.CALL -> strings.call().toLowerCase()
                ActionType.SEND_WISH -> strings.sendWishes().toLowerCase()
            }
}

