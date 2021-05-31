package com.github.ngeor.yak4j;

import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import static com.github.ngeor.yak4j.Util.versionToTag;

/**
 * A mojo that publishes git tags to Bitbucket.
 */
@Mojo(name = "publish-tag", defaultPhase = LifecyclePhase.DEPLOY)
public class PublishTagMojo extends AbstractBitbucketMojo {
    @Override
    protected void doExecute(RestClient restClient, BitbucketApi api, Log log)
        throws MojoExecutionException, MojoFailureException {
        try {
            PomVersion pomVersion = new PomVersion(getVersion());
            if (pomVersion.isSnapshot()) {
                throw new MojoFailureException("Cannot publish tag for snapshot version " + pomVersion);
            }

            String username = getUsername();
            String password = getPassword();
            String version = getVersion();
            String owner = getOwner();
            String slug = getSlug();
            String hash = getHash();
            restClient.setCredentials(new Credentials(username, password));
            String tag = versionToTag(version);
            api.createTag(owner, slug, tag, hash);
            log.info(String.format("Published tag %s", tag));
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new MojoFailureException(ex.getMessage());
        }
    }
}
