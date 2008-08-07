package org.intalio.tempo.versions;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.noggit.JSONParser;
import org.apache.noggit.ObjectBuilder;
import org.intalio.tempo.uiframework.versions.BpmsVersionsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BpmsDescriptorParser {
    private static final String BUILD_NUMBER = "build";
    private static final String BPMS_VERSION = "version";
    private static final String TEMPO_CONFIG_DIRECTORY = "org.intalio.tempo.configDirectory";
    private static final String RELEASE_DESC_FILE = "release-desc.json";
    private static final String tempoConfigPath = System.getProperty(TEMPO_CONFIG_DIRECTORY) + '/' + RELEASE_DESC_FILE;
    private static final Logger LOGGER = LoggerFactory.getLogger(BpmsVersionsServlet.class);
    private HashMap<?, ?> bpmsVersions = new HashMap();

    public BpmsDescriptorParser() {
        getBPMSVersionsProperties(tempoConfigPath);
    }

    public BpmsDescriptorParser(String path) {
        getBPMSVersionsProperties(path);
    }

    private void getBPMSVersionsProperties(String path) {
        try {
            FileReader r = new FileReader(path);
            JSONParser parser = new JSONParser(r);
            bpmsVersions = (LinkedHashMap) ObjectBuilder.getVal(parser);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(bpmsVersions.toString());
            }
        } catch (IOException e) {
            LOGGER.error("Could find versioning information:"+e.getMessage());
        }
    }

    private void iterateOverSubComponents(PrintWriter out, HashMap c) {
        ArrayList components = (ArrayList) c.get("components");
        if (components == null || components.size() == 0)
            return;
        out.write("<ul>");
        Iterator iter = components.iterator();
        while (iter.hasNext()) {
            LinkedHashMap subc = (LinkedHashMap) iter.next();
            out.write("<li>" + subc.get("name"));
            if (subc.containsKey("components"))
                iterateOverSubComponents(out, subc);
            else {
                String v = (String) subc.get(BPMS_VERSION);
                if (v != null)
                    out.write(":" + v);
                out.write("</li>");
            }
            out.write("</li>");
        }
        out.write("</ul>");
    }

    public Map addBpmsBuildVersionsPropertiesToMap(Map map) {
        map = (map != null) ? map : new HashMap();
        if (!bpmsVersions.isEmpty()) {
            String version = (String) bpmsVersions.get(BPMS_VERSION);
            int lastIndexOf = version.lastIndexOf('.');
            String bpmsVersion = version.substring(0, lastIndexOf);
            String buildNumber = version.substring(lastIndexOf + 1);
            map.put(BPMS_VERSION, bpmsVersion);
            map.put(BUILD_NUMBER, buildNumber);
        }
        return map;
    }

    public void getBpmsVersionsAsHtml(PrintWriter out) {
        Map map = addBpmsBuildVersionsPropertiesToMap(null);
        out.write("<html><body>");
        out.write("<h1>Intalio|BPMS ("+map.get(BPMS_VERSION)+"), Build ("+map.get(BUILD_NUMBER)+")</h1>");
        out.write("<h2>Components Versions</h2>");
        iterateOverSubComponents(out, bpmsVersions);
        out.write("</body></html>");
        out.flush();
    }
}
