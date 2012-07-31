/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.interpreter;

/**
 * Store interpreter values that can behave well in different execution
 * contexts
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

    /**
     * The current object's numerical value
     */
    private final int value;

    /**
     * The current object's type
     */
    private final TYPE type;

    /**
     * Creates a typed value.
     *
     * @param type     the behavior type
     * @param value    the numerical value
     */
    private Value(TYPE type, int value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Create a new integer-typed Value.
     *
     * @param value    the integer value to represent
     * @return  an integer-typed Value storing value
     */
    public static Value newInt(int value) {
        return new Value(TYPE.INTEGER, value);
    }

    /**
     * @return  A value from {@link TYPE}
     */
    public TYPE getType() {
        return type;
    }

    /**
     * @return  Whether this is a boolean.
     */
    public boolean isBool() {
        return type == TYPE.BOOLEAN;
    }

    /**
     * @return  Whether this is an integer.
     */
    public boolean isInt() {
        return type == TYPE.INTEGER;
    }

    /**
     * @return  Whether this is null.
     */
    public boolean isNull() {
        return type == TYPE.NULL;
    }

    /**
     * Return a representation of this Value suitable for use as a
     * multiplicand.  Integer-type Values will return themselves and other
     * types will return 1 (integer, division, and exponentiation identity).
     *
     * @return  an int
     */
    public int multiplicand() {
        if (type == TYPE.INTEGER) {
            return value;
        }
        return 1;
    }

    /**
     * Return a representation of this Value suitable for use as a addend.
     * Integer-type Values will return themselves and other types will return 0
     * (addition, and subtraction identity).
     *
     * @return  an int
     */
    public int addend() {
        if (type == TYPE.INTEGER) {
            return value;
        }
        return 0;
    }

    /**
     * Return a representation of this Value suitable for use as a boolean
     * value.
     *
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
        if (type == TYPE.INTEGER)
            return value * type.hashCode();
        return super.hashCode() * ((value == 0) ? 1 : value) * type.hashCode();
    }

    @Override
    public String toString() {
        return "(" + type + ") " + value;
    }
}
