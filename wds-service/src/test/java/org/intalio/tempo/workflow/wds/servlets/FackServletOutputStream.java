package org.intalio.tempo.workflow.wds.servlets;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FackServletOutputStream extends ServletOutputStream{
    private static final Logger _logger = LoggerFactory.getLogger(FackServletOutputStream.class);
    
    @Override
    public void write(int b) throws IOException {
        _logger.debug("In the output stream:" + Integer.toString(b));
    }

}
