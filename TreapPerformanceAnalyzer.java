import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class TreapPerformanceAnalyzer {
    private static class StopWatch {
        private long startTime;
        private long stopTime;
        private long cumulativeTime;
        private boolean started;

        public void start() {
            if (started) {
                return;
            }
            startTime = System.nanoTime();
            started = true;
        }

        public void stop() {
            if (!started) {
                return;
            }
            stopTime = System.nanoTime();
            cumulativeTime += (stopTime  - startTime);
            startTime = stopTime = 0;
            started = false;
        }

        public void reset() {
            started = false;
            cumulativeTime = 0;
        }

        // return elapsed time in nanoseconds
        public long cumulativeTime() {
            return cumulativeTime;
        }

    }

//        public static int [] INPUT_SIZES = new int [] {100000, 500000, 1000000, 5000000, 10000000, 15000000};
//        public static int [] INPUT_SIZES = new int [] {10000, 25000, 50000, 100000, 150000};
        public static int [] INPUT_SIZES = new int [] {10000, 20000, 30000, 40000, 50000};
//    public static int [] INPUT_SIZES = new int [] {5000, 10000, 15000, 20000, 30000};
    public static int NUM_TRIALS = 1000;
    public static int INSERT_INDEX = 0;
    public static int DELETE_INDEX = 1;
    public static int FIND_INDEX = 2;
    public static int SPLIT_INDEX = 3;
    public static int JOIN_INDEX = 4;

    private static int [] keys = new int[INPUT_SIZES[INPUT_SIZES.length-1]+NUM_TRIALS];
    // times in milliseconds or nanoseconds
    private static double [][] cumTimes = new double[5][INPUT_SIZES.length];

    // populate treap up to certain size
    private static void populate(Treap<Integer> treap, int nextInputSize) {
        int currentSize = treap.size();
        ConcurrentSkipListMap<Integer, Integer> skipList = new ConcurrentSkipListMap<>();
        Random rand = new Random();
        for (int i = 0; i < nextInputSize; i++) {
            skipList.put(rand.nextInt(nextInputSize), i);
        }
        for (int i=currentSize; i < nextInputSize; i++) {
            treap.insert(keys[i]); // note this insert function inserts key with a random priority
        }
    }

    // Pick randomly a sample of keys from sortedKeyArr
    private static List<Integer> keysForSuccessfulFind(int [] sortedKeyArr, int sampleSize) {
        List<Integer> lst = new ArrayList<>();
        Random random = new Random();
        int i=0;
        while (i < sampleSize) {
            int idx = random.nextInt(sortedKeyArr.length);
            int key = sortedKeyArr[idx];
            if (lst.contains(key)) {
                continue;
            }
            lst.add(key);
            i++;
        }
        return lst;
    }

    private static List<Integer> keysForUnsuccessfulFind(int [] sortedKeyArr, int sampleSize) {
        List<Integer> lst = new ArrayList<>();
        Random random = new Random();
        int i=0;
        int key= 0;
        while (i <= sampleSize) {
            int idx = random.nextInt(sortedKeyArr.length+1);
            if (idx == sortedKeyArr.length) {
                key = sortedKeyArr[sortedKeyArr.length-1]+1;
            } else {
                key = sortedKeyArr[idx]-1;
            }
            if (lst.contains(key)) {
                continue;
            }
            lst.add(key);
            i++;
        }
        return lst;
    }


    public static void dumpPerformanceStats() {

        int[] column = INPUT_SIZES;
        int numCol = column.length;
        String[] row = {"insert", "remove", "find", "split","join"};

        // Create a 2D array to hold the average times
        double[][] avgTimes = new double[5][numCol];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < numCol; j++) {
                avgTimes[i][j] = cumTimes[i][j] / NUM_TRIALS;
            }
        }

        // Print the table using formatted strings
        System.out.println("Average Times (in milliseconds):");
        System.out.print("Operation  ");
        for (int j = 0; j < 5; j++) {
            System.out.printf("%-8s", column[j]);
        }
        System.out.println();
        for (int i = 0; i < 5; i++) {
            System.out.printf("%-10s", row[i]);
            for (int j = 0; j < 5; j++) {
                System.out.printf("%-8.2f", avgTimes[i][j]);
            }
            System.out.println();
        }
    }


    public static void measurePerformance(Treap<Integer> treap) {
        List<Integer> lst = new ArrayList<>();
        Random rand = new Random();
        for (int i=0; i < keys.length; i++) {
            lst.add(2*i);
        }
        for(int i = keys.length-1; i > 0; i--){
            int j = rand.nextInt(i + 1);
            int temp = lst.get(i);
            lst.set(i, lst.get(j));
            lst.set(j, temp);
        }
        Collections.shuffle(lst);
        for (int i=0; i < keys.length; i++) {
            keys[i] = lst.get(i);
        }
        StopWatch watch = new StopWatch();
        StopWatch watch1 = new StopWatch();
        for (int i=0; i < INPUT_SIZES.length; i++) {
//            1
            populate(treap,INPUT_SIZES[i]);
            System.out.println("After populate: "+INPUT_SIZES[i] + " Treap size is: "+ treap.size());

//            2
            int [] subArr = Arrays.copyOfRange(keys, i > 0 ? INPUT_SIZES[i-1] : 0, INPUT_SIZES[i]);
            Arrays.sort(subArr);
//            3
            List<Integer> sample = keysForSuccessfulFind(subArr, NUM_TRIALS);
//            4
            for (int j = 0; j < sample.size(); j++) {
                watch.start();
                treap.findKey(sample.get(j));
                watch.stop();
            }
            cumTimes[FIND_INDEX][i] = watch.cumulativeTime() / (double) NUM_TRIALS;

//            5
            List<Integer> UnSuccessfulSample = keysForUnsuccessfulFind(subArr,NUM_TRIALS);

//            4
            for (int j = 0; j < UnSuccessfulSample.size(); j++) {
                watch.start();
                treap.findKey(UnSuccessfulSample.get(j));
                watch.stop();
            }
            cumTimes[FIND_INDEX][i] = watch.cumulativeTime() / (double) NUM_TRIALS;

//            6
            cumTimes[FIND_INDEX][i] += cumTimes[FIND_INDEX][i];
            cumTimes[FIND_INDEX][i] /= 2;

//            7
            List<Integer> splitJoinKeys = keysForSuccessfulFind(subArr, NUM_TRIALS);
            watch.reset();
            watch1.reset();
            for (int j = 0; j < splitJoinKeys.size(); j++) {
                watch.start();
                Treap<Integer> splitResult = treap.split(splitJoinKeys.get(j));
                watch.stop();
                watch1.start();
                treap.join(splitResult);
                watch1.stop();
            }
            cumTimes[SPLIT_INDEX][i] = watch.cumulativeTime() / (double) NUM_TRIALS;
            cumTimes[JOIN_INDEX][i] = watch1.cumulativeTime() / (double) NUM_TRIALS;

////            8
            List<Integer> UnsplitJoinKeys = keysForUnsuccessfulFind(subArr, NUM_TRIALS);
            watch.reset();
            watch1.reset();
            for (int j = 0; j < UnsplitJoinKeys.size(); j++) {
                watch.start();
                Treap<Integer> UnsplitResult = treap.split(UnsplitJoinKeys.get(j));
                watch.stop();
                watch1.start();
                treap.join(UnsplitResult);
                watch1.stop();
            }
            cumTimes[SPLIT_INDEX][i] = watch.cumulativeTime() / (double) NUM_TRIALS;
            cumTimes[JOIN_INDEX][i] = watch1.cumulativeTime() / (double) NUM_TRIALS;

//            9
            cumTimes[SPLIT_INDEX][i] += cumTimes[SPLIT_INDEX][i];
            cumTimes[SPLIT_INDEX][i] /= 2;
            cumTimes[JOIN_INDEX][i] += cumTimes[JOIN_INDEX][i];
            cumTimes[JOIN_INDEX][i] /= 2;

//            10
            List<Integer> insretDeleteKeys = keysForSuccessfulFind(subArr, NUM_TRIALS);
            watch.reset();
            watch1.reset();
            for (int j = 0; j < insretDeleteKeys.size(); j++) {
                watch.start();
                treap.insert(insretDeleteKeys.get(j));
                watch.stop();
                watch1.start();
                treap.delete(insretDeleteKeys.get(j));
                watch1.stop();
            }
            cumTimes[INSERT_INDEX][i] = watch.cumulativeTime() / (double) NUM_TRIALS;
            cumTimes[DELETE_INDEX][i] = watch1.cumulativeTime() / (double) NUM_TRIALS;


            /**
             * ToDO :
             *   1. Populate treap up to next input size (use populate() function)
             *   2.  Find the subarray of keys inserted so far and sort it
             *          e.g.int [] subArr = Arrays.copyOfRange(keys,
             *                         i > 0 ? INPUT_SIZES[i-1] : 0, INPUT_SIZES[i]);
             *              Arrays.sort(subArr);
             *   3. Get a sample from arr1 of size equal to NUM_TRIALS for successful find (use keysForSucccessfulFind())
             *   4. Measure cum times for doing search for these keys (using stop watch) and store in cumTimes array
             *   5. Repeat steps 3 and 4 for unsuccessful find (for this use keysForUnsuccessfulFind())
             *   6. You may want to compute average of cumTimes over 2*NUM_TRIALS and store it in
             *        cumTimes[FIND_INDEX][i]
             *   7. Do steps 3-4 for split and join operations using keysForSucccessfulFind(); you may want to use one stop watch
             *        for split and another for join; for each trial, do split followed by join of result of split
             *   8. Repeat step 7 for keysForUnsucccessfulFind()
             *   9. Again compute average times using cumTimes for these operations over 2*NUM_TRIALS
             *       and store the respectively in cumTimes arrays cumTimes[SPLIT_INDEX][i]
             *       and cumTimes[JOIN_INDEX][i]
             *  10. Next we insert NUM_TRIALS keys from keys array starting in indexes INPUT_SIZES[i] and
             *      measure the average time for insert and store it in cumTimes[INSERT_INDEX][i]
             *  11. We then remove the same keys from keys array starting in indexes INPUT_SIZES[i] and
             *      measure the time for remove and store it in cumTimes[DELETE_INDEX][i]
             *      (you could combine steps 10 and 11 by doing insert followed by delete of the same key)
             */
        }
        dumpPerformanceStats();
    }

    public static void main(String [] args) {
        Treap<Integer> treap = new Treap<>();
        measurePerformance(treap);
    }

}
