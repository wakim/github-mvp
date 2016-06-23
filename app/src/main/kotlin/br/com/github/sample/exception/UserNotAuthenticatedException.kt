package br.com.github.sample.exception

class UserNotAuthenticatedException: Exception() {
    companion object {
        @SuppressWarnings("ThrowableInstanceNeverThrown")
        val INSTANCE = UserNotAuthenticatedException()
    }
}