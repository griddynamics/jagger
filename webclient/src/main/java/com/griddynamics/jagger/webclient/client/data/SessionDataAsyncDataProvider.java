package com.griddynamics.jagger.webclient.client.data;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.webclient.client.SessionDataServiceAsync;
import com.griddynamics.jagger.webclient.client.components.ExceptionPanel;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;

/**
 * Fetches all sessions from server
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/9/12
 */
public class SessionDataAsyncDataProvider extends ExtendedAsyncDataProvider<SessionDataDto> {

    public SessionDataAsyncDataProvider() {
    }

    public SessionDataAsyncDataProvider(ProvidesKey<SessionDataDto> keyProvider) {
        super(keyProvider);
    }

    @Override
    protected void onRangeChanged(HasData<SessionDataDto> display) {
        Range range = display.getVisibleRange();
        final int start = range.getStart();
        int end = start + range.getLength();

        SessionDataServiceAsync sessionDataService = SessionDataService.Async.getInstance();
        AsyncCallback<PagedSessionDataDto> callback = new AsyncCallback<PagedSessionDataDto>() {
            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel("Error is occurred during server request processing (Session data fetching): \n" + caught.getMessage());
            }

            @Override
            public void onSuccess(PagedSessionDataDto result) {
                updateRowData(start, result.getSessionDataDtoList());
                updateRowCount(result.getTotalSize(), true);
            }
        };
        sessionDataService.getAll(start, range.getLength(), callback);
    }
}
