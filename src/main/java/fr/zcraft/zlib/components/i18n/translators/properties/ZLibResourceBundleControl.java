/*
 * Copyright or © or Copr. ZLib contributors (2015 - 2016)
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package fr.zcraft.zlib.components.i18n.translators.properties;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


public class ZLibResourceBundleControl extends ResourceBundle.Control
{
    private final File bundleFile;

    public ZLibResourceBundleControl(File bundleFile)
    {
        this.bundleFile = bundleFile;
    }


    /**
     * <p>Uses the file name as the bundle name.</p>
     *
     * <hr />
     *
     * <p> {@inheritDoc}
     */
    @Override
    public String toBundleName(String baseName, Locale locale)
    {
        final String[] nameParts = bundleFile.getName().split("\\.");
        if (nameParts.length >= 2)
        {
            nameParts[nameParts.length - 1] = "";
        }

        return baseName + "." + StringUtils.join(nameParts, ".");
    }

    /**
     * <p>Loads the bundles from the file system instead of the JAR file, to allow modifications by
     * the end user.</p>
     *
     * <hr />
     *
     * <p> {@inheritDoc}
     */
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException
    {
        switch (format)
        {
            case "java.properties":
                ResourceBundle bundle = null;
                InputStream stream = null;

                try
                {
                    stream = new FileInputStream(bundleFile);
                    bundle = new PropertyResourceBundle(stream);
                }
                finally
                {
                    if (stream != null)
                        stream.close();
                }

                return bundle;

            default:
                return super.newBundle(baseName, locale, format, loader, reload);
        }
    }

    /**
     * <p>The bundles are only loaded on startup, one time, so the cache is not needed.<br />
     * Plus, the cache may cause problems if one reloads the plugin to update the translation.</p>
     *
     * <hr />
     *
     * <p> {@inheritDoc}
     */
    @Override
    public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime)
    {
        return true;
    }
}
