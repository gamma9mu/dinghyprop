/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.interpreter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
* A drawable counterpart to an Expression object.
*/
final class Node extends JComponent {
    private static final long serialVersionUID = 8064723739784397824L;
    private static Logger log = Logger.getLogger("Node");

    /**
     * Horizontal spacing between nodes
     */
    private static final int HSPACE = 20;

    /**
     * Vertical spacing between nodes
     */
    private static final int VSPACE = 20;

    /**
     * The printing value of the node
     */
    String value;

    /**
     * Child nodes in the Node tree
     */
    List<Node> children;

    /*
     * x coordinate of this node in the drawing space
     */
    int x = 0;

    /**
     * y coordinate of this node in the drawing space
     */
    int y = VSPACE;

    /**
     * The width of this node when drawn
     */
    int w = 0;

    /**
     * The height of this node when drawn
     */
    int h = 0;

    /**
     * The height of the text when printing {@code value}
     */
    int strHeight = 0;

    /**
     * @param value       the value to print for this node
     * @param children    the child subtrees of this node
     */
    public Node(String value, List<Node> children) {
        this.value = value;
        if (children == null) {
            this.children = new ArrayList<Node>(0);
        } else {
            this.children = children;
        }
    }

    /**
     * Compute the size necessary to properly display this (sub)tree.
     *
     * @param g    the graphics object that would draw this
     */
    public void computeSize(Graphics g) {
        log.entering("Node", "computeSize");

        FontMetrics fm = g.getFontMetrics();
        int height = (int) fm.getLineMetrics(value, g).getHeight();
        strHeight = height;
        int width = fm.stringWidth(value);

        w = 0;
        int maxH = 0;
        for (Node n : children) {
            n.computeSize(g);
            w += n.w;
            maxH = (n.h > maxH) ? n.h : maxH;
        }

        h = height;
        w += ((children.size() - 1) * HSPACE);
        if (! children.isEmpty()) {
            h += VSPACE + maxH;
        }
        // ensure this width accounts for the width of 'value'
        if (w < width) {
            w = width;
        }
    }

    @Override
    public void paint(Graphics g) {
        log.entering("Node ", "paint");

        int strWidth = g.getFontMetrics().stringWidth(value);
        g.drawString(value, x + w/2 - strWidth/2, y);
        int cx = x;
        int cy = y + strHeight + VSPACE;
        for (Node node : children) {
            node.x = cx;
            node.y = cy;
            g.drawLine(x + w/2, y + strHeight/2, node.x + node.w/2, node.y - strHeight);
            node.paint(g);
            cx += node.w + HSPACE;
        }
    }
}
