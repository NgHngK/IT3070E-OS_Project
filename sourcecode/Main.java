import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== CPU Scheduler Simulator ===");
        System.out.print("Number of processes: ");
        int n = sc.nextInt();

        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("Process " + (i + 1) + ":");
            System.out.print("- PID: ");
            String pid = sc.next();
            System.out.print("- Arrival Time: ");
            int at = sc.nextInt();
            System.out.print("- Burst Time: ");
            int bt = sc.nextInt();
            processes.add(new Process(pid, at, bt));
        }

        System.out.println("\nSelect Architecture:");
        System.out.println("1. Single-Queue Multiprocessor (SQMS)");
        System.out.println("2. Multi-Queue Multiprocessor (MQMS)");
        System.out.print("Choice: ");
        int arch = sc.nextInt();

        System.out.print("Enter Number of Processors: ");
        int numProcessors = sc.nextInt();

        System.out.print("Enter Time Quantum (for Round Robin): ");
        int quantum = sc.nextInt();

        System.out.println("\n\n==========================================================");
        System.out.println("   SIMULATION RESULTS (" + numProcessors + " Processors)");
        System.out.println("==========================================================");

        if (arch == 1) {
            // SQMS
            System.out.println("\n[1] SQMS - First Come First Served (FCFS)");
            CPUScheduler sqmsFcfs = new SQMScheduler(numProcessors, SQMScheduler.AlgorithmType.FCFS, 0);
            runAndPrint(sqmsFcfs, processes);

            System.out.println("\n[2] SQMS - Shortest Job Next (SJN)");
            CPUScheduler sqmsSjn = new SQMScheduler(numProcessors, SQMScheduler.AlgorithmType.SJN, 0);
            runAndPrint(sqmsSjn, processes);

            System.out.println("\n[3] SQMS - Round Robin (RR)");
            CPUScheduler sqmsRr = new SQMScheduler(numProcessors, SQMScheduler.AlgorithmType.RR, quantum);
            runAndPrint(sqmsRr, processes);

        } else if (arch == 2) {
            // MQMS
            System.out.println("\n[1] MQMS - First Come First Served (FCFS)");
            CPUScheduler mqmsFcfs = new MQMScheduler(numProcessors, new FCFSScheduler());
            runAndPrint(mqmsFcfs, processes);

            System.out.println("\n[2] MQMS - Shortest Job Next (SJN)");
            CPUScheduler mqmsSjn = new MQMScheduler(numProcessors, new SJNScheduler());
            runAndPrint(mqmsSjn, processes);

            System.out.println("\n[3] MQMS - Round Robin (RR)");
            CPUScheduler mqmsRr = new MQMScheduler(numProcessors, new RRScheduler(quantum));
            runAndPrint(mqmsRr, processes);
        }

        sc.close();
    }

    private static void runAndPrint(CPUScheduler scheduler, List<Process> processes) {
        List<Process> copy = new ArrayList<>(processes);
        List<ProcessStats> stats = scheduler.schedule(copy);

        stats.sort((s1, s2) -> s1.getProcess().getPid().compareTo(s2.getProcess().getPid()));

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s %-10s %-10s%n",
                "PID", "Arrival", "Burst", "Start", "Finish", "Wait", "Turnaround", "Processor");
        System.out.println("-----------------------------------------------------------------------------------------");

        for (ProcessStats ps : stats) {
            Process p = ps.getProcess();
            System.out.printf("%-10s %-10d %-10d %-10d %-10d %-10d %-10d %-10s%n",
                    p.getPid(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    ps.getStartTime(),
                    ps.getCompletionTime(),
                    ps.getWaitingTime(),
                    ps.getTurnaroundTime(),
                    ps.getProcessorId());
        }
        System.out.println("-----------------------------------------------------------------------------------------");

        double avgWt = scheduler.computeAverageWaitingTime(stats);
        double avgTat = scheduler.computeAverageTurnaroundTime(stats);
        double util = scheduler.computeCpuUtilization(stats);

        System.out.printf("Avg Waiting Time:    %.2f%n", avgWt);
        System.out.printf("Avg Turnaround Time: %.2f%n", avgTat);
        System.out.printf("CPU Utilization:     %.2f%%%n", util * 100);
    }
}