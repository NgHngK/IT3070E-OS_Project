import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MQMScheduler extends CPUScheduler {
    private int numProcessors;
    private CPUScheduler localScheduler;

    public MQMScheduler(int numProcessors, CPUScheduler localScheduler) {
        if (numProcessors <= 0) throw new IllegalArgumentException("Processors must be > 0");
        this.numProcessors = numProcessors;
        this.localScheduler = localScheduler;
    }

    @Override
    public List<ProcessStats> schedule(List<Process> processes) {
        // Initialize queues and load trackers
        List<List<Process>> processorQueues = new ArrayList<>();
        int[] cpuLoad = new int[numProcessors];

        for (int i = 0; i < numProcessors; i++) {
            processorQueues.add(new ArrayList<>());
            cpuLoad[i] = 0;
        }

        List<Process> sorted = new ArrayList<>(processes);
        sorted.sort(Comparator.comparingInt(Process::getArrivalTime));

        // Optimized by assigning to the CPU with the least current load
        for (Process p : sorted) {
            int bestCpuIndex = 0;
            int minLoad = cpuLoad[0];

            for (int i = 1; i < numProcessors; i++) {
                if (cpuLoad[i] < minLoad) {
                    minLoad = cpuLoad[i];
                    bestCpuIndex = i;
                }
            }

            processorQueues.get(bestCpuIndex).add(p);
            cpuLoad[bestCpuIndex] += p.getBurstTime();
        }

        // Run local scheduler on each processor's queue independently
        List<ProcessStats> allStats = new ArrayList<>();

        for (int i = 0; i < numProcessors; i++) {
            List<Process> queue = processorQueues.get(i);
            if (!queue.isEmpty()) {
                List<ProcessStats> localStats = localScheduler.schedule(queue);

                String cpuName = "CPU " + (i + 1);
                for (ProcessStats ps : localStats) {
                    ps.setProcessorId(cpuName);
                    allStats.add(ps);
                }
            }
        }
        return allStats;
    }

    @Override
    public double computeCpuUtilization(List<ProcessStats> stats) {
        if (stats == null || stats.isEmpty()) return 0.0;
        double totalBurst = stats.stream().mapToInt(s -> s.getProcess().getBurstTime()).sum();
        int maxComp = stats.stream().mapToInt(ProcessStats::getCompletionTime).max().orElse(1);
        return totalBurst / (maxComp * numProcessors);
    }
}