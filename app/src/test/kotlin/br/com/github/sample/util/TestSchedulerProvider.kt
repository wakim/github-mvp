package br.com.github.sample.util

import br.com.github.sample.util.schedulers.SchedulerProviderContract
import io.reactivex.Scheduler
import io.reactivex.internal.schedulers.ImmediateThinScheduler

class TestSchedulerProvider : SchedulerProviderContract {

    val scheduler: Scheduler = ImmediateThinScheduler.INSTANCE

    override val io: Scheduler = scheduler
    override val ui: Scheduler = scheduler
    override val computation: Scheduler = scheduler
}
