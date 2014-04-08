package com.griddynamics.jagger.dbapi.provider;

import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.TagDto;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by kgribov on 4/7/14.
 */
public interface SessionInfoProvider {
    Long getFirstPosition(Set<String> selectedIds) throws RuntimeException;
    Integer getTotalSize() throws RuntimeException;
    Integer getTotalSizeByDate(Date from, Date to);
    Integer getTotalSizeByIds(Set<String> sessionIds);
    Integer getTotalSizeByTags(Set<String> sessionTagNames);

    List<SessionDataDto> getAll(int offset, int maxResult) throws RuntimeException;
    List<SessionDataDto> getByDatePeriod(int offset, int maxResult, Date from, Date to) throws RuntimeException;
    List<SessionDataDto> getBySessionIds(int offset, int maxResult, Set<String> sessionIds) throws RuntimeException;
    List<SessionDataDto> getBySessionTagsName (int offset, int maxResult, Set<String> sessionTagNames) throws RuntimeException;
    List<TagDto> getAllTags();

    void saveUserComment(Long sessionData_id, String userComment);
    void saveTags(Long sessionData_id, List<TagDto> tags);
}
