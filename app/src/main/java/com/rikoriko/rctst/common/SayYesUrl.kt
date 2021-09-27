package com.rikoriko.rctst.common

import com.rikoriko.rctst.Interface.SayYesServices
import com.rikoriko.rctst.retrofit.SayYesClient

object SayYesUrl {
    private val SAYYES_URL = GlobalVariable.SayYes_URL
    val sayYesServices: SayYesServices
        get() = SayYesClient.getSayYesClient(SAYYES_URL).create(SayYesServices::class.java)
}