/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.interpreter;

import cs412.dinghyprop.simulator.ExecutionException;
import cs412.dinghyprop.simulator.ISimulator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Program interpreter
 *
 * Objects of this class run GP generated programs by the following process:
 * <ul>
 *     <li>Have a parser convert the program to an AST</li>
 *     <li>Walk the AST left to right, depth first</li>
 *     <li>Nodes are evaluated in post-order</li>
 *     <li>Function invocations and variable references are passed to a
 *     simulator</li>
 * </ul>
 */
public class Interpreter {
    private static Logger log = Logger.getLogger("Interpreter");

    /**
     * The simulator tasked with managing the environment, function invocation,
     * and variable references
     */
    private ISimulator simulator;

    /**
     * AST
     */
    private Expression program;

    /**
     * Whether execution of the program raised an exception
     */
    private boolean programRaised = false;

    /**
     * The original program text
     */
    private String programText;

    /**
     * Creates an interpreter.
     *
     * @param simulator    the Simulator that provides context
     * @param program      the program to execute
     */
    public Interpreter(ISimulator simulator, String program) throws ParsingException {
        this.simulator = simulator;
        programText = program;
        this.program = new Parser(program).parse();
    }

    /**
     * Evaluate the program once.
     */
    public void execute() {
        if (! canContinue())
            return;
        try {
            evaluateExpression(program);
        } catch (ExecutionException e) {
            log.log(Level.INFO, "execute throwing:", e);
            log.info("Exception caused by :" + programText);
            programRaised = true;
        }
    }

    /**
     * Evaluate the program in the simulation for a given count of iterations.
     * <p>
     * Evaluation will end if the simulation indicates that it cannot continue.
     * 
     * @param iterations    the number of times evaluate the program
     */
    public void run(int iterations) {
        while (canContinue() && iterations > 0) {
            execute();
            iterations--;
        }
    }

    /**
     * Evaluates an Expression tree.
     *
     * @param expr    the Expression tree's root
     * @return  the resulting value from evaluating {@code expr}
     */
    private Value evaluateExpression(Expression expr) throws ExecutionException {
        String operator = expr.getOperator();
        Object[] operands = expr.getOperands();
        int operandCount = operands.length;

        if (operandCount == 0) {
            simulator.invoke(operator);
            return Value.NULL_VALUE;
        }

        Value[] results = new Value[operandCount];
        for (int i = 0; i < operandCount; i++) {
            Value result = Value.NULL_VALUE;
            if (operands[i] instanceof Expression) {
                result = evaluateExpression((Expression) operands[i]);
            } else if (operands[i] instanceof Value) {
                result = (Value) operands[i];
            } else if (operands[i] instanceof String) {
                result = Value.newInt(simulator.reference((String) operands[i]));
            }
            results[i] = result;
        }
        return evaluateOperator(operator, results);
    }

    /**
     * Evaluates an operator on a set of values.
     *
     * @param operator    name of the operator
     * @param operands    value array to operate on
     * @return  the value returned by applying {@code operator} on
     * {@code operands}
     */
    private Value evaluateOperator(String operator, Value[] operands) {
        if (operator.compareTo("if") == 0) {
            return evalIf(operands);
        } else if (operator.compareTo("+") == 0) {
            return evalAdd(operands);
        } else if (operator.compareTo("-") == 0) {
            return evalSub(operands);
        } else if (operator.compareTo("*") == 0) {
            return evalMult(operands);
        } else if (operator.compareTo("/") == 0) {
            return evalDivSafe(operands);
        } else if (operator.compareTo("^") == 0) {
            return evalExp(operands);
        } else if (operator.compareTo("<") == 0) {
            return evalLess(operands);
        } else if (operator.compareTo("<=") == 0) {
            return evalLessOrEqual(operands);
        } else if (operator.compareTo(">") == 0) {
            return evalGreater(operands);
        } else if (operator.compareTo(">=") == 0) {
            return evalGreaterOrEqual(operands);
        } else if (operator.compareTo("==") == 0) {
            return evalEqual(operands);
        } else if (operator.compareTo("!=") == 0) {
            return evalNotEqual(operands);
        }
        return Value.NULL_VALUE;
    }

    /**
     * Checks if all the values in an array are in strictly decreasing order.
     *
     * @param operands    value array
     * @return  true if the values are strictly decreasing, false otherwise
     */
    private Value evalLess(Value[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i].addend() >= operands[i + 1].addend()) {
                return Value.FALSE_VALUE;
            }
        }
        return Value.TRUE_VALUE;
    }

    /**
     * Checks if all the values in an array are in non-increasing order.
     *
     * @param operands    value array
     * @return  true if the values are non-increasing, false otherwise
     */
    private Value evalLessOrEqual(Value[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i].addend() > operands[i + 1].addend()) {
                return Value.FALSE_VALUE;
            }
        }
        return Value.TRUE_VALUE;
    }

    /**
     * Checks if all the values in an array are in strictly increasing order.
     *
     * @param operands    value array
     * @return  true if the values are strictly increasing, false otherwise
     */
    private Value evalGreater(Value[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i].addend() <= operands[i + 1].addend()) {
                return Value.FALSE_VALUE;
            }
        }
        return Value.TRUE_VALUE;
    }

    /**
     * Checks if all the values in an array are in non-decreasing order.
     *
     * @param operands    value array
     * @return  true if the values are non-decreasing, false otherwise
     */
    private Value evalGreaterOrEqual(Value[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i].addend() < operands[i + 1].addend()) {
                return Value.FALSE_VALUE;
            }
        }
        return Value.TRUE_VALUE;
    }

    /**
     * Check an array for value equality.
     *
     * @param operands    value array
     * @return  true if all the values are equal, false otherwise
     */
    private Value evalEqual(Value[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (!operands[i].equals(operands[i+1]))
                return Value.FALSE_VALUE;
        }
        return Value.TRUE_VALUE;
    }

    /**
     * Check an array for value inequality.
     *
     * @param operands    value array
     * @return  true if all the values are different, false otherwise
     */
    private Value evalNotEqual(Value[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            for (int j = i + 1; j < operands.length; j++) {
                if (operands[i].equals(operands[j])) {
                    return Value.FALSE_VALUE;
                }
            }
        }
        return Value.TRUE_VALUE;
    }

    /**
     * Evaluates an if construct.
     *
     * @param operands    the boolean, true branch, and optional false branch
     * @return  If the value of the first argument is true, the value of the
     * second argument is returned.  Otherwise, the value of the third argument
     * is returned (or Value.NULL_VALUE if no third argument was provided).
     */
    private Value evalIf(Value[] operands) {
        if (operands[0].bool())
            return operands[1];
        else
            return (operands.length > 2) ? operands[2] : Value.NULL_VALUE;
    }

    /**
     * Sums the arguments.
     *
     * @param operands    Te values to sum
     * @return  the sum of operands
     */
    private Value evalAdd(Value[] operands) {
        int accum = 0;
        for (Value value : operands) {
            accum += value.addend();
        }
        return Value.newInt(accum);
    }

    /**
     * Sequentially subtracts the values in an array. (Left associative)
     *
     * @param operands    the value array
     * @return  the first value less the second, less the third, etc.
     */
    private Value evalSub(Value[] operands) {
        int accum = operands[0].addend(); // + has 0 as identity
        for (int i = 1; i < operands.length; i++)
            accum -= operands[i].addend();
        return Value.newInt(accum);
    }

    /**
     * Multiplies an array of values together.
     *
     * @param operands    the values to multiply together
     * @return  the product of {@code operands}
     */
    private Value evalMult(Value[] operands) {
        int accum = 1;
        for (Value value : operands) {
            accum *= value.multiplicand();
        }
        return Value.newInt(accum);
    }

    /**
     * Sequentially divides an array of values.  (Left associative)
     *
     * @param operands    the values to divide
     * @return  the first value divided by the second, divided by the third, etc.
     */
    private Value evalDivSafe(Value[] operands) {
        int accum = operands[0].multiplicand();
        for (int i = 1; i < operands.length; i++) {
            int divisor = operands[i].multiplicand();
            if (divisor != 0)
                accum /= divisor;
            else
                accum = 0;
        }
        return Value.newInt(accum);
    }

    /**
     * Exponentiates a series of values. (Left associative)
     *
     * @param operands    the value array
     * @return  the first to the power of the second, to the power of the third,
     * etc.
     */
    private Value evalExp(Value[] operands) {
        int accum = operands[0].addend(); // + has 0 as identity
        for (int i = 1; i < operands.length; i++) {
            int divisor = operands[i].multiplicand();
            if (divisor != 0)
                Math.pow(accum, divisor);
            else
                accum = 1;
        }
        return Value.newInt(accum);
    }

    /**
     * @return  the fitness of the program, unless the program caused an
     * exception to be raised.  In that case, return 0.
     */
    public int getFitness() {
        if (programRaised)
            return 0;
        return simulator.getFitness();
    }

    /**
     * @return  whether execution can continue
     */
    public boolean canContinue() {
        return simulator.canContinue() && ! programRaised;
    }
}
