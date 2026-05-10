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

    static List<Process> readProcesses() {
        int n = readInt("Number of processes (1-20): ", 1, 20);
        List<Process> list = new ArrayList<>();
        Set<String> usedIDs = new HashSet<>();

        System.out.println();

        for (int i = 1; i <= n; i++) {
            System.out.println("Process " + i + ":");
            String pid = "P" + i;
            usedIDs.add(pid);

            int at = readInt("  Arrival Time (>=0): ", 0, Integer.MAX_VALUE);
            int bt = readInt("  Burst Time (>=1): ", 1, Integer.MAX_VALUE);
            int pri = readInt("  Priority (1-10): ", 1, 10);

            list.add(new Process(pid, at, bt, pri));
            System.out.println("  OK\n");
        }

        return list;
    }

    static List<Process> copy(List<Process> src) {
        List<Process> out = new ArrayList<>();
        for (Process p : src) {
            out.add(new Process(p));
        }
        return out;
    }
}