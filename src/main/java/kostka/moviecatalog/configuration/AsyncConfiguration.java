package kostka.moviecatalog.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for enabling concurrency in the project. Thread pool is specified.
 */
@Configuration
public class AsyncConfiguration {
    public static final int CORE_POOL_SIZE = 3;
    public static final int MAX_POOL_SIZE = 3;
    public static final int QUEUE_CAPACITY = 50;
    public static final String MOVIE_THREAD = "MovieThread-";
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        LOGGER.info("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(MOVIE_THREAD);
        executor.initialize();
        return executor;
    }
}
