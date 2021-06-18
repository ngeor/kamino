package com.github.ngeor.yak4jcli;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Gets information from a remote Maven repository.
 */
public class RemoteRepo {
    private static final int TIMEOUT = 5_000;
    private static final int BAD_REQUEST = 400;

    /**
     * Gets the most recently published version of an artifact.
     */
    public String getLatestPublishedVersion(String groupId, String artifactId) throws IOException {
        String url = "https://repo1.maven.org/maven2/"
            + groupId.replace('.', '/') + "/" + artifactId + "/maven-metadata.xml";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            int status = connection.getResponseCode();
            if (status >= BAD_REQUEST) {
                throw new IOException("Error: " + status);
            }
            try (InputStream inputStream = connection.getInputStream()) {
                DocumentWrapper document = DocumentWrapper.parse(inputStream);
                ElementWrapper metadataElement = document.getDocumentElement();
                ElementWrapper versioning = metadataElement.firstElement("versioning").orElse(null);
                if (versioning != null) {
                    return versioning.firstElementText("latest");
                } else {
                    return null;
                }
            }
        } finally {
            connection.disconnect();
        }
    }
}
