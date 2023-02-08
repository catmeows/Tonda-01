package cz.catmeows.emulator.tonda.m6809.test;

public class TestResult {
    private boolean result;
    private String description;
    TestResult(String description, boolean result) {
        this.description = description;
        this.result = result;
    }
    public boolean getResult() {
        return result;
    }
    public String getDescription() {
        return description;
    }
 }
