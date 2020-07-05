import lesson6.Main;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ArrayFirstTests {

    @Parameter(0)
    public int[] arr1;

    @Parameter(1)
    public int[] arr2;

    @Parameter(2)
    public Class<? extends Exception> expectedException;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new int[]{1, 4, 6, 5, 7}, new int[]{6, 5, 7}, null},
                {new int[]{4}, new int[]{}, null},
                {new int[]{1, 1, 3, 5}, new int[]{}, RuntimeException.class},
                {new int[]{1, 7, 6, 4}, new int[]{}, null}
        });
    }

    @Test
    public void testAdd() {
        if (expectedException != null) {
            thrown.expect(RuntimeException.class);
        }
        assertArrayEquals(Main.first(arr1), arr2);
    }

}
