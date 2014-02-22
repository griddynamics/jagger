package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.NodeInfoEntity;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.NodePropertyEntity;
import com.griddynamics.jagger.webclient.client.NodeInfoService;
import com.griddynamics.jagger.webclient.client.dto.NodeInfoDto;
import com.griddynamics.jagger.webclient.client.dto.NodeInfoPerSessionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.*;

public class NodeInfoServiceImpl implements NodeInfoService {

    private static final Logger log = LoggerFactory.getLogger(NodeInfoServiceImpl.class);
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<NodeInfoPerSessionDto> getNodeInfo(Set<String> sessionIds) {

        Long time = System.currentTimeMillis();
        List<NodeInfoPerSessionDto> nodeInfoPerSessionDtoList = new ArrayList<NodeInfoPerSessionDto>();

        try {
            List<NodeInfoEntity> nodeInfoEntityList = (List<NodeInfoEntity>)
                    entityManager.createQuery("select nie from NodeInfoEntity as nie where nie.sessionId in (:sessionIds)").
                            setParameter("sessionIds", new ArrayList<String>(sessionIds)).
                            getResultList();

            Map<String,List<NodeInfoDto>> sessions = new HashMap<String, List<NodeInfoDto>>();

            for (NodeInfoEntity nodeInfoEntity : nodeInfoEntityList) {
                Map<String,String> parameters = new HashMap<String, String>();

                parameters.put("CPU model",nodeInfoEntity.getCpuModel());
                parameters.put("CPU frequency, MHz",String.valueOf(nodeInfoEntity.getCpuMHz()));
                parameters.put("CPU number of cores",String.valueOf(nodeInfoEntity.getCpuTotalCores()));
                parameters.put("CPU number of sockets",String.valueOf(nodeInfoEntity.getCpuTotalSockets()));
                parameters.put("Jagger Java version", nodeInfoEntity.getJaggerJavaVersion());
                parameters.put("OS name", nodeInfoEntity.getOsName());
                parameters.put("OS version", nodeInfoEntity.getOsVersion());
                parameters.put("System RAM, MB",String.valueOf(nodeInfoEntity.getSystemRAM()));

                List<NodePropertyEntity> nodePropertyEntityList = nodeInfoEntity.getProperties();
                for (NodePropertyEntity nodePropertyEntity : nodePropertyEntityList) {
                    parameters.put("Property '" + nodePropertyEntity.getName() + "'",nodePropertyEntity.getValue());
                }
                NodeInfoDto nodeInfoDto = new NodeInfoDto(nodeInfoEntity.getNodeId(),parameters);

                String sessionId = nodeInfoEntity.getSessionId();
                if (sessions.containsKey(sessionId)) {
                    sessions.get(sessionId).add(nodeInfoDto);
                }
                else {
                    List <NodeInfoDto> node = new ArrayList<NodeInfoDto>();
                    node.add(nodeInfoDto);
                    sessions.put(sessionId, node);
                }
            }

            for (Map.Entry<String,List<NodeInfoDto>> session : sessions.entrySet()) {
                nodeInfoPerSessionDtoList.add(new NodeInfoPerSessionDto(session.getKey(),session.getValue()));
            }

            log.info("For session ids " + sessionIds + " were found node info values in " + (System.currentTimeMillis() - time) + " ms");
        }
        catch (NoResultException ex) {
            log.info("No node info data was found for session id " + sessionIds, ex);
        }
        catch (PersistenceException ex) {
            log.info("No node info data was found for session id " + sessionIds, ex);
        }
        catch (Exception ex) {
            log.error("Error occurred during loading node info data for session ids " + sessionIds, ex);
            throw new RuntimeException("Error occurred during loading node info data for session ids " + sessionIds,ex);
        }

        return nodeInfoPerSessionDtoList;
    }

}