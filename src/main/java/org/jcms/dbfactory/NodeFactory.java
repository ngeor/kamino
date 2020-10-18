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
import org.jcms.model.Node;
import org.jcms.model.NodeType;
import org.jcms.model.RootNode;

/**
 * @author ngeor
 */
public class NodeFactory extends IDEntityFactory {

    /**
     * Creates a new instance of UserFactory.
     */
    public NodeFactory() {
    }

    private Node createByType(int type) {
        Configuration conf = Configuration.getInstance();
        NodeType nodeType = conf.nodeType(type);
        if (nodeType != null) {
            return nodeType.newInstance();
        } else {
            throw new IllegalArgumentException("Cannot find node type!");
        }
    }

    /**
     * Creates a new node.
     * @param table
     * @param rs
     * @return
     * @throws SQLException
     */
    public IDEntity create(String table, ResultSet rs) throws SQLException {
        int nodeType = rs.getInt(table + ".n_type");
        Node node = createByType(nodeType);
        node.setId(rs.getInt(table + ".n_id"));
        node.setTitle(rs.getString(table + ".n_title"));
        return node;
    }

    protected String getInsertSQL(IDEntity obj) {
        return "INSERT INTO nodes (n_id, n_type, n_title) VALUES (?,?,?)";
    }

    protected String getUpdateSQL(IDEntity obj) {
        return "UPDATE nodes SET n_type=?, n_title=? WHERE n_id=?";
    }

    /**
     * Prepares an insert statement.
     * @param pst
     * @param obj
     * @throws SQLException
     */
    protected void prepareInsertStatement(PreparedStatement pst, IDEntity obj) throws SQLException {
        Node node = (Node) obj;
        int f = 0;
        node.setId(getNewId("nodes", "n_id"));
        pst.setInt(++f, node.getId());
        pst.setInt(++f, node.getType().getType());
        pst.setString(++f, node.getTitle());
    }

    /**
     * Prepares an update statement.
     * @param pst
     * @param obj
     * @throws SQLException
     */
    protected void prepareUpdateStatement(PreparedStatement pst, IDEntity obj) throws SQLException {
        Node node = (Node) obj;
        int f = 0;

        pst.setInt(++f, node.getType().getType());
        pst.setString(++f, node.getTitle());

        pst.setInt(++f, node.getId());
    }

    /**
     * Selects all nodes.
     * @param orderBy
     * @return
     * @throws SQLException
     */
    public Node[] selectAll(String orderBy) throws SQLException {
        String sql = "SELECT * FROM nodes";
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
                vector.addElement(create("nodes", rs));
            }

            return (Node[]) vector.toArray(new Node[vector.size()]);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Selects a node by the given id.
     * @param id
     * @return
     * @throws SQLException
     */
    public Node selectOne(int id) throws SQLException {
        String sql = "SELECT * FROM nodes WHERE n_id=" + id;
        Connection conn = null;
        try {
            conn = connect();
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                return (Node) create("nodes", rs);
            } else {
                return null;
            }
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Seelcts the root node.
     * @return
     * @throws SQLException
     */
    public RootNode rootNode() throws SQLException {
        String sql = "SELECT * FROM nodes WHERE n_type=0";
        Connection conn = null;
        try {
            conn = connect();
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            rs.next();
            return (RootNode) create("nodes", rs);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Selects children of the given node type.
     * @param parent
     * @param childNodeType
     * @param orderBy
     * @return
     * @throws SQLException
     */
    public Node[] selectChildrenOfType(Node parent, NodeType childNodeType, String orderBy) throws SQLException {
        String sql = "SELECT * FROM nodes JOIN node2parent ON nodes.n_id = node2parent.np_child";

        if (childNodeType != null) {
            sql += " WHERE node2parent.np_parent=? AND nodes.n_type=?";
        } else {
            sql += " WHERE node2parent.np_parent=?";
        }

        if (orderBy != null) {
            sql += " ORDER BY " + orderBy;
        }

        Vector vector = new Vector();
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);

            if (childNodeType != null) {
                // sql += " WHERE node2parent.np_parent=? AND nodes.n_type=?";
                pst.setInt(1, parent.getId());
                pst.setInt(2, childNodeType.getType());
            } else {
                //sql += " WHERE node2parent.np_parent=?";
                pst.setInt(1, parent.getId());
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                vector.addElement(create("nodes", rs));
            }

            return (Node[]) vector.toArray(new Node[vector.size()]);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Counts the child nodes of the given type.
     * @param parent
     * @param childNodeType
     * @return
     * @throws SQLException
     */
    public int countChildrenOfType(Node parent, NodeType childNodeType) throws SQLException {
        String sql = "SELECT COUNT(n_id) FROM nodes JOIN "
            + "node2parent ON nodes.n_id = node2parent.np_child "
            + "WHERE node2parent.np_parent=? AND nodes.n_type=?";
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, parent.getId());
            pst.setInt(2, childNodeType.getType());
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Checks if the node can be placed under a parent node.
     * @param parent
     * @param childNodeType
     * @return
     * @throws SQLException
     */
    public boolean canPlaceUnder(Node parent, NodeType childNodeType) throws SQLException {
        Configuration conf = Configuration.getInstance();
        NodeType parentNodeType = parent.getType();
        NodeTypeRelation ntr = conf.relation(parentNodeType, childNodeType);
        if (ntr == null) {
            return false;
        }
        if (ntr.getCardinality() == NodeTypeRelation.ZERO_OR_MORE) {
            return true;
        }
        int c = countChildrenOfType(parent, childNodeType);
        return ntr.getCardinality() > c;
    }

    private void insertRelation(Node parent, Node child) throws SQLException {
        String sql = "INSERT INTO node2parent (np_parent, np_child) VALUES (?,?)";
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, parent.getId());
            pst.setInt(2, child.getId());
            pst.executeUpdate();
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Inserts a new node.
     * @param parent
     * @param child
     * @return
     * @throws SQLException
     */
    public boolean insert(Node parent, Node child) throws SQLException {
        if (!canPlaceUnder(parent, child.getType())) {
            return false;
        }

        insert(child);
        insertRelation(parent, child);
        return true;
    }

    /**
     * Selects the child node of the given type.
     * @param parent
     * @param childNodeType
     * @return
     * @throws SQLException
     */
    public Node selectChildOfType(Node parent, NodeType childNodeType) throws SQLException {
        String sql = "SELECT * FROM nodes JOIN node2parent ON nodes.n_id = node2parent.np_child";

        sql += " WHERE node2parent.np_parent=? AND nodes.n_type=?";

        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, parent.getId());
            pst.setInt(2, childNodeType.getType());

            ResultSet rs = pst.executeQuery();
            rs.next();
            return (Node) create("nodes", rs);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Selects descendant nodes.
     * @param parent
     * @return
     * @throws SQLException
     */
    public Node[] selectChildrenRecursive(Node parent) throws SQLException {
        String sql = "SELECT * FROM nodes JOIN node2parent ON nodes.n_id = node2parent.np_child";

        sql += " WHERE node2parent.np_parent=?";

        Vector vector = new Vector();
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, parent.getId());

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Node n = (Node) create("nodes", rs);
                vector.addElement(n);
                n.setChildren(selectChildrenRecursive(n));
            }
            return (Node[]) vector.toArray(new Node[vector.size()]);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Selects ancestor nodes.
     * @param child
     * @param parentNodeType
     * @param orderBy
     * @return
     * @throws SQLException
     */
    public Node[] selectParentsOfType(Node child, NodeType parentNodeType, String orderBy) throws SQLException {
        String sql = "SELECT * FROM nodes JOIN node2parent ON nodes.n_id = node2parent.np_parent";

        if (parentNodeType != null) {
            sql += " WHERE node2parent.np_child=? AND nodes.n_type=?";
        } else {
            sql += " WHERE node2parent.np_child=?";
        }

        if (orderBy != null) {
            sql += " ORDER BY " + orderBy;
        }

        Vector vector = new Vector();
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);

            if (parentNodeType != null) {
                // sql += " WHERE node2parent.np_parent=? AND nodes.n_type=?";
                pst.setInt(1, child.getId());
                pst.setInt(2, parentNodeType.getType());
            } else {
                //sql += " WHERE node2parent.np_parent=?";
                pst.setInt(1, child.getId());
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                vector.addElement(create("nodes", rs));
            }

            return (Node[]) vector.toArray(new Node[vector.size()]);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Selects ancestor nodes.
     * @param child
     * @return
     * @throws SQLException
     */
    public Node[] selectParentsRecursive(Node child) throws SQLException {
        String sql = "SELECT * FROM nodes JOIN node2parent ON nodes.n_id = node2parent.np_parent";

        sql += " WHERE node2parent.np_child=?";

        Vector vector = new Vector();
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, child.getId());

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Node n = (Node) create("nodes", rs);
                vector.addElement(n);
                n.setParents(selectParentsRecursive(n));
            }
            return (Node[]) vector.toArray(new Node[vector.size()]);
        } finally {
            closeConnection(conn);
        }
    }
}
