package cs412.dinghyprop.interpreter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A creatively named class that displays trees.
 *
 * For all but the smallest trees, objects of this class should be placed in a
 * {@code JScrollPane}.  A convenience method is provided that creates a
 * {@code JFrame} with the created TreeViewer in a {@code JScrollPane}.
 */
public final class TreeViewer extends JPanel {
    private static final long serialVersionUID = -447063110938138711L;
    private Node root;
    private boolean sizeComputed = false;

    /**
     * Create a {@code JFrame} containing a TreeViewer wrapped in a
     * {@code JScrollPane}.
     * @param expression    The expression to display/
     * @return  A JFrame displaying a scrollable TreeViewer
     */
    public static JFrame createFramedExpression(Expression expression) {
        JFrame jf = new JFrame();
        TreeViewer tv = new TreeViewer(expression);
        JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(tv);
        jf.add(jsp);
        jf.pack();
        return jf;
    }

    /**
     * Create a TreeViewer wrapped in a {@code JScrollPane}.
     * @param expression    The expression to display/
     * @return  A scrollable TreeViewer
     */
    public static JScrollPane createScrollableExpression(Expression expression) {
        TreeViewer tv = new TreeViewer(expression);
        JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(tv);
        return jsp;
    }

    /**
     * Create a TreeViewer.
     * @param expr    The expression to display.
     */
    public TreeViewer(Expression expr) {
        super(true);
        root = createTree(expr);
    }

    /**
     * Convert an {@code Expression} tree into a {@code Node} tree.
     * @param expr    The root {@code Expression}
     * @return  The root of the newly created {@code Node} tree
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
     * Compute the size of the root node.  The node will cache its value
     * implicitly, so {@code sizeComputed} is set to disable future computation.
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

    /**
     * Testing main.
     * @param args    ignored
     */
    public static void main(String[] args) {
        Logger log = Logger.getLogger("TreeViewer");
        Expression expr = null;
        try {
            String str = "(hey der (if (< 0 1) (brother) (+ 9 7)) 6)";
            expr = Expression.fromString(str);
        } catch (ParsingException e) {
            log.log(Level.WARNING, "Exception while parsing", e);
        }

        if (expr != null) {
            final TreeViewer tv = new TreeViewer(expr);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JFrame jf = new JFrame("TreeViewer Test");
                    jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    jf.add(tv);
                    jf.pack();
                    jf.setVisible(true);
                }
            });
        }
    }
}
