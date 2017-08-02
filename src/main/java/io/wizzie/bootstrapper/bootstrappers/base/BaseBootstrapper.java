package io.wizzie.bootstrapper.bootstrappers.base;

import io.wizzie.bootstrapper.builder.Bootstrapper;
import io.wizzie.bootstrapper.builder.Config;
import io.wizzie.bootstrapper.builder.Listener;
import io.wizzie.bootstrapper.builder.SourceSystem;

import java.util.List;

public abstract class BaseBootstrapper implements Bootstrapper {
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

    @Override
    public Config getConfig() {
        return config;
    }
}
