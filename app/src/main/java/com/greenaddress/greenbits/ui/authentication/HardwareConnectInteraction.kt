package com.greenaddress.greenbits.ui.authentication

import android.content.Context
import com.blockstream.DeviceBrand
import com.blockstream.gdk.data.Network
import com.greenaddress.greenapi.HWWalletBridge
import com.greenaddress.greenapi.Session
import com.greenaddress.greenapi.data.NetworkData
import com.greenaddress.greenbits.wallets.FirmwareInteraction
import io.reactivex.rxjava3.core.Single

interface HardwareConnectInteraction : FirmwareInteraction, HWWalletBridge {
    fun context(): Context
    fun showInstructions(resId: Int)
    fun getSession(): Session
    fun showError(error: String)
    fun getConnectionNetwork(): Network
    fun onLoginSuccess()

    fun requestPin(deviceBrand: DeviceBrand): Single<String>
}