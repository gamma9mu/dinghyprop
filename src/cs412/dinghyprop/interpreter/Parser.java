package cs412.dinghyprop.interpreter;

import java.io.*;

/**
 * A (very) recursive descent parser for the generated programs.
 */
public final class Parser {
    private boolean originator;
    private StreamTokenizer lexer;

    /**
     * Create a program parser.
     * @param inputStream    The program text.
     * @throws ParsingException if a parsing error occurs.
     */
    public Parser(InputStream inputStream) throws ParsingException {
        originator = true;
        lexer = createLexer(new BufferedReader(new InputStreamReader(inputStream)));
        checkStart();
    }

    /**
     * Convenience constructor for a program in string form.
     * @param inputString    The program text.
     * @throws ParsingException if a parsing error occurs.
     */
    public Parser(String inputString) throws ParsingException {
        originator = true;
        Reader r = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(inputString.getBytes())));
        lexer = createLexer(r);
        checkStart();
    }

    /**
     * Setup the lexical analyzer.
     * @param reader    The input stream reader
     * @return  A suitable lexer
     */
    private StreamTokenizer createLexer(Reader reader) {
        StreamTokenizer tokenizer = new StreamTokenizer(reader);

        tokenizer.resetSyntax();
        tokenizer.eolIsSignificant(false);
        tokenizer.whitespaceChars(0, 32);
        tokenizer.wordChars(33, 33);
        tokenizer.wordChars(42, 122);

        return tokenizer;
    }

    /**
     * Ensures the program's first token is a '('.
     * @throws ParsingException if this assumption fails
     */
    private void checkStart() throws ParsingException {
        try {
            lexer.nextToken();
        } catch (IOException e) {
            throw new ParsingException("Error reading program text.", e);
        }
        if (lexer.ttype != '(') {
            throw new ParsingException("Program does not begin with '('.");
        }
    }

    /**
     * Internal constructor to maintain lexer state.
     * @param lexer    The current lexer.
     */
    private Parser(StreamTokenizer lexer) {
        originator = false;
        this.lexer = lexer;
    }

    /**
     * Perform the actual parsing of the input program.
     * @return  an expression object that can be interpreted by an
     *      {@code Interpreter}.
     * @throws ParsingException if a parsing error occurs.
     */
    public Expression parse() throws ParsingException {
        // Trap reader exceptions
        try {
            // Test for a proper start.
            if (lexer.nextToken() != StreamTokenizer.TT_WORD) {
                System.err.println("type: " + lexer.ttype);
                throw new ParsingException("Operator must be a symbol.");
            }

            Expression expr = new Expression(lexer.sval);

            int t = lexer.nextToken();
            while (t != ')' && t != StreamTokenizer.TT_EOF) {
                Object obj;
                if (t == '(') { // descend into a sub-expression
                    obj = new Parser(lexer).parse();
                } else if (t == StreamTokenizer.TT_WORD) {
                    try {
                        // test for a number
                        obj = Value.newInt(Integer.parseInt(lexer.sval));
                    } catch (NumberFormatException ignored) {
                        // fall back on a symbol
                        obj = lexer.sval;
                    }
                } else {
                    throw new ParsingException("Unknown token type: " + lexer.ttype);
                }
                expr.addOperand(obj);
                t = lexer.nextToken();
            }

            // Ensure the S-exp is terminated
            if (t != ')') {
                throw new ParsingException("Unexpected end of input.");
            }

            // If this is the original parser, ensure the input has ended
            if (originator) {
                int last = lexer.nextToken();
                if (last != StreamTokenizer.TT_EOF) {
                    throw new ParsingException("Expected end of input.  Got: " + last);
                }
            }

            return expr;
        } catch (IOException e) {
            // Wrap reader exceptions in ParsingException
            throw new ParsingException("Error reading program text.", e);
        }
    }
}
