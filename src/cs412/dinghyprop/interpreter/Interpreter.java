package cs412.dinghyprop.interpreter;

import cs412.dinghyprop.simulator.Simulator;

public class Interpreter {
    private Simulator simulator;
    private Expression program = null;

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
        evaluateExpression(program);
    }

    private Object evaluateExpression(Expression expr) {
        Object[] operands = expr.getOperands();
        if (operands.length == 0) {
            simulator.invoke(expr.getOperator());
            return null;
        }
        Object[] results = new Object[operands.length];
        for (int i = 0; i < operands.length; i++) {
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
        return evaluateOperator(expr.getOperator(), results);
    }

    @SuppressWarnings({"SuppressionAnnotation", "OverlyLongMethod", "OverlyComplexMethod", "HardcodedFileSeparator"})
    private Object evaluateOperator(String operator, Object[] operands) {
        if (operator.compareTo("") == 0) {
            return null;
        } else if (operator.compareTo("if") == 0) {
            if ((Boolean) operands[0])
                return operands[1];
            else
                return (operands.length > 2) ? operands[2] : null;
        } else if (operator.compareTo("+") == 0) {
            int accum = 0;
            for (Object obj : operands) {
                if (obj != null)
                    accum += (Integer) obj;
            }
            return accum;
        } else if (operator.compareTo("-") == 0) {
            int accum = (operands[0] != null) ? (Integer) operands[0] : 0;
            for (int i = 1; i < operands.length; i++)
                if (operands[i] != null)
                    accum -= (Integer) operands[i];
            return accum;
        } else if (operator.compareTo("*") == 0) {
            int accum = 1;
            for (Object obj : operands) {
                if (obj != null)
                    accum *= (Integer) obj;
            }
            return accum;
        } else if (operator.compareTo("/") == 0) {
            int accum = (operands[0] != null) ? (Integer) operands[0] : 0;
            for (int i = 1; i < operands.length; i++) {
                int divisor = (operands[i] != null) ? (Integer) operands[i] : 1;
                if (divisor != 0)
                    accum /= divisor;
                else
                    accum = 0;
            }
            return accum;
        } else if (operator.compareTo("^") == 0) {
            int accum = (operands[0] != null) ? (Integer) operands[0] : 0;
            for (int i = 1; i < operands.length; i++) {
                if (operands[i] == null)
                    accum = 0;
                else
                    accum = (int) Math.pow(accum, (Integer) operands[i]);
            }
            return accum;
        } else if (operator.compareTo("<") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if (operands[i] == null) return true;
                if (operands[i+1] == null) return false;
                if ((Integer) operands[i] >= (Integer) operands[i + 1]) {
                    return false;
                }
            }
            return true;
        } else if (operator.compareTo("<=") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if (operands[i] == null) return true;
                if (operands[i+1] == null) return false;
                if ((Integer) operands[i] > (Integer) operands[i + 1]) {
                    return false;
                }
            }
            return true;
//            int accum = (operands[0] != null) ? (Integer) operands[0] : 0;
        } else if (operator.compareTo(">") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if (operands[i] == null) return false;
                if (operands[i+1] == null) return true;
                if ((Integer) operands[i] <= (Integer) operands[i + 1]) {
                    return false;
                }
            }
            return true;
        } else if (operator.compareTo(">=") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if (operands[i] == null) return false;
                if (operands[i+1] == null) return true;
                if ((Integer) operands[i] < (Integer) operands[i + 1]) {
                    return false;
                }
            }
            return true;
        } else if (operator.compareTo("==") == 0) {
            if (operands[0] == null) return false;
            for (int i = 0; i < operands.length - 1; i++) {
                if (operands[i+1] == null) return false;
                if (!operands[i].equals(operands[i + 1])) {
                    return false;
                }
            }
            return true;
        } else if (operator.compareTo("!=") == 0) {
            if (operands[0] == null) return true;
            for (int i = 0; i < operands.length - 1; i++) {
                if (operands[i+1] == null) return true;
                if (operands[i].equals(operands[i + 1])) {
                    return false;
                }
            }
            return true;
        }
        return null;
    }

    /**
     * Get the fitness of the program as computed by the simulator.
     * @return  The fitness of the program
     */
    public int getFitness() {
        return simulator.getFitness();
    }
}
