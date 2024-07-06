package stark.stellasearch.service.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfiguration
{
    @Bean(name = "highPriorityTaskExecutor")
    public ThreadPoolTaskExecutor highPriorityTaskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("HighPriorityExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "lowPriorityTaskExecutor")
    public ThreadPoolTaskExecutor lowPriorityTaskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("LowPriorityExecutor-");
        executor.initialize();
        return executor;
    }
}
