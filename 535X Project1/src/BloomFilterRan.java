import java.util.BitSet;
import java.util.Random;

/**
 * Created by chenguanghe on 1/29/15.
 */
public class BloomFilterRan implements Configuration{
    /**
     * Creates a Bloom filter that can store a set S of cardinality setSize and the filter uses bitsPerElement many bits for element of the set.
     * This implies that the size of the filter should (approximately) setSize × bitsPerElement. The
     * number of hash functions should be the optimal choice which is ln 2×bitsPerElement.
     *
     * @param setSize        The size of BF
     * @param bitsPerElement the bits of element
     */
    private int setSize;
    private BitSet bloomFilter;
    private int numsOfHashFunction;
    private int dataSize;
    private RanHashFunction[] hashFunctions;
    public BloomFilterRan(int setSize) {
        this.setSize = setSize;
        bloomFilter = new BitSet(setSize * bitsPerElement);
        numsOfHashFunction = (int) Math.ceil(Math.log(2) * bitsPerElement);
        dataSize = 0;
        hashFunctions = new RanHashFunction[numsOfHashFunction];
        for (int i = 0; i < numsOfHashFunction; i++) {
            hashFunctions[i] = new RanHashFunction(setSize * bitsPerElement);
        }
    }

    /**
     * Adds the string s to the filter. Type of this method is void. This method
     * should be case-insensitive. For example, it should not distinguish between “Galaxy” and “galaxy”.
     *
     * @param s the string to be added
     */
    public void add(String s) {
        if (s == null)
            throw new NullPointerException("String used to add can not be null");
        s = s.toLowerCase();
        byte[] bytes = s.getBytes();
        for (RanHashFunction f : hashFunctions) {
            bloomFilter.set(f.hash(bytes));
        }
        dataSize++;
    }

    /**
     * Returns true if s appears in the filter; otherwise returns false
     *
     * @param s the string
     * @return Returns true if s appears in the filter; otherwise returns false
     */
    public boolean appears(String s) {
        if (s == null)
            throw new NullPointerException("String used to find can not be null");
        s = s.toLowerCase();
        byte[] bytes = s.getBytes();
        for (RanHashFunction f : hashFunctions) {
            if (!bloomFilter.get(f.hash(bytes)))
                return false;
        }
        return true;
    }

    /**
     * Returns the size of the filter (the size of the table).
     *
     * @return Returns the size of the filter (the size of the table).
     */
    public int filterSize() {
        return bloomFilter.size();
    }

    /**
     * Returns the number of elements added to the filter.
     *
     * @return Returns the number of elements added to the filter.
     */
    public int dataSize() {
        return dataSize;
    }

    /**
     * Returns the number of hash function used
     *
     * @return Returns the number of hash function used
     */
    public int numHashes() {
        return numsOfHashFunction;
    }

   static class RanHashFunction implements HashFunction {
        private RanHashFunction() {
        }

        private int hash_bits;
        private Random random = new Random();
        private int prime;
        private int a;
        private  int b;
        public RanHashFunction(int hash_bits) {
            this.hash_bits = hash_bits;
            prime = generateRandomPrime(hash_bits, 2 * hash_bits);
            a = random.nextInt(prime);
            b = random.nextInt(prime);
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

        @Override
        public int hash(byte[] bytes) {
            long hash = 0;
            int apl = a;
            int bet = b;
            for (int i = 0; i < bytes.length; i++) {
                apl = apl*bet%(hash_bits-1);
                hash = (apl*hash+bytes[i])%hash_bits;
            }
            return (int) ((hash & Integer.MAX_VALUE)%hash_bits);
        }
    }

    public static void main(String[] args) {
        RanHashFunction ranHashFunction = new RanHashFunction(1000);
        String s = "test";
        int first = ranHashFunction.hash(s.getBytes());
        int second = ranHashFunction.hash(s.getBytes());
        System.out.println(first == second);
    }
}
