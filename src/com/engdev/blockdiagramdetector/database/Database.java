/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.database;

import android.content.Context;
import com.engdev.blockdiagramdetector.io.IOUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Final class for database operations. Provide interface between xml database and application.
 *
 * @author Engineer
 */
public abstract class Database {

    private static Document dom = null;

    public static void load(Context context, String fileName) throws Exception {
        dom = IOUtility.getXmlDOM(context, fileName);
    }

    public static List<ObjectStatistics> getCharactersStatistics() {
        Element xmlChars = getSingleElementByTagName(dom.getElementById("statistics"), "characters");
        return getStatistics(xmlChars);
    }

    public static List<ObjectStatistics> getObjectsStatistics() {
        Element xmlObjects = getSingleElementByTagName(dom.getElementById("statistics"), "objects");
        return getStatistics(xmlObjects);
    }

    public static ObjectStatistics getObjectStatistics(String id) {
        ObjectStatistics objStatistics = null;
        List<ObjectStatistics> objectStats = getObjectsStatistics();
        for (ObjectStatistics stats : objectStats) {
            if (stats.id.equals(id)) {
                objStatistics = stats;
                break;
            }
        }
        return objStatistics;
    }

    private static List<ObjectStatistics> getStatistics(Element root) {
        List<ObjectStatistics> statistics = new ArrayList<ObjectStatistics>();
        List<Element> xmlObjects = toElementList(root.getElementsByTagName("object"));
        for (Element xmlObject : xmlObjects) {
            Node xmlText = xmlObject.getAttributes().getNamedItem("text");
            String ObjectID = xmlObject.getAttributes().getNamedItem("id").getNodeValue();
            List<Element> xmlVars = toElementList(xmlObject.getElementsByTagName("random_var"));
            Map<String, Statistics> vars = new HashMap<String, Statistics>();
            for (Element xmlVar : xmlVars) {
                String varName = xmlVar.getAttributes().getNamedItem("name").getNodeValue();
                Element xmlMean = getSingleElementByTagName(xmlVar, "mean");
                Element xmlStdev = getSingleElementByTagName(xmlVar, "stdev");
                float mean = Float.parseFloat(getText(xmlMean));
                float stdev = Float.parseFloat(getText(xmlStdev));
                vars.put(varName, new Statistics(mean, stdev));
            }
            ObjectStatistics chars = new ObjectStatistics();
            if (xmlText != null)
                chars.text = xmlText.getNodeValue();
            chars.id = ObjectID;
            chars.randomVars = vars;
            statistics.add(chars);
        }
        return statistics;
    }

    public static float getStatisticsThreshold() {
        Element xmlStatistics = dom.getElementById("statistics");
        Element xmlThreshold = toElementList(xmlStatistics.getElementsByTagName("threshold")).get(0);
        return Float.parseFloat(getText(xmlThreshold));
    }

    private static Element getSingleElementByTagName(Element element, String name) {
        return (Element) element.getElementsByTagName(name).item(0);
    }

    private static List<Element> toElementList(NodeList xmlNodes) {
        List<Element> nodes = new ArrayList<Element>();
        for (int i = 0; i < xmlNodes.getLength(); i++) {
            Node xmlNode = xmlNodes.item(i);
            if (xmlNode.getNodeType() == Node.ELEMENT_NODE)
                nodes.add((Element) xmlNode);
        }
        return nodes;
    }

    private static String getText(Node node) {
        String text = null;
        NodeList xmlChildren = node.getChildNodes();
        for (int i = 0; i < xmlChildren.getLength(); i++) {
            Node xmlNode = xmlChildren.item(i);
            if (xmlNode.getNodeType() == Node.TEXT_NODE) {
                text = xmlNode.getNodeValue();
            }
        }
        return text;
    }

    public static Document getDom() {
        return dom;
    }

    public static void setDom(Document dom) {
        Database.dom = dom;
    }

}
