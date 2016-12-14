package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.TagDto;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class SessionDataServiceImpl /*extends RemoteServiceServlet*/ implements SessionDataService {

    private DatabaseService databaseService;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Long getStartPosition(Set<String> selectedIds) throws RuntimeException {
        return databaseService.getSessionInfoService().getFirstPosition(selectedIds);
    }

    @Override
    public PagedSessionDataDto getAll(int start, int length) throws RuntimeException {
        List<SessionDataDto> result = databaseService.getSessionInfoService().getAll(start, length);
        Integer size = databaseService.getSessionInfoService().getTotalSize().intValue();
        return new PagedSessionDataDto(result, size);
    }

    @Override
    public PagedSessionDataDto getByDatePeriod(int start, int length, Date from, Date to) throws RuntimeException {
        List<SessionDataDto> result = databaseService.getSessionInfoService().getByDatePeriod(start, length, from, to);
        Integer size = databaseService.getSessionInfoService().getTotalSizeByDate(from, to).intValue();
        return new PagedSessionDataDto(result, size);
    }

    @Override
    public PagedSessionDataDto getBySessionIds(int start, int length, Set<String> sessionIds) throws RuntimeException {
        List<SessionDataDto> result = databaseService.getSessionInfoService().getBySessionIds(start, length, sessionIds);
        Integer size = databaseService.getSessionInfoService().getTotalSizeByIds(sessionIds).intValue();
        return new PagedSessionDataDto(result, size);
    }

    @Override
    public PagedSessionDataDto getBySessionTagsName(int start, int length, Set<String> sessionTagNames) throws RuntimeException {
        List<SessionDataDto> result = databaseService.getSessionInfoService().getBySessionTagsName(start, length, sessionTagNames);
        Integer size = databaseService.getSessionInfoService().getTotalSizeByTags(sessionTagNames).intValue();
        return new PagedSessionDataDto(result, size);
    }

    @Override
    public SessionDataDto getBySessionId(String sessionId) throws RuntimeException {
        Set<String> sessionIds = new HashSet<String>(Arrays.asList(sessionId));
        List<SessionDataDto> result = databaseService.getSessionInfoService().getBySessionIds(0, 1, sessionIds);
        if (result.isEmpty()){
            throw new RuntimeException("Unable to find session with id="+sessionId);
        }
        return result.iterator().next();
    }

    @Override
    public List<TagDto> getAllTags() {
        return databaseService.getSessionInfoService().getAllTags();
    }

    @Override
    public void saveUserComment(Long sessionData_id, String userComment) {
        databaseService.getSessionInfoService().saveUserComment(sessionData_id, userComment);
    }

    @Override
    public void saveTags(Long sessionData_id, List<TagDto> tags) {
        databaseService.getSessionInfoService().saveTags(sessionData_id, tags);
    }
}
