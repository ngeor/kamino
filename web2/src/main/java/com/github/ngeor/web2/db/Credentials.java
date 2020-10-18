package com.github.ngeor.web2.db;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Credentials to connect to Bitbucket.
 */
@Entity
public class Credentials {
  @Id private String owner;

  private String username;

  private String password;

  public String getOwner() { return owner; }

  public void setOwner(String owner) { this.owner = owner; }

  public String getUsername() { return username; }

  public void setUsername(String username) { this.username = username; }

  public String getPassword() { return password; }

  public void setPassword(String password) { this.password = password; }
}
