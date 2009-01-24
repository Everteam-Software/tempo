package org.intalio.tempo.workflow.tas.core;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TASUtil {

    /**
     * Sanitize filename for inclusion into URL. e.g. C:\Foo -> Foo My
     * Document.doc -> My+Document.doc
     */
    public static String sanitize(String filename) {
        // find the local name: the last portion of the filename that does not
        // contain ":", "/" or "\"
        Pattern regex = Pattern.compile("(.*?)([^:/\\\\]+)$");
        Matcher match = regex.matcher(filename);
        if (match.find()) {
            try {
                String localname = match.group(2);
                return URLEncoder.encode(localname, "UTF-8");
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        } else
            throw new IllegalArgumentException("Invalid filename: " + filename);
    }

    /**
     * Replace localhost by a proper host name, otherwise the attachment could
     * not be accessed.
     */
    public static String filterLocalhost(String endpoint) {
        try {
            URL url = new URL(endpoint);
            if (url.getHost().equalsIgnoreCase("localhost")) {
                InetAddress[] list = InetAddress.getAllByName(InetAddress.getLocalHost().getHostAddress());
                if (list.length > 0) {
                    URL filtered = new URL(url.getProtocol(), list[0].getHostName(), url.getPort(), url.getFile());
                    return filtered.toExternalForm();
                }
            }
            return endpoint;
        } catch (Exception e) {
            // if we are here, that means either:
            // 1. the url for the endpoint is not a valid url
            // 2 the url in the config file has a host set to localhost, but an
            // exception happened while
            // trying to find the hostname of the machine.
            throw new RuntimeException(e);
        }
    }

}