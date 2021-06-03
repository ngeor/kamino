package com.github.ngeor.web2.db;

import org.springframework.data.rest.core.config.Projection;

/**
 * A projection that inlines the pipeline importer history and the PR imported
 * history fields.
 */
@Projection(name = "inlineHistory", types = {Repository.class})
interface InlineHistory {
    String getOwner();

    String getSlug();

    PipelineImporterHistory getPipelineImporterHistory();

    PullRequestImporterHistory getPullRequestImporterHistory();
}
