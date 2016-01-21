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
package fr.zcraft.zlib.components.i18n.translators.gettext;

import fr.zcraft.zlib.components.i18n.translators.Translation;
import fr.zcraft.zlib.components.i18n.translators.Translator;
import fr.zcraft.zlib.tools.PluginLogger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;


/**
 * Loads Gettext .po files (uncompiled).
 */
public class GettextPOTranslator extends Translator
{
    private POFile source = null;

    /**
     * A script engine used to compute plural rules.
     *
     * The official documentation mentions that the plural determination script is in C format, but
     * the JavaScript format is the same for these scripts (containing only basic mathematics and
     * ternary operators), excepted for the booleans, but this case is handled manually.
     *
     * @see #getPluralIndex(Integer)
     */
    private ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");


    public GettextPOTranslator(Locale locale, File file)
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
                registerTranslation(translation);
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
    public Integer getPluralIndex(Integer count)
    {
        try
        {
            scriptEngine.put("n", count);
            Object rawPluralIndex = scriptEngine.eval(source.getPluralFormScript());

            // If the index is a boolean, as some po files use the C handling of booleans, we convert them
            // into the appropriate numbers.
            // Else, we try to convert the output to an integer.
            Integer pluralIndex = rawPluralIndex instanceof Boolean ? (((Boolean) rawPluralIndex) ? 1 : 0) : (rawPluralIndex instanceof Number ? ((Number) rawPluralIndex).intValue() : Integer.valueOf(rawPluralIndex.toString()));
            if (pluralIndex <= source.getPluralCount())
                return pluralIndex;
            else
                return 0;
        }
        catch (ScriptException | NumberFormatException e)
        {
            PluginLogger.error("Invalid plural script for language {0}: “{1}”", e, getLocale(), source.getPluralFormScript());
            return 0;
        }
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
