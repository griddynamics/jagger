package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.TagDto;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public interface SessionDataServiceAsync {
    void getAll(int start, int length, AsyncCallback<PagedSessionDataDto> async);

    void getBySessionId(String sessionId, AsyncCallback<SessionDataDto> async);

    void getByDatePeriod(int start, int length, Date from, Date to, AsyncCallback<PagedSessionDataDto> async);

    void getBySessionIds(int start, int length, Set<String> sessionIds, AsyncCallback<PagedSessionDataDto> async);

    void getBySessionTagsName(int start, int length, Set<String> sessionTagNames, AsyncCallback<PagedSessionDataDto> async);

    void getAllTags(AsyncCallback<List<TagDto>> async);

    void saveUserComment(Long sessionData_id, String userComment, AsyncCallback<Void> async);

    void saveTags(Long sessionData_id, List<TagDto> tags, AsyncCallback<Void> async);

    void getStartPosition(Set<String> selectedIds, AsyncCallback<Long> async);
}
