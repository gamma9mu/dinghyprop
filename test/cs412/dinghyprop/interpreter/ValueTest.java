package cs412.dinghyprop.interpreter;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * TestNG for Value
 */
public class ValueTest {
    Value[] multiOnes = {Value.FALSE_VALUE, Value.NULL_VALUE, Value.TRUE_VALUE};
    Value[] addZeros = multiOnes;
    int[] values = {-100, -10, -1, 0, 1, 10, 100};
    Value[] Values = null;

    @BeforeSuite
    public void setUp() {
        Values = new Value[values.length];
        for (int i = 0; i < values.length; i++) {
            Values[i] = Value.newInt(values[i]);
        }
    }

    @Test
    public void testNewInt() throws Exception {
        int[] values = {0, 1, 2, 3, 10, 100, -1, -2, -3, -10, -100};
        for (int value : values) {
            Value v = Value.newInt(value);
            Assert.assertEquals(v.addend(), value);
            Assert.assertEquals(v.multiplicand(), value);
        }
    }

    @Test
    public void testGetType() throws Exception {
        Assert.assertEquals(Value.NULL_VALUE.getType(), Value.TYPE.NULL);
        Assert.assertEquals(Value.TRUE_VALUE.getType(), Value.TYPE.BOOLEAN);
        Assert.assertEquals(Value.FALSE_VALUE.getType(), Value.TYPE.BOOLEAN);
        Assert.assertEquals(Value.newInt(1000).getType(), Value.TYPE.INTEGER);
    }

    @Test
    public void testIsBool() throws Exception {
        Assert.assertTrue(Value.TRUE_VALUE.isBool());
        Assert.assertTrue(Value.FALSE_VALUE.isBool());
        Assert.assertFalse(Value.NULL_VALUE.isBool());
        Assert.assertFalse(Value.newInt(1000).isBool());
    }

    @Test
    public void testIsInt() throws Exception {
        Assert.assertFalse(Value.TRUE_VALUE.isInt());
        Assert.assertFalse(Value.FALSE_VALUE.isInt());
        Assert.assertFalse(Value.NULL_VALUE.isInt());
        Assert.assertTrue(Value.newInt(1000).isInt());
    }

    @Test
    public void testIsNull() throws Exception {
        Assert.assertFalse(Value.TRUE_VALUE.isNull());
        Assert.assertFalse(Value.FALSE_VALUE.isNull());
        Assert.assertTrue(Value.NULL_VALUE.isNull());
        Assert.assertFalse(Value.newInt(1000).isNull());
    }

    @Test
    public void testMultiplicand() throws Exception {
        for (Value v : multiOnes) {
            Assert.assertEquals(v.multiplicand(), 1);
        }
        for (int i = 0; i < values.length; i++) {
            Assert.assertEquals(Values[i].multiplicand(), values[i]);
        }
    }

    @Test
    public void testAddend() throws Exception {
        for (Value v : addZeros) {
            Assert.assertEquals(v.addend(), 0);
        }
        for (int i = 0; i < values.length; i++) {
            Assert.assertEquals(Values[i].addend(), values[i]);
        }
    }

    @Test
    public void testBool() throws Exception {
        Assert.assertTrue(Value.TRUE_VALUE.bool());
        Assert.assertFalse(Value.FALSE_VALUE.bool());
        Assert.assertFalse(Value.NULL_VALUE.bool());
        for (Value value : Values) {
            if (value.addend() > 0) {
                Assert.assertTrue(value.bool());
            } else {
                Assert.assertFalse(value.bool());
            }
        }
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertEquals(Value.TRUE_VALUE, Value.TRUE_VALUE);
        Assert.assertTrue(Value.TRUE_VALUE.equals(Value.TRUE_VALUE));
        Assert.assertEquals(Value.FALSE_VALUE, Value.FALSE_VALUE);
        Assert.assertTrue(Value.FALSE_VALUE.equals(Value.FALSE_VALUE));
        Assert.assertEquals(Value.NULL_VALUE, Value.NULL_VALUE);
        Assert.assertTrue(Value.NULL_VALUE.equals(Value.NULL_VALUE));

        Assert.assertTrue(Value.newInt(-100).equals(Value.newInt(-100)));
        Assert.assertTrue(Value.newInt(-10).equals(Value.newInt(-10)));
        Assert.assertTrue(Value.newInt(-1).equals(Value.newInt(-1)));
        Assert.assertTrue(Value.newInt(0).equals(Value.newInt(0)));
        Assert.assertTrue(Value.newInt(1).equals(Value.newInt(1)));
        Assert.assertTrue(Value.newInt(10).equals(Value.newInt(10)));
        Assert.assertTrue(Value.newInt(100).equals(Value.newInt(100)));
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertSame(Value.FALSE_VALUE, Value.FALSE_VALUE);
        Assert.assertTrue(Value.FALSE_VALUE.hashCode() == Value.FALSE_VALUE.hashCode());

        Assert.assertNotSame(Value.FALSE_VALUE, Value.TRUE_VALUE);
        Assert.assertFalse(Value.FALSE_VALUE.hashCode() == Value.TRUE_VALUE.hashCode());

        Assert.assertNotSame(Value.FALSE_VALUE, Value.NULL_VALUE);
        Assert.assertFalse(Value.FALSE_VALUE.hashCode() == Value.NULL_VALUE.hashCode());

        Assert.assertSame(Value.TRUE_VALUE, Value.TRUE_VALUE);
        Assert.assertTrue(Value.TRUE_VALUE.hashCode() == Value.TRUE_VALUE.hashCode());

        Assert.assertNotSame(Value.TRUE_VALUE, Value.FALSE_VALUE);
        Assert.assertFalse(Value.TRUE_VALUE.hashCode() == Value.FALSE_VALUE.hashCode());

        Assert.assertNotSame(Value.TRUE_VALUE, Value.NULL_VALUE);
        Assert.assertFalse(Value.TRUE_VALUE.hashCode() == Value.NULL_VALUE.hashCode());

        Assert.assertSame(Value.NULL_VALUE, Value.NULL_VALUE);
        Assert.assertTrue(Value.NULL_VALUE.hashCode() == Value.NULL_VALUE.hashCode());

        Assert.assertNotSame(Value.NULL_VALUE, Value.TRUE_VALUE);
        Assert.assertFalse(Value.NULL_VALUE.hashCode() == Value.TRUE_VALUE.hashCode());

        Assert.assertNotSame(Value.NULL_VALUE, Value.FALSE_VALUE);
        Assert.assertFalse(Value.NULL_VALUE.hashCode() == Value.FALSE_VALUE.hashCode());

        Assert.assertTrue(Value.newInt(0).hashCode() == Value.newInt(0).hashCode());
        Assert.assertFalse(Value.newInt(0).hashCode() == Value.newInt(1).hashCode());
    }
}
