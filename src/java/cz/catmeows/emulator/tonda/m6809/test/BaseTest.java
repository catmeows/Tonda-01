package cz.catmeows.emulator.tonda.m6809.test;

public class BaseTest {
    private boolean result;
    private String description;

    void init(String description) {
        this.description = description;
        result = true;
    }
    void expect(boolean expected, String detail) {
        if (!expected) {
            result = false;
            description=description+" ["+detail+"] ";
        }
    }
    TestResult finish() {
        return new TestResult(description, result);
    }
}
