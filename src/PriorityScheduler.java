import java.util.*;
    public class PriorityScheduler {

        static List<GanttEntry> run(List<Process> procs) {
            return Scheduler.run(procs, PriorityScheduler::winsOver);
        }

        static boolean winsOver(Process a, Process b) {
            if (a.priority != b.priority)
                return a.priority < b.priority;
            if (a.at != b.at)
                return a.at < b.at;

            return a.pid.compareTo(b.pid) < 0;
        }
    }

