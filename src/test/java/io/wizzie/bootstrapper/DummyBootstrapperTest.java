package io.wizzie.bootstrapper;

import io.wizzie.bootstrapper.builder.Bootstrapper;
import io.wizzie.bootstrapper.builder.BootstrapperBuilder;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DummyBootstrapperTest {

    @Test
    public void testFileBootstrapper() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File fileConf = new File(classLoader.getResource("base-config-file.json").getFile());

        Bootstrapper bootstrapper = BootstrapperBuilder.makeBuilder()
                .withConfigFile(fileConf.getAbsolutePath())
                .build();

        assertNotNull(bootstrapper);
        assertEquals("config", bootstrapper.getConfig().get("base"));
    }

    @Test
    public void closeFileBootstrapper() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File fileConf = new File(classLoader.getResource("base-config-file.json").getFile());

        Bootstrapper bootstrapper = BootstrapperBuilder.makeBuilder()
                .withConfigFile(fileConf.getAbsolutePath())
                .build();

        bootstrapper.close();
    }
}
