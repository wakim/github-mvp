package br.com.github.sample.bus

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

object GenericPublishSubject {
    val CONNECTIVITY_CHANGE_TYPE = 1
    val PUBLISH_SUBJECT: Subject<PublishItem<Any>> = PublishSubject.create<PublishItem<Any>>()
            .toSerialized()
}
