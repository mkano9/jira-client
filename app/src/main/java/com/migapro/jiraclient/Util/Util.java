package com.migapro.jiraclient.Util;

import android.util.Base64;

public class Util {

    public static String generateBase64Credentials() {
        String username = "";
        String password = "";

        String credentials = username + ":" + password;
        String basicAuth =
                "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return basicAuth;
    }
}
