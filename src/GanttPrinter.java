import java.util.*;
class GanttPrinter {

    static void print(List<GanttEntry> gantt) {
        if (gantt.isEmpty()) {
            System.out.println("  (no entries)\n");
            return;
        }

        System.out.println("Gantt Chart:");
        StringBuilder bar   = new StringBuilder("|");
        StringBuilder times = new StringBuilder();

        times.append(gantt.get(0).start);

        for (GanttEntry g : gantt) {
            String cell = " " + g.pid + " |";
            bar.append(cell);
            times.append(String.format("%" + cell.length() + "s", g.end));
        }

        System.out.println("  " + bar);
        System.out.println("  " + times);
        System.out.println();
    }
}
