
class Comparison {

    static void compare(double[] pAvg, double[] sAvg) {
        System.out.println("===== Comparison =====\n");

        printMetricRow("Avg Waiting Time",    pAvg[0], sAvg[0]);
        printMetricRow("Avg Turnaround Time", pAvg[1], sAvg[1]);
        printMetricRow("Avg Response Time",   pAvg[2], sAvg[2]);

        System.out.println("\nBehavior:");
        System.out.println("  Priority always runs the most urgent process first, no matter how");
        System.out.println("  long it takes. Works well when some tasks genuinely can't wait.");
        System.out.println("  SRTF picks whichever job is closest to finishing. Gives optimal");
        System.out.println("  avg waiting time, but long processes can get stuck waiting.");

        System.out.println("\nStarvation risk:");
        System.out.println("  Priority - HIGH.     Low-priority jobs may never run if high-");
        System.out.println("             priority ones keep arriving.");
        System.out.println("             Fix: aging (gradually raise priority over time).");
        System.out.println("  SRTF     - MODERATE. Long jobs starve if short ones keep coming.");
        System.out.println("             Fix: aging or a maximum wait-time cap.");

        System.out.println("\nRecommendation:");
        System.out.println("  " + "-".repeat(44));
        if (sAvg[0] <= pAvg[0] && sAvg[1] <= pAvg[1]) {
            System.out.println("  SRTF performed better on this workload.");
            System.out.println("  Lower WT and TAT make it the right pick when you want");
            System.out.println("  to minimise how long processes spend waiting overall.");
            System.out.println("  Switch to Priority if some tasks need guaranteed urgency.");
        } else if (pAvg[0] <= sAvg[0] && pAvg[1] <= sAvg[1]) {
            System.out.println("  Priority Scheduling performed better on this workload.");
            System.out.println("  The process mix favours urgent-task ordering over burst-");
            System.out.println("  length ordering, so Priority wins on both WT and TAT.");
        } else {
            System.out.println("  Neither algorithm wins on every metric here.");
            System.out.println("  SRTF is the better choice for batch/throughput systems.");
            System.out.println("  Priority fits real-time systems where urgency comes first.");
        }
        System.out.println("  " + "-".repeat(44));
    }

    private static void printMetricRow(String label, double pVal, double sVal) {
        System.out.println("\n" + label + ":");
        System.out.printf("  Priority : %.2f%n", pVal);
        System.out.printf("  SRTF     : %.2f%n", sVal);
        System.out.println("  Lower    : " + lowerLabel(pVal, sVal));
    }

    static String lowerLabel(double a, double b) {
        if (a < b)  return "Priority";
        if (b < a)  return "SRTF";
        return "Equal";
    }
}