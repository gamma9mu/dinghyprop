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

/**
 * A creatively named class that displays trees.
 *
 * For all but the smallest trees, objects of this class should be placed in a
 * {@link JScrollPane}.  A convenience method is provided that creates a
 * {@link JFrame} with the created TreeViewer in a {@link JScrollPane}.
 */
public final class TreeViewer extends JPanel implements Scrollable {
    private static final long serialVersionUID = -447063110938138711L;
    private Node root;
    private boolean sizeComputed = false;

    /**
     * @param expression    the expression to display
     * @return  a JFrame displaying a scrollable TreeViewer
     */
    public static JFrame createFramedExpression(Expression expression) {
        JFrame jf = new JFrame();
        jf.setLayout(new BorderLayout());
        TreeViewer tv = new TreeViewer(expression);
        JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(tv);
        jf.add(jsp, BorderLayout.CENTER);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jf.setPreferredSize(new Dimension(screenSize.width / 3, screenSize.height / 2));

        jf.pack();
        return jf;
    }

    /**
     * @param expression    the expression to display
     * @return  a TreeViewer wrapped in a JScrollPane
     */
    public static JScrollPane createScrollableExpression(Expression expression) {
        TreeViewer tv = new TreeViewer(expression);
        JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(tv);
        return jsp;
    }

    /**
     * Create a TreeViewer.
     * @param expr    the expression to display
     */
    public TreeViewer(Expression expr) {
        super(true);
        root = createTree(expr);
    }

    /**
     * Converts (recursively) an Expression tree into a Node tree.
     *
     * @param expr    the root Expression
     * @return  the root of the newly created Node tree
     */
    private Node createTree(Expression expr) {
        List<Node> children = new ArrayList<Node>(expr.getOperands().length);
        for (Object child : expr.getOperands()) {
            if (child instanceof Expression) {
                children.add(createTree((Expression) child));
            } else {
                children.add(new Node(child.toString(), null));
            }
        }
        return new Node(expr.getOperator(), children);
    }

    /**
     * Cause the root Node of the display tree to calculate its size.
     */
    private void computeSize() {
        if (! sizeComputed) {
            root.computeSize(getGraphics());
            sizeComputed = true;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        computeSize();
        root.paint(g);
    }

    @Override
    public Dimension getPreferredSize() {
        computeSize();
        return new Dimension(root.w, root.h + root.strHeight);
    }

    @Override
    public String toString() {
        return "TreeViewer{root=" + root + ", sizeComputed=" + sizeComputed + '}';
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
