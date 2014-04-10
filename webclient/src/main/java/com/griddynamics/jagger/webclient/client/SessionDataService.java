package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.TagDto;

import java.util.Date;
import java.util.Set;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
@RemoteServiceRelativePath("rpc/SessionDataService")
public interface SessionDataService extends RemoteService {

    Long getStartPosition(Set<String> selectedIds) throws RuntimeException;
    PagedSessionDataDto getAll(int start, int length) throws RuntimeException;
    PagedSessionDataDto getByDatePeriod(int start, int length, Date from, Date to) throws RuntimeException;
    PagedSessionDataDto getBySessionIds(int start, int length, Set<String> sessionIds) throws RuntimeException;
    PagedSessionDataDto getBySessionTagsName (int start, int length, Set<String> sessionTagNames) throws RuntimeException;
    SessionDataDto getBySessionId(String sessionId) throws RuntimeException;
    List<TagDto> getAllTags();
    void saveUserComment(Long sessionData_id, String userComment);
    void saveTags(Long sessionData_id, List<TagDto> tags);

    public static class Async {
        private static final SessionDataServiceAsync ourInstance = (SessionDataServiceAsync) GWT.create(SessionDataService.class);

        public static SessionDataServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
