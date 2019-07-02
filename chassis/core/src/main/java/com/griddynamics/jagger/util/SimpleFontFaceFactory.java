package com.griddynamics.jagger.util;

import net.sf.jasperreports.engine.fonts.SimpleFontFace;
import org.springframework.core.io.Resource;

import java.awt.*;
import java.io.IOException;

/**
 * Created by Andrey Badaev
 * Date: 11/01/17
 */
public class SimpleFontFaceFactory {
    
    public static SimpleFontFace newInstance(final Resource fontFile)
            throws IOException, FontFormatException {
        return new SimpleFontFace(Font.createFont(Font.TRUETYPE_FONT, fontFile.getFile()));
    }
}
