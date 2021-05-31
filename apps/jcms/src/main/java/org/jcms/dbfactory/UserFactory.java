/*
 * UserFactory.java
 *
 * Created on July 5, 2004, 7:52 PM
 */

package org.jcms.dbfactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import org.jcms.model.IDEntity;
import org.jcms.model.User;

/**
 * @author ngeor
 */
public class UserFactory extends IDEntityFactory {

    /**
     * Creates a new instance of UserFactory.
     */
    public UserFactory() {
    }

    /**
     * Creates a user.
     * @param table
     * @param rs
     * @return
     * @throws SQLException
     */
    public IDEntity create(String table, ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt(table + ".u_id"));
        user.setEmail(rs.getString(table + ".u_email"));
        user.setPassword(rs.getString(table + ".u_password"));
        user.setLastname(rs.getString(table + ".u_lastname"));
        user.setFirstname(rs.getString(table + ".u_firstname"));
        user.setNickname(rs.getString(table + ".u_nickname"));
        user.setActive(rs.getBoolean(table + ".u_active"));
        return user;
    }

    /**
     * Gets the insert SQL.
     * @param obj
     * @return
     */
    protected String getInsertSQL(IDEntity obj) {
        return "INSERT INTO users "
            + "(u_id, u_email, u_password, u_lastname, u_firstname, u_nickname, u_active) VALUES (?,?,MD5(?),?,?,?,?)";
    }

    /**
     * Gets the update SQL.
     * @param obj
     * @return
     */
    protected String getUpdateSQL(IDEntity obj) {
        return "UPDATE users SET "
            + "u_email=?, u_password=?, u_lastname=?, u_firstname=?, u_nickname=?, u_active=? WHERE u_id=?";
    }

    @Override
    protected void prepareInsertStatement(PreparedStatement pst, IDEntity obj) throws SQLException {
        User user = (User) obj;
        int f = 0;
        user.setId(getNewId("users", "u_id"));
        pst.setInt(++f, user.getId());
        pst.setString(++f, user.getEmail());
        pst.setString(++f, user.getPassword());
        pst.setString(++f, user.getLastname());
        pst.setString(++f, user.getFirstname());
        pst.setString(++f, user.getNickname());
        pst.setBoolean(++f, user.isActive());
    }

    @Override
    protected void prepareUpdateStatement(PreparedStatement pst, IDEntity obj) throws SQLException {
        User user = (User) obj;
        int f = 0;

        pst.setString(++f, user.getEmail());
        pst.setString(++f, user.getPassword());
        pst.setString(++f, user.getLastname());
        pst.setString(++f, user.getFirstname());
        pst.setString(++f, user.getNickname());
        pst.setBoolean(++f, user.isActive());

        pst.setInt(++f, user.getId());
    }

    /**
     * Selects all users.
     * @param orderBy
     * @return
     * @throws SQLException
     */
    public User[] selectAll(String orderBy) throws SQLException {
        String sql = "SELECT * FROM users";
        if (orderBy != null) {
            sql += " ORDER BY " + orderBy;
        }

        Vector vector = new Vector();
        Connection conn = null;
        try {
            conn = connect();
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                vector.addElement(create("users", rs));
            }

            return (User[]) vector.toArray(new User[vector.size()]);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Selects one user.
     * @param id
     * @return
     * @throws SQLException
     */
    public User selectOne(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE u_id=" + id;
        Connection conn = null;
        try {
            conn = connect();
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            rs.next();
            return (User) create("users", rs);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Deletes one user.
     * @param id
     * @throws SQLException
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE u_id=" + id;
        Connection conn = null;
        try {
            conn = connect();
            Statement stm = conn.createStatement();
            stm.executeUpdate(sql);
        } finally {
            closeConnection(conn);
        }
    }
}
