/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.blockdiagram;

import com.engdev.blockdiagramdetector.geometry.GeometricObject;
import com.engdev.blockdiagramdetector.geometry.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Block diagram AST data structure
 *
 * @author Lucas Batista
 */
public class BlockDiagramAST {

    public Region region = null;
    public GeometricObject gObject = null;
    public List<BlockDiagramAST> children = null;
    public BlockDiagramObject bdObject = null;

    public BlockDiagramAST() {
        children = new ArrayList<BlockDiagramAST>();
    }

    public BlockDiagramAST(Region region, GeometricObject gObject) {
        this();
        this.region = region;
        this.gObject = gObject;
    }

    @Override
    public String toString() {
        return toString(this);
    }

    public String toHTML() {
        String astString = toString();
        astString = astString.replaceAll("children>", "ul>");
        astString = astString.replaceAll("node>", "li>");
        return astString;
    }

    private String toString(BlockDiagramAST root) {
        String string = "";
        string += "<children>";
        for (BlockDiagramAST node : root.children)
            string += toString(node, "");
        string += "</children>";
        return string;
    }

    private String toString(BlockDiagramAST node, String string) {
        String structString = "{Region=" + node.region + ", GeometricObject=" + node.gObject + ", BlockDiagramObject=" + node.bdObject + "}";
        if (node.children.size() == 0)
            return "<node>" + structString + "</node>";
        else {
            string += "<node>" + structString + "<children>";
            for (BlockDiagramAST child : node.children)
                string += toString(child, string);
            string += "</children></node>";
            return string;
        }
    }

}
