package kmalfa.utils;

public class PinCodeAnalyzer {
    private static final int[] CONSTANTS = {231, 455, 892, 351, 952, 116};

    public static int getPin (int pin, int operation) {
        int newPin = pin;
        switch (operation) {
            case 1:
                newPin = CONSTANTS[0] * (CONSTANTS[2] - pin);
                break;
            case 2:
                newPin = CONSTANTS[3] / (CONSTANTS[5] + pin);
                break;
            case 3:
                newPin = CONSTANTS[4] + CONSTANTS[1] - pin;
                break;
            case 4:
                newPin = CONSTANTS[2] - CONSTANTS[5] + pin;
                break;
            //In future may be more operations
        }
        return newPin;
    }

    public static int[] generatePinOp (int actualPin) {
        int operation = (int)(4 * Math.random() + 1);
        int codedPin = actualPin;
        switch (operation) {
            case 1:
                codedPin = CONSTANTS[2] - (actualPin / CONSTANTS[0]);
                break;
            case 2:
                codedPin = (CONSTANTS[3] / actualPin) - CONSTANTS[5];
                break;
            case 3:
                codedPin = CONSTANTS[4] + CONSTANTS[1] - actualPin;
                break;
            case 4:
                codedPin = actualPin - CONSTANTS[2] + CONSTANTS[5];
                break;
            //In future may be more operations
        }
        return new int[]{codedPin, operation};
    }
}
