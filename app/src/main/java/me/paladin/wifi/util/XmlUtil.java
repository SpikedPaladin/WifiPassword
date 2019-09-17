package me.paladin.wifi.util;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {
    
    public static Node getNodeByAttribute(Node root, String tag, String attribute) {
        NodeList list = ((Element) root).getElementsByTagName(tag);
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap nodeAttributes = node.getAttributes();
                for (int t = 0; t < nodeAttributes.getLength(); t++) {
                    if (nodeAttributes.item(t).getNodeValue().equals(attribute)) {
                        return node;
                    }
                }
            }
        }
        return null;
    }
    
    public static String getNodeValueByAttribute(Node root, String tag, String attribute) {
        Node node = getNodeByAttribute(root, tag, attribute);
        if (node != null) {
            return node.getChildNodes().item(0).getNodeValue();
        }
        return "";
    }
    
    public static boolean hasNodeAttribute(Node root, String tag, String attribute) {
        return getNodeByAttribute(root, tag, attribute) != null;
    }
}
