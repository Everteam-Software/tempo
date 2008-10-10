package org.intalio.tempo.workflow.wds.servlets;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

public class FackServletInputStream extends ServletInputStream {

    private InputStream _is;

    public FackServletInputStream(InputStream is) throws IOException {
        _is = is;
    }

    @Override
    public int read() throws IOException {
        return _is.read();
    }

}
