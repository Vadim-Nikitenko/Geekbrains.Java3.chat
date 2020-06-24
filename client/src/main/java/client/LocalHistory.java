package client;

import java.io.*;
import java.util.Scanner;

public class LocalHistory {

    public static void saveHistory(String login, String message) throws IOException {
        try (Writer writer = new FileWriter(String.format("client/src/main/java/client/history/history_%s.txt", login), true)) {
            writer.write(message + "\n");
        }
    }

    public static void createLocalHistory(String login) throws IOException {
        File file = new File(String.format("client/src/main/java/client/history/history_%s.txt", login));
        file.createNewFile();
    }

    public static String showHistory(String login) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(String.format("client/src/main/java/client/history/history_%s.txt", login)));
        String result = "";
        String tmp;
        try {
            for (int i = 100; i > 0; i--) {
                if((tmp = scanner.nextLine()) != null) {
                    result += tmp + "\n";
                }
            }
        } catch (RuntimeException e){
            System.out.println("В локальной истории меньше 100 сообщений");
        }
        scanner.close();
        return result;
    }
}
