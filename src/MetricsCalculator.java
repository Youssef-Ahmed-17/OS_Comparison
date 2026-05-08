import java.util.*;
class MetricsCalculator {

    static void calculate(List<Process> procs) {
        for (Process p : procs) {
            p.tat = p.ct - p.at;
            p.wt  = p.tat - p.bt;
        }
    }
    static double[] getAverages(List<Process> procs) {
        double wt = 0, tat = 0, rt = 0;
        for (Process p : procs) {
            wt  += p.wt;
            tat += p.tat;
            rt  += p.rt;
        }
        int n = procs.size();
        return new double[]{ wt / n, tat / n, rt / n };
    }
}