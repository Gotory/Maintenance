package com.dmt_winches.maintenance.Common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequest {
    private HashMap<String, String> postParams;
    private String postURL;
    private String response;

    public HttpRequest(HashMap<String, String> postParams, String url) {
        this.postParams = postParams;
        this.postURL = url;
        response = "";
    }

    public String connect() {
        BufferedReader br = null;
        HttpsURLConnection conn = null;
//        postParams.put(VipconnConstants.JSON_MOBILE, VipconnConstants.OS);
//        postParams.put(VipconnConstants.JSON_OS, VipconnConstants.OS);

        try {
            URL url = new URL(postURL);
            conn = (HttpsURLConnection) url.openConnection();
//            conn.setReadTimeout(VipconnConstants.HTTP_READ_TIMEOUT);
//            conn.setConnectTimeout(VipconnConstants.CONNECTION_TIME_OUT);
//            conn.setRequestMethod(VipconnConstants.POST);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write(VipconnHelper.getInstance().getPostDataString(postParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
//                response = VipconnConstants.JSON_CONNECTION_ERROR;
//                if(VipconnConstants.DBG) Log.d(VipconnConstants.TAG, "PostResponseAsyncTask " + responseCode);
            }

        } catch (ProtocolException e) {
//            if(VipconnConstants.DBG) Log.d(VipconnConstants.TAG, "HttpConnection: protocol exception " + e.toString());
//            response = VipconnConstants.JSON_CONNECTION_ERROR;
        } catch (MalformedURLException e) {
//            if(VipconnConstants.DBG) Log.d(VipconnConstants.TAG, "HttpConnection: malformed url exception " + e.toString());
//            response = VipconnConstants.JSON_CONNECTION_ERROR;
        } catch (IOException e) {
//            if(VipconnConstants.DBG) Log.d(VipconnConstants.TAG, "HttpConnection: I/O exception " + e.toString());
//            response = VipconnConstants.JSON_CONNECTION_ERROR;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
//                    if(VipconnConstants.DBG) Log.d(VipconnConstants.TAG, "HttpConnection: I/O exception " + e.toString());
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response;
    }
}
