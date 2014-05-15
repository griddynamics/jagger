package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.webclient.client.ControlTreeCreatorService;
import com.griddynamics.jagger.dbapi.model.*;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class ControlTreeCreatorServiceImpl implements ControlTreeCreatorService {

    private DatabaseService databaseService;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public RootNode getControlTreeForSession(String sessionId) throws RuntimeException {
        return getControlTreeForSessions(new HashSet<String>(Arrays.asList(sessionId)));
    }

    @Override
    public RootNode getControlTreeForSessions(Set<String> sessionIds) throws RuntimeException {

        SessionMatchingSetup sessionMatchingSetup = new SessionMatchingSetup(
                databaseService.getWebClientProperties().isShowOnlyMatchedTests(),
                EnumSet.of(SessionMatchingSetup.MatchBy.ALL));

        return databaseService.getControlTreeForSessions(sessionIds,sessionMatchingSetup);
    }
}
