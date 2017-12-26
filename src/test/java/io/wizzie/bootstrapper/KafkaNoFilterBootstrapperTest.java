package io.wizzie.bootstrapper;

import io.wizzie.bootstrapper.bootstrappers.impl.KafkaBootstrapper;
import io.wizzie.bootstrapper.builder.Bootstrapper;
import io.wizzie.bootstrapper.builder.BootstrapperBuilder;
import kafka.utils.MockTime;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class KafkaNoFilterBootstrapperTest {

    @ClassRule
    public static EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(1);
    private final static MockTime MOCK_TIME = CLUSTER.time;

    private static final int REPLICATION_FACTOR = 1;

    private static Properties producerConfig = new Properties();


    @BeforeClass
    public static void startKafkaCluster() throws Exception {
        // inputs
        CLUSTER.createTopic("topic1", 1, REPLICATION_FACTOR);
        CLUSTER.createTopic("topic2", 1, REPLICATION_FACTOR);

        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
        producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producerConfig.put(ProducerConfig.RETRIES_CONFIG, 0);
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
    }

    @Test
    public void kafkaTest() throws Exception {

        Map<String, Object> configMap = new HashMap<>();

        configMap.put(KafkaBootstrapper.BOOTSTRAP_TOPICS_CONFIG, Arrays.asList("topic1", "topic2"));
        configMap.put(KafkaBootstrapper.APPLICATION_ID_CONFIG, "kafka-no-filter-id");
        configMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());

        IntegrationTestUtils.produceKeyValuesSynchronously(
                "topic1", Arrays.asList(new KeyValue<>("my-id-test-1", "myConfig1"))
                , producerConfig, MOCK_TIME
        );

        IntegrationTestUtils.produceKeyValuesSynchronously(
                "topic1", Arrays.asList(new KeyValue<>("my-id-test-2", "myConfig2"))
                , producerConfig, MOCK_TIME
        );


        IntegrationTestUtils.produceKeyValuesSynchronously(
                "topic2", Arrays.asList(new KeyValue<>("my-id-test-1", "myConfig11"))
                , producerConfig, MOCK_TIME
        );

        IntegrationTestUtils.produceKeyValuesSynchronously(
                "topic2", Arrays.asList(new KeyValue<>("my-id-test-2", "myConfig22"))
                , producerConfig, MOCK_TIME
        );


        Bootstrapper bootstrapper = BootstrapperBuilder.makeBuilder()
                .boostrapperClass("io.wizzie.bootstrapper.bootstrappers.impl.KafkaNoFilterBootstrapper")
                .withConfigMap(configMap)
                .listener(
                        (sourceSystem, config) -> {
                            switch (sourceSystem.source) {
                                case "topic1":
                                    if (sourceSystem.key.equals("my-id-test-1")) {
                                        assertEquals("myConfig1", config);
                                    } else if (sourceSystem.key.equals("my-id-test-2")) {
                                        assertEquals("myConfig2", config);
                                    }
                                    break;
                                case "topic2":
                                    if (sourceSystem.key.equals("my-id-test-1")) {
                                        assertEquals("myConfig11", config);
                                    } else if (sourceSystem.key.equals("my-id-test-2")) {
                                        assertEquals("myConfig22", config);
                                    }
                                    break;
                            }
                        }
                )
                .build();

        assertNotNull(bootstrapper);
    }
}
