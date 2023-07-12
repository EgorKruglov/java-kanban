package manager.util;

import task.Task;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task t1, Task t2) {
        if (t2.getStartTime() == null) {
            return 1;
        }
        if (t1.getStartTime() == null) {
            return -1;
        }
        return t1.getStartTime().compareTo(t2.getStartTime());
    }
}
