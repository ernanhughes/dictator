package programmer.ie.dictator.util;

import android.os.Bundle;

/**
 * Created by Ernan on 28/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class BundleUtil {
    public static String toString(Bundle bundle) {
        StringBuilder buf = new StringBuilder("Bundle {");
        if (null != bundle) {
            for (String key : bundle.keySet()) {
                buf.append(" ")
                        .append(key)
                        .append(" => ")
                        .append(bundle.get(key))
                        .append(";");
            }
        } else {
            buf.append(" null ");
        }
        buf.append(" }");
        return buf.toString();
    }
}
