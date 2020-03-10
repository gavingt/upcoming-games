package com.gavinsappcreations.upcominggames.network

sealed class NetworkState {

    // Network calls were successful.
    object Success : NetworkState()

    // Network error has occurred.
    object Failure : NetworkState()

/*    // Network is actively loading. The "progress" value can be either 0 or 1.
    class NetworkLoading(val progress: Int) : NetworkState()*/

    object LoadingSortChange: NetworkState()

    object Loading: NetworkState()
}