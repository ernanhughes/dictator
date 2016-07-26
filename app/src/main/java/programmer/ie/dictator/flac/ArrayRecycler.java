package programmer.ie.dictator.flac;

import java.util.concurrent.LinkedBlockingQueue;

public class ArrayRecycler {
    LinkedBlockingQueue<int[]> usedIntArrays = null;

    ArrayRecycler() {
        usedIntArrays = new LinkedBlockingQueue<int[]>();
    }

    public void add(int[] array) {
        usedIntArrays.add(array);
    }

    public int[] getArray(int size) {
        int[] result = usedIntArrays.poll();
        if (result == null) {
            result = new int[size];
        } else if (result.length < size) {
            usedIntArrays.offer(result);
            result = new int[size];
        }
        return result;
    }
}
