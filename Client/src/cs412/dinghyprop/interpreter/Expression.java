/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the AST of a generated program.
 */
public final class Expression {
    /**
     * Node contents
     */
    private String operator;

    /**
     * Subtrees
     */
    private List<Object> operands;

    /**
     * Creates an Expression object from an S-expression.
     *
     * @param program    the text of the expression tree
     * @return an Expression tree corresponding to {@code program}'s text
     * @throws ParsingException if a problem occurs parsing {@code program}
     */
    public static Expression fromString(String program) throws ParsingException {
        Parser p = new Parser(program);
        return p.parse();
    }

    /**
     * Creates a new expression tree node.
     *
     * @param operator    the operator at this node
     */
    public Expression(String operator) {
        this.operator = operator;
        operands = new ArrayList<Object>(3);
    }

    /**
     * Appends an expression tree or value as an operand to this node.  The
     * argument should be either an Expression object or a Value object.
     * <b>Important:</b> This method will not check the type of operand.
     *
     * @param operand    the subtree or value to add
     */
    public void addOperand(Object operand) {
        operands.add(operand);
    }

    /**
     * @return  the name of the function or operator used to evaluate this node
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @return  an array of this expression operands
     */
    public Object[] getOperands() {
        return operands.toArray();
    }

    /**
     * @return  a flat, S-expression representation of this expression
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
