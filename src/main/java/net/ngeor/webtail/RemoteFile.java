package net.ngeor.webtail;

/**
 * @author ngeor
 */
class RemoteFile {
    private String url;
    private Credentials credentials;

    RemoteFile(String url, Credentials credentials) {
        this.url = url;
        this.credentials = credentials;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

}
