package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.NodeInfoPerSessionDto;
import com.griddynamics.jagger.webclient.client.NodeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class NodeInfoServiceImpl implements NodeInfoService {

    private static final Logger log = LoggerFactory.getLogger(NodeInfoServiceImpl.class);

    private DatabaseService databaseService;

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public List<NodeInfoPerSessionDto> getNodeInfo(Set<String> sessionIds) throws RuntimeException {
        return databaseService.getNodeInfo(sessionIds);
    }
}