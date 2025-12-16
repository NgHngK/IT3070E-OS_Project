public class Process {
    private final String pid;      // Process ID
    private final int arrivalTime;
    private final int burstTime;

    public Process(String pid, int arrivalTime, int burstTime) {
        if (arrivalTime < 0) {
            throw new IllegalArgumentException("Arrival time must be >= 0");
        }

        if (burstTime <= 0) {
            throw new IllegalArgumentException("Burst time must be > 0");
        }

        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }

    public String getPid() {
        return pid;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    @Override
    public String toString() {
        return "Process{" +
                "pid='" + pid + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", burstTime=" + burstTime +
                '}';
    }
}
