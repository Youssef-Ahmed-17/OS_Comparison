import java.util.*;
class ResultPrinter {

    static double[] printAll(List<Process> procs) {
        procs.sort(Comparator.comparing(p -> p.pid));

        System.out.printf("  %-7s %-5s %-5s %-5s %-5s %-5s %-5s %-5s%n",
                "PID", "AT", "BT", "PRI", "CT", "WT", "TAT", "RT");
        System.out.println("  " + "-".repeat(46));

        for (Process p : procs) {
            System.out.printf("  %-7s %-5d %-5d %-5d %-5d %-5d %-5d %-5d%n",
                    p.pid, p.at, p.bt, p.priority, p.ct, p.wt, p.tat, p.rt);
        }

        System.out.println("  " + "-".repeat(46));

        double[] avg = MetricsCalculator.getAverages(procs);
        System.out.printf("  Avg WT  : %.2f%n", avg[0]);
        System.out.printf("  Avg TAT : %.2f%n", avg[1]);
        System.out.printf("  Avg RT  : %.2f%n", avg[2]);
        System.out.println();

        return avg;
    }
}
