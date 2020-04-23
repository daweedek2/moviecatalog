package kostka.moviecatalog.entity.counters;

/**
 * Thread not safe counter just for education purposes.
 */
public class Counter {
    private int counterValue;

    public int getCounterValue() {
        return counterValue;
    }

    public void increment() {
        counterValue++;
    }
}
