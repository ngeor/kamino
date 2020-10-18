package com.github.ngeor.yak4j;

import java.io.IOException;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import static com.github.ngeor.yak4j.Util.versionToTag;

/**
 * A mojo that ensures that the tag does not exist.
 */
@Mojo(name = "ensure-tag-does-not-exist", defaultPhase = LifecyclePhase.VALIDATE)
public class EnsureTagDoesNotExistMojo extends AbstractBitbucketMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Log log = getLog();
        RestClient restClient = new RestClientImpl();
        BitbucketApiImpl api = new BitbucketApiImpl(restClient);
        doExecute(restClient, api, log);
    }

    /**
     * Executes the mojo. This method is used in unit tests.
     */
    @Override
    protected void doExecute(RestClient restClient, BitbucketApi api, Log log)
        throws MojoFailureException, MojoExecutionException {
        try {
            PomVersion pomVersion = new PomVersion(getVersion());
            if (pomVersion.isSnapshot()) {
                log.info(String.format("Skipped check for snapshot version %s", pomVersion));
                return;
            }

            String username = getUsername();
            String password = getPassword();
            String owner = getOwner();
            String slug = getSlug();

            restClient.setCredentials(new Credentials(username, password));
            String tag = versionToTag(pomVersion.toString());
            if (api.tagExists(owner, slug, tag)) {
                throw new MojoFailureException(
                    String.format("Tag %s already exists! Please bump the version in pom.xml", tag));
            }

            String biggestTag = api.tagOfBiggestVersion(owner, slug);
            if (biggestTag != null && !biggestTag.isEmpty()) {
                PomVersion biggestVersion = new PomVersion(biggestTag.replaceAll("v", ""));
                List<String> allowedVersions = biggestVersion.allowedVersions();
                if (!pomVersion.isAllowedNextVersionOf(biggestVersion)) {
                    throw new MojoFailureException(
                        String.format(
                            "Version %s would create a gap in semver. Allowed versions are %s",
                            pomVersion,
                            String.join(", ", allowedVersions)
                        )
                    );
                }
            }

            log.info(String.format("Ensured that tag %s does not exist and is allowed semver", tag));
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new MojoFailureException(ex.getMessage());
        }
    }
}
