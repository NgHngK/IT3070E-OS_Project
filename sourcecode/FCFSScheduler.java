import java.util.ArrayList;
import java.util.List;

public class FCFSScheduler extends CPUScheduler {
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

        List<ProcessStats> result = new ArrayList<ProcessStats>();
        int currentTime = 0;

        for (int i = 0; i < list.size(); i++) {
            Process p = list.get(i);

            if (currentTime < p.getArrivalTime()) {
                currentTime = p.getArrivalTime();
            }

            int start = currentTime;
            int completion = start + p.getBurstTime();
            ProcessStats stats = new ProcessStats(p, start, completion);
            result.add(stats);

            currentTime = completion;
        }

        return result;
    }
}
