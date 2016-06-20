package br.com.github.sample.controller

import br.com.github.sample.api.ApiService
import br.com.github.sample.application.Application

class ApiController(app: Application, var apiService: ApiService, preferencesManager: PreferencesManager): BaseController(app, preferencesManager) {

}