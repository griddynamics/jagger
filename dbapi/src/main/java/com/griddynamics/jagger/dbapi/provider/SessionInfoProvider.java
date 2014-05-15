package com.griddynamics.jagger.dbapi.provider;

import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.TagDto;

import java.util.Date;
import java.util.List;
import java.util.Set;

/** This class provide information about sessions in jagger db
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n */
public interface SessionInfoProvider {
    /** Returns position of session with smallest startTime in whole session list
     * @return position */
    Long getFirstPosition(Set<String> selectedIds) throws RuntimeException;

    /** Returns the number of sessions in jagger db
     * @return number of sessions */
    Long getTotalSize() throws RuntimeException;

    /** Returns the number of sessions in jagger db in specified date period
     * @return number of sessions */
    Long getTotalSizeByDate(Date from, Date to);

    /** Returns the number of sessions in jagger db with current ids
     * @param sessionIds - a set of sessions ids
     * @return number of sessions */
    Long getTotalSizeByIds(Set<String> sessionIds);

    /** Returns the number of sessions in jagger db with specified tags
     * @param sessionTagNames - a set of sessions tags
     * @return number of sessions */
    Long getTotalSizeByTags(Set<String> sessionTagNames);

    /** Returns a list of sessions
     * @param offset - select sessions from this position
     * @param maxResult - a number of max results
     * @return list of SessionDataDto */
    List<SessionDataDto> getAll(int offset, int maxResult) throws RuntimeException;

    /** Returns a list of sessions in specified date period
     * @param offset - select sessions from this position
     * @param maxResult - a number of max results
     * @param from - low date limit
     * @param to - high date limit
     * @return list of SessionDataDto */
    List<SessionDataDto> getByDatePeriod(int offset, int maxResult, Date from, Date to) throws RuntimeException;

    /** Returns a list of sessions with specified ids
     * @param offset - select sessions from this position
     * @param maxResult - a number of max results
     * @param sessionIds - - a set of sessions ids
     * @return list of SessionDataDto */
    List<SessionDataDto> getBySessionIds(int offset, int maxResult, Set<String> sessionIds) throws RuntimeException;

    /** Returns a list of sessions with specified tags
     * @param offset - select sessions from this position
     * @param maxResult - a number of max results
     * @param sessionTagNames - - a set of sessions tags
     * @return list of SessionDataDto */
    List<SessionDataDto> getBySessionTagsName (int offset, int maxResult, Set<String> sessionTagNames) throws RuntimeException;

    /** Returns a list all session tags in jagger database
     * @return list of tags */
    List<TagDto> getAllTags();

    /** Save comment for current session
     * @param sessionData_id - session id
     * @param userComment - a comment for this session */
    void saveUserComment(Long sessionData_id, String userComment);

    /** Save tags for current session
     * @param sessionData_id - session id
     * @param tags - a list of sessions tags*/
    void saveTags(Long sessionData_id, List<TagDto> tags);
}
