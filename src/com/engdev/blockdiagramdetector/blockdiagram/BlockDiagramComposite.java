/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package com.engdev.blockdiagramdetector.blockdiagram;

import android.graphics.Canvas;
import com.engdev.blockdiagramdetector.geometry.Point;

import java.util.*;

/**
 * Composite of block diagrams.
 *
 * @author Lucas Batista
 */
public class BlockDiagramComposite implements BlockDiagramObject {
    protected Point point = new Point();
    private List<BlockDiagramObject> components = null;
    private Map<String, BlockDiagramObject> componentsMap = null;

    public BlockDiagramComposite() {
        init();
    }

    private void init() {
        components = new ArrayList<BlockDiagramObject>();
        componentsMap = new HashMap<String, BlockDiagramObject>();
    }

    public void add(BlockDiagramObject c, String name) {
        components.add(c);
        componentsMap.put(name, c);
    }

    public void remove(String name) {
        BlockDiagramObject object = componentsMap.get(name);
        components.remove(object);
        componentsMap.remove(name);
    }

    public BlockDiagramObject get(int location) {
        return components.get(location);
    }

    public BlockDiagramObject get(String name) {
        return componentsMap.get(name);
    }

    public boolean contains(String name) {
        return componentsMap.containsKey(name);
    }

    public void clear() {
        components.clear();
    }

    public boolean contains(BlockDiagramObject object) {
        return components.contains(object);
    }

    public boolean containsAll(Collection<BlockDiagramObject> arg0) {
        return components.containsAll(arg0);
    }

    public boolean isEmpty() {
        return components.isEmpty();
    }

    public void draw(Canvas canvas) {
        for (BlockDiagramObject c : components)
            c.draw(canvas);
    }

    public int getX() {
        return point.x;
    }

    public void setX(int x) {
        for (BlockDiagramObject c : components)
            c.setX(x + c.getX());
    }

    public int getY() {
        return point.y;
    }

    public void setY(int y) {
        for (BlockDiagramObject c : components)
            c.setY(y + c.getY());
    }

    public int getHeight() {
        BlockDiagramObject c = getFurthestYComponent();
        if (c != null)
            return c.getY() + c.getHeight();
        else
            return 0;
    }

    public int getWidth() {
        BlockDiagramObject c = getFurthestXComponent();
        if (c != null)
            return c.getX() + c.getWidth();
        else
            return 0;
    }

    public Point getPoint() {
        return point;
    }

    private BlockDiagramObject getFurthestXComponent() {
        int max = 0;
        BlockDiagramObject component = null;
        for (BlockDiagramObject c : components) {
            if (c.getX() > max) {
                max = c.getX();
                component = c;
            }
        }
        return component;
    }

    private BlockDiagramObject getFurthestYComponent() {
        int max = 0;
        BlockDiagramObject component = null;
        for (BlockDiagramObject c : components) {
            if (c.getY() > max) {
                max = c.getY();
                component = c;
            }
        }
        return component;
    }

}
