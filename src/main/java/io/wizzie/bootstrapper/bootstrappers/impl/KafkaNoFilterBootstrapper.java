package io.wizzie.bootstrapper.bootstrappers.impl;

import io.wizzie.bootstrapper.bootstrappers.base.ThreadBootstrapper;
import io.wizzie.bootstrapper.builder.Config;
import io.wizzie.bootstrapper.builder.SourceSystem;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class KafkaNoFilterBootstrapper extends ThreadBootstrapper {
    private static final Logger log = LoggerFactory.getLogger(KafkaNoFilterBootstrapper.class);
    public final static String BOOTSTRAP_TOPICS_CONFIG = "bootstrap.kafka.topics";
    public final static String APPLICATION_ID_CONFIG = "application.id";

    AtomicBoolean closed = new AtomicBoolean(false);
    List<TopicPartition> storePartitions;

    String appId;
    KafkaConsumer<String, String> restoreConsumer;

    @Override
    public void init(Config config) throws IOException {
        Properties consumerConfig = new Properties();
        appId = config.get(APPLICATION_ID_CONFIG);
        consumerConfig.put(BOOTSTRAP_SERVERS_CONFIG, config.get(BOOTSTRAP_SERVERS_CONFIG));
        consumerConfig.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfig.put(ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerConfig.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerConfig.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerConfig.put(GROUP_ID_CONFIG, String.format(
                "kafka-no-filter-bootstraper-%s-%s", appId, UUID.randomUUID().toString())
        );

        List<String> bootstrapperTopics = config.get(BOOTSTRAP_TOPICS_CONFIG);

        storePartitions = new ArrayList<>();
        restoreConsumer = new KafkaConsumer<>(consumerConfig);

        for (String bootstrapperTopic : bootstrapperTopics) {
            storePartitions.add(new TopicPartition(bootstrapperTopic, 0));
        }

        restoreConsumer.assign(storePartitions);

        // calculate the end offset of the partition
        // TODO: this is a bit hacky to first seek then position to get the end offset
        restoreConsumer.seekToEnd(storePartitions);

        Map<TopicPartition, Long> endOffsets = new HashMap<>();

        for (TopicPartition storePartition : storePartitions) {
            Long endOffset = restoreConsumer.position(storePartition);
            endOffsets.put(storePartition, endOffset);
        }

        // restore the state from the beginning of the change log otherwise
        restoreConsumer.seekToBeginning(storePartitions);

        for (Map.Entry<TopicPartition, Long> entry : endOffsets.entrySet()) {
            long offset = 0L;

            while (offset < entry.getValue()) {
                for (ConsumerRecord<String, String> record : restoreConsumer.poll(100).records(entry.getKey())) {
                    if (record.key() != null) {
                        log.info("Find stream configuration with record key id [{}] inside topic [{}]", record.key(),
                                record.topic());
                        update(new SourceSystem("kafka", entry.getKey().topic(), record.key()), record.value());
                    }

                }
                offset = restoreConsumer.position(entry.getKey());
                log.info("Recover from kafka offset[{}], endOffset[{}]", offset, entry.getValue());
            }
        }
    }

    @Override
    public void run() {
        currentThread().setName("KafkaNoFilterBootstrapper");

        while (!closed.get()) {
            try {
                for (TopicPartition storePartition : storePartitions) {
                    for (ConsumerRecord<String, String> record : restoreConsumer.poll(5000).records(storePartition.topic())) {
                        if (record.key() != null) {
                            log.info("Find stream configuration with record key id [{}] inside topic [{}]", record.key(),
                                    record.topic());
                            update(new SourceSystem("kafka", storePartition.topic(), record.key()), record.value());
                        }
                    }
                }
            } catch (WakeupException e) {
                if (!closed.get()) throw e;
                log.info("Closing restore consumer ...");
                restoreConsumer.close();
            }
        }
    }

    @Override
    public void close() {
        closed.set(true);
        restoreConsumer.wakeup();
        log.info("Stop KafkaBootstrapper service");
    }
}
