package io.wizzie.bootstrapper.builder;

public class SourceSystem {
    public String system;
    public String source;
    public String key;

    public SourceSystem(String system, String source) {
       this(system, source, null);
    }

    public SourceSystem(String system, String source, String key) {
        this.system = system;
        this.source = source;
        this.key = key;
    }
}
