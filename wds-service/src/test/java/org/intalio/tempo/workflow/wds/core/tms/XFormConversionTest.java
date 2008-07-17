package org.intalio.tempo.workflow.wds.core.tms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;

import org.intalio.tempo.workflow.wds.core.xforms.XFormsConverter;
import org.junit.runner.RunWith;

import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Specification;


@RunWith(InstinctRunner.class)
public class XFormConversionTest {

    String[]  RESOURCES = new String[] {"/ViewUpload.xform", "/schemaType.xform"};
    
    @Specification
    public void runTheXFormConversionWithReader() throws Exception {
        for(String r : RESOURCES) {
            URL url = XFormConversionTest.class.getResource(r);
            XFormsConverter.fixStyle(new FileReader(url.getFile()));    
        }
    }
    
    @Specification
    public void runTheXFormConversionWithByteArray() throws Exception {
        for(String r : RESOURCES) {
            URL url = XFormConversionTest.class.getResource(r);
            BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while((line = reader.readLine())!=null) {sb.append(line);}
            XFormsConverter.fixStyle(sb.toString().getBytes());    
        }
    }
}
