/*
 * User.java
 *
 * Created on July 5, 2004, 7:00 PM
 */

package org.jcms.model;

/**
 * @author ngeor
 */
public class User extends IDEntity {

    private String email;
    private String password;
    private String lastname;
    private String firstname;
    private String nickname;
    private boolean active;

    public User() {
        super();
    }

    public User(int id) {
        super(id);
    }

    /**
     * Creates a guest user.
     * @return
     */
    public User guestUser() {
        User u = new User();
        u.setId(2);
        return u;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
