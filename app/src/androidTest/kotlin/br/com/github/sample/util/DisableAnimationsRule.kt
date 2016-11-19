package br.com.github.sample.util

import android.support.test.InstrumentationRegistry.getInstrumentation
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DisableAnimationsRule(): TestRule {

    lateinit var systemAnimations: SystemAnimations

    override fun apply(statement: Statement, description: Description): Statement {
        systemAnimations = SystemAnimations(getInstrumentation().context)

        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                systemAnimations.disableAll()

                try {
                    statement.evaluate()
                } finally {
                    systemAnimations.enableAll()
                }
            }
        }
    }
}