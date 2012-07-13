package cs412.dinghyprop.genetics;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TestNG for TournamentSelector
 */
public class TournamentSelectorTest {
    @Test
    public void testSelect() throws Exception {
        Program prog1 = new Program("");
        Program prog2 = new Program("");
        Program[] pop1 = {prog1};
        Program[] pop2 = {prog1, prog2};

        TournamentSelector ts = new TournamentSelector(2);
        Assert.assertEquals(ts.select(pop1), prog1);
        Program selected = ts.select(pop2);
        Assert.assertTrue(selected == prog1 || selected == prog2);
    }
}
