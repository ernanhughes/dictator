package com.banba.dictator.ui;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Ernan on 18/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultExceptionHandler;

    // constructor
    public DefaultExceptionHandler(Thread.UncaughtExceptionHandler pDefaultExceptionHandler) {
        defaultExceptionHandler = pDefaultExceptionHandler;
    }

    // Default exception handler
    public void uncaughtException(Thread t, Throwable e) {
        // Here you should have a more robust, permanent record of problems
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        try {
            // Random number to avoid duplicate files
            Random generator = new Random();
            int random = generator.nextInt(99999);
            // Embed version in stacktrace filename
            String filename = G.APP_VERSION + "-" + Integer.toString(random);
            L.d("Writing unhandled exception to: " + G.FILES_PATH + "/" + filename + ".stacktrace");
            // Write the stacktrace to disk
            BufferedWriter bos = new BufferedWriter(new FileWriter(G.FILES_PATH + "/" + filename + ".stacktrace"));
            bos.write(G.ANDROID_VERSION + "\n");
            bos.write(G.PHONE_MODEL + "\n");
            bos.write(result.toString());
            bos.flush();
            // Close up everything
            bos.close();
        } catch (Exception ebos) {
            // Nothing much we can do about this - the game is over
            ebos.printStackTrace();
        }
        L.d(result.toString());
        //call original handler
        defaultExceptionHandler.uncaughtException(t, e);
    }

    static class G {
        // This must be set by the application - it used to automatically
        // transmit exceptions to the trace server
        public static String FILES_PATH = null;
        public static String APP_VERSION = "unknown";
        public static String APP_PACKAGE = "unknown";
        public static String PHONE_MODEL = "unknown";
        public static String ANDROID_VERSION = "unknown";
        // Where are the stack traces posted?
        public static String URL = "http://banba.ca";
        public static String TraceVersion = "0.3.0";
    }

    static class ExceptionHandler {
        private static String[] stackTraceFileList = null;

        public static boolean register(Context context) {
            L.i("Registering default exceptions handler");
            // Get information about the Package
            PackageManager pm = context.getPackageManager();
            try {
                PackageInfo pi;
                // Version
                pi = pm.getPackageInfo(context.getPackageName(), 0);
                G.APP_VERSION = pi.versionName;
                // Package name
                G.APP_PACKAGE = pi.packageName;
                // Files dir for storing the stack traces
                G.FILES_PATH = context.getFilesDir().getAbsolutePath();
                // Device model
                G.PHONE_MODEL = android.os.Build.MODEL;
                // Android version
                G.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            L.i("TRACE_VERSION: " + G.TraceVersion);
            L.d("APP_VERSION: " + G.APP_VERSION);
            L.d("APP_PACKAGE: " + G.APP_PACKAGE);
            L.d("FILES_PATH: " + G.FILES_PATH);
            L.d("URL: " + G.URL);

            boolean stackTracesFound = false;
            // We'll return true if any stack traces were found
            if (searchForStackTraces().length > 0) {
                stackTracesFound = true;
            }

            new Thread() {
                @Override
                public void run() {
                    // First of all transmit any stack traces that may be lying around
                    submitStackTraces();
                    UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
                    if (currentHandler != null) {
                        L.d("current handler class=" + currentHandler.getClass().getName());
                    }
                    // don't register again if already registered
                    if (!(currentHandler instanceof DefaultExceptionHandler)) {
                        // Register default exceptions handler
                        Thread.setDefaultUncaughtExceptionHandler(
                                new DefaultExceptionHandler(currentHandler));
                    }
                }
            }.start();

            return stackTracesFound;
        }

        public static void register(Context context, String url) {
            L.i("Registering default exceptions handler: " + url);
            // Use custom URL
            G.URL = url;
            // Call the default register method
            register(context);
        }


        /**
         * Search for stack trace files.
         *
         * @return
         */
        private static String[] searchForStackTraces() {
            if (stackTraceFileList != null) {
                return stackTraceFileList;
            }
            File dir = new File(G.FILES_PATH + "/");
            // Try to create the files folder if it doesn't exist
            dir.mkdir();
            // Filter for ".stacktrace" files
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".stacktrace");
                }
            };
            return (stackTraceFileList = dir.list(filter));
        }

        /**
         * Look into the files folder to see if there are any "*.stacktrace" files.
         * If any are present, submit them to the trace server.
         */
        public static void submitStackTraces() {
            try {
                L.d("Looking for exceptions in: " + G.FILES_PATH);
                String[] list = searchForStackTraces();
                if (list != null && list.length > 0) {
                    L.d("Found " + list.length + " stacktrace(s)");
                    for (String aList : list) {
                        String filePath = G.FILES_PATH + "/" + aList;
                        // Extract the version from the filename: "packagename-version-...."
                        String version = aList.split("-")[0];
                        L.d("Stack trace in file '" + filePath + "' belongs to version " + version);
                        // Read contents of stacktrace
                        StringBuilder contents = new StringBuilder();
                        BufferedReader input = new BufferedReader(new FileReader(filePath));
                        String line = null;
                        String androidVersion = null;
                        String phoneModel = null;
                        while ((line = input.readLine()) != null) {
                            if (androidVersion == null) {
                                androidVersion = line;
                                continue;
                            } else if (phoneModel == null) {
                                phoneModel = line;
                                continue;
                            }
                            contents.append(line);
                            contents.append(System.getProperty("line.separator"));
                        }
                        input.close();
                        String stackTrace = contents.toString();
                        L.d("Transmitting stack trace: " + stackTrace);
                        // Transmit stack trace with POST request
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(G.URL);
                        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                        nvps.add(new BasicNameValuePair("package_name", G.APP_PACKAGE));
                        nvps.add(new BasicNameValuePair("package_version", version));
                        nvps.add(new BasicNameValuePair("phone_model", phoneModel));
                        nvps.add(new BasicNameValuePair("android_version", androidVersion));
                        nvps.add(new BasicNameValuePair("stack_trace", stackTrace));
                        httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                        // We don't care about the response, so we just hope it went well and on with it
                        httpClient.execute(httpPost);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    String[] list = searchForStackTraces();
                    for (String aList : list) {
                        File file = new File(G.FILES_PATH + "/" + aList);
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
