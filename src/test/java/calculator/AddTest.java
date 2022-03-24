package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("The is a test class to test Add.java")
class AddTest {

    @Test
    @DisplayName("Addition of Positive Numbers")
    void addTestPos() {
        Add case_1 = new Add(10,3);
        assertEquals(13, case_1.add(case_1));
        Add case_2 = new Add(10,13);
        assertEquals(23, case_2.add(case_2));
    }

    @Test
    @DisplayName("Addition of Negative Numbers")
    void addTestNeg() {
        Add case_1 = new Add(-10,-3);
        assertEquals(-13, case_1.add(case_1));
        Add case_2 = new Add(-10,-13);
        assertEquals(-23, case_2.add(case_2));
    }

    @Test
    void addTestMixed() {
        Add case_1 = new Add(10,-3);
        assertEquals(7, case_1.add(case_1));
        Add case_2 = new Add(10,-13);
        assertEquals(-3, case_2.add(case_2));
    }

}