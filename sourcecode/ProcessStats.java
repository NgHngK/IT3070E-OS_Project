public class ProcessStats {
    private final Process process;
    private final int startTime;
    private final int completionTime;
    private final int waitingTime;
    private final int turnaroundTime;
    private String processorId;

    public ProcessStats(Process process, int startTime, int completionTime, String processorId) {
        this.process = process;
        this.startTime = startTime;
        this.completionTime = completionTime;
        this.processorId = processorId;

        int arrival = process.getArrivalTime();
        int burst = process.getBurstTime();

        this.turnaroundTime = completionTime - arrival;
        this.waitingTime = turnaroundTime - burst;
    }

    // Overloaded constructor for backward compatibility with existing FCFS/SJN/RR
    public ProcessStats(Process process, int startTime, int completionTime) {
        this(process, startTime, completionTime, "CPU 1");
    }

    public Process getProcess() {
        return process;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }
}
