package util

import org.mockito.Mockito

inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)

fun mockSchedulers() {
    RxJavaHooks.reset()

    RxJavaHooks.setOnComputationScheduler { Schedulers.immediate() }
    RxJavaHooks.setOnNewThreadScheduler { Schedulers.immediate() }
    RxJavaHooks.setOnIOScheduler { Schedulers.immediate() }

    RxAndroidPlugins.getInstance().reset()
    RxAndroidPlugins.getInstance().registerSchedulersHook(object: RxAndroidSchedulersHook() {
        override fun getMainThreadScheduler(): Scheduler {
            return Schedulers.immediate()
        }
    })
}

fun resetSchedulers() {
    RxAndroidPlugins.getInstance().reset()
    RxJavaHooks.reset()
}