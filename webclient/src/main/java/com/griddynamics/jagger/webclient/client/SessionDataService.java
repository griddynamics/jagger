package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.dto.TestInfoDto;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
@RemoteServiceRelativePath("rpc/SessionDataService")
public interface SessionDataService extends RemoteService {

    PagedSessionDataDto getAll(int start, int length) throws RuntimeException;
    PagedSessionDataDto getByDatePeriod(int start, int length, Date from, Date to) throws RuntimeException;
    PagedSessionDataDto getBySessionIds(int start, int length, Set<String> sessionIds) throws RuntimeException;
    SessionDataDto getBySessionId(String sessionId) throws RuntimeException;
    void saveUserComment(Long sessionData_id, String userComment);

    public static class Async {
        private static final SessionDataServiceAsync ourInstance = (SessionDataServiceAsync) GWT.create(SessionDataService.class);

        public static SessionDataServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
