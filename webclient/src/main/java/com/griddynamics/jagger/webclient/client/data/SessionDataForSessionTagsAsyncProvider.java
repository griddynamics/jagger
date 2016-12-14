package com.griddynamics.jagger.webclient.client.data;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.webclient.client.components.ExceptionPanel;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 3/7/14
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionDataForSessionTagsAsyncProvider extends ExtendedAsyncDataProvider<SessionDataDto> {
    private Set<String> tagNames;
    public SessionDataForSessionTagsAsyncProvider() {
    }

    public SessionDataForSessionTagsAsyncProvider(Set<String> tagNames) {
        this.tagNames = tagNames;
    }

    public SessionDataForSessionTagsAsyncProvider(ProvidesKey<SessionDataDto> keyProvider, Set<String> tagNames) {
        super(keyProvider);
        this.tagNames = tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
        update();
    }
    @Override
    protected void onRangeChanged(HasData<SessionDataDto> display) {
        Range range = display.getVisibleRange();
        final int start = range.getStart();

        SessionDataService.Async.getInstance().getBySessionTagsName(start, range.getLength(), tagNames, new AsyncCallback<PagedSessionDataDto>() {
            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel("Error is occurred during server request processing (Session data fetching) for tags " + tagNames);
            }

            @Override
            public void onSuccess(PagedSessionDataDto result) {
                updateRowData(start, result.getSessionDataDtoList());
                updateRowCount(result.getTotalSize(), true);
            }
        });
    }
}
