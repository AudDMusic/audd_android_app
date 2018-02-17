package anton.peer_id.network;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import anton.peer_id.audd.Application;


public class Network {

    private static final String TAG = "NetworkLog";

    public static final String USER_AGENT;

    static {
        ArrayMap<String, String> agentData = new ArrayMap<>();
        agentData.put("Language", Application.lang);
        agentData.put("Android", String.valueOf(Build.VERSION.RELEASE));
        agentData.put("Android SDK", String.valueOf(Build.VERSION.SDK_INT));
        agentData.put("Architecture", String.valueOf(Build.CPU_ABI));
        agentData.put("Phone", String.valueOf(Build.MANUFACTURER) + " (" + String.valueOf(Build.MODEL) + ")");
        agentData.put("Application Version", Application.version + " (" + Application.build_number + ")");
        agentData.put("Package Name", Application.packageName);
        List<String> data = new ArrayList<>();
        for(Map.Entry<String, String> entry : agentData.entrySet()) {
            data.add(entry.getKey() + " " + entry.getValue());
        }
        Log.e(TAG, String.valueOf(data));
        USER_AGENT = "Audd.io Android App {" + TextUtils.join("; ", data) + "}";
        Log.e(TAG, "User-Agent: " + USER_AGENT);
    }

    static HttpURLConnection createHttpConnection(String httpURL, long length) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(httpURL).openConnection();
        httpConnection.addRequestProperty("User-Agent", USER_AGENT);
        httpConnection.setRequestProperty("Connection", "Keep-Alive");
        httpConnection.addRequestProperty("Accept-Encoding", "gzip");
        if (length != 0) {
            httpConnection.setFixedLengthStreamingMode(length);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setDoInput(true);
        }
        httpConnection.setConnectTimeout(25000);
        httpConnection.setReadTimeout(25000);
        return httpConnection;
    }

    static String getRequest(String httpURL, String body, int attempt) {
        attempt++;
        HttpURLConnection connection = null;
        try {
            connection = Network.createHttpConnection(httpURL + "?" + body, 0);
            InputStream is = new BufferedInputStream(connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream(), 8192);
            String enc = connection.getHeaderField("Content-Encoding");
            if (enc != null && enc.equalsIgnoreCase("gzip")) {
                is = new GZIPInputStream(is);
            }
            String result = streamToString(is);
            if (TextUtils.isEmpty(result) && 7 > attempt) {
                return getRequest(httpURL, body, attempt);
            }
            return result;
        } catch (Throwable e) {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Throwable ignored){}
            }
            if (e instanceof InterruptedIOException) {
                return null;
            }
            if (attempt >= 7) {
                return null;
            }
            return getRequest(httpURL, body, attempt);
        }
    }

    static String postRequest(String httpURL, String body, int attempt) {
        attempt++;
        HttpURLConnection connection = null;
        try {
            connection = Network.createHttpConnection(httpURL, body.getBytes().length);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.close();
            connection.connect();
            InputStream is = new BufferedInputStream(connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream(), 8192);
            String enc = connection.getHeaderField("Content-Encoding");
            if (enc != null && enc.equalsIgnoreCase("gzip")) {
                is = new GZIPInputStream(is);
            }
            String result = streamToString(is);
            if (TextUtils.isEmpty(result) && 7 > attempt) {
                return postRequest(httpURL, body, attempt);
            }
            return result;
        } catch (Throwable e) {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Throwable ignored){}
            }
            if (e instanceof InterruptedIOException) {
                return null;
            }
            if (attempt >= 7) {
                return null;
            }
            return postRequest(httpURL, body, attempt);
        }
    }

    static String uploadFile(String httpURL, File file, String valueName, int attempt) {
        attempt++;
        final String boundary = "----AppFormBoundary" + new Random().nextInt();
        final UploadFile fileUpload = new UploadFile(file, boundary, valueName);
        HttpURLConnection connection = null;
        try {
            connection = Network.createHttpConnection(httpURL, fileUpload.getContentLength());
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Content-Length", fileUpload.getContentLength() + "");
            connection.setRequestMethod("POST");
            OutputStream os = connection.getOutputStream();
            fileUpload.writeTo(os);
            os.close();
            connection.connect();
            InputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
            String enc = connection.getHeaderField("Content-Encoding");
            if (enc != null && enc.equalsIgnoreCase("gzip")) {
                is = new GZIPInputStream(is);
            }
            return streamToString(is);
        } catch (Throwable e) {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Throwable ignored){}
            }
            if (e instanceof InterruptedIOException) {
                return null;
            }
            if (attempt >= 7) {
                return null;
            }
            return uploadFile(httpURL, file, valueName, attempt);
        }
    }

    private static String streamToString(InputStream is) throws IOException {
        InputStreamReader r = new InputStreamReader(is);
        StringWriter sw = new StringWriter();
        char[] buffer = new char[1024 * 8];
        try {
            for (int n; (n = r.read(buffer)) != -1;)
                sw.write(buffer, 0, n);
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {}
        }
        return String.valueOf(sw);
    }
}
