import java.util.List;

public abstract class CPUScheduler {
    public abstract List<ProcessStats> schedule(List<Process> processes);
    public double computeAverageWaitingTime(List<ProcessStats> stats) {
        double sum = 0;

        for (ProcessStats ps : stats) {
            sum += ps.getWaitingTime();
        }

        if (stats == null || stats.isEmpty()) {
            return 0.0;
        }

        else {
            return sum / stats.size();
        }
    }

    public double computeAverageTurnaroundTime(List<ProcessStats> stats) {
        double sum = 0;

        for (ProcessStats ps : stats) {
            sum += ps.getTurnaroundTime();
        }

        if (stats == null || stats.isEmpty()) {
            return 0.0;
        }

        else {
            return sum / stats.size();
        }
    }

    public double computeCpuUtilization(List<ProcessStats> stats) {
        double totalRunningTime = 0;
        double endTime = 0;

        for (ProcessStats ps: stats) {
            Process p = ps.getProcess();
            totalRunningTime += p.getBurstTime();
            endTime = Math.max(endTime, ps.getCompletionTime());
        }

        if (endTime == 0) {
            return 0.0;
        }

        return totalRunningTime / endTime;
    }
}