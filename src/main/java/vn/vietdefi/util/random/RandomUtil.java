package vn.vietdefi.util.random;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

public class RandomUtil {
    private static final Random random = new Random();

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static int nextInt(int low, int height) {
        return random.nextInt(height - low) + low;
    }

    public static int nextInt(int[] rate) throws Exception {
        int rnd = nextInt(Arrays.stream(rate).sum());
        for (int i = 0; i < rate.length; i++) {
            if (rnd < rate[i]) {
                return i;
            }
            rnd -= rate[i];
        }
        throw new Exception("Random Error");
    }

    public static int randomIndexByRate(int[] rate, int total){
        int random = nextInt(total);
        for (int i = 0; i < rate.length; i++) {
            if (random < rate[i]) {
                return i;
            }
            random -= rate[i];
        }
        return -1;
    }

    public static boolean nextBoolean(double rate) {
        return random.nextDouble() < rate;
    }

    public static int randomExclude(int[] include, Vector<Integer> exclude) {
        Vector<Integer> res = new Vector<>();
        for (int i : include) {
            res.add(i);
        }
        res.removeAll(exclude);
        return res.get(nextInt(res.size()));
    }

    public static int randomExclude(int startIndexInclude, int endIndexInclude, Vector<Integer> exclude) {
        Vector<Integer> res = new Vector<>();
        for (int i = startIndexInclude; i < endIndexInclude; i++) {
            res.add(i);
        }
        res.removeAll(exclude);
        return res.get(nextInt(res.size()));
    }
}
