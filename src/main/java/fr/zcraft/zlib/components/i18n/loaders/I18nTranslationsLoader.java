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
package fr.zcraft.zlib.components.i18n.loaders;

import fr.zcraft.zlib.components.i18n.loaders.gettext.GettextPOLoader;

import java.io.File;
import java.util.Locale;
import java.util.Map;


/**
 * Classes used to load & store the translations extends this class.
 */
public abstract class I18nTranslationsLoader
{
    protected final Locale locale;
    protected final File file;

    public I18nTranslationsLoader(Locale locale, File file)
    {
        this.locale = locale;
        this.file = file;
    }

    /**
     * Translates a string into the given locale.
     *
     * @param toTranslate The sentence to translate.
     *
     * @return The translated sentence. {@code null} if the sentence is not translated into this
     * locale.
     */
    public abstract String translate(String toTranslate);

    /**
     * Returns all loaded translations for a given locale.
     *
     * @return The translations loaded for this locale, in a map: {@code original → translated}.
     */
    public abstract Map<String, String> getTranslations();


    /**
     * @return The Locale loaded by this loader.
     */
    public Locale getLocale()
    {
        return locale;
    }


    /**
     * Returns a new translations loader for this locale and this file.
     *
     * @param locale The locale
     * @param file   The file
     *
     * @return A translations loader for this file.
     */
    public static I18nTranslationsLoader getInstance(Locale locale, File file)
    {
        String[] fileNameParts = file.getName().split("\\.");
        String fileExtension = fileNameParts[fileNameParts.length - 1].toLowerCase();

        switch (fileExtension.toLowerCase())
        {
            case "po":
                return new GettextPOLoader(locale, file);

            default:
                return null;
        }
    }
}
