package com.griddynamics.jagger.webclient.client.data;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;

import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class SessionDataForSessionIdsAsyncProvider extends AsyncDataProvider<SessionDataDto> {

    private Set<String> sessionIds;

    public SessionDataForSessionIdsAsyncProvider() {
    }

    public SessionDataForSessionIdsAsyncProvider(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public SessionDataForSessionIdsAsyncProvider(ProvidesKey<SessionDataDto> keyProvider, Set<String> sessionIds) {
        super(keyProvider);
        this.sessionIds = sessionIds;
    }

    public void setSessionIds(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    @Override
    protected void onRangeChanged(HasData<SessionDataDto> display) {
        Range range = display.getVisibleRange();
        final int start = range.getStart();

        SessionDataService.Async.getInstance().getBySessionIds(start, range.getLength(), sessionIds, new AsyncCallback<PagedSessionDataDto>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error is occurred during server request processing (Session data fetching) for session IDs " + sessionIds);
            }

            @Override
            public void onSuccess(PagedSessionDataDto result) {
                updateRowData(start, result.getSessionDataDtoList());
                updateRowCount(result.getTotalSize(), true);
            }
        });
    }
}
