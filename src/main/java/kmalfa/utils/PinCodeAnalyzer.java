package kmalfa.utils;

public class PinCodeAnalyzer {
    private static final float[] CONSTANTS = {231.f, 455.f, 892.f, 351.f, 952.f, 116.f};

    public static int getPin (int pin, int operation) {
        int newPin = pin;
        switch (operation) {
            case 1:
                newPin = (int)(CONSTANTS[0] * (CONSTANTS[2] - pin));
                break;
            case 2:
                newPin = (int)(CONSTANTS[3] / (CONSTANTS[5] + pin));
                break;
            case 3:
                newPin = (int)(CONSTANTS[4] + CONSTANTS[1] - pin);
                break;
            case 4:
                newPin = (int)(CONSTANTS[2] - CONSTANTS[5] + pin);
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
                codedPin = (int)(CONSTANTS[2] - (actualPin / CONSTANTS[0]));
                break;
            case 2:
                codedPin = (int)((CONSTANTS[3] / actualPin) - CONSTANTS[5]);
                break;
            case 3:
                codedPin = (int)(CONSTANTS[4] + CONSTANTS[1] - actualPin);
                break;
            case 4:
                codedPin = (int)(actualPin - CONSTANTS[2] + CONSTANTS[5]);
                break;
            //In future may be more operations
        }
        return new int[]{codedPin, operation};
    }
}
