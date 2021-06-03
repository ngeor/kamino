package com.github.ngeor.web2.api;

/**
 * Repository statistics.
 */
class RepositoryStats {
    private String slug;
    private Long count;

    RepositoryStats(String slug, Long count) {
        this.slug = slug;
        this.count = count;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
