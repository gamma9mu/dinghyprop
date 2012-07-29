/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.interpreter;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TestNG for Parser
 */
public class ParserTest {
    private String[] exprs = {
            "(hey there (brother) 6)",
            "(hey there(brother) 6)",
            "(hey there \n(brother) 6)",
            "(hey there\n(brother)6)",
            "(hey there \r\n(brother)\n6)",
            "(hey there\r\n(brother)\r\n6)"
    };

    @Test
    public void testParseSimple() throws Exception {
        Expression expression = new Expression("hey");
        expression.addOperand("there");
        expression.addOperand(new Expression("brother"));
        expression.addOperand(Value.newInt(6));

        Assert.assertTrue(expression.equals(expression));

        for (String string : exprs) {
            Parser p = new Parser(string);
            Assert.assertTrue(expression.equals(p.parse()));
        }
    }

    @Test
    public void testParseMathy() throws Exception {
        Expression add = new Expression("+");
        Expression subtract = new Expression("-");
        Expression multiply = new Expression("*");
        multiply.addOperand(Value.newInt(1));
        multiply.addOperand(Value.newInt(2));
        subtract.addOperand(multiply);
        Expression divide = new Expression("/");
        divide.addOperand(Value.newInt(1));
        divide.addOperand(Value.newInt(2));
        subtract.addOperand(divide);
        add.addOperand(subtract);
        Expression raise = new Expression("^");
        raise.addOperand(Value.newInt(2));
        raise.addOperand(Value.newInt(3));
        add.addOperand(raise);

        String mathy = "(+ (-(* 1 2)(/ 1 2))(^ 2 3))";
        Parser p = new Parser(mathy);
        Assert.assertTrue(add.equals(p.parse()));
    }

    @Test
    public void testParseThrows() throws Exception {
        String exprThrows = "(+ 1 (- 1 3)";
        Parser p0 = new Parser(exprThrows);
        try {
            p0.parse();
        } catch (ParsingException e) {
            return;
        }
        Assert.fail("Parser should fail on 'exprThrows'.");
    }
}
