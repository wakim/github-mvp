package br.com.github.sample.dagger

import android.content.Context
import br.com.github.sample.ui.UIComponent

abstract class Injector private constructor() {

    init {
        throw AssertionError("No instances.")
    }

    companion object {

        private val APP_COMPONENT_SERVICE = "com.app.id.AppComponent"
        private val UI_COMPONENT_SERVICE = "br.com.github.sample.ui.UIComponent"

        @SuppressWarnings("WrongConstant")
        fun obtainAppComponent(context: Context): AppComponent {
            return context.applicationContext.getSystemService(APP_COMPONENT_SERVICE) as AppComponent
        }

        @SuppressWarnings("WrongConstant")
        fun obtainUIComponent(context: Context) =
                context.getSystemService(UI_COMPONENT_SERVICE) as UIComponent

        fun matchesAppComponentService(name: String) = APP_COMPONENT_SERVICE == name

        fun matchesUIComponentService(name: String) = UI_COMPONENT_SERVICE == name
    }
}
