package cs412.dinghyprop.interpreter;

import cs412.dinghyprop.simulator.Simulator;

public class Interpreter {
    private Simulator simulator;
    private Expression program = null;
    private int fitness = 0;

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
        fitness = simulator.getTravelMetric() +
                simulator.getGoalDistanceMetric() +
                simulator.getSuccessMetric();
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
            for (Object obj : operands)
                accum += (Integer) obj;
            return accum;
        } else if (operator.compareTo("-") == 0) {
            int accum = (Integer) operands[0];
            for (int i = 1; i < operands.length; i++)
                accum -= (Integer) operands[i];
            return accum;
        } else if (operator.compareTo("*") == 0) {
            int accum = 1;
            for (Object obj : operands)
                accum *= (Integer) obj;
            return accum;
        } else if (operator.compareTo("/") == 0) {
            int accum = (Integer) operands[0];
            for (int i = 1; i < operands.length; i++)
                accum /= (Integer) operands[i];
            return accum;
        } else if (operator.compareTo("^") == 0) {
            int accum = (Integer) operands[0];
            for (int i = 1; i < operands.length; i++)
                accum = (int) Math.pow(accum, (Integer) operands[i]);
            return accum;
        } else if (operator.compareTo("<") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if ((Integer) operands[i] >= (Integer) operands[i + 1]) {
                    return false;
                }
            }
            return true;
        } else if (operator.compareTo("<=") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if ((Integer) operands[i] > (Integer) operands[i + 1]) {
                    return false;
                }
            }
            return true;
        } else if (operator.compareTo(">") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if ((Integer) operands[i] <= (Integer) operands[i + 1]) {
                    return false;
                }
            }
            return true;
        } else if (operator.compareTo(">=") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if ((Integer) operands[i] < (Integer) operands[i + 1]) {
                    return false;
                }
            }
            return true;
        } else if (operator.compareTo("==") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if (!operands[i].equals(operands[i + 1])) {
                    return false;
                }
            }
            return true;
        } else if (operator.compareTo("!=") == 0) {
            for (int i = 0; i < operands.length - 1; i++) {
                if (operands[i].equals(operands[i + 1])) {
                    return false;
                }
            }
            return true;
        }
        return null;
    }

    public int getFitness() {
        return fitness;
    }
}
