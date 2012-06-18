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

import java.util.Date;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/9/12
 */
public class SessionDataForDatePeriodAsyncProvider extends AsyncDataProvider<SessionDataDto> {
    private Date from;
    private Date to;

    public SessionDataForDatePeriodAsyncProvider(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public SessionDataForDatePeriodAsyncProvider(ProvidesKey<SessionDataDto> keyProvider, Date from, Date to) {
        super(keyProvider);
        this.from = from;
        this.to = to;
    }

    @Override
    protected void onRangeChanged(HasData<SessionDataDto> display) {
        Range range = display.getVisibleRange();
        final int start = range.getStart();

        SessionDataService.Async.getInstance().getByDatePeriod(start, range.getLength(), from, to, new AsyncCallback<PagedSessionDataDto>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error is occurred during server request processing (Session data fetching)" + caught.getMessage());
            }

            @Override
            public void onSuccess(PagedSessionDataDto result) {
                updateRowData(start, result.getSessionDataDtoList());
                updateRowCount(result.getTotalSize(), true);
            }
        });
    }
}

