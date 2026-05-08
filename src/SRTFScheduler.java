import java.util.*;
public class SRTFScheduler {
    static List<GanttEntry> run(List<Process> procs) {
        return Scheduler.run(procs, SRTFScheduler::winsOver);
    }

    static boolean winsOver(Process a,Process b) {
        if (a.remaining != b.remaining) return a.remaining < b.remaining;
        if (a.at != b.at)               return a.at < b.at;
        return a.pid.compareTo(b.pid) < 0;
    }
}
