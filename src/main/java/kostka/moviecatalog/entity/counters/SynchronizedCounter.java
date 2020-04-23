package kostka.moviecatalog.entity.counters;

/**
 * Thread safe counter which uses synchronized methods.
 */
public class SynchronizedCounter {
    private volatile int counterValue;

    public synchronized void syncedIncrement() {
        counterValue++;
    }

    public synchronized int getCounterValue() {
        return counterValue;
    }

    public synchronized void setCounterValue(final int counter) {
        this.counterValue = counter;
    }
}
