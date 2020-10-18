package net.ngeor.webtail;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for {@link RemoteFile}.
 * Created by ngeor on 15/6/2017.
 */
public class RemoteFileTest {

    private static final Credentials CREDENTIALS = new Credentials("user", "password");

    @Test
    public void getUrl() throws Exception {
        String url = "some url";
        RemoteFile file = new RemoteFile(url, CREDENTIALS);
        assertEquals(url, file.getUrl());
    }

    @Test
    public void setUrl() throws Exception {
        RemoteFile file = new RemoteFile("a url", CREDENTIALS);
        String newUrl = "new url";
        file.setUrl(newUrl);
        assertEquals(newUrl, file.getUrl());
    }

    @Test
    public void getCredentials() throws Exception {
        RemoteFile file = new RemoteFile("some other url", CREDENTIALS);
        assertEquals(CREDENTIALS, file.getCredentials());
    }

    @Test
    public void setCredentials() throws Exception {
        RemoteFile file = new RemoteFile("yet another url", CREDENTIALS);
        Credentials otherCredentials = new Credentials("other user", "other password");
        file.setCredentials(otherCredentials);
        assertEquals(otherCredentials, file.getCredentials());
    }
}
