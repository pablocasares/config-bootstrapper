package io.wizzie.bootstrapper;

import io.wizzie.bootstrapper.builder.Bootstrapper;
import io.wizzie.bootstrapper.builder.BootstrapperBuilder;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileBootstrapperTest {

    @Test
    public void testFileBootstrapper() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File fileConf = new File(classLoader.getResource("file-bootstrapper-config.json").getFile());

        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileConf.getAbsolutePath()));

        StringBuilder stringBuffer = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line).append("\n");
        }

        Map<String, Object> configMap = new HashMap<>();
        configMap.put("file.bootstraper.path", Thread.currentThread().getContextClassLoader().getResource("file-bootstrapper-config.json").getFile());

        Bootstrapper bootstrapper = BootstrapperBuilder.makeBuilder()
                .boostrapperClass("io.wizzie.bootstrapper.bootstrappers.impl.FileBootstrapper")
                .withConfigMap(configMap)
                .listener((sourceSystem, config) -> {
                    assertEquals("file", sourceSystem.system);
                    assertEquals(fileConf.getAbsolutePath(), sourceSystem.source);
                    assertEquals(stringBuffer.toString(), config);
                })
                .build();

        assertNotNull(bootstrapper);
    }

    @Test
    public void closeFileBootstrapper() throws Exception {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("file.bootstraper.path", Thread.currentThread().getContextClassLoader().getResource("file-bootstrapper-config.json").getFile());

        Bootstrapper bootstrapper = BootstrapperBuilder.makeBuilder()
                .boostrapperClass("io.wizzie.bootstrapper.bootstrappers.impl.FileBootstrapper")
                .withConfigMap(configMap)
                .listener((sourceSystem, config) -> {})
                .build();

        bootstrapper.close();
    }
}
