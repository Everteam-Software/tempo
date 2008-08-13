package org.intalio.tempo.workflow.wds.core.tms;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import org.intalio.tempo.workflow.wds.core.xforms.XFormsConverter;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Specification;

@RunWith(InstinctRunner.class)
public class XFormConversionTest {

    String[] RESOURCES = new String[] { "/personnel.xform" };
    final static ExpectThat expect = new ExpectThatImpl();

    Pattern[] patterns = new Pattern[] { Pattern.compile(".*width:.*px;"), Pattern.compile(".*height:.*px;"), Pattern.compile(".*top:.*px;") };

    @Specification
    public void runTheXFormConversionWithReader() throws Exception {
        for (String r : RESOURCES) {
            URL url = XFormConversionTest.class.getResource(r);
            String sb = new String(XFormsConverter.fixStyle(new FileReader(url.getFile())));
            matchesPatterns(sb);
        }
    }

    @Specification
    public void runTheXFormConversionWithByteArray() throws Exception {
        for (String r : RESOURCES)
            matchesPatterns(convert(r));
    }

    @Specification
    public void runConversionOnCustomStyleForm() throws Exception {
        String converted = convert("/selectItem.xform");
        matchesPatterns(converted);
    }

    @Specification
    public void runXFromAndCheckForVerticalAlign() throws Exception {
        expect.that(convert("/personnel.xform").toString().contains("vertical-align:40%")).isTrue();
    }

    private void matchesPatterns(String sb) {
        for (Pattern p : patterns)
            expect.that(p.matcher(sb));
    }

    private String convert(String r) throws FileNotFoundException, IOException {
        URL url = XFormConversionTest.class.getResource(r);
        BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return new String(XFormsConverter.fixStyle(sb.toString().getBytes()));
    }
}
