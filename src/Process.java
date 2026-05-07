import java.util.*;

public class Process {
    String pid;
    int at, bt, priority;
    int remaining;
    boolean started;
    int ct,wt,tat,rt;

    Process(String pid, int bt , int at,int priority){
        this.pid=pid;
        this.at=at;
        this.bt=bt;
        this.priority=priority;
        this.remaining=bt;
        this.started=false;
        this.ct= this.wt= this.tat= this.rt=0;
    }

    Process(Process p){
        this.pid=p.pid;
        this.at=p.at;
        this.bt=p.bt;
        this.priority=p.priority;
        this.remaining=p.bt;
        this.started=false;
        this.ct=this.wt=this.tat=this.rt=0;
    }
}
