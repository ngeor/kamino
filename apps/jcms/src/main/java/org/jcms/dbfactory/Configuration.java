/*
 * Configuration.java
 *
 * Created on July 6, 2004, 2:52 PM
 */

package org.jcms.dbfactory;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.jcms.model.Node;
import org.jcms.model.NodeType;

/**
 * Configuration class.
 *
 * @author ngeor
 */
public final class Configuration {
    private static final String ORG_JCMS_MODEL_ROOT_NODE = "org.jcms.model.RootNode";
    private static final String ROOT = "root";
    private static Configuration instance = new Configuration();
    private Hashtable hashTypeNodeType = new Hashtable();
    private Hashtable hashNameNodeType = new Hashtable();
    private Vector vNodeTypeRelations = new Vector();

    /**
     * Creates a new instance of Configuration.
     */
    private Configuration() {

        addNodeType(new NodeType(0, ORG_JCMS_MODEL_ROOT_NODE, ROOT));
        /* Σε κάποιο αρχείο πρέπει να υπάρχουν γραμμές τύπου
         * 1 = org.demosite.model.Forums
         * 2 = org.demosite.model.ForumGroup
         * κλπ
         * Επίσης, οι κλάσεις αυτές δεν ανήκουν στο jcms,
         * αλλά στην εφαρμογή (π.χ. ένα φόρουμ)
         * έτσι πηγαίνουν στο δικό τους πακέτο.
         */

        /*
        addNodeType(new NodeType(1, "org.demosite.model.Forums", "forums"));
        addNodeType(new NodeType(2, "org.demosite.model.ForumGroup", "forumGroup"));
        addNodeType(new NodeType(3, "org.demosite.model.Forum", "forum"));
        addNodeType(new NodeType(4, "org.demosite.model.ForumPost", "forumPost"));

        addNodeTypeRelation("root", "forums", 1);
        addNodeTypeRelation("forums", "forumGroup", NodeTypeRelation.ZERO_OR_MORE);
        addNodeTypeRelation("forumGroup", "forumGroup", NodeTypeRelation.ZERO_OR_MORE);
        addNodeTypeRelation("forumGroup", "forum", NodeTypeRelation.ZERO_OR_MORE);
        addNodeTypeRelation("forum", "forumPost", NodeTypeRelation.ZERO_OR_MORE);
        addNodeTypeRelation("forumPost", "forumPost", NodeTypeRelation.ZERO_OR_MORE);*/
    }

    /**
     * Resets the node.
     */
    public void reset() {
        hashTypeNodeType.clear();
        hashNameNodeType.clear();
        vNodeTypeRelations.clear();
        addNodeType(new NodeType(0, ORG_JCMS_MODEL_ROOT_NODE, ROOT));
    }

    /**
     * Adds a new node type.
     */
    public void addNodeType(NodeType nodeType) {
        hashTypeNodeType.put(new Integer(nodeType.getType()), nodeType);
        hashNameNodeType.put(nodeType.getName(), nodeType);
    }

    public void addNodeTypeRelation(String parentTypeName, String childTypeName, int cardinality) {
        vNodeTypeRelations.addElement(
            new NodeTypeRelation(nodeType(parentTypeName), nodeType(childTypeName), cardinality));
    }

    public NodeType nodeType(int type) {
        return (NodeType) hashTypeNodeType.get(new Integer(type));
    }

    public NodeType nodeType(String name) {
        return (NodeType) hashNameNodeType.get(name);
    }

    public NodeType nodeType(Node node) {
        return node == null ? null : node.getType();
    }

    private boolean equal(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    /**
     * Gets the relation between two node types.
     */
    public NodeTypeRelation relation(NodeType parentNodeType, NodeType childNodeType) {
        for (int i = 0; i < vNodeTypeRelations.size(); i++) {
            NodeTypeRelation o = (NodeTypeRelation) vNodeTypeRelations.get(i);
            NodeType oParentType = o.getParent();
            NodeType oChildType = o.getChild();
            if (equal(oParentType, parentNodeType) && equal(oChildType, childNodeType)) {
                return (NodeTypeRelation) o;
            }
        }

        return null;
    }

    public static Configuration getInstance() {
        return instance;
    }

    public Enumeration nodeTypeNames() {
        return hashNameNodeType.keys();
    }
}
