/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.interpreter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TestNG for Expression
 */
public class ExpressionTest {
    Expression main = null;
    String mainOperator = "mainOperator Text";

    Expression deep0 = null;
    Expression deep1 = null;

    @BeforeMethod
    public void setUp() throws Exception {
        main = new Expression(mainOperator);
        deep0 = createDeep();
        deep1 = createDeep();
    }

    private Expression createDeep() {
        Expression expression = new Expression("top");
        expression.addOperand(new Expression("no-arg"));

        Expression subExpr = new Expression("subExp");
        subExpr.addOperand(4);
        subExpr.addOperand(Value.newInt(10));
        subExpr.addOperand(Value.FALSE_VALUE);
        subExpr.addOperand(Value.TRUE_VALUE);
        subExpr.addOperand(Value.NULL_VALUE);
        subExpr.addOperand(Value.newInt(100));

        return expression;
    }

    @Test
    public void testFromString() throws Exception {
        String text = "(+ (^ 1 (/ 2 front)) (* 12 (move)))";
        Parser p = new Parser(text);
        Assert.assertEquals(Expression.fromString(text), p.parse());
    }

    @Test
    public void testAddOperand() throws Exception {
        Assert.assertEquals(main.getOperands().length, 0);

        int value = 6;
        main.addOperand(value);
        Assert.assertEquals(main.getOperands().length, 1);
        Assert.assertEquals(main.getOperands()[0], value);

        int value1 = 10;
        main.addOperand(value1);
        Assert.assertEquals(main.getOperands().length, 2);
        Assert.assertEquals(main.getOperands()[1], value1);
    }

    @Test
    public void testGetOperator() throws Exception {
        Assert.assertEquals(main.getOperator(), mainOperator);
    }

    @Test
    public void testGetOperands() throws Exception {
        Object[] op0 = deep0.getOperands();
        Object[] op1 = deep1.getOperands();

        Assert.assertNotNull(op0);
        Assert.assertNotNull(op1);

        Assert.assertTrue(op0.length > 0);
        Assert.assertTrue(op1.length > 0);

        Assert.assertEquals(op0, op1);
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertTrue(deep0.equals(deep1));
        Assert.assertFalse(deep0.equals(main));
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertTrue(deep0.hashCode() == deep1.hashCode());
        Assert.assertFalse(deep0.hashCode() == main.hashCode());
    }
}
