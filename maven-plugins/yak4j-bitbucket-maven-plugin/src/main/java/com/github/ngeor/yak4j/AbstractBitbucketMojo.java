package com.github.ngeor.yak4j;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base class for Bitbucket mojos.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractBitbucketMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.version}")
    private String version;

    /**
     * The username to access the Bitbucket REST API.
     */
    @Parameter(required = true)
    private String username;

    /**
     * The password to access the Bitbucket REST API.
     */
    @Parameter(required = true)
    private String password;

    /**
     * The owner of the repository.
     */
    @Parameter(required = true)
    private String owner;

    /**
     * The slug of the repository.
     */
    @Parameter(required = true)
    private String slug;

    /**
     * The git SHA of the current commit.
     */
    @Parameter(required = true)
    private String hash;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Log log = getLog();
        RestClient restClient = new RestClientImpl();
        BitbucketApiImpl api = new BitbucketApiImpl(restClient);
        doExecute(restClient, api, log);
    }

    protected abstract void doExecute(RestClient restClient, BitbucketApi api, Log log)
        throws MojoFailureException, MojoExecutionException;
}
