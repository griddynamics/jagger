package com.griddynamics.jagger.dbapi;

import com.griddynamics.jagger.dbapi.dto.TagDto;

import java.util.List;

/**
 * User: mnovozhilov
 * Date: 3/14/14
 * Time: 5:59 PM
 */
public interface DataSaverService {

    void saveUserComment(Long sessionData_id, String userComment);

    void saveTags(Long sessionData_id, List<TagDto> tags);

}
