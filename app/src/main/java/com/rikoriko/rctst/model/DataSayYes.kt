package com.rikoriko.rctst.model

import androidx.annotation.Keep

@Keep
data class DataSayYes (
        var success: Int? = null,
        var reg: Int? = null,
        var dep: Int? = null
)