package lesson6;

import java.util.Arrays;

public class Main {
    private static int[] arr1 = {0,0,0};

    public static int[] first(int[] arr) {
        int arrSize = 0;
        int[] result;
        if (Arrays.toString(arr).contains("4")) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 4) arrSize = arr.length - (i + 1);
            }
            result = new int[arrSize];
            System.arraycopy(arr, arr.length - arrSize, result, 0, arrSize);
        } else {
            throw new RuntimeException();
        }
        return result;
    }

    public static boolean second(int[] arr) {
        boolean hasOne = false, hasFour = false;
        for (int value : arr) {
            if (value == 1) {
                hasOne = true;
            } else if (value == 4) {
                hasFour = true;
            }
            else return false;
        }
        return hasOne && hasFour;
    }

}
