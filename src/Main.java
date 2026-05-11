import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("CPU Scheduling Simulator - Project C1");
        System.out.println("Preemptive Priority vs SRTF");
        System.out.println("(lower priority number = higher urgency)\n");

        List<Process> input = InputHandler.readProcesses();


        System.out.println("===== Priority Scheduling =====");
        List<Process> priorityList = InputHandler.copy(input);
        List<GanttEntry> pGantt   = PriorityScheduler.run(priorityList);
        GanttPrinter.print(pGantt);
        MetricsCalculator.calculate(priorityList);
        double[] pAvg = ResultPrinter.printAll(priorityList);


        System.out.println("===== SRTF =====");
        List<Process> srtfList  = InputHandler.copy(input);
        List<GanttEntry> sGantt = SRTFScheduler.run(srtfList);
        GanttPrinter.print(sGantt);
        MetricsCalculator.calculate(srtfList);
        double[] sAvg = ResultPrinter.printAll(srtfList);


        Comparison.compare(pAvg, sAvg);

        System.out.println("\nProgram finished.");
        InputHandler.sc.close();
    }
}