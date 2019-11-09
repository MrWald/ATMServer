package kmalfa.utils;

public class PinCodeAnalyzer {
    private static final float[] CONSTANTS = {231.f, 455.f, 892.f, 351.f, 952.f, 116.f};

    public static int getPin (int pin, int operation) {
        int newPin = pin;
        switch (operation) {
            case 1:
                newPin = Math.round(CONSTANTS[0] * (CONSTANTS[2] - (float)pin));
                break;
            case 2:
                newPin = Math.round(CONSTANTS[3] / (CONSTANTS[5] + (float)pin));
                break;
            case 3:
                newPin = Math.round(CONSTANTS[4] + CONSTANTS[1] - (float)pin);
                break;
            case 4:
                newPin = Math.round(CONSTANTS[2] - CONSTANTS[5] + (float)pin);
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
                codedPin = Math.round(CONSTANTS[2] - ((float)actualPin / CONSTANTS[0]));
                break;
            case 2:
                codedPin = Math.round((CONSTANTS[3] / (float)actualPin) - CONSTANTS[5]);
                break;
            case 3:
                codedPin = Math.round(CONSTANTS[4] + CONSTANTS[1] - (float)actualPin);
                break;
            case 4:
                codedPin = Math.round((float)actualPin - CONSTANTS[2] + CONSTANTS[5]);
                break;
            //In future may be more operations
        }
        return new int[]{codedPin, operation};
    }
}
