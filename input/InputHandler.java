import java.util.*;
class InputHandler {
    static Scanner sc = new Scanner(System.in);

    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v < min || v > max)
                    System.out.println("  Must be between " + min + " and " +
                            (max == Integer.MAX_VALUE ? "any positive value" : max) + ". Got: " + v);
                else
                    return v;
            } catch (NumberFormatException e) {
                System.out.println("  '" + line + "' is not a valid number.");
            }
        }
    }


