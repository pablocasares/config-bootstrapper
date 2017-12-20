package io.wizzie.bootstrapper.bootstrappers.impl;

import io.wizzie.bootstrapper.bootstrappers.base.BaseBootstrapper;
import io.wizzie.bootstrapper.builder.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyBootstrapper extends BaseBootstrapper {
    private static final Logger log = LoggerFactory.getLogger(DummyBootstrapper.class);

    @Override
    public void init(Config config) throws Exception {
        log.info("I'm dummy bootstrapper, so I do nothing!!");
    }

    @Override
    public void close() throws Exception {
        log.info("Dummy bootstrapper goes to sleep ... ZzZz");
    }
}
