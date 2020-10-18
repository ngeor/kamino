/*
 * UserWriter.java
 *
 * Created on July 13, 2004, 11:30 PM
 */

package org.jcms.xml;

import java.io.Writer;
import org.jcms.model.User;

/**
 * @author ngeor
 */
public class UserWriter extends XMLWriter {

    /**
     * Creates a new instance of UserWriter.
     */
    public UserWriter(Writer out) {
        super(out);
    }

    /**
     * Writes user attributes.
     * @param user
     * @throws java.io.IOException
     */
    protected void writeUserAttributes(User user) throws java.io.IOException {
        writeAttribute("id", user.getId());
        writeAttribute("email", user.getEmail());
        writeAttribute("password", user.getPassword());
        writeAttribute("lastname", user.getLastname());
        writeAttribute("firstname", user.getFirstname());
        writeAttribute("nickname", user.getNickname());
        writeAttribute("active", String.valueOf(user.isActive()));
    }

    /**
     * Writes the user.
     * @param user
     * @throws java.io.IOException
     */
    public void writeUser(User user) throws java.io.IOException {
        write("<");
        write("user");
        writeUserAttributes(user);
        write(" />");

    }
}
