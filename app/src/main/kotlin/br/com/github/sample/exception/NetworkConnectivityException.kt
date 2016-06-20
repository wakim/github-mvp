package br.com.github.sample.exception

class NetworkConnectivityException : Exception() {
    companion object {
        @SuppressWarnings("ThrowableInstanceNeverThrown")
        val INSTANCE = NetworkConnectivityException()
    }
}
