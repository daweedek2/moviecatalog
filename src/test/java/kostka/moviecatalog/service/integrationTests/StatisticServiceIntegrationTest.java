package kostka.moviecatalog.service.integrationTests;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.entity.counters.AtomicCounter;
import kostka.moviecatalog.entity.counters.Counter;
import kostka.moviecatalog.entity.counters.SynchronizedCounter;
import kostka.moviecatalog.service.StatisticService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class StatisticServiceIntegrationTest {

    public static final int THREADS = 3;
    public static final int FROM_RANGE = 0;
    public static final int TO_RANGE = 1000;

    @Autowired
    private StatisticService statisticService;

    /***
     * Test verifies that not synchronized counter is not incremented correctly
     * sometimes it could happen that the test fail, but it is not probable
     * @throws InterruptedException .
     */
    @Test
    public void multiThreadNonSyncCounterIntegrationTest() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(THREADS);
        Counter counter = new Counter();

        IntStream.range(FROM_RANGE, TO_RANGE)
                .forEach(count -> service.submit(counter::increment));
        service.awaitTermination(TO_RANGE, TimeUnit.MILLISECONDS);

        assertThat(counter.getCounterValue()).isNotEqualTo(TO_RANGE);
    }

    /***
     * Test verifies that using sync method the counter is incremented properly
     * @throws InterruptedException .
     */
    @Test
    public void multiThreadSyncCounterIntegrationTest() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(THREADS);
        SynchronizedCounter syncCounter = new SynchronizedCounter();

        IntStream.range(FROM_RANGE, TO_RANGE)
                .forEach(count -> service.submit(syncCounter::syncedIncrement));
        service.awaitTermination(TO_RANGE, TimeUnit.MILLISECONDS);

        assertThat(syncCounter.getCounterValue()).isEqualTo(TO_RANGE);
    }

    /***
     * Test verifies that using atomic integer counter is incremented properly
     * @throws InterruptedException .
     */
    @Test
    public void multiThreadAtomicCounterIntegrationTest() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(THREADS);
        AtomicCounter atomicCounter = new AtomicCounter();

        IntStream.range(FROM_RANGE, TO_RANGE)
                .forEach(count -> service.submit(atomicCounter::atomicIncrement));
        service.awaitTermination(TO_RANGE, TimeUnit.MILLISECONDS);

        assertThat(atomicCounter.getCounterValue()).isEqualTo(TO_RANGE);
    }
}
