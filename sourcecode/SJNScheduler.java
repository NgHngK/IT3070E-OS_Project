import java.util.ArrayList;
import java.util.List;

public class SJNScheduler extends CPUScheduler {
    @Override
    public List<ProcessStats> schedule(List<Process> processes) {
        List<Process> list = new ArrayList<Process>();
        for (Process p : processes) {
            list.add(p);
        }

        List<ProcessStats> result = new ArrayList<ProcessStats>();

        if (list.size() == 0) {
            return result;
        }

        int currentTime = 0;
        int i;

        // find smallest arrival time to start from
        int minArrival = list.get(0).getArrivalTime();
        for (i = 1; i < list.size(); i++) {
            if (list.get(i).getArrivalTime() < minArrival) {
                minArrival = list.get(i).getArrivalTime();
            }
        }
        currentTime = minArrival;

        while (list.size() > 0) {
            int bestIndex = -1;
            int bestBurst = Integer.MAX_VALUE;

            // find process that has arrived and has smallest burst time
            for (i = 0; i < list.size(); i++) {
                Process p = list.get(i);
                if (p.getArrivalTime() <= currentTime) {
                    if (p.getBurstTime() < bestBurst) {
                        bestBurst = p.getBurstTime();
                        bestIndex = i;
                    }
                }
            }

            // if no process is ready, just move time forward by 1
            if (bestIndex == -1) {
                currentTime = currentTime + 1;
                continue;
            }

            Process next = list.get(bestIndex);
            int start = currentTime;
            int completion = start + next.getBurstTime();

            ProcessStats stats = new ProcessStats(next, start, completion);
            result.add(stats);

            currentTime = completion;
            list.remove(bestIndex);
        }

        return result;
    }
}
