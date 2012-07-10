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
     * null constant
     */
    public static final Value NULL_VALUE = new Value(TYPE.NULL, 0);

    /**
     * true constant
     */
    public static final Value TRUE_VALUE = new Value(TYPE.BOOLEAN, 1);

    /**
     * false constant
     */
    public static final Value FALSE_VALUE = new Value(TYPE.BOOLEAN, 0);

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
     * Get the {@code Value}'s type.
     * @return  A value from {@code TYPE}
     */
    public TYPE getType() {
        return type;
    }

    /**
     * @return  Whether {@code this} is a boolean.
     */
    public boolean isBool() {
        return type == TYPE.BOOLEAN;
    }

    /**
     * @return  Whether {@code this} is an integer.
     */
    public boolean isInt() {
        return type == TYPE.INTEGER;
    }

    /**
     * @return  Whether {@code this} is null.
     */
    public boolean isNull() {
        return type == TYPE.NULL;
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
    public boolean equals(Object obj) {
        if (!(obj instanceof Value))
            return false;
        Value other = (Value) obj;
        return (type == other.type) && (value == other.value);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * value * type.hashCode();
    }

    @Override
    public String toString() {
        return "(" + type + ") " + value;
    }
}
