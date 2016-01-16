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
package fr.zcraft.zlib.components.i18n.loaders.gettext;

import fr.zcraft.zlib.components.i18n.loaders.I18nTranslationsLoader;
import fr.zcraft.zlib.components.i18n.loaders.Translation;
import fr.zcraft.zlib.tools.PluginLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Loads Gettext .po files (uncompiled).
 */
public class GettextPOLoader extends I18nTranslationsLoader
{
    private POFile source = null;

    /**
     * Extracted source to translation map, for performances purposes.
     */
    private final Map<String, Translation> translations = new HashMap<>();


    public GettextPOLoader(Locale locale, File file)
    {
        super(locale, file);

        load();
    }

    private void load()
    {
        try
        {
            source = new POFile(file);
            source.parse();

            for (Translation translation : source.getTranslations())
            {
                translations.put(translation.getOriginal(), translation);
            }
        }
        catch (FileNotFoundException e)
        {
            PluginLogger.error("Cannot load the {0} translations file.", e, file.getAbsolutePath());
            source = null;
        }
        catch (POFile.CannotParsePOException e)
        {
            PluginLogger.error("Cannot parse the {0} translations file.", e, file.getAbsolutePath());
            source = null;
        }
    }

    @Override
    public Translation translate(String messageId)
    {
        if (source == null)
            return null;

        return translations.get(messageId);
    }

    @Override
    public String getLastTranslator()
    {
        return source != null ? source.getLastTranslator() : null;
    }

    @Override
    public String getTranslationTeam()
    {
        return source != null ? source.getTranslationTeam() : null;
    }

    @Override
    public String getReportErrorsTo()
    {
        return source != null ? source.getReportErrorsTo() : null;
    }
}
