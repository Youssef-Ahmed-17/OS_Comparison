import java.util.ArrayList;
import java.util.List;

public class Scheduler {

        static List<GanttEntry> run(List<Process> procs, ProcessComparator pick) {
            List<GanttEntry> gantt = new ArrayList<>();

            int total = procs.stream().mapToInt(p -> p.bt).sum();
            int maxT  = procs.stream().mapToInt(p -> p.at).max().orElse(0) + total;

            int time    = 0;
            int done    = 0;
            String cur  = "";
            int segStart = 0;

            while (done < procs.size() && time <= maxT) {

                Process chosen = null;
                for (Process p : procs) {
                    if (p.at <= time && p.remaining > 0) {
                        if (chosen == null || pick.winsOver(p, chosen))
                            chosen = p;
                    }
                }

                if (chosen == null) {
                    if (!cur.equals("IDLE")) {
                        if (!cur.isEmpty()) gantt.add(new GanttEntry(cur, segStart, time));
                        cur      = "IDLE";
                        segStart = time;
                    }
                    time++;
                    continue;
                }

                if (!chosen.started) {
                    chosen.rt      = time - chosen.at;
                    chosen.started = true;
                }

                if (!chosen.pid.equals(cur)) {
                    if (!cur.isEmpty()) gantt.add(new GanttEntry(cur, segStart, time));
                    cur      = chosen.pid;
                    segStart = time;
                }

                chosen.remaining--;
                time++;

                if (chosen.remaining == 0) {
                    chosen.ct = time;
                    done++;
                }
            }

            if (!cur.isEmpty()) gantt.add(new GanttEntry(cur, segStart, time));
            return gantt;
        }
    }
