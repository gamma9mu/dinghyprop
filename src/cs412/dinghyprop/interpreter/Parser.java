package cs412.dinghyprop.interpreter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * A (very) recursive descent parser for the generated programs.
 */
public final class Parser {
    private boolean originator;
    private Lexer lex = null;

    /**
     * Create a program parser.
     * @param inputStream    The program text.
     * @throws ParsingException if a parsing error occurs.
     */
    public Parser(InputStream inputStream) throws ParsingException {
        originator = true;
        lex = new Lexer(inputStream);
        checkStart();
    }

    /**
     * Convenience constructor for a program in string form.
     * @param inputString    The program text.
     * @throws ParsingException if a parsing error occurs.
     */
    public Parser(String inputString) throws ParsingException {
        originator = true;
        lex = new Lexer(new ByteArrayInputStream(inputString.getBytes()));
        checkStart();
    }

    /**
     * Ensures the program's first token is a '('.
     * @throws ParsingException if this assumption fails
     */
    private void checkStart() throws ParsingException {
        Token t = lex.nextToken();
        if (t.type != Token.TYPE.PAREN_OPEN) {
            throw new ParsingException("Program does not begin with '('.");
        }
    }

    /**
     * Internal constructor to maintain lexer state.
     * @param lexer    The current lexer.
     */
    private Parser(Lexer lexer) {
        originator = false;
        lex = lexer;
    }

    /**
     * Perform the actual parsing of the input program.
     * @return  an expression object that can be interpreted by an
     *      {@code Interpreter}.
     * @throws ParsingException if a parsing error occurs.
     */
    public Expression parse() throws ParsingException {
        Token first = lex.nextToken();
        if (first.type != Token.TYPE.SYMBOL) {
            throw new ParsingException("Operator must be a symbol.");
        }

        Expression expr = new Expression(first.text);
        Token t = lex.nextToken();
        while (t.type != Token.TYPE.PAREN_CLOSE && t.type != Token.TYPE.EOF) {
            Object obj;
            if (t.type == Token.TYPE.PAREN_OPEN) {
                obj = new Parser(lex).parse();
            } else if (t.type == Token.TYPE.SYMBOL) {
                obj = t.text;
            } else if (t.type == Token.TYPE.NUMBER) {
                obj = Value.newInt(Integer.parseInt(t.text));
            } else {
                throw new ParsingException("Unknown token type: " + t.type);
            }
            expr.addOperand(obj);
            t = lex.nextToken();
        }

        if (t.type == Token.TYPE.EOF) {
            throw new ParsingException("Unexpected end of input.");
        }

        if (originator) {
            Token last = lex.nextToken();
            if (last.type != Token.TYPE.EOF) {
                throw new ParsingException("Expected end of input.  Got: " + last.type);
            }
        }

        return expr;
    }

    /**
     * Testing main
     * @param args    ignored
     */
    @SuppressWarnings({"HardcodedLineSeparator", "SuppressionAnnotation"})
    public static void main(String[] args) throws ParsingException {
        String[] exprs = {
            "(hey der (broder) 6)",
            "(hey der(broder) 6)",
            "(hey der \n(broder) 6)",
            "(hey der\n(broder)6)",
            "(hey der \r\n(broder)\n6)",
            "(hey der\r\n(broder)\r\n6)"
        };

        for (String str : exprs) {
            System.out.println(" Original: " + str);
            Expression expr = new Parser(new ByteArrayInputStream(str.getBytes())).parse();
            System.out.println("From expr: " + expr);
            System.out.println();
        }
    }
}
