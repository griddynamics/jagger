package com.griddynamics.jagger.util;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.fonts.FontExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;

import java.util.Collections;

/**
 * Created by Andrey Badaev
 * Date: 11/01/17
 */
public class SimpleFontExtensionsRegistryFactory implements ExtensionsRegistryFactory {
    @Override
    public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) {
        FontExtensionsRegistry fontExtensionsRegistry = new FontExtensionsRegistry(Collections.emptyList());
        fontExtensionsRegistry.
        return null;
    }
}
