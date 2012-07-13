package cs412.dinghyprop.interpreter;

import junit.framework.Assert;
import org.testng.annotations.Test;

/**
 * TestNG for Parser
 */
public class ParserTest {
    String[] exprs = {
            "(hey there (brother) 6)",
            "(hey there(brother) 6)",
            "(hey there \n(brother) 6)",
            "(hey there\n(brother)6)",
            "(hey there \r\n(brother)\n6)",
           "(hey there\r\n(brother)\r\n6)"
    };
    String mathy = "(+ (-(* 1 2)(/ 1 2))(^ 2 3))";
    String exprThrows = "(+ 1 (- 1 3)";

    @Test
    public void testParse() throws Exception {
        Expression expression = new Expression("hey");
        expression.addOperand("there");
        expression.addOperand(new Expression("brother"));
        expression.addOperand(6);

        for (String string : exprs) {
            Parser p = new Parser(string);
            Assert.assertTrue(expression.equals(p.parse()));
        }

        Expression add = new Expression("+");
        Expression subtract = new Expression("-");
        Expression multiply = new Expression("*");
        multiply.addOperand(1);
        multiply.addOperand(2);
        subtract.addOperand(multiply);
        Expression divide = new Expression("/");
        divide.addOperand(1);
        divide.addOperand(2);
        subtract.addOperand(divide);
        add.addOperand(subtract);
        Expression raise = new Expression("^");
        raise.addOperand(2);
        raise.addOperand(3);
        add.addOperand(raise);

        Parser p = new Parser(mathy);
        Assert.assertTrue(add.equals(p.parse()));

        Parser p0 = new Parser(exprThrows);
        try {
            p0.parse();
        } catch (ParsingException e) {
            return;
        }
        Assert.assertTrue("Parser should fail on 'exprThrows'.", false);
    }
}
