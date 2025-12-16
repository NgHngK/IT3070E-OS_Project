import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SQMScheduler extends CPUScheduler {
    public enum AlgorithmType { FCFS, SJN, RR }

    private int numProcessors;
    private AlgorithmType type;
    private int timeQuantum;

    public SQMScheduler(int numProcessors, AlgorithmType type, int timeQuantum) {
        this.numProcessors = numProcessors;
        this.type = type;
        this.timeQuantum = timeQuantum;
    }

    private class Job {
        Process process;
        int remainingTime;
        int startTime = -1;

        public Job(Process p) {
            this.process = p;
            this.remainingTime = p.getBurstTime();
        }
    }

    @Override
    public List<ProcessStats> schedule(List<Process> processes) {
        List<ProcessStats> completedStats = new ArrayList<>();

        List<Job> allJobs = new ArrayList<>();
        for (Process p : processes) allJobs.add(new Job(p));
        allJobs.sort(Comparator.comparingInt(j -> j.process.getArrivalTime()));

        LinkedList<Job> readyQueue = new LinkedList<>();
        Job[] processors = new Job[numProcessors];
        int[] rrTimeSlice = new int[numProcessors];

        int currentTime = 0;
        int completedCount = 0;
        int jobIndex = 0;

        while (completedCount < processes.size()) {

            // Add newly arrived processes to queue
            while (jobIndex < allJobs.size() && allJobs.get(jobIndex).process.getArrivalTime() <= currentTime) {
                readyQueue.add(allJobs.get(jobIndex));
                jobIndex++;
            }

            for (int i = 0; i < numProcessors; i++) {
                // Handle currently running job
                if (processors[i] != null) {
                    Job current = processors[i];
                    current.remainingTime--;
                    if (type == AlgorithmType.RR) rrTimeSlice[i]--;

                    if (current.remainingTime == 0) {
                        completedStats.add(new ProcessStats(
                                current.process, current.startTime, currentTime, "CPU " + (i + 1)
                        ));
                        processors[i] = null;
                        completedCount++;
                    }
                    else if (type == AlgorithmType.RR && rrTimeSlice[i] == 0) {
                        readyQueue.add(current);
                        processors[i] = null;
                    }
                }

                // Assign new job if CPU is free
                if (processors[i] == null && !readyQueue.isEmpty()) {
                    Job nextJob = pickNextJob(readyQueue);
                    readyQueue.remove(nextJob);
                    processors[i] = nextJob;

                    if (nextJob.startTime == -1) nextJob.startTime = currentTime;
                    if (type == AlgorithmType.RR) rrTimeSlice[i] = timeQuantum;
                }
            }

            if (completedCount < processes.size()) currentTime++;
        }

        return completedStats;
    }

    private Job pickNextJob(LinkedList<Job> queue) {
        if (type == AlgorithmType.SJN) {
            Job shortest = queue.get(0);
            for (Job j : queue) {
                if (j.remainingTime < shortest.remainingTime) shortest = j;
            }
            return shortest;
        }
        return queue.getFirst();
    }

    @Override
    public double computeCpuUtilization(List<ProcessStats> stats) {
        if (stats == null || stats.isEmpty()) return 0.0;
        double totalBurst = stats.stream().mapToInt(s -> s.getProcess().getBurstTime()).sum();
        int maxComp = stats.stream().mapToInt(ProcessStats::getCompletionTime).max().orElse(1);
        return totalBurst / (maxComp * numProcessors);
    }
}