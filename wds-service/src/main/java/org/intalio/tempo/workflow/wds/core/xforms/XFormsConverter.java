package org.intalio.tempo.workflow.wds.core.xforms;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.intalio.tempo.workflow.wds.core.Item;

public class XFormsConverter {

    final static String[] KEYWORDS = new String[] { "top", "left", "width", "height", "margin", "padding" };
    final static List<String> KEYWORDSLIST = Arrays.asList(KEYWORDS);

    @SuppressWarnings("unchecked")
    public static byte[] fixStyle(byte[] payload) {
        return fixStyle(new BufferedReader(new StringReader(new String(payload))));
    }

    @SuppressWarnings("unchecked")
    /**
     * Basically just adding "px" to measurement values, of the style of the
     * xform
     */
    public static byte[] fixStyle(Reader byteArrayReader) {
        try {
            BufferedReader r = new BufferedReader(byteArrayReader);

            // workaround for the usual content in prolog. I love java.
            char[] buffer = new char[1];
            for (int i = 0; i < 10; i++) {
                r.mark(1);
                r.read(buffer);
                if (buffer[0] == '<') {
                    r.reset();
                    break;
                }
            }

            SAXReader xmlReader = new SAXReader();
            Document doc = xmlReader.read(r);
            XPath xpathSelector = DocumentHelper.createXPath("/xhtml:html/xhtml:head/xhtml:style");
            List<Element> results = (List<Element>) xpathSelector.selectNodes(doc);
            // don't waste time if no style define in the headers.
            if (results.size() <= 0)
                return doc.asXML().getBytes("UTF-8");
            Element element = (Element) results.get(0);
            String style = element.getStringValue();
            BufferedReader sr = new BufferedReader(new StringReader(style));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = sr.readLine()) != null) {
                int indexOf = 0;
                String line2 = line;
                while((indexOf = line.indexOf("}"))>1) {
                    line2 = line.substring(0,indexOf+1);
                    sb.append(convertLine(line2));
                    line = line.substring(indexOf+1);
                }
            }
            element.setText(sb.toString());
            return doc.asXML().getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not apply new style to xform");
        }

    }

    private static StringBuffer convertLine(String line) {
        StringBuffer sb = new StringBuffer();
        int start = line.indexOf("{");
        // if we can't parse this properly, let's return silently.
        if(start == -1) return sb.append(line);
        
        String elem = line.substring(0, start).trim();
        sb.append(elem);
        sb.append("{");
        String stile = line.substring(start + 1, line.lastIndexOf("}"));
        StringTokenizer st = new StringTokenizer(stile, ";");
        while (st.hasMoreTokens()) {
            String att = st.nextToken();
            int dotdot = att.indexOf(':') + 1;
            if (dotdot > 1) {
                String cssProperty = att.substring(0, dotdot - 1).trim();
                String right = att.substring(dotdot).trim();
                // fix size values, append px where needed
                if (KEYWORDSLIST.contains(cssProperty)) {
                    if (!right.endsWith("px"))
                        right += "px";
                }
                
                // fix vertical-align in tables, top behaves differently depending on the browser
                // set to a % value, as recommended on some CSS web sites.
                
//                if (elem.endsWith("table") || elem.endsWith("table tr") || elem.endsWith("table tr td")) {
                    if (cssProperty.contains("vertical-align") && (right.contains("top"))) {
                        sb.append(cssProperty + ":40%");
                    }
//                } 
            else {
                    sb.append(cssProperty + ":" + right);
                }
                
            } else {
                sb.append(att);
            }
            sb.append(";");
        }
        sb.append("}");
        sb.append("\n");
        return sb;
    }

    public static Item fixStyle(Item item) {
        return new Item(item.getURI(), item.getContentType(), fixStyle(item.getPayload()));
    }
}
