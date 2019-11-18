package kmalfa.utils;

import java.util.Random;

public class PinCodeAnalyzer {
    private static final int[] CONSTANTS = {231, 455, 892, 351, 952, 116};

    private PinCodeAnalyzer(){}

    public static int getPin (int codedPin, int operation) {
        int actualPin = codedPin;
        switch (operation) {
            case 1:
                actualPin = CONSTANTS[2] - (codedPin / CONSTANTS[0]);
                break;
            case 2:
                actualPin = (codedPin / CONSTANTS[3]) - CONSTANTS[5];
                break;
            case 3:
                actualPin = CONSTANTS[4] + CONSTANTS[1] - codedPin;
                break;
            case 4:
                actualPin = (codedPin - CONSTANTS[2] + CONSTANTS[5]);
                break;
            default:
                System.out.println("WARNING: No implementation for case " + operation);
            //In future may be more operations
        }
        return actualPin;
    }

    public static int generateOp () {
        return 1 + new Random().nextInt(3);
    }

    public static int generatePin (int actualPin, int operation) {
        int codedPin = actualPin;
        switch (operation) {
            case 1:
                codedPin = CONSTANTS[0] * (CONSTANTS[2] - actualPin);
                break;
            case 2:
                codedPin = CONSTANTS[3] * (CONSTANTS[5] + actualPin);
                break;
            case 3:
                codedPin = CONSTANTS[4] + CONSTANTS[1] - actualPin;
                break;
            case 4:
                codedPin = CONSTANTS[2] - CONSTANTS[5] + actualPin;
                break;
            default:
                System.out.println("WARNING: No implementation for case " + operation);
            //In future may be more operations
        }
        return codedPin;
    }
}
