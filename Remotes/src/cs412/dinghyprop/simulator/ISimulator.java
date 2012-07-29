/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

import java.io.Serializable;

/**
 * Simulator Interface
 */
public interface ISimulator extends Cloneable, Serializable {

    /**
     *  This method receives an action from the interpreter and
     *  tells the dinghy to take that action.
     *  @param function The action that must be taken by the dinghy
     *  @throws UnknownFunctionException if the action is not valid.
     */
    void invoke(String function) throws UnknownFunctionException;

    /**
     *  This method receives a reference to a variable from the interpreter
     *  and sends back its value.
     *  @param variable The variable that is referenced by the interpreter.
     *  @return The value of the referenced variable
     *  @throws VariableReferenceException If the variable does not exist
     */
    int reference(String variable) throws VariableReferenceException;

    /**
     *  Calculates the fitness of the program. This is calculated by adding
     *  the goal distance metric, the success metric, and the travel metric.
     *  @return The fitness of the program
     */
    int getFitness();

    /**
     * Determine whether execution can continue.
     * @return Whether execution can continue
     */
    boolean canContinue();

    ISimulator clone() throws CloneNotSupportedException;
}
