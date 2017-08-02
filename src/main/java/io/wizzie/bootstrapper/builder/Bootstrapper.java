package io.wizzie.bootstrapper.builder;

import java.util.List;

/**
 * A simple interface for self-starting process definition
 */
public interface Bootstrapper {
    void prepare(Config config, List<Listener> listeners) throws Exception;
    void init(Config config) throws Exception;
    void close()throws Exception;
    void update(SourceSystem sourceSystem, String bootstrapperConfig);
    Config getConfig();
}
