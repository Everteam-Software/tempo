package org.intalio.tempo.workflow.tas.sling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.workflow.tas.core.AttachmentMetadata;
import org.intalio.tempo.workflow.tas.core.StorageStrategy;
import org.intalio.tempo.workflow.tas.core.UnavailableAttachmentException;

public class FileSystemStorageStrategy implements StorageStrategy {

    private String path = "/Users/niko/Sites";
    private String publicPath = "http://localhost/~niko/";

    public void deleteAttachment(Property[] props, String url) throws UnavailableAttachmentException {
        if (url.startsWith(url)) {
            String localUrl = url.substring(publicPath.length() + 1);
            File localFile = new File(path + File.separator + localUrl);
            localFile.delete();
        }

    }

    public String storeAttachment(Property[] properties, AttachmentMetadata metadata, InputStream payload) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(path + File.separator + metadata.getFilename()));
        IOUtils.copy(payload, fos);
        return publicPath + File.separator + metadata.getFilename();
    }

}
