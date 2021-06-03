package com.github.ngeor.web2.api;

/**
 * User statistics.
 */
class UserStats {
    private String username;
    private Long count;

    /**
     * Creates an instance of this class.
     */
    UserStats(String username, Long count) {
        this.username = username;
        this.count = count;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
