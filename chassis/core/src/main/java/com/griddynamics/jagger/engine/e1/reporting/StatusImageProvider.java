/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.util.Decision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class StatusImageProvider {

    private static final Logger log = LoggerFactory.getLogger(StatusImageProvider.class);

    private Image statusImageOK;
    private Image statusImageWarning;
    private Image statusImageFatal;
    private Image statusImageError;

    public Image getImageByDecision(Decision decision) {
        switch (decision) {
            case OK: return statusImageOK;
            case WARNING: return statusImageWarning;
            case FATAL: return statusImageFatal;
            case ERROR: return statusImageError;
        }

        throw new IllegalStateException("Unknown decision : " + decision);
    }

    public void setStatusImageOKLocation(Resource statusImageOKLocation) {
        try {
            this.statusImageOK = ImageIO.read(statusImageOKLocation.getInputStream());
        } catch (IOException e) {
            log.error("Failed to resolve image [" + statusImageOKLocation + "]");
        }
    }

    public void setStatusImageWarningLocation(Resource statusImageWarningLocation) {
        try {
            this.statusImageWarning = ImageIO.read(statusImageWarningLocation.getInputStream());
        } catch (IOException e) {
            log.error("Failed to resolve image [" + statusImageWarningLocation + "]");
        }
    }

    public void setStatusImageFatalLocation(Resource statusImageFatalLocation) {
        try {
            this.statusImageFatal = ImageIO.read(statusImageFatalLocation.getInputStream());
        } catch (IOException e) {
            log.error("Failed to resolve image [" + statusImageFatalLocation + "]");
        }
    }

    public void setStatusImageErrorLocation(Resource statusImageErrorLocation) {
        try {
            this.statusImageError = ImageIO.read(statusImageErrorLocation.getInputStream());
        } catch (IOException e) {
            log.error("Failed to resolve image [" + statusImageErrorLocation + "]");
        }
    }
}
