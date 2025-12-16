import java.util.ArrayList;
import java.util.List;

public class RRScheduler extends CPUScheduler {
    private int quantum;

    public RRScheduler(int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("Quantum must be > 0");
        }
        this.quantum = quantum;
    }

    public int getQuantum() {
        return quantum;
    }

    @Override
    public List<ProcessStats> schedule(List<Process> processes) {
        List<Process> list = new ArrayList<Process>();
        for (Process p : processes) {
            list.add(p);
        }

        // Sort by arrival time
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getArrivalTime() > list.get(j).getArrivalTime()) {
                    Process temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }

        int n = list.size();
        List<ProcessStats> result = new ArrayList<ProcessStats>();
        if (n == 0) {
            return result;
        }

        int[] remainingTime = new int[n];
        int[] firstStart = new int[n];
        int[] completionTime = new int[n];

        for (int i = 0; i < n; i++) {
            remainingTime[i] = list.get(i).getBurstTime();
            firstStart[i] = -1;      // not started yet
            completionTime[i] = 0;
        }

        // Ready queue stores indices of processes in "list"
        List<Integer> readyQueue = new ArrayList<Integer>();

        int currentTime = list.get(0).getArrivalTime();
        int index = 0; // next process in sorted list to arrive

        // Add processes that arrive at the initial currentTime
        while (index < n && list.get(index).getArrivalTime() <= currentTime) {
            readyQueue.add(index);
            index++;
        }

        while (!readyQueue.isEmpty() || index < n) {
            if (readyQueue.isEmpty()) {
                // CPU idle until next process arrives
                currentTime = list.get(index).getArrivalTime();
                while (index < n && list.get(index).getArrivalTime() <= currentTime) {
                    readyQueue.add(index);
                    index++;
                }
                continue;
            }

            // Get first process in the queue
            int procIndex = readyQueue.remove(0);
            Process p = list.get(procIndex);

            if (firstStart[procIndex] == -1) {
                firstStart[procIndex] = currentTime; // first time it gets CPU
            }

            int rem = remainingTime[procIndex];
            int execTime = quantum;
            if (rem < quantum) {
                execTime = rem;
            }

            currentTime += execTime;
            rem -= execTime;
            remainingTime[procIndex] = rem;

            // Add processes that arrived during this time slice
            while (index < n && list.get(index).getArrivalTime() <= currentTime) {
                readyQueue.add(index);
                index++;
            }

            if (rem == 0) {
                completionTime[procIndex] = currentTime;
            } else {
                // still remaining -> go back to end of queue
                readyQueue.add(procIndex);
            }
        }

        // Build ProcessStats in the sorted order
        for (int i = 0; i < n; i++) {
            Process p = list.get(i);
            int start = firstStart[i];
            int completion = completionTime[i];
            ProcessStats stats = new ProcessStats(p, start, completion);
            result.add(stats);
        }

        return result;
    }
}
