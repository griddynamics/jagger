package com.griddynamics.jagger.rawdata.util;

import com.griddynamics.jagger.rawdata.receiver.RawDataReceiver;
import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@SuppressWarnings("unused")
public class KafkaTopicCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RawDataReceiver.class);

    /**
     * @param topicName         The name of topic to be created.
     * @param zookeeperHosts    A list of host/port pairs to use for establishing the initial connection to the Zookeeper cluster.
     *                          This list should be in the form "host1:port1,host2:port2,...".
     * @param noOfPartitions    A number of partitions of new topic.
     * @param replicationFactor A number of replications for new topic.
     */
    public static void createTopicIfNotExists(String topicName, String zookeeperHosts, int noOfPartitions, int replicationFactor) {
        ZkClient zkClient = null;
        ZkUtils zkUtils;
        try {
            int sessionTimeOutInMs = 15 * 1000; // 15 secs
            int connectionTimeOutInMs = 10 * 1000; // 10 secs

            zkClient = new ZkClient(zookeeperHosts, sessionTimeOutInMs, connectionTimeOutInMs, ZKStringSerializer$.MODULE$);
            zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), false);

            if (!AdminUtils.topicExists(zkUtils, topicName)) {
                AdminUtils.createTopic(zkUtils, topicName, noOfPartitions, replicationFactor, new Properties());
                LOGGER.info("Topic {} created", topicName);
            } else {
                LOGGER.info("Topic {} already exists", topicName);
            }
        } catch (Exception ex) {
            LOGGER.error("Error on topic creation.", ex);
        } finally {
            if (zkClient != null) zkClient.close();
        }
    }
}
