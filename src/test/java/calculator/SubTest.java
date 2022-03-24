package calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTest {

    @Test
    void subTest() {
        Sub case_1 = new Sub(10,3);
        assertEquals(7, case_1.sub(case_1));
        Sub case_2 = new Sub(10,-3);
        assertEquals(13, case_2.sub(case_2));
    }
}