package kostka.moviecatalog.entity.counters;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread safe counter which uses AtomicInteger.
 */
public class AtomicCounter {
    private AtomicInteger counterValue = new AtomicInteger(0);

    public int getCounterValue() {
        return counterValue.get();
    }

    public void atomicIncrement() {
        counterValue.getAndIncrement();
    }
}
