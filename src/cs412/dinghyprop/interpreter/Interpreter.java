package cs412.dinghyprop.interpreter;

import cs412.dinghyprop.simulator.ExecutionException;
import cs412.dinghyprop.simulator.Simulator;

import java.util.logging.Logger;

/**
 * Dinghy navigation program interpreter
 */
public class Interpreter {
    private static Logger log = Logger.getLogger("Interpreter");
    private Simulator simulator;
    private Expression program;
    private boolean programRaised = false;
    private String programText;

    /**
     * Create an interpreter for a {@code Simulator} and program combination.
     * @param simulator    The {@code Simulator} that provides context to the
     *                     interpreter
     * @param program      The program to execute
     */
    public Interpreter(Simulator simulator, String program) throws ParsingException {
        this.simulator = simulator;
        programText = program;
        this.program = new Parser(program).parse();
    }

    /**
     * Evaluate the program in the simulation
     */
    public void execute() {
        try {
            evaluateExpression(program);
        } catch (ExecutionException e) {
            log.throwing("Interpreter", "execute", e);
            log.info("Exception cause by :" + programText);
            programRaised = true;
        }

    }

    /**
     * Evaluate the program in the simulation for a given count of iterations.
     *
     * Evaluation will end if the simulation indicates that it cannot continue.
     * 
     * @param iterations    The iteration count.
     */
    public void run(int iterations) {
        while (canContinue() && iterations > 0) {
            execute();
            iterations--;
        }
    }

    /**
     * Evaluate an {@code Expression} tree
     * @param expr    The {@code Expression} tree's root
     * @return  The resulting value from evaluating {@code expr}
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
     * Evaluate an operator on a set of values
     * @param operator    The name of the operator
     * @param operands    The value array to operate on
     * @return  The value returned by applying {@code operator} on
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
     * Check if all the values in an array are in strictly decreasing order.
     * @param operands    The value array
     * @return  {@code true} if the values are strictly decreasing,
     * {@code false} otherwise.
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
     * Check if all the values in an array are in non-increasing order.
     * @param operands    The value array
     * @return  {@code true} if the values are non-increasing,
     * {@code false} otherwise.
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
     * Check if all the values in an array are in strictly increasing order.
     * @param operands    The value array
     * @return  {@code true} if the values are strictly increasing,
     * {@code false} otherwise.
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
     * Check if all the values in an array are in non-decreasing order.
     * @param operands    The value array
     * @return  {@code true} if the values are non-decreasing,
     * {@code false} otherwise.
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
     * @param operands    The value array
     * @return  True if all the values are equal, false otherwise.
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
     * @param operands    The value array
     * @return  {@code true} if all the values are different, {@code false} otherwise.
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
     * Evaluate an if construct.
     * @param operands    The boolean, true, and option false statements.
     * @return  If the value of the first argument is {@code true}, he value of
     * the second argument is returned.  Otherwise, the value of the third
     * argument is returned (or {@code Value.NULL_VALUE} if no third argument
     * was provided).
     */
    private Value evalIf(Value[] operands) {
        if (operands[0].bool())
            return operands[1];
        else
            return (operands.length > 2) ? operands[2] : Value.NULL_VALUE;
    }

    /**
     * Sum the arguments.
     * @param operands    The values to sum
     * @return  the sum of {@code operands}
     */
    private Value evalAdd(Value[] operands) {
        int accum = 0;
        for (Value value : operands) {
            accum += value.addend();
        }
        return Value.newInt(accum);
    }

    /**
     * Sequentially subtract values in an array. (Left associative)
     * @param operands    The value array
     * @return  the first value less the second less the third, etc.
     */
    private Value evalSub(Value[] operands) {
        int accum = operands[0].addend(); // + has 0 as identity
        for (int i = 1; i < operands.length; i++)
                accum -= operands[i].multiplicand();
        return Value.newInt(accum);
    }

    /**
     * Multiply an array of values together.
     * @param operands    The values to multiply together
     * @return  The product of {@code operands}
     */
    private Value evalMult(Value[] operands) {
        int accum = 1;
        for (Value value : operands) {
            accum *= value.multiplicand();
        }
        return Value.newInt(accum);
    }

    /**
     * Sequentially divide an array of values.  (Left associative)
     * @param operands    The values to divide
     * @return  The first value divided by the second divided by the third, etc.
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
     * Exponentiate a series of values. (Left associative)
     * @param operands    The value array
     * @return  The first to the power of the second to the power of the third,
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
     * Get the fitness of the program as computed by the simulator.
     * @return  The fitness of the program, unless the program caused an
     * exception to be raised.  In that case, return 0.
     */
    public int getFitness() {
        if (programRaised)
            return 0;
        return simulator.getFitness();
    }

    /**
     * Determine whether execution can continue.
     * @return  Whether execution can continue
     */
    public boolean canContinue() {
        return simulator.canContinue();
    }
}
