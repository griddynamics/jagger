package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
public interface SessionDataServiceAsync {
    void getAll(int start, int length, AsyncCallback<PagedSessionDataDto> async);

    void getBySessionId(String sessionId, AsyncCallback<SessionDataDto> async);

    void getByDatePeriod(int start, int length, Date from, Date to, AsyncCallback<PagedSessionDataDto> async);

    void getBySessionIds(int start, int length, Set<String> sessionIds, AsyncCallback<PagedSessionDataDto> async);

    void saveUserComment(Long sessionData_id, String userComment, AsyncCallback<Void> async);
}
