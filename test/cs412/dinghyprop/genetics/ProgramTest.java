package cs412.dinghyprop.genetics;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TestNG for Program
 */
public class ProgramTest {
    @Test
    public void testGetFitness() throws Exception {
        Program p = new Program("()");
        Assert.assertEquals(p.fitness, -1);
        Assert.assertEquals(p.getFitness(), -1);

        p.fitness = 100;
        Assert.assertEquals(p.fitness, 100);
        Assert.assertEquals(p.getFitness(), 100);
    }
}
