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
     * Ensures the program's first token is a '('.
     *
     * @throws IOException if reading the input fails
     * @throws ParsingException if this assumption fails
     */
    private void checkStart() throws ParsingException, IOException {
        lexer.nextToken();
        if (lexer.ttype != '(') {
            throw new ParsingException("Program does not begin with '('.");
        }
    }

    /**
     * Performs the parsing of the input program.
     *
     * @return  an expression object that can be interpreted by an Interpreter
     * @throws ParsingException if a parsing error occurs.
     */
    public Expression parse() throws ParsingException {
        // Trap reader exceptions
        try {
            checkStart();

            // Test for a proper start.
            if (lexer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new ParsingException("Expected symbol. Got: " + lexer.ttype);
            }

            Expression expr = new Expression(lexer.sval);

            int t = lexer.nextToken();
            while (t != StreamTokenizer.TT_EOF) {
                if (t == ')') {
                    if (stack.empty())
                        break;
                    stack.peek().addOperand(expr);
                    expr = stack.pop();
                } else if (t == '(') { // descend into a sub-expression
                    stack.push(expr);
                    if (lexer.nextToken() != StreamTokenizer.TT_WORD) {
                        throw new ParsingException("Expected symbol. Got: " + lexer.ttype);
                    }
                    expr = new Expression(lexer.sval);
                } else if (t == StreamTokenizer.TT_WORD) {
                    try {
                        // test for a number
                        expr.addOperand(Value.newInt(Integer.parseInt(lexer.sval)));
                    } catch (NumberFormatException ignored) {
                        // fall back on a symbol
                        expr.addOperand(lexer.sval);
                    }
                } else {
                    throw new ParsingException("Unknown token type: " + lexer.ttype);
                }
                t = lexer.nextToken();
            }

            // Ensure the S-exp is terminated
            if (t != ')') {
                throw new ParsingException("Unexpected end of input.");
            }

            // Ensure the input has ended
            if (lexer.nextToken() != StreamTokenizer.TT_EOF) {
                throw new ParsingException("Expected end of input.  Got: " + lexer.ttype);
            }

            return expr;
        } catch (IOException e) {
            // Wrap reader exceptions in ParsingException
            throw new ParsingException("Error reading program text.", e);
        }
    }
}
