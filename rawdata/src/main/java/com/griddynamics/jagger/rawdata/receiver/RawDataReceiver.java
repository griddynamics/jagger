package com.griddynamics.jagger.rawdata.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import com.griddynamics.jagger.rawdata.protobuf.RawDataPackageProtos;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.rawdata.protobuf.RawDataPackageProtos.RawDataPackage.parseFrom;
import static com.griddynamics.jagger.rawdata.sender.RawDataSender.KAFKA_KEY_SEPARATOR;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.split;

@SuppressWarnings("unused")
public class RawDataReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RawDataReceiver.class);
    private final String groupId;
    private final String kafkaTopic;
    private final Properties kafkaProperties = new Properties();
    private final Consumer<String, byte[]> consumer;

    /**
     * @param kafkaTopic   The name of Kafka topic from which data must be received.
     * @param kafkaServers A list of host/port pairs to use for establishing the initial connection to the Kafka cluster.
     *                     This list should be in the form "host1:port1,host2:port2,...".
     * @param groupId      A string that uniquely identifies the group of consumer processes to which this consumer belongs.
     *                     By setting the same group id multiple processes indicate that they are all part of the same consumer group.
     */
    public RawDataReceiver(String kafkaTopic, String kafkaServers, String groupId) {
        this.kafkaTopic = kafkaTopic;
        this.groupId = groupId;

        kafkaProperties.put("bootstrap.servers", kafkaServers);
        kafkaProperties.put("group.id", groupId);
        kafkaProperties.put("enable.auto.commit", "true");
        kafkaProperties.put("auto.commit.interval.ms", "1000");
        kafkaProperties.put("session.timeout.ms", "30000");
        kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        this.consumer = new KafkaConsumer<>(kafkaProperties);

        consumer.subscribe(singletonList(kafkaTopic));
    }

    /**
     * Fetches data from the topic {@link #kafkaTopic}.
     *
     * @param timeout The time, in milliseconds, spent waiting in poll if data is not available. If 0, returns
     *                immediately with any records that are available now. Must not be negative.
     * @return List of raw data packages mapped by metricId.
     * @throws InvalidProtocolBufferException if parsing of protobuf data failed.
     * @see KafkaConsumer#poll(long)
     */
    public Map<String, List<RawDataPackageProtos.RawDataPackage>> receiveData(long timeout) throws InvalidProtocolBufferException {
        Map<String, List<RawDataPackageProtos.RawDataPackage>> rawDataByMetricId = new HashMap<>();

        ConsumerRecords<String, byte[]> records = consumer.poll(timeout);
        for (ConsumerRecord<String, byte[]> record : records) {
            String nodeName = split(record.key(), KAFKA_KEY_SEPARATOR)[0];
            String metricId = split(record.key(), KAFKA_KEY_SEPARATOR)[1];
            RawDataPackageProtos.RawDataPackage rawDataPackage = parseFrom(record.value());

            LOGGER.debug("Received: nodeName = {}, metricId = {}, offset = {}", nodeName, record.key(), record.offset());

            if (rawDataByMetricId.get(metricId) == null)
                rawDataByMetricId.put(metricId, newArrayList(rawDataPackage));
            else
                rawDataByMetricId.get(metricId).add(rawDataPackage);
        }
        return rawDataByMetricId;
    }

    /**
     * @return group id of {@link #consumer}.
     * @see #RawDataReceiver(String, String, String)
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @return name of Kafka topic.
     */
    public String getKafkaTopic() {
        return kafkaTopic;
    }

    /**
     * @return copy of {@link #kafkaProperties}.
     */
    public Properties getKafkaProperties() {
        return new Properties(kafkaProperties);
    }
}
