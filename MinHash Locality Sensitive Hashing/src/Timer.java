/**
 * Created by chenguanghe on 2/26/15.
 */
public class Timer {
        private final long start;
        public Timer() {
            start = System.currentTimeMillis();
        }
        public double elapsedTime() {
            long now = System.currentTimeMillis();
            return (now - start) / 1000.0;
        }

    }
