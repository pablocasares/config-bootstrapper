package io.wizzie.bootstrapper.builder;

import io.wizzie.bootstrapper.bootstrappers.base.ThreadBootstrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BootstrapperBuilder {
    private static final Logger log = LoggerFactory.getLogger(BootstrapperBuilder.class);

    Config config;
    String className = "io.wizzie.bootstrapper.bootstrappers.impl.DummyBootstrapper";
    List<Listener> listeners;
    String filePath;
    Map<String, Object> configMap;

    public BootstrapperBuilder() {
        this.listeners = new ArrayList<>();
    }

    public static BootstrapperBuilder makeBuilder() {
        return new BootstrapperBuilder();
    }

    public BootstrapperBuilder boostrapperClass(String className) {
        this.className = className;
        return this;
    }

    public BootstrapperBuilder listener(Listener listener) {
        listeners.add(listener);
        return this;
    }

    public BootstrapperBuilder withConfigFile(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public BootstrapperBuilder withConfigMap(Map<String, Object> configMap) {
        this.configMap = configMap;
        return this;
    }

    public BootstrapperBuilder withConfigInstance(Config config) {
        this.config = config;
        return this;
    }

    public BootstrapperBuilder listeners(List<Listener> listeners) {
        listeners.addAll(listeners);
        return this;
    }

    public Bootstrapper build() {
        Bootstrapper bootstrapper = null;

        try {
            Class bootstraperClass = Class.forName(className);
            bootstrapper = (Bootstrapper) bootstraperClass.newInstance();

            if(config == null ) config = new Config(filePath, configMap);

            bootstrapper.prepare(config, listeners);

            if (bootstrapper instanceof ThreadBootstrapper) {
                ((ThreadBootstrapper) bootstrapper).start();
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error(String.format("Can't build the bootstrapper class [%s]", className), e);
        } catch (Exception e) {
            log.error("Exception on initializing the bootstrapper", e);
            bootstrapper = null;
        }


        return bootstrapper;
    }
}

