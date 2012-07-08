package cs412.dinghyprop.interpreter;

import cs412.dinghyprop.simulator.Simulator;

import java.util.logging.Logger;

/**
 * Dinghy navigation program interpreter
 */
public class Interpreter {
    private static Logger log = Logger.getLogger("Interpreter");
    private Simulator simulator;
    private Expression program = null;
    private boolean programRaised = false;

    /**
     * Create an interpreter for a {@code Simulator} and program combination.
     * @param simulator    The {@code Simulator} that provides context to the
     *                     interpreter
     * @param program      The program to execute
     */
    public Interpreter(Simulator simulator, String program) {
        this.simulator = simulator;
        try {
            this.program = new Parser(program).parse();
        } catch (ParsingException ignored) { }
    }

    /**
     * Evaluate the program in the simulation
     */
    public void execute() {
        if (program == null) {
            return;
        }

        try {
            evaluateExpression(program);
        } catch (Exception e) {
            log.throwing("Interpreter", "execute", e);
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
        if (program == null) {
            return;
        }

        while (canContinue() && iterations > 0) {
            evaluateExpression(program);
            iterations--;
        }
    }

    /**
     * Evaluate an {@code Expression} tree
     * @param expr    The {@code Expression} tree's root
     * @return  The resulting value from evaluating {@code expr}
     */
    private Object evaluateExpression(Expression expr) {
        String operator = expr.getOperator();
        Object[] operands = expr.getOperands();
        int operandCount = operands.length;

        if (operandCount == 0) {
            simulator.invoke(operator);
            return null;
        }

        Object[] results = new Object[operandCount];
        for (int i = 0; i < operandCount; i++) {
            Object result = null;
            if (operands[i] instanceof Expression) {
                result = evaluateExpression((Expression) operands[i]);
            } else if (operands[i] instanceof Integer) {
                result = operands[i];
            } else if (operands[i] instanceof String) {
                result = simulator.reference((String) operands[i]);
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
    private Object evaluateOperator(String operator, Object[] operands) {
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
        return null;
    }

    /**
     * Check if all the values in an array are in strictly decreasing order.
     * @param operands    The value array
     * @return  {@code true} if the values are strictly decreasing,
     * {@code false} otherwise.
     */
    private Object evalLess(Object[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i] == null) return true;
            if (operands[i+1] == null) return false;
            if ((Integer) operands[i] >= (Integer) operands[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all the values in an array are in non-increasing order.
     * @param operands    The value array
     * @return  {@code true} if the values are non-increasing,
     * {@code false} otherwise.
     */
    private Object evalLessOrEqual(Object[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i] == null) return true;
            if (operands[i+1] == null) return false;
            if ((Integer) operands[i] > (Integer) operands[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all the values in an array are in strictly increasing order.
     * @param operands    The value array
     * @return  {@code true} if the values are strictly increasing,
     * {@code false} otherwise.
     */
    private Object evalGreater(Object[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i] == null) return false;
            if (operands[i+1] == null) return true;
            if ((Integer) operands[i] <= (Integer) operands[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all the values in an array are in non-decreasing order.
     * @param operands    The value array
     * @return  {@code true} if the values are non-decreasing,
     * {@code false} otherwise.
     */
    private Object evalGreaterOrEqual(Object[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i] == null) return false;
            if (operands[i+1] == null) return true;
            if ((Integer) operands[i] < (Integer) operands[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check an array for value equality.
     * @param operands    The value array
     * @return  True if all the values are equal, false otherwise.
     */
    private Object evalEqual(Object[] operands) {
        if (operands[0] == null) return false;
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i+1] == null) return false;
            if (!operands[i].equals(operands[i + 1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check an array for value inequality.
     * @param operands    The value array
     * @return  {@code true} if all the values are different, {@code false} otherwise.
     */
    private Object evalNotEqual(Object[] operands) {
        for (int i = 0; i < operands.length - 1; i++) {
            if (operands[i] == null) return true;
            for (int j = i + 1; j < operands.length; j++) {
                if (operands[j] == null) return true;
                if (operands[i].equals(operands[j])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Evaluate an if construct.
     * @param operands    The boolean, true, and option false statements.
     * @return  If the value of the first argument is {@code true}, he value of
     * the second argument is returned.  Otherwise, the value of the third
     * argument is returned (or {@code null} if no third argument was provided).
     */
    private Object evalIf(Object[] operands) {
        if ((Boolean) operands[0])
            return operands[1];
        else
            return (operands.length > 2) ? operands[2] : null;
    }

    /**
     * Sum the arguments.
     * @param operands    The values to sum
     * @return  the sum of {@code operands}
     */
    private int evalAdd(Object[] operands) {
        int accum = 0;
        for (Object obj : operands) {
            if (obj != null)
                accum += (Integer) obj;
        }
        return accum;
    }

    /**
     * Sequentially subtract values in an array. (Left associative)
     * @param operands    The value array
     * @return  the first value less the second less the third, etc.
     */
    private int evalSub(Object[] operands) {
        int accum = (operands[0] != null) ? (Integer) operands[0] : 0;
        for (int i = 1; i < operands.length; i++)
            if (operands[i] != null)
                accum -= (Integer) operands[i];
        return accum;
    }

    /**
     * Multiply an array of values together.
     * @param operands    The values to multiply together
     * @return  The product of {@code operands}
     */
    private int evalMult(Object[] operands) {
        int accum = 1;
        for (Object obj : operands) {
            if (obj != null)
                accum *= (Integer) obj;
        }
        return accum;
    }

    /**
     * Sequentially divide an array of values.  (Left associative)
     * @param operands    The values to divide
     * @return  The first value divided by the second divided by the third, etc.
     */
    private int evalDivSafe(Object[] operands) {
        int accum = (operands[0] != null) ? (Integer) operands[0] : 0;
        for (int i = 1; i < operands.length; i++) {
            int divisor = (operands[i] != null) ? (Integer) operands[i] : 1;
            if (divisor != 0)
                accum /= divisor;
            else
                accum = 0;
        }
        return accum;
    }

    /**
     * Exponentiate a series of values. (Left associative)
     * @param operands    The value array
     * @return  The first to the power of the second to the power of the third,
     * etc.
     */
    private int evalExp(Object[] operands) {
        int accum = (operands[0] != null) ? (Integer) operands[0] : 0;
        for (int i = 1; i < operands.length; i++) {
            if (operands[i] == null)
                accum = 0;
            else
                accum = (int) Math.pow(accum, (Integer) operands[i]);
        }
        return accum;
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
