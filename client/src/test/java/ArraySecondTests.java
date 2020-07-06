import lesson6.Main;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ArraySecondTests {
    @Parameter(0)
    public int[] arr1;

    @Parameter(1)
    public boolean bool;


    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new int[]{1, 4, 1, 1}, true},
                {new int[]{4}, false},
                {new int[]{1, 1, 1, 1}, false},
                {new int[]{}, false}

        });
    }

    @Test
    public void testAdd() {
        assertEquals(Main.second(arr1), bool);
    }

}
