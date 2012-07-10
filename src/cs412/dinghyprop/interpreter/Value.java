package cs412.dinghyprop.interpreter;

/**
 * Stores an interpreter value.
 */
public final class Value {
    /**
     * The types available for use
     */
    public static enum TYPE {NULL, INTEGER, BOOLEAN}

    /**
     * The null constant
     */
    public static final Value NULL_VALUE = new Value(TYPE.NULL, 0);

    private final int value;
    private final TYPE type;

    /**
     * Create a typed value.
     * @param type     The type
     * @param value    The value
     */
    private Value(TYPE type, int value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Create a new integer typed {@code Value}.
     * @param value    The int value
     * @return  A value storing {@code value}
     */
    public static Value newInt(int value) {
        return new Value(TYPE.INTEGER, value);
    }

    /**
     * Create a new boolean-typed {@code Value}.
     * @param value    The boolean value
     * @return  A value storing {@code value}
     */
    public static Value newBool(boolean value) {
        return new Value(TYPE.BOOLEAN, value ? 1 : 0);
    }

    /**
     * Return the null {@code Value}.
     * @return  The static {@code Value} representing {@code null}
     */
    public static Value newNull() {
        return NULL_VALUE;
    }

    /**
     * Return a representation of {@code this} suitable for use as a
     * multiplicand.
     * @return  The value if integer-type or a 1.
     */
    public int multiplicand() {
        if (type == TYPE.INTEGER) {
            return value;
        }
        return 1;
    }

    /**
     * Return a representation of {@code this} suitable for use as a addend.
     * @return  The value if integer-type or a 0.
     */
    public int addend() {
        if (type == TYPE.INTEGER) {
            return value;
        }
        return 0;
    }

    /**
     * Return a representation of {@code this} suitable for use as a boolean
     * value.
     * @return  The value if boolean-type or whether the value is greater than
     * 0.
     */
    public boolean bool() {
        return value > 0;
    }

    @Override
    public String toString() {
        return "(" + type + ") " + value;
    }
}
