package org.intalio.tempo.persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

public class XFormsConverter {

    final static String[] KEYWORDS = new String[] { "top", "left", "width", "height", "margin", "padding" };
    final static List<String> KEYWORDSLIST = Arrays.asList(KEYWORDS);

    @SuppressWarnings("unchecked")
    public static byte[] convert(byte[] payload) throws Exception {
        ByteArrayReader byteArrayReader = new ByteArrayReader(payload);
        return convert(byteArrayReader);
    }

    @SuppressWarnings("unchecked")
    /**
     * Basically just adding "px" to measurement values, of the style of the xform
     */
    public static byte[] convert(Reader byteArrayReader) throws IOException, DocumentException, UnsupportedEncodingException {
        BufferedReader r = new BufferedReader(byteArrayReader);
        r.readLine(); // skip xml line
        SAXReader xmlReader = new SAXReader();
        Document doc = xmlReader.read(r);
        XPath xpathSelector = DocumentHelper.createXPath("/xhtml:html/xhtml:head/xhtml:style");
        List<Element> results = (List<Element>) xpathSelector.selectNodes(doc);
        Element element = (Element) results.get(0);
        String style = element.getStringValue();
        BufferedReader sr = new BufferedReader(new StringReader(style));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while ((line = sr.readLine()) != null) {
            int start = line.indexOf("{");
            String elem = line.substring(0, start);
            sb.append(elem);
            sb.append("{");
            String stile = line.substring(start + 1, line.lastIndexOf("}"));
            StringTokenizer st = new StringTokenizer(stile, ";");
            while (st.hasMoreTokens()) {
                String att = st.nextToken();
                int dotdot = att.indexOf(':') + 1;
                if (dotdot > 1) {
                    String left = att.substring(0, dotdot - 1).trim();
                    String right = att.substring(dotdot).trim();
                    if (KEYWORDSLIST.contains(left)) {
                        if (!right.endsWith("px"))
                            right += "px";
                    }
                    sb.append(left + ":" + right);
                } else {
                    sb.append(att);
                }
                sb.append(";");
            }
            sb.append("}");
            sb.append(System.getProperty("line.separator"));
        }
        element.setText(sb.toString());
        return doc.asXML().getBytes("UTF-8");
    }

    public static void main(String[] args) throws Exception {
        FileReader fr = new FileReader("/Users/niko/Desktop/UploadFileForm.xform");
        convert(fr);
    }
}
