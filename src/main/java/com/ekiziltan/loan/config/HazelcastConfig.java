package com.ekiziltan.loan.config;


import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class HazelcastConfig {

    @Bean
    public Config hazelcastConfiguration() {
        Config config = new Config();
        config.setInstanceName("hazelcast-instance");

        MapConfig loanMapConfig = new MapConfig();
        loanMapConfig.setName("loansCache");
        loanMapConfig.setTimeToLiveSeconds(300);
        config.addMapConfig(loanMapConfig);

        MapConfig installmentMapConfig = new MapConfig();
        installmentMapConfig.setName("installmentsCache");
        installmentMapConfig.setTimeToLiveSeconds(300);
        config.addMapConfig(installmentMapConfig);

        return config;
    }
}

