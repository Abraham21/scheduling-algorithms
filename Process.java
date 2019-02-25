import java.util.Comparator;

/**
 *
 * @author abe
 */
public class Process {
    
    private int pid;
    private int burstTime;
    private int priority;
    
    public Process(int pid, int burstTime, int priority) {
        this.pid = pid;
        this.burstTime = burstTime;
        this.priority = priority;
    }
    
    public int getPid() {
        return pid;
    }
    
    public int getBurstTime() {
        return burstTime;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPid(int pid) {
        this.pid = pid;
    }
    
    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }

    // Comparator for sorting by burst time
    public static Comparator<Process> ProcessBurstTimeComparator = new Comparator<Process>() {

	public int compare(Process p1, Process p2) {

	   int burstTime1 = p1.getBurstTime();
	   int burstTime2 = p2.getBurstTime();

	   // For ascending order
	   return burstTime1 - burstTime2;

	   // For descending order
	   // return burstTime2 - burstTime1;
    }};
    
    // Comparator for sorting by priority
    public static Comparator<Process> ProcessPriorityComparator = new Comparator<Process>() {

	public int compare(Process p1, Process p2) {

	   int priority1 = p1.getPriority();
	   int priority2 = p2.getPriority();

	   // For ascending order
	   // return priority1 - priority2;

	   // For descending order
	   return priority2 - priority1;
    }};
    
    public Process clone() {
        Process p = new Process(this.pid, this.burstTime, this.priority);
        return p;
    }
    
    @Override
    public String toString() {
        return "[ " + pid + ", " + burstTime + ", " + priority + " ]";
    }
    
}
