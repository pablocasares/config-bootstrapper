package io.wizzie.bootstrapper.bootstrappers.base;

import io.wizzie.bootstrapper.builder.Bootstrapper;
import io.wizzie.bootstrapper.builder.Config;
import io.wizzie.bootstrapper.builder.Listener;
import io.wizzie.bootstrapper.builder.SourceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Basic abstract class that inherits from Thread class and implements Bootstrapper interface.
 */
public abstract class ThreadBootstrapper extends Thread implements Bootstrapper{
    private static final Logger log = LoggerFactory.getLogger(ThreadBootstrapper.class);
    Config config;
    List<Listener> listeners;

    @Override
    public void prepare(Config config, List<Listener> listeners) throws Exception {
        this.listeners = listeners;
        this.config = config;
        init(config);
    }

    @Override
    public void update(SourceSystem sourceSystem, String bootstrapperConfig) {
        listeners.forEach(listeners -> listeners.updateConfig(sourceSystem, bootstrapperConfig));
    }

    /**
     * Interrupt thread and close bootstraper
     */
    @Override
    public void interrupt() {
        try {
            close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Config getConfig() {
        return config;
    }
}
