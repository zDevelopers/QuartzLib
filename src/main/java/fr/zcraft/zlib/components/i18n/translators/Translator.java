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
package fr.zcraft.zlib.components.i18n.translators;

import fr.zcraft.zlib.components.i18n.translators.gettext.GettextPOTranslator;
import fr.zcraft.zlib.components.i18n.translators.properties.PropertiesTranslator;
import fr.zcraft.zlib.components.i18n.translators.yaml.YAMLTranslator;
import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.reflection.Reflection;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Classes used to load & store the translations extends this class.
 *
 * Translators are lazy: strings are only loaded on first use.
 */
public abstract class Translator
{
    /**
     * We need a string without meaning to use as the no-context key below.
     * A {@code null} value cannot be used because {@link ConcurrentHashMap}s does not accept {@code null} keys.
     * An empty value cannot be used because an empty context is not the same as no context, and some applications
     * may use them.
     */
    protected final static String NO_CONTEXT_KEY = UUID.randomUUID().toString();

    protected final Locale locale;
    protected final File file;
    protected final String resourceReference;

    private boolean loaded = false;
    private int priority = 0;

    /**
     * Context → messageId → translation
     */
    protected final Map<String, Map<String, Translation>> translations = new ConcurrentHashMap<>();


    public Translator(Locale locale, File file)
    {
        this.locale = locale;
        this.file = file;
        this.resourceReference = null;
    }

    public Translator(final Locale locale, final String resourceReference)
    {
        this.locale = locale;
        this.resourceReference = resourceReference;
        this.file = null;
    }

    protected Reader getReader()
    {
        if (file != null)
        {
            try
            {
                return new FileReader(file);
            }
            catch (FileNotFoundException e)
            {
                PluginLogger.error("Unable to load file {0} in translator {1}", e, getFilePath(), getClass().getSimpleName());
                return null;
            }
        }

        else if (resourceReference != null)
        {
            final InputStream stream = ZLib.getPlugin().getResource(resourceReference);

            if (stream == null)
            {
                PluginLogger.error("Unable to load file {0} in translator {1}", getFilePath(), getClass().getSimpleName());
                return null;
            }

            return new InputStreamReader(stream);
        }

        else return null;
    }

    public String getFilePath()
    {
        if (file != null)
        {
            return file.getAbsolutePath();
        }

        else if (resourceReference != null)
        {
            return "jar:" + resourceReference;
        }

        else return "<unknown>";
    }

    /**
     * Loads the translations into the {@link #translations} map. Translators are
     * lazy: this method will automatically be called on first translation request.
     */
    protected abstract void load();

    /**
     * Loads the translations if not already loaded.
     */
    private void load0()
    {
        if (!loaded)
        {
            load();
            loaded = true;
        }
    }

    /**
     * Translates a string into the given locale.
     *
     * @param context         The translation context. {@code null} if no context defined.
     * @param messageId       The string to translate.
     * @param messageIdPlural The plural version of the string to translate. {@code null} if this
     *                        translation does not have a plural form.
     * @param count           The count of items to use to choose the singular or plural form.
     *                        {@code null} if this translation does not have a plural form.
     *
     * @return The translated sentence. {@code null} if the sentence is not translated into this
     * locale.
     */
    public String translate(String context, String messageId, String messageIdPlural, Integer count)
    {
        load0();

        final Map<String, Translation> contextMap = translations.get(getContextKey(context));
        if (contextMap == null)
            return null;

        Translation translation = contextMap.get(messageId);
        if (translation == null || translation.getTranslations().size() == 0)
            return null;

        if (count != null && translation.getTranslations().size() != 1)
        {
            Integer pluralIndex = getPluralIndex(count);

            // Ensures the translation is available
            if (translation.getTranslations().size() <= pluralIndex)
                return null;

            return translation.getTranslations().get(pluralIndex);
        }
        else
        {
            return translation.getTranslations().get(0);
        }
    }

    /**
     * Returns the plural index to use for the given integer.
     *
     * Translators should override this to use custom plural rules loaded from the files,
     * if available.<br /> The default implementation ignores the plural and always returns {@code
     * 0}.
     *
     * @param count The count.
     *
     * @return The translation index to use for this count.
     */
    public Integer getPluralIndex(Integer count)
    {
        return 0;
    }

    /**
     * @return The last translator.
     */
    public abstract String getLastTranslator();

    /**
     * @return The name of the translation team.
     */
    public abstract String getTranslationTeam();

    /**
     * @return The person to contact if there is an error in the translations.
     */
    public abstract String getReportErrorsTo();


    /**
     * @return The Locale loaded by this loader.
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * @return The priority of this translator: higher priority translators will
     * called first for a translation in the translators chain.
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * Sets the priority of this translator. Higher priority translators
     * will be called first for a translation in the translators chain.
     *
     * @param priority The priority. Default to 0 if unset.
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    /**
     * Registers a translation to be stored in the system.
     *
     * @param translation The translation to store.
     */
    protected void registerTranslation(Translation translation)
    {
        final String context = getContextKey(translation.getContext());
        Map<String, Translation> contextMap = translations.get(context);

        if (contextMap == null)
        {
            contextMap = new ConcurrentHashMap<>();
            translations.put(context, contextMap);
        }

        contextMap.put(translation.getOriginal(), translation);
    }

    /**
     * Returns the key to use in the translation map for the given context. Must be consistent:
     * multiple calls with the same context must return the same value.
     *
     * <p>The default implementation returns the context, or a randomly generated no-context key if
     * the context is null.</p>
     *
     * @param context The context. May be {@code null}.
     *
     * @return The key to use. Cannot be {@code null}.
     */
    protected String getContextKey(String context)
    {
        return context != null ? context : NO_CONTEXT_KEY;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Translator that = (Translator) o;

        return new EqualsBuilder()
                .append(priority, that.priority)
                .append(locale, that.locale)
                .append(getFilePath(), that.getFilePath())
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(locale)
                .append(getFilePath())
                .append(priority)
                .toHashCode();
    }

    /**
     * Returns a new translations loader for this locale and this file.
     *
     * @param locale The locale
     * @param file   The file
     *
     * @return A translations loader for this file.
     */
    public static Translator getInstance(Locale locale, File file)
    {
        return getInstance(getTranslatorClass(file.getName()), locale, file);
    }

    /**
     * Returns a new translations loader for this locale and a resource from the plugin's JAR.
     *
     * @param locale The locale
     * @param resourceReference The path to the resource.
     *
     * @return A translations loader for this file.
     */
    public static Translator getInstance(Locale locale, String resourceReference)
    {
        return getInstance(getTranslatorClass(resourceReference), locale, resourceReference);
    }

    /**
     * Extracts from the file name and returns the translator type to use.
     *
     * @param fileName The file name.
     * @return The translator type to be used.
     */
    private static Class<? extends Translator> getTranslatorClass(final String fileName)
    {
        final String[] fileNameParts = fileName.split("\\.");

        if (fileNameParts.length < 2) return null;

        switch (fileNameParts[fileNameParts.length - 1].toLowerCase())
        {
            case "po":
                return GettextPOTranslator.class;

            case "yml":
            case "yaml":
                return YAMLTranslator.class;

            case "properties":
            case "class":
                return PropertiesTranslator.class;

            default:
                return null;
        }
    }

    /**
     * Instanciate a Translator instance from the given class.
     *
     * @param clazz The class.
     * @param locale The locale for this translator.
     * @param resourcePointer A pointer to the resource: either a {@link File} or a {@link String} to reference a
     *                        bundled resource.
     *
     * @return The new instance, or {@code null} if none could be made ({@code null} class or invalid constructor
     * or exception).
     */
    private static Translator getInstance(final Class<? extends Translator> clazz, final Locale locale, final Object resourcePointer)
    {
        if (clazz == null) return null;

        try
        {
            return Reflection.instantiate(clazz, locale, resourcePointer);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            return null;
        }
    }
}
