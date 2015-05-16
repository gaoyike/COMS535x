import java.util.BitSet;

/**
 * Created by chenguanghe on 1/29/15.
 */
public class BloomFilterDet implements Configuration {
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
    private FNVHashFunction[] hashFunctions;

    public BloomFilterDet(int setSize) {
        this.setSize = setSize;
        bloomFilter = new BitSet(setSize * bitsPerElement);
        numsOfHashFunction = (int) Math.ceil(Math.log(2) * bitsPerElement);
        dataSize = 0;
        hashFunctions = new FNVHashFunction[numsOfHashFunction];
        for (int i = 0; i < numsOfHashFunction; i++) {
            hashFunctions[i] = new FNVHashFunction(this.filterSize());
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
        for (FNVHashFunction f : hashFunctions) {
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
        s = s.toLowerCase();
        if (s == null)
            throw new NullPointerException("String used to find can not be null");
        byte[] bytes = s.getBytes();
        for (FNVHashFunction f : hashFunctions) {
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

   static class FNVHashFunction implements HashFunction {
        public final long prime = 16777619;

        private FNVHashFunction() {
        }

        private int hash_bits;
        private long offset;

        public FNVHashFunction(int hash_bits) {
            this.hash_bits = hash_bits;
            offset = generateOffset(Long.toHexString(Double.doubleToLongBits(Math.random())) + Long.toHexString(Double.doubleToLongBits(Math.random())) + Long.toHexString(Double.doubleToLongBits(Math.random())));
        }

        @Override
        public int hash(byte[] bytes) {

            long hash = offset;
            for (int i = 0; i < bytes.length; i++) {
                hash ^= bytes[i];
                hash *= prime;
            }
            return (int) ((hash & Integer.MAX_VALUE) % hash_bits);
        }

        private long generateOffset(String s) {
            long offset = 0;
            int hash_mod = 2 ^ hash_bits;
            int str_len = s.length();
            for (int i = 0; i < str_len; i++) {
                offset = (offset * prime) % hash_mod;
                offset = offset ^ s.charAt(i);
            }
            return offset;
        }
    }
    public static void main(String[] args) {
        FNVHashFunction fnvHashFunction = new FNVHashFunction(1000);
        String s = "test";
        int first = fnvHashFunction.hash(s.getBytes());
        int second = fnvHashFunction.hash(s.getBytes());
        System.out.println(first == second);
    }
}
