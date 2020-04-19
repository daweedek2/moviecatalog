package kostka.moviecatalog.entity.counters;

public class Counter {
    private int counterValue;

    public int getCounterValue() {
        return counterValue;
    }

    public void increment() {
        counterValue++;
    }
}
