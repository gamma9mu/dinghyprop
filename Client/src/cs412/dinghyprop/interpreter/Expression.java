/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a section of a program tree.
 */
public final class Expression {
    private String operator;
    private List<Object> operands;

    public static Expression fromString(String program) throws ParsingException {
        Parser p = new Parser(program);
        return p.parse();
    }

    /**
     * Create a new expression tree node.
     * @param operator    The operator at this node.
     */
    public Expression(String operator) {
        this.operator = operator;
        operands = new ArrayList<Object>(3);
    }

    /**
     * Appends an expression tree as an operand to this node.
     * @param operand    The subtree to add.
     */
    public void addOperand(Object operand) {
        operands.add(operand);
    }

    /**
     * Get this node's operator name.
     * @return  The name of the function to call to evaluate this node.
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Obtain the child subtrees under this node.
     * @return  An array of this node's children.
     */
    public Object[] getOperands() {
        return operands.toArray();
    }

    /**
     * Create a string representation of an expression.
     * @return  A flat S-expression representing {@code expr}.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder('(' + operator);
        for (Object obj : getOperands()) {
            sb.append(' ');
            try {
                Expression internal = (Expression) obj;
                sb.append(internal.toString());
            } catch (ClassCastException cce) {
                sb.append(obj);
            }
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Expression))
            return false;

        Expression other = (Expression) obj;
        return operator.equals(other.operator)
                && operands.size() == other.operands.size()
                && operands.equals(other.operands);

    }

    @Override
    public int hashCode() {
        return operator.hashCode() * operands.hashCode();
    }
}
