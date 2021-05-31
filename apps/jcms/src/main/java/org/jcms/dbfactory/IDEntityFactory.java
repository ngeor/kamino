/*
 * IDEntityFactory.java
 *
 * Created on July 5, 2004, 7:19 PM
 */

package org.jcms.dbfactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.jcms.model.IDEntity;

/**
 * @author ngeor
 */
public abstract class IDEntityFactory {

    /**
     * Creates a new instance of IDEntityFactory.
     */
    protected IDEntityFactory() {
    }

    /**
     * Connect to the database.
     * @return A connection.
     * @throws SQLException
     */
    protected Connection connect() throws SQLException {
        String strDriver = "com.mysql.jdbc.Driver";

        // serverTimezone=UTC to solve error:
        // The server time zone value 'CEST' is unrecognized or represents more than one time zone.
        String strUrl = "jdbc:mysql://localhost/jcms?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC";
        String strUser = "jcms";
        String strPassword = "jcms";

        try {
            Class.forName(strDriver).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            throw new IllegalArgumentException("Misconfiguration error or database error. " + ex.toString());
        }

        return DriverManager.getConnection(strUrl, strUser, strPassword);
    }

    /**
     * Close the given connection.
     * @param conn
     */
    protected void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException ex) {
        }
    }

    public abstract IDEntity create(String table, ResultSet rs) throws SQLException;

    protected abstract String getInsertSQL(IDEntity obj);

    protected abstract void prepareInsertStatement(PreparedStatement pst, IDEntity obj) throws SQLException;

    protected abstract String getUpdateSQL(IDEntity obj);

    protected abstract void prepareUpdateStatement(PreparedStatement pst, IDEntity obj) throws SQLException;

    /**
     * Insert a new record in the database.
     * @param obj
     * @throws SQLException
     */
    public void insert(IDEntity obj) throws SQLException {
        String sql = getInsertSQL(obj);
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);
            prepareInsertStatement(pst, obj);
            pst.executeUpdate();
        } catch (SQLException ex) {
            obj.setId(0);
            closeConnection(conn);
            throw ex;
        }
    }

    /**
     * Update a record in the database.
     * @param obj
     * @throws SQLException
     */
    public void update(IDEntity obj) throws SQLException {
        String sql = getUpdateSQL(obj);
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);
            prepareUpdateStatement(pst, obj);
            pst.executeUpdate();
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Gets the ID of the newly inserted record.
     * @param table
     * @param idfield
     * @return
     * @throws SQLException
     */
    protected int getNewId(String table, String idfield) throws SQLException {
        String sql = "SELECT MAX(" + idfield + ") FROM " + table;
        Connection conn = null;
        try {
            conn = connect();
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            rs.next();
            return rs.getInt(1) + 1;
        } finally {
            closeConnection(conn);
        }
    }
}
