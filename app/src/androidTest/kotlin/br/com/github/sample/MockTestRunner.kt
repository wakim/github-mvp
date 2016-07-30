package br.com.github.sample

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner
import br.com.github.sample.application.TestApplication

class MockTestRunner: AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}