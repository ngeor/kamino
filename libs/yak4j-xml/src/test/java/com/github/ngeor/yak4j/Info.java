package com.github.ngeor.yak4j;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Just a class to serialize in the tests.
 */
@XmlRootElement
class Info {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
