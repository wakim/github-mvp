package br.com.github.sample.activity

import android.os.Bundle
import br.com.github.sample.R
import br.com.github.sample.controller.ApiController
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var apiController: ApiController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }
}
