package com.rikoriko.rctst.utils;

import android.app.Application;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.rikoriko.rctst.Interface.AppsFlyerData;
import com.rikoriko.rctst.common.GlobalVariable;

import java.util.Map;

public class AppsFlyer extends Application {
    public static final String AF_DEV_KEY = GlobalVariable.INSTANCE.getAF_DEV_KEY();
    public static AppsFlyer AppsFlyer;

    @Override
    public void onCreate() {
        super.onCreate();
        AppsFlyer = this;
    }

    public void appsFlyer(AppsFlyerData appsFlyerData) {
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {

            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {

                if(conversionData.get(GlobalVariable.INSTANCE.getAf_status()).equals(GlobalVariable.INSTANCE.getNon_organic())) {
                    String campaign = conversionData.get(GlobalVariable.INSTANCE.getCampaign()).toString();
                    appsFlyerData.getCampaign(campaign);

                    //Log.d("MyLog", "campaing: " + campaign);
                } else {
                    appsFlyerData.getCampaign("");
                }

                /*for (String attrName : conversionData.keySet()) {
                    Log.d("LOG_TAG", "attribute: " + attrName + " = " + conversionData.get(attrName));
                }*/
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                //Log.d("LOG_TAG", "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                for (String attrName : attributionData.keySet()) {
                    // Log.d("LOG_TAG", "attribute: " + attrName + " = " + attributionData.get(attrName));
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                //Log.d("LOG_TAG", "error onAttributionFailure : " + errorMessage);
            }
        };

        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, this);
        AppsFlyerLib.getInstance().start(this);
        AppsFlyerLib.getInstance().setDebugLog(true);

    }
}
