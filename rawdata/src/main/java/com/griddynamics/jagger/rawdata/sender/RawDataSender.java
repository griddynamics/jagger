package com.griddynamics.jagger.rawdata.sender;

import com.griddynamics.jagger.rawdata.protobuf.RawDataPackageProtos;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unused")
public class RawDataSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(RawDataSender.class);
    public static final String KAFKA_KEY_SEPARATOR = ".";
    private final AtomicLong sentMetricsCounter = new AtomicLong(0);
    private String kafkaTopic;
    private Properties kafkaProperties = new Properties();
    private Producer<String, byte[]> kafkaProducer;
    private String nodeName;

    /**
     * @param kafkaTopic   The name of Kafka topic to which data must be sent.
     * @param kafkaServers A list of host/port pairs to use for establishing the initial connection to the Kafka cluster.
     *                     This list should be in the form "host1:port1,host2:port2,...".
     * @param nodeName     The name of current node, which will be used in message key.
     */
    public RawDataSender(String kafkaTopic, String kafkaServers, String nodeName) {
        this.kafkaTopic = kafkaTopic;
        this.nodeName = nodeName;

        kafkaProperties.put("bootstrap.servers", kafkaServers);
        kafkaProperties.put("acks", "all");
        kafkaProperties.put("retries", 0);
        kafkaProperties.put("batch.size", 16_384);
        kafkaProperties.put("linger.ms", 1);
        kafkaProperties.put("buffer.memory", 33_554_432);
        kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        this.kafkaProducer = new KafkaProducer<>(kafkaProperties);
    }

    /**
     * @param metricId   The ID of sent metric. It's used on the Consumer side for sorting metrics of different types.
     * @param metricData Metric data.
     */
    public void sendData(String metricId, RawDataPackageProtos.RawDataPackage metricData) {
        String messageKey = nodeName + KAFKA_KEY_SEPARATOR + metricId + KAFKA_KEY_SEPARATOR + sentMetricsCounter.getAndIncrement();
        ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(kafkaTopic, messageKey, metricData.toByteArray());
        kafkaProducer.send(producerRecord, (metadata, exception) -> {
            LOGGER.debug("Sent: nodeName = {}, metricId = {}, offset = {}", nodeName, metricId, metadata.offset());
            if (exception != null)
                LOGGER.error("Exception on sending message.", exception);
        });
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

    /**
     * @return number of sent messages.
     */
    public long getMessagesSent() {
        return sentMetricsCounter.get();
    }
}
