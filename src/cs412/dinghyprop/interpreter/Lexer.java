package cs412.dinghyprop.interpreter;

import java.io.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Performs lexical analysis on a program.
 */
public class Lexer {
    private static Logger log = Logger.getLogger("Lexer");
    private static final Pattern NUMBER = Pattern.compile("\\d+");
    private BufferedReader in;
    private String lastLine = null;
    private String line = null;
    private int length = 0;
    private int offset = 0;

    /**
     * Create a new Lexer object.
     * @param inputStream    The input stream containing the program text to
     *                       lex.
     */
    public Lexer(InputStream inputStream) {
        in = new BufferedReader(new InputStreamReader(inputStream));
        getNextLine();
    }

    /**
     * Obtain the next token from the lexer.
     * @return  A {@code Token} object representing the next segment of input.
     */
    public Token nextToken() {
        log.entering("Lexer", "nextToken");

        StringBuilder text = new StringBuilder(10);
        Token token = null;
        boolean done = false;
        do {
            char c = getChar();
            if (text.length() == 0) {
                switch (c) {
                    case '(':
                        token = new Token(Token.TYPE.PAREN_OPEN, "(");
                        done = true;
                        break;
                    case ')':
                        token = new Token(Token.TYPE.PAREN_CLOSE, ")");
                        done = true;
                        break;
                    case '\0':
                        token = new Token(Token.TYPE.EOF, null);
                        done = true;
                        break;
                    case ' ':
                        break;
                    default:
                        text.append(c);
                        break;
                }
            } else { // (text.length() != 0)
                switch (c) {
                    case '(':
                        reject();
                        done = true;
                        break;
                    case ')':
                        reject();
                        done = true;
                        break;
                    case '\0':
                        reject();
                        done = true;
                        break;
                    case ' ':
                        done = true;
                        break;
                    default:
                        text.append(c);
                        break;
                }
            }
        } while (!done);
        if (token == null) {
            String str = text.toString();
            if (NUMBER.matcher(str).matches())
                token = new Token(Token.TYPE.NUMBER, str);
            else
                token = new Token(Token.TYPE.SYMBOL, str);
        }
        return token;
    }

    /**
     * "Reject" the last character returned by {@code getChar()}, pushing it
     * back into the read buffer.
     */
    private void reject() {
        log.entering("Lexer", "reject");
        offset--;
    }

    /**
     * Get the next character from the input.
     * @return  The next character read or '\0' at end of input.
     */
    private char getChar() {
        log.entering("Lexer", "getChar");

        if (offset < 0) {
            char c = lastLine.charAt(lastLine.length() + offset);
            offset++;
            return c;
        }
        if (offset == length) {
            getNextLine();
        }
        if (line == null) {
            return '\0';
        }
        return line.charAt(offset++);
    }

    /**
     * Read the next line from the input and update the lex state.
     */
    private void getNextLine() {
        log.entering("Lexer", "getNextLine");

        lastLine = line;
        try {
            do {
                line = in.readLine();
                log.fine("line: " + line);
            } while (line != null && "".compareTo(line) == 0);
        } catch (IOException e) {
            log.warning(e.getLocalizedMessage());
        }

        offset = 0;

        if (line == null) {
            length = 0;
        } else {
            length = line.length();
        }
    }

    /**
     * Test runner.  Prints the tokens in the string.
     * @param str    The string to parse.
     */
    private static void runLexer(String str) {
        System.out.println("Line: {{ " + str + " }}");
        Lexer l = new Lexer(new ByteArrayInputStream(str.getBytes()));
        Token t;
        do {
            t = l.nextToken();
            System.out.println(t);
        } while (t.type != Token.TYPE.EOF);
    }

    /**
     * Testing main.
     * @param args    imaginary arguments
     */
    public static void main(String[] args) {
        runLexer("(hey der (broder) 6)");
        runLexer("(hey der \n(broder) 6)");
        runLexer("(hey der\n(broder)6)");
        runLexer("(hey der \r\n(broder)\n6)");
        runLexer("(hey der\r\n(broder)\r\n6)");
    }
}
