package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestModule
{

    @Test
    @DisplayName("1 + 1 = 2")
    void addsTwoNumbers()
    {
        assertEquals( 2, 1 + 2, "1 + 1 should equal 2" );
    }
}
