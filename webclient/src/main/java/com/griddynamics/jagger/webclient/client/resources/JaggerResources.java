package com.griddynamics.jagger.webclient.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/1/12
 */
public interface JaggerResources extends ClientBundle {
    public static final JaggerResources INSTANCE = GWT.create(JaggerResources.class);

    @Source("ajax-loader.gif")
    ImageResource getLoadIndicator();

    @Source("arrow-left.gif")
    ImageResource getArrowLeft();

    @Source("arrow-right.gif")
    ImageResource getArrowRight();

    @Source("show-checked.png")
    ImageResource getShowChecked();

    @Source("uncheck-all.png")
    ImageResource getUncheckAll();

    @Source("render-plots.png")
    ImageResource getRenderPlots();

    @Source("clear.png")
    ImageResource getClearImage();

    @Source("task.png")
    ImageResource getTaskImage();

    @Source("plot.png")
    ImageResource getPlotImage();

    @Source("hyperlink.png")
    ImageResource getHyperlinkImage();

    @Source("pencil.png")
    ImageResource getPencilImage();

    // icons downloaded from http://www.iconsdb.com/gray-icons
    @Source("icons/cross-2-24.png")
    ImageResource getCrossImage();

    @Source("icons/cross-blue-2-24.png")
    ImageResource getCrossBlueImage();

    @Source("icons/download-24.png")
    @ImageResource.ImageOptions(height = 16, width = 16)
    ImageResource getDownloadImage();

    @Source("icons/settings-with-triangle-4-24.png")
    ImageResource getGearImage();

    @Source("icons/settings-with-triangle-blue-4-24.png")
    ImageResource getGearBlueImage();

    @Source("JaggerWebClient.css")
    @CssResource.NotStrict
    JaggerStyle css();

    public interface JaggerStyle extends CssResource {
        String currentItem();

        String infoPanel();

        String centered();

        String taskDetailsTree();

        String xAxisLabel();

        String plotHeader();

        String plotLegend();

        String summaryPanel();

        String nodesPanel();

        String zoomLabel();

        String zoomPanel();

        String mainPanel();

        String navPanel();

        String contentPanel();

        String panLabel();

        String toolBar();

        String searchPanel();

        String tagButton();

        String searchTabPanel();

        String sessionNameHeader();

        String testNameHeader();

        String exceptionPanel();

        String controlFont();

        String userCommentBox();

        String descriptionPanel();

        String abstractWindow();

        String textAreaPanel();

        String dragLabel();

        String mainTabPanel();

        String pointer();

        String plotSettingsMenu();

        String draggable();

        String legendPanel();

        String padding2px();

        String overflowYScroll();

        String plotButtonsPanel();

    }
}
