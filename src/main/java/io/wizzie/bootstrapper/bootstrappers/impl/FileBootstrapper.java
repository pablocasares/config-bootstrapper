package io.wizzie.bootstrapper.bootstrappers.impl;

import io.wizzie.bootstrapper.bootstrappers.base.BaseBootstrapper;
import io.wizzie.bootstrapper.builder.Config;
import io.wizzie.bootstrapper.builder.SourceSystem;

import java.io.BufferedReader;
import java.io.FileReader;

public class FileBootstrapper extends BaseBootstrapper {
    public static final String FILE_PATH = "file.bootstraper.path";

    @Override
    public void init(Config config) throws Exception {
        String filePath = config.get(FILE_PATH);
        SourceSystem sourceSystem = new SourceSystem("file", filePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

        StringBuilder stringBuffer = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {

            stringBuffer.append(line).append("\n");
        }

        update(sourceSystem, stringBuffer.toString());
    }


    @Override
    public void close() {
        //Nothing to do
    }
}
