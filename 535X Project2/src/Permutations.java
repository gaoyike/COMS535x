import java.util.Random;

/**
 * Created by chenguanghe on 2/25/15.
 */
public class Permutations {
    private int a;
    private int b;
    private int p;
    private Random random = new Random();
    private Permutations() {
    }

    public Permutations(int nums) {
        p = generateRandomPrime(nums, 2*nums);
        a = random.nextInt(p);
        b = random.nextInt(p);
    }

    public int mapTo(int i) {
        return (a + b*i) % p;
    }
    private int generateRandomPrime(int from, int to) {
        int res;
        do {
            res = random.nextInt(to - from) + from;
        } while (!isPrime(res));
        return res;
    }
    private boolean isPrime(int num) {
        if (num % 2 == 0) return false;
        for (int i = 3; i * i <= num; i += 2)
            if (num % i == 0) return false;
        return true;
    }
}
