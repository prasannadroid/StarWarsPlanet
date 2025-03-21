package com.assesment.starwarsplanet.core.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import javax.inject.Inject

/**
 * Checks if the device has an active internet connection.
 * It uses the `ConnectivityManager` to check network capabilities.
 *
 * @return true if there is an active internet connection, false otherwise.
 */
class NetworkService @Inject constructor(private val context: Context) {
    fun isInternetAvailable(): Boolean {
        // Get the ConnectivityManager system service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Check if the device is running on Android M (API 23) or later
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get the currently active network (if any)
            val network = connectivityManager.activeNetwork ?: return false

            // Get the network capabilities (like internet availability)
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            // Check if the active network has internet capabilities
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            // For devices below Android M (API 23), use the deprecated method
            // Get the network information (if any) and check if it's connected
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }
}