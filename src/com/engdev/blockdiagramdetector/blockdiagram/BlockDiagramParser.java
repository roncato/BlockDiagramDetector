package com.engdev.blockdiagramdetector.blockdiagram;

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

import com.engdev.blockdiagramdetector.geometry.GeometricObject;
import com.engdev.blockdiagramdetector.geometry.GeometricObject.GeometricObjectType;
import com.engdev.blockdiagramdetector.geometry.GeometricObjectRecognizer;
import com.engdev.blockdiagramdetector.geometry.Region;
import com.engdev.blockdiagramdetector.imageprocessing.EdgeMap;
import com.engdev.blockdiagramdetector.state.BlockDiagramParserState;
import com.engdev.blockdiagramdetector.state.BlockDiagramParserState.StateType;
import com.engdev.blockdiagramdetector.state.State;
import com.engdev.blockdiagramdetector.state.StateMachine;
import com.engdev.blockdiagramdetector.tracer.ContourTracer;
import com.engdev.blockdiagramdetector.tracer.ContourTracer.TracingResult;
import com.engdev.blockdiagramdetector.tracer.MooreNeighborTracer;
import com.engdev.blockdiagramdetector.util.Buffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects Block Diagram structures from a edge map
 *
 * @author Engineer
 */
public class BlockDiagramParser implements Runnable {

    private final static int STATE_BUFFER_SIZE = 15;
    private final StateMachine stateMachine = new BlockDiagramDetectorMachine();
    private Buffer<BlockDiagramParserState> states = null;
    private EdgeMap edgeMap = null;
    private EdgeMap exploredMap = null;
    private DetectionResult result = null;
    private OnFinishListener listener = null;
    private List<Region> regions = null;
    private List<Region> unrecognizedRegions = null;
    private BlockDiagramAST root = null;
    public BlockDiagramParser(EdgeMap edgeMap) {
        this.edgeMap = edgeMap;
        exploredMap = edgeMap.clone();
        states = new Buffer<BlockDiagramParserState>(STATE_BUFFER_SIZE);
        init();
        setState(StateType.Undetected);
    }

    private void init() {
        regions = new ArrayList<Region>();
        unrecognizedRegions = new ArrayList<Region>();
    }

    @Override
    public void run() {
        switch (getCurrentState()) {
            case Undetected:
                setState(StateType.Tracing);
                break;
            case Traced:
                setState(StateType.Parsing);
                break;
            default:
        }
        listener.onFinish(new FinishEvent(result, regions, root));
    }

    /**
     * Traces regions
     */
    private void traceRegions() {

        Region region = null;

        // Instantiates contours collection
        regions = new ArrayList<Region>();

        // Attempts first time
        ContourTracer contourTracer = new MooreNeighborTracer(exploredMap);
        contourTracer.setExploredMap(exploredMap);
        TracingResult perceptResult = contourTracer.trace();

        switch (perceptResult) {
            case NotAbleToFindStartPoint:
                result = DetectionResult.Unsuccessful;
                break;
            default:

                // Attempts the rest until all the contours are covered
                region = Region.createRegion(contourTracer.getContour());
                regions.add(region);

                // Set this first if all shapes are closed it means that it
                // through without any open shape.
                result = DetectionResult.Successful;

                while (true) {

                    contourTracer = new MooreNeighborTracer(exploredMap);
                    contourTracer.setExploredMap(exploredMap);
                    perceptResult = contourTracer.trace();

                    if (perceptResult == TracingResult.NotAbleToFindStartPoint)
                        break;
                    else if (perceptResult == TracingResult.NotAbleToContour)
                        result = DetectionResult.PartialSuccessful;

                    region = Region.createRegion(contourTracer.getContour());
                    if (!GeometricObjectRecognizer.isNoize(region))
                        regions.add(region);

                }
        }
        // Build inner regions
        if (result == DetectionResult.Successful || result == DetectionResult.PartialSuccessful)
            buildInnerRegions();

        setState(StateType.Traced);
    }

    /**
     * Build Regions and their inner regions
     */
    private void buildInnerRegions() {
        Region region = null;
        int i = 0;
        int j = 0;
        for (i = 0, j = i + 1; j < regions.size(); i++, j++) {
            region = regions.get(i);
            List<Region> innerRegions = findInnerRegions(region, regions.subList(j, regions.size()));
            region.setInnerRegions(innerRegions);
        }
    }

    /**
     * Finds inner regions on region and stores them inside
     *
     * @param region
     * @param regions
     * @return
     */
    private List<Region> findInnerRegions(Region region, List<Region> regions) {

        List<Region> innerRegions = new ArrayList<Region>();

        for (Region item : regions) {
            if (Region.isContained(region, item))
                innerRegions.add(item);
        }

        return innerRegions;
    }

    /**
     * Recognize regions into objects.
     */
    private void parse() {

        // AST Root
        root = new BlockDiagramAST();

        // Gets copy of regions collection and removes inner regions
        ArrayList<Region> outerRegions = new ArrayList<Region>(regions);
        for (Region region : regions)
            outerRegions.removeAll(region.getInnerRegions());

        // Loops through regions
        for (Region region : outerRegions) {

            // Recognizes object and adds to the tree.
            GeometricObject object = GeometricObjectRecognizer.recognize(region);
            BlockDiagramAST node = new BlockDiagramAST(region, object);
            root.children.add(node);

            if (object != null)
                parseObject(node);
            else
                unrecognizedRegions.add(region);

        }

        // Returns result
        if (unrecognizedRegions.size() == regions.size()) {
            result = DetectionResult.Unsuccessful;
            setState(StateType.PartialRecognized);
        } else if (unrecognizedRegions.size() == 0) {
            result = DetectionResult.Successful;
            setState(StateType.Parsed);
        } else {
            result = DetectionResult.PartialSuccessful;
            setState(StateType.PartialRecognized);
        }

    }

    /**
     * Scans objects. This will not recurse as there is only two levels of object composition
     *
     * @param region
     * @param gObject
     */
    private void parseObject(BlockDiagramAST parent) {

        GeometricObjectType objectType = GeometricObject.getObjectType(parent.gObject);

        switch (objectType) {
            case Ellipse:
            case Rectangle:
                if (parent.region.getInnerRegions().size() > 0)
                    parseInnerRegions(parent);
                break;
            default:
        }
    }

    /**
     * Scans inner regions
     *
     * @param parentRegion
     * @param objectType
     */
    private void parseInnerRegions(BlockDiagramAST parent) {

        List<Region> innerRegions = parent.region.getInnerRegions();
        for (Region innerRegion : innerRegions) {

            // Recognize object and adds to the tree
            BlockDiagramAST node = new BlockDiagramAST(innerRegion, GeometricObjectRecognizer.recognize(innerRegion));
            parent.children.add(node);

            // Detects Devision rectangles and subtraction operators
            if (node.gObject != null && node.gObject.getObjectType() == GeometricObject.GeometricObjectType.Bar)
                parseBar(parent, node);

                // Objects of same type
            else if (node.gObject != null && node.gObject.getObjectType() == parent.gObject.getObjectType())
                parseEqualInnerRegion(parent, node);

                // Unrecognized object
            else if (node.gObject == null)
                unrecognizedRegions.add(innerRegion);

        }
    }

    public void parseBar(BlockDiagramAST parent, BlockDiagramAST node) {
        // If it is a bar that is not a division symbol, may be a character
        if (!GeometricObjectRecognizer.isDivisionSymbol(parent.region, node.region))
            node.gObject = GeometricObjectRecognizer.recognizeCharacter(node.region);

        // If not recognized
        if (node.gObject == null)
            unrecognizedRegions.add(node.region);
    }

    public void parseEqualInnerRegion(BlockDiagramAST parent, BlockDiagramAST node) {
        // If it is a contour region, remove it from the tree
        if (GeometricObjectRecognizer.isInnerContourRegion(parent.region, node.region))
            parent.children.remove(node);

            // If not, detects inner characters by context
        else {
            // Detects zeros by context
            if (node.gObject.getObjectType() == GeometricObject.GeometricObjectType.Ellipse)
                node.gObject = GeometricObjectRecognizer.recognizeCharacter(node.region);

            // If not recognized
            if (node.gObject == null)
                unrecognizedRegions.add(node.region);
        }
    }

    public BlockDiagramAST getAST() {
        return root;
    }

    public BlockDiagramParserState.StateType getCurrentState() {
        return states.peek(0).getStateType();
    }

    public void setOnFinishHandler(OnFinishHandler handler) {
        listener = new OnFinishListener(handler);
    }

    public EdgeMap getExploredMap() {
        return exploredMap;
    }

    public void setExploredMap(EdgeMap exploredMap) {
        this.exploredMap = exploredMap;
    }

    public EdgeMap getEdgeMap() {
        return edgeMap;
    }

    public void setEdgeMap(EdgeMap edgeMap) {
        this.edgeMap = edgeMap;
    }

    private void setState(BlockDiagramParserState.StateType stateType) {
        BlockDiagramParserState state = new BlockDiagramParserState(stateType);
        states.add(state);
        stateMachine.onStateChange(state);
    }

    /**
     * Manually sets the object for the correspondent region
     *
     * @param region
     * @param objectType
     */
    public void setRecognition(Region region, GeometricObjectType objectType) {
        if (getCurrentState() != BlockDiagramParserState.StateType.PartialRecognized)
            throw new IllegalStateException("Illegal operation, not in Partial Recognized state");

        if (unrecognizedRegions.size() > 0) {

            // Traverses tree and set node
            setNodeTree(region, GeometricObjectRecognizer.createObject(region, objectType), root);
            unrecognizedRegions.remove(region);
        }

        if (unrecognizedRegions.size() == 0) {
            pruneTree();
            setState(StateType.Parsed);
            listener.onFinish(new FinishEvent(result, regions, root));
        }

    }

    /**
     * Traverses AST and sets the object type for the correspondent region
     *
     * @param region
     * @param object
     * @param node
     */
    private void setNodeTree(Region region, GeometricObject object, BlockDiagramAST node) {
        if (node.region == region) {
            node.gObject = object;
            return;
        } else {
            for (BlockDiagramAST child : node.children)
                setNodeTree(region, object, child);
        }
        return;
    }

    /**
     * Prunes discarded or unrecognized tree nodes
     */
    private void pruneTree() {
        List<BlockDiagramAST> deadNodes = new ArrayList<BlockDiagramAST>();
        for (BlockDiagramAST child : root.children)
            fillDeadNodes(deadNodes, child);

        for (BlockDiagramAST deadNode : deadNodes)
            pruneNode(root.children, deadNode);
    }

    /**
     * Prunes dead node
     *
     * @param deadNode
     * @param nodes
     */
    private void pruneNode(List<BlockDiagramAST> nodes, BlockDiagramAST deadNode) {
        if (nodes.contains(deadNode))
            nodes.remove(deadNode);
        else {
            for (BlockDiagramAST node : nodes)
                pruneNode(node.children, deadNode);
        }
    }

    /**
     * Finds dead nodees
     *
     * @param deadNodes
     * @param node
     */
    private void fillDeadNodes(List<BlockDiagramAST> deadNodes, BlockDiagramAST node) {
        if (node.gObject == null)
            deadNodes.add(node);
        else
            for (BlockDiagramAST child : node.children)
                fillDeadNodes(deadNodes, child);
    }

    public List<Region> getUnrecognizedRegions() {
        if (getCurrentState() != BlockDiagramParserState.StateType.PartialRecognized)
            throw new IllegalStateException("Illegal operation, not in Partial Recognized state");
        return unrecognizedRegions;
    }

    public static enum DetectionResult {
        Unsuccessful,
        Successful,
        PartialSuccessful
    }

    // Event Handlers
    public interface OnFinishHandler {
        public void onFinish(FinishEvent event);
    }

    public final class FinishEvent {
        public DetectionResult result = null;
        public List<Region> regions = null;
        public BlockDiagramAST root = null;

        public FinishEvent(DetectionResult result, List<Region> regions) {
            this.result = result;
            this.regions = regions;
        }

        public FinishEvent(DetectionResult result, List<Region> regions, BlockDiagramAST root) {
            this(result, regions);
            this.root = root;
        }

    }

    private final class OnFinishListener implements OnFinishHandler {

        private OnFinishHandler handler = null;

        private OnFinishListener(OnFinishHandler handler) {
            this.handler = handler;
        }

        public void onFinish(FinishEvent event) {
            handler.onFinish(event);
        }

    }

    private final class BlockDiagramDetectorMachine extends StateMachine {

        @Override
        public void onStateChange(State state) {
            switch (((BlockDiagramParserState) state).getStateType()) {
                case Undetected:
                    break;
                case Tracing:
                    traceRegions();
                    break;
                case Parsing:
                    parse();
                    break;
                default:
            }
        }
    }

}
