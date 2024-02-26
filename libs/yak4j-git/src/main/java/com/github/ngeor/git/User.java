package com.github.ngeor.git;

import org.apache.commons.lang3.Validate;

public record User(String name, String email) {
    public User {
        Validate.notBlank(name);
        Validate.notBlank(email);
    }
}
