/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.interpreter;

import java.io.*;
import java.util.Stack;

/**
 * Stack-based parser for the GP generated programs
 */
public final class Parser {
    /**
     * std. lib. tokenizer used as a lexical analyzer
     */
    private StreamTokenizer lexer;

    /**
     * AST stack
     */
    private Stack<Expression> stack = new Stack<Expression>();

    /**
     * Parses a program from an InputStream.
     *
     * @param inputStream    the program text
     */
    public Parser(InputStream inputStream) {
        lexer = createLexer(new BufferedReader(new InputStreamReader(inputStream)));
    }

    /**
     * Convenience constructor for a programs in string form
     *
     * @param inputString    the program text
     */
    public Parser(String inputString) {
        Reader r = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(inputString.getBytes())));
        lexer = createLexer(r);
    }

    /**
     * Configure the lexical analyzer.
     *
     * @param reader    the input stream reader
     * @return  an s-expression lexer
     */
    private StreamTokenizer createLexer(Reader reader) {
        StreamTokenizer tokenizer = new StreamTokenizer(reader);

        tokenizer.resetSyntax();
        tokenizer.eolIsSignificant(false);
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.wordChars('!', '!');
        tokenizer.wordChars('*', 'z');

        return tokenizer;
    }

    /**
     * Ensures the program's first token is '(' and is followed by a symbol.
     * <p>
     * The first symbol in the input will be the current value of the lexer
     * after this call completes.
     *
     * @throws IOException if reading the input fails
     * @throws ParsingException if this assumption fails
     */
    private void checkStart() throws ParsingException, IOException {
        lexer.nextToken();
        if (lexer.ttype != '(')
            throw new ParsingException("Program does not begin with '('.");

        if (lexer.nextToken() != StreamTokenizer.TT_WORD)
            throw new ParsingException("Expected symbol. Got: " + lexer.ttype);
    }

    /**
     * Parses the input program.
     *
     * @return  an expression object that can be interpreted by an Interpreter
     * @throws ParsingException if a parsing error occurs.
     */
    public Expression parse() throws ParsingException {
        // Trap reader exceptions
        try {
            checkStart();
            Expression expr = parseSymbolicExpression();
            checkEnd();
            return expr;
        } catch (IOException e) {
            // Wrap reader exceptions in ParsingException
            throw new ParsingException("Error reading program text.", e);
        }
    }

    /**
     * Parses the S-Expression from the lexer output.  The lexer should be
     * positioned on the first symbol after the opening parenthesis.
     *
     * @return the parse tree of the input
     * @throws IOException if a read error occurs in the lexer
     * @throws ParsingException if the input cannot be parsed successfully
     */
    private Expression parseSymbolicExpression() throws IOException, ParsingException {
        Expression expr = new Expression(lexer.sval);

        int t = lexer.nextToken();
        while (t != StreamTokenizer.TT_EOF) {
            switch (t) {
                case ')':
                    if (stack.empty())
                        return expr;
                    stack.peek().addOperand(expr);
                    expr = stack.pop();
                    break;
                case '(': // descend into a sub-expression
                    stack.push(expr);
                    if (lexer.nextToken() != StreamTokenizer.TT_WORD) {
                        throw new ParsingException("Expected symbol. Got: " + lexer.ttype);
                    }
                    expr = new Expression(lexer.sval);
                    break;
                case StreamTokenizer.TT_WORD:
                    try {
                        // test for a number
                        expr.addOperand(Value.newInt(Integer.parseInt(lexer.sval)));
                    } catch (NumberFormatException ignored) {
                        // fall back on a symbol
                        expr.addOperand(lexer.sval);
                    }
                    break;
                default:
                    throw new ParsingException("Unknown token type: " + lexer.ttype);
            }
            t = lexer.nextToken();
        }

        throw new ParsingException("Expected end of input.  Got: " + lexer.ttype);
    }

    /**
     * Ensures the program's last token is ')' and is followed by EOF.
     *
     * @throws IOException if reading the input fails
     * @throws ParsingException if this assumption fails
     */
    private void checkEnd() throws ParsingException, IOException {
        if (lexer.ttype != ')')
            throw new ParsingException("Unexpected end of input.");

        if (lexer.nextToken() != StreamTokenizer.TT_EOF)
            throw new ParsingException("Expected end of input.  Got: " + lexer.ttype);
    }
}
