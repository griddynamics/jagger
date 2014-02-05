package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.NodeInfoEntity;
import com.griddynamics.jagger.webclient.client.NodeInfoService;
import com.griddynamics.jagger.webclient.client.dto.NodeInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class NodeInfoServiceImpl implements NodeInfoService {

    private static final Logger log = LoggerFactory.getLogger(NodeInfoServiceImpl.class);
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<NodeInfoDto> getNodeInfo(String sessionId) {

        Long time = System.currentTimeMillis();
        List<NodeInfoDto> nodeInfoDtoList = new ArrayList<NodeInfoDto>();

        try {
            List<NodeInfoEntity> nodeInfoEntityList = (List<NodeInfoEntity>)
                    entityManager.createQuery("select nie from NodeInfoEntity as nie where nie.sessionId = (:sessionId)").
                            setParameter("sessionId", sessionId).
                            getResultList();

            for (NodeInfoEntity nodeInfoEntity : nodeInfoEntityList) {
                NodeInfoDto nodeInfoDto = new NodeInfoDto();
                nodeInfoDto.setId(nodeInfoEntity.getId());
                nodeInfoDto.setSessionId(nodeInfoEntity.getSessionId());
                nodeInfoDto.setNodeId(nodeInfoEntity.getNodeId());
                nodeInfoDto.setSystemTime(nodeInfoEntity.getSystemTime());
                nodeInfoDto.setOsName(nodeInfoEntity.getOsName());
                nodeInfoDto.setOsVersion(nodeInfoEntity.getOsVersion());
                nodeInfoDto.setJaggerJavaVersion(nodeInfoEntity.getJaggerJavaVersion());
                nodeInfoDto.setCpuModel(nodeInfoEntity.getCpuModel());
                nodeInfoDto.setCpuMHz(nodeInfoEntity.getCpuMHz());
                nodeInfoDto.setCpuTotalCores(nodeInfoEntity.getCpuTotalCores());
                nodeInfoDto.setCpuTotalSockets(nodeInfoEntity.getCpuTotalSockets());
                nodeInfoDto.setSystemRAM(nodeInfoEntity.getSystemRAM());

                nodeInfoDtoList.add(nodeInfoDto);
            }
            log.info("For session id " + sessionId + " was found " + nodeInfoDtoList.size() + " node info values in " + (System.currentTimeMillis() - time) + " ms");
        }
        catch (NoResultException ex) {
            log.info("No node info data was found for session ID=" + sessionId, ex);
            return null;
        }
        catch (Exception ex) {
            log.error("Error occurred during loading node info data for session id=" + sessionId, ex);
            throw new RuntimeException(ex);
        }

        return nodeInfoDtoList;
    }

}