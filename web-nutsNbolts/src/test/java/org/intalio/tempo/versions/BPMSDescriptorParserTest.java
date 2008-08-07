package org.intalio.tempo.versions;

import java.io.PrintWriter;

import org.junit.runner.RunWith;

import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Specification;

@RunWith(InstinctRunner.class)
public class BPMSDescriptorParserTest {

    final String path = this.getClass().getResource("/bpms.json").getPath();

    @Specification
    public void printAsHtml() throws Exception {
        PrintWriter pw = new PrintWriter(System.out);
        BpmsDescriptorParser bdp = new BpmsDescriptorParser(path);
        bdp.getBpmsVersionsAsHtml(pw);
    }

    @Specification
    public void getVersionAndBuildNumber() throws Exception {
        BpmsDescriptorParser bdp = new BpmsDescriptorParser(path);
        System.out.println(bdp.addBpmsBuildVersionsPropertiesToMap(null));
    }
    
    @Specification
    public void getVersionAndBuildNumberDoNotCrashWhenNoVersionFound() {
        BpmsDescriptorParser bdp = new BpmsDescriptorParser("/blahblah.hson");
    }
}
