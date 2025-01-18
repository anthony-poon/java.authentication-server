package com.anthonypoon.authenticationserver.config.initialization;

import com.anthonypoon.authenticationserver.config.initialization.runner.InitializationRunner;
import com.anthonypoon.authenticationserver.persistence.repository.InitializationEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@Slf4j
public class InitializationConfigurator implements CommandLineRunner {
    private final List<InitializationRunner> runners;
    private final InitializationEntryRepository entries;

    public InitializationConfigurator(List<InitializationRunner> runners, InitializationEntryRepository entries) {
        this.runners = runners;
        this.entries = entries;
    }

    @Override
    @Transactional
    public void run(String... args) {
        var shouldSkip = entries.hasAny();
        if (shouldSkip) {
            return;
        }
        log.info("Running initialization");
        for (InitializationRunner runner : runners) {
            runner.run();
        }
    }
}
