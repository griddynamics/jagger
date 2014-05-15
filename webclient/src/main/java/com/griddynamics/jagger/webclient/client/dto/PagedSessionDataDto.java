package com.griddynamics.jagger.webclient.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class PagedSessionDataDto implements IsSerializable {
    private List<SessionDataDto> sessionDataDtoList;
    private int totalSize;

    public PagedSessionDataDto() {
    }

    public PagedSessionDataDto(List<SessionDataDto> sessionDataDtoList, int totalSize) {
        this.sessionDataDtoList = sessionDataDtoList;
        this.totalSize = totalSize;
    }

    public List<SessionDataDto> getSessionDataDtoList() {
        return sessionDataDtoList;
    }

    public int getTotalSize() {
        return totalSize;
    }
}
