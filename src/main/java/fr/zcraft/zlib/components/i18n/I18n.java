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
package fr.zcraft.zlib.components.i18n;

import com.google.common.base.Strings;
import fr.zcraft.zlib.components.i18n.translators.Translator;
import fr.zcraft.zlib.tools.FileUtils;
import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.core.ZLibComponent;
import fr.zcraft.zlib.core.ZPlugin;
import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.reflection.Reflection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class I18n extends ZLibComponent
{
    private final static String LAST_VERSION_UPDATE_CHECK_FILE = ".version";
    private final static String ALWAYS_OVERWRITE_FILE = ".overwrite";
    private final static String BACKUP_DIRECTORY = "backups";

    private final static Map<Locale, Translator> translators = new ConcurrentHashMap<>();

    private static Locale primaryLocale = null;
    private static Locale fallbackLocale = null;

    private static String i18nDirectory = "i18n";
    private static JarFile jarFile = null;

    private static boolean userFriendlyFormatting = true;
    private static String errorColor = ChatColor.RED.toString();
    private static String noticeColor = ChatColor.WHITE.toString();
    private static String successColor = ChatColor.GREEN.toString();
    private static String statusColor = ChatColor.GRAY.toString();
    private static String commandColor = ChatColor.GOLD.toString();

    private static boolean addCountToParameters = true;

    private static boolean filesWritten = false;


    @Override
    protected void onEnable()
    {
        if (ZLib.getPlugin() instanceof ZPlugin)
            jarFile = ((ZPlugin) ZLib.getPlugin()).getJarFile();
        
        setFallbackLocale(Locale.getDefault());
    }




    /* **  I18N OPTIONS  ** */


    /**
     * Call this to auto-detect the default system locale and to use it.
     */
    public static void useDefaultPrimaryLocale()
    {
        setPrimaryLocale(null);
    }

    /**
     * Sets the primary locale, the locale always used if available to translate the strings.
     *
     * @param locale The locale. If {@code null}, system locale used.
     */
    public static void setPrimaryLocale(Locale locale)
    {
        if (locale == null)
            locale = Locale.getDefault();

        try
        {
            loadLocale(locale);
            primaryLocale = locale;
        }
        catch (UnsupportedLocaleException e)
        {
            PluginLogger.warning("The primary locale {0} cannot be loaded.", locale);
        }
    }

    /**
     * Sets the fallback locale, used if a translation is not available in the primary locale.
     *
     * @param locale The locale.
     */
    public static void setFallbackLocale(Locale locale)
    {
        if (locale == null)
            return;

        try
        {
            loadLocale(locale);
            fallbackLocale = locale;
        }
        catch (UnsupportedLocaleException e)
        {
            PluginLogger.warning("The fallback locale {0} cannot be loaded.", locale);
        }
    }

    /**
     * @param i18nDirectory The name of the subdirectory where the translations are stored. Default:
     *                      "i18n".
     */
    public static void setI18nDirectory(String i18nDirectory)
    {
        I18n.i18nDirectory = i18nDirectory;
    }

    /**
     * @return The name of the subdirectory where the translations are stored. Default: "i18n".
     */
    public static String getI18nDirectory()
    {
        return i18nDirectory;
    }

    /**
     * Sets the plugin's JAR file. Required if you don't use {@link ZPlugin}.
     *
     * @param jarFile A reference to the plugin's JAR file.
     */
    public static void setJarFile(File jarFile)
    {
        try
        {
            I18n.jarFile = new JarFile(jarFile);
        }
        catch (IOException e)
        {
            PluginLogger.error("Unable to load JAR file {0}", e, jarFile.getAbsolutePath());
        }
    }

    /**
     * Enable the automatic replacement of user-friendly color codes in the strings.
     *
     * <p>If enabled, the following codes will be available.</p>
     *
     * <ul>
     *     <li>
     *         Every colors & formatting codes without underscores, written in english:
     *         <pre>
     *             {black}, {darkblue}, {darkgreen}, {darkaqua}, {darkred}, {darkpurple}, {gold}, {gray},
     *             {darkgray}, {blue}, {yellow}, {green}, {aqua}, {red}, {lightpurple}, {bold}, {strikethrough},
     *             {underline}, {italic}, {obfuscated}, {white}, {reset}
     *         </pre>
     *     </li>
     *     <li>
     *         Some codes for some special types of messages:
     *         <pre>{ce}, {ci}, {cs}, {cst}, {cc}</pre>
     *         (resp. error, information, success, status, command).
     *     </li>
     * </ul>
     *
     * @param userFriendlyFormatting {@code false} to disable. Enabled by default.
     *
     * @see #setErrorColor(String)
     * @see #setNoticeColor(String)
     * @see #setSuccessColor(String)
     * @see #setStatusColor(String)
     * @see #setCommandColor(String)
     */
    public static void setUserFriendlyFormatting(boolean userFriendlyFormatting)
    {
        I18n.userFriendlyFormatting = userFriendlyFormatting;
    }

    /**
     * @param errorColor The color (or formatting) used to replace {@code {ce}}. Default: red.
     *                   {@code null}: no formatting.
     */
    public static void setErrorColor(String errorColor)
    {
        I18n.errorColor = Strings.nullToEmpty(errorColor);
    }

    /**
     * @param noticeColor The color (or formatting) used to replace {@code {ci}}. Default: white.
     *                    {@code null}: no formatting.
     */
    public static void setNoticeColor(String noticeColor)
    {
        I18n.noticeColor = Strings.nullToEmpty(noticeColor);
    }

    /**
     * @param successColor The color (or formatting) used to replace {@code {cs}}. Default: green.
     *                     {@code null}: no formatting.
     */
    public static void setSuccessColor(String successColor)
    {
        I18n.successColor = Strings.nullToEmpty(successColor);
    }

    /**
     * @param statusColor The color (or formatting) used to replace {@code {cst}}. Default: gray.
     *                    {@code null}: no formatting.
     */
    public static void setStatusColor(String statusColor)
    {
        I18n.statusColor = Strings.nullToEmpty(statusColor);
    }

    /**
     * @param commandColor The color (or formatting) used to replace {@code {cc}}. Default: gold.
     *                     {@code null}: no formatting.
     */
    public static void setCommandColor(String commandColor)
    {
        I18n.commandColor = Strings.nullToEmpty(commandColor);
    }

    /**
     * @param addCountToParameters if {@code true}, when a translation method with plurals is called
     *                             without object parameters (the ones replacing {@code {0}}, {@code
     *                             {1}}...), the count is added as the first (and only) parameter, as
     *                             it will likely be used in the string and this avoid giving it twice.
     *                             Default: {@code true}.
     */
    public static void addCountToParameters(boolean addCountToParameters)
    {
        I18n.addCountToParameters = addCountToParameters;
    }
    
    static private boolean playerLocaleWarning = false;
    
    /**
     * Return the locale used by the player's client.
     * @param player The player
     * @return The player's client locale.
     */
    public static Locale getPlayerLocale(Player player)
    {
        if(player == null) {
            return null;
        }
        
        try
        {
            Object playerHandle = Reflection.call(player, "getHandle");
            String localeName = (String) Reflection.getFieldValue(playerHandle, "locale");
            String[] splitLocale = localeName.split("[_\\-]", 2);
            return new Locale(splitLocale[0], splitLocale[1]);
        }
        catch(Exception e)
        {
            if(!playerLocaleWarning)
            {
                PluginLogger.warning("Could not retrieve locale for player {0}", e, player.getName());
                playerLocaleWarning = true;
            }
            
            return null;
        }
    }

    /* **  TRANSLATIONS LOADING METHODS  ** */
    
    /**
     * Fetches the closest translator for the given local, and stores it in the
     * translator cache.
     * 
     * @param locale The locale
     * @return The closest translator that could be found, or {@code null} in some rare cases if no translator can be found at all.
     */
    private static Translator getClosestTranslator(Locale locale)
    {
        Translator translator = null;
        
        if(locale == null) return null;
        
        if(translators.containsKey(locale))
            return translators.get(locale);
        
        try
        {
            translator = loadLocale(locale);
        }
        catch (UnsupportedLocaleException ignored) {}
        
        if (translator == null)
        {
            for (Locale curLocale : translators.keySet())
            {
                if (curLocale.getLanguage().equals(locale.getLanguage()))
                    translator = translators.get(curLocale);

                if (curLocale.getCountry().equals(locale.getCountry()))
                    break;
            }
        }
        
        if(translator == null && I18n.primaryLocale != null)  translator = translators.get(I18n.primaryLocale);
        if(translator == null && I18n.fallbackLocale != null) translator = translators.get(I18n.fallbackLocale);
        
        if(translator != null) translators.put(locale, translator);
        return translator;
    }
    
    /**
     * Writes the translation files in the plugin storage directory, and updates the files if
     * needed.
     */
    private static void writeFiles()
    {
        if (filesWritten)
            return;

        filesWritten = true;


        File serverDirectory = new File(ZLib.getPlugin().getDataFolder(), i18nDirectory);

        if (!serverDirectory.exists())
        {
            if (!serverDirectory.mkdirs())
            {
                PluginLogger.warning("Unable to create the i18n directory at {0}, is the directory writable?", serverDirectory.getAbsolutePath());
                return;
            }
        }

        if (!serverDirectory.canWrite())
        {
            PluginLogger.warning("The i18n directory ({0}) is not readable: the translations system is disabled.", serverDirectory.getAbsolutePath());
            return;
        }

        if (!serverDirectory.canWrite())
        {
            PluginLogger.warning("The i18n directory ({0}) is not writable, the translation files will not be updated if needed.", serverDirectory.getAbsolutePath());
            return;
        }

        if (!(ZLib.getPlugin() instanceof ZPlugin) && jarFile == null)
        {
            PluginLogger.warning("Unable to load the i18n files: if your plugin main class does not extends ZPlugin, you have to call I18n.setJarFile(getFile()) from your main class somewhere.");
            return;
        }


        final File updateCheckFile = new File(serverDirectory, LAST_VERSION_UPDATE_CHECK_FILE);

        final boolean alwaysOverwrite = (new File(serverDirectory, ALWAYS_OVERWRITE_FILE)).exists();
        final String writtenVersion = FileUtils.readFile(updateCheckFile).replace("\n", "").replace("\r", "").trim();
        final String currentVersion = ZLib.getPlugin().getDescription().getVersion();

        Boolean updateNeeded = false;
        if (alwaysOverwrite || writtenVersion.isEmpty() || !writtenVersion.equals(currentVersion))
            updateNeeded = true;

        // Update of the stored current version
        if (writtenVersion.isEmpty() || !writtenVersion.equals(currentVersion))
        {
            Writer writer = null;
            try
            {
                if (!updateCheckFile.exists() && !updateCheckFile.createNewFile())
                    throw new FileNotFoundException(updateCheckFile.getAbsolutePath());

                if (updateCheckFile.exists())
                {
                    writer = new BufferedWriter(new FileWriter(updateCheckFile));
                    writer.write(currentVersion);
                }
            }
            catch (IOException e)
            {
                PluginLogger.error("Unable to update the last translation version, the system will may update the languages every time. Please check if the {0} file is writable.", e, updateCheckFile.getAbsolutePath());
            }
            finally
            {
                if (writer != null)
                    try { writer.close(); } catch (IOException ignored) {}
            }
        }

        // Backup directory
        File backupDirectory = new File(serverDirectory, BACKUP_DIRECTORY + File.separator + writtenVersion.replace(File.separatorChar, '-').replace("\n", "").replace("\r", "").trim());
        if (updateNeeded && !alwaysOverwrite && !backupDirectory.exists() && !backupDirectory.mkdirs())
        {
            PluginLogger.warning("Unable to create the backup directory at {0}: old files will not be saved!", backupDirectory.getAbsolutePath());
        }


        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements())
        {
            JarEntry entry = entries.nextElement();

            if (entry.getName().startsWith(i18nDirectory + "/"))
            {
                File serverFile = new File(serverDirectory, entry.getName().substring((i18nDirectory + "/").length()));
                if (serverFile.isDirectory())
                    continue;

                if (!serverFile.exists())
                {
                    ZLib.getPlugin().saveResource(entry.getName(), true);
                }
                else if (updateNeeded)
                {
                    // Backup
                    if (!alwaysOverwrite)
                    {
                        if (backupDirectory.exists() && backupDirectory.isDirectory())
                        {
                            File backupFile = new File(backupDirectory, serverFile.getName());
                            String oldPath = serverFile.getAbsolutePath();

                            if (!serverFile.renameTo(backupFile))
                                PluginLogger.warning("Unable to backup file {0} to {1}", oldPath, backupFile.getAbsolutePath());
                            else
                                PluginLogger.info("Translation file {0} backed up to {1}", oldPath, backupFile.getAbsolutePath());
                        }
                    }

                    ZLib.getPlugin().saveResource(entry.getName(), true);
                }
            }
        }
    }

    /**
     * Loads a locale.
     *
     * @param locale the locale to be loaded.
     * @return The translator associated to the locale.
     *
     * @throws UnsupportedLocaleException if the locale is not available.
     */
    private static Translator loadLocale(Locale locale) throws UnsupportedLocaleException
    {
        writeFiles();

        Translator loader = null;

        // The files names checked to find the translation file for this locale
        List<String> checkedFileNames = Arrays.asList(
                locale.toString().toLowerCase(),
                locale.toLanguageTag().toLowerCase(),
                (locale.getLanguage() + "_" + locale.getCountry()).toLowerCase(),
                locale.getLanguage().toLowerCase(),
                locale.getCountry().toLowerCase()
        );


        final File i18nServerDirectory = new File(ZLib.getPlugin().getDataFolder(), i18nDirectory);
        final File[] files = i18nServerDirectory.listFiles();
        if (files == null)
        {
            PluginLogger.warning("Cannot list files of the i18n directory ({0}). Aborting loading of locale {1}.", i18nServerDirectory.getAbsolutePath(), locale);
            return null;
        }

        filesLoop:
        for (File file : files)
        {
            if (!file.isFile())
                continue;

            String lowerCaseFileName = file.getName().toLowerCase();

            for (String checkedFileName : checkedFileNames)
            {
                if (lowerCaseFileName.startsWith(checkedFileName))
                {
                    loader = Translator.getInstance(locale, file);
                    if (loader != null)
                        break filesLoop;
                }
            }
        }


        if (loader == null)
        {
            throw new UnsupportedLocaleException(locale);
        }
        else
        {
            translators.put(locale, loader);
        }
        
        return loader;
    }



    /* **  TRANSLATION METHODS  ** */

    /**
     * Translates the given string using the given locale.
     *
     * <p> If the given locale is null, tries to use the primary locale; 
     * fallbacks to the fallback locale if the string cannot be translated; 
     * fallbacks to the input text if the string still cannot be translated. </p>
     *
     * <p> The count is likely to be used in the string, so if, for a translation with plurals, only
     * a count is given, this count is also interpreted as a parameter (the first and only one, {@code
     * {0}}). If this behavior annoys you, you can disable it using {@link
     * #addCountToParameters(boolean)}. </p>
     *
     * @param locale          The locale to use to translate the string.
     * @param context         The translation context. {@code null} if no context defined.
     * @param messageId       The string to translate.
     * @param messageIdPlural The plural version of the string to translate. {@code null} if this
     *                        translation does not have a plural form.
     * @param count           The count of items to use to choose the singular or plural form.
     *                        {@code null} if this translation does not have a plural form.
     * @param parameters      The parameters, replacing values like {@code {0}} in the translated
     *                        string.
     *
     * @return The translated text, with the parameters replaced by their values.
     */
    public static String translate(Locale locale, String context, String messageId, String messageIdPlural, Integer count, Object... parameters)
    {
        String translated = null;
        Translator translator = null;
        Locale usedLocale = Locale.getDefault();

        // Simplifies the programmer's work. The count is likely to be used in the string, so if,
        // for a translation with plurals, only a count is given, this count is also interpreted as
        // a parameter (the first and only one, {0}).
        if (addCountToParameters && count != null && (parameters == null || parameters.length == 0))
            parameters = new Object[] {count};

        if(locale != null && (translator = getClosestTranslator(locale)) != null)
        {
            translated = translator.translate(context, messageId, messageIdPlural, count);
            usedLocale = translator.getLocale();
        }
        
        if (translated == null && primaryLocale != null && (translator = translators.get(primaryLocale)) != null)
        {
            translated = translator.translate(context, messageId, messageIdPlural, count);
            usedLocale = translator.getLocale();
        }
        
        if (translated == null && fallbackLocale != null && (translator = translators.get(fallbackLocale)) != null)
        {
            translated = translator.translate(context, messageId, messageIdPlural, count);
            usedLocale = translator.getLocale();
        }

        if (translated == null)
        {
            // We use english rules to handle plurals, in this case.
            if (count != null && count != 1 && messageIdPlural != null)
                translated = messageIdPlural;
            else
                translated = messageId;

            usedLocale = primaryLocale != null ? primaryLocale : (fallbackLocale != null ? fallbackLocale : Locale.getDefault());
        }


        if (userFriendlyFormatting)
            translated = replaceFormattingCodes(translated);

        // We replace « ' » with « '' » to escape single quotes, so the formatter leave them alive
        MessageFormat formatter = new MessageFormat(translated.replace("'", "''"), usedLocale);

        // We remove non-breaking spaces, as Minecraft ignores them (breaking texts regardless of their presence) and
        // often badly displays them (dashed square with NBSP inside).
        return formatter.format(parameters)
                .replace("\u00A0", " ").replace("\u2007", " ").replace("\u202F", " ")  // Non-breaking spaces
                .replace("\u2009", " ")                                                // Thin space
                .replace("\u2060", "");                                                // “WORD-JOINER” non-breaking space (zero-width)
    }
    
    /**
     * Translates the given string.
     *
     * <p> Tries to use the primary locale; fallbacks to the fallback locale if the string cannot be
     * translated; fallbacks to the input text if the string still cannot be translated. </p>
     *
     * <p> The count is likely to be used in the string, so if, for a translation with plurals, only
     * a count is given, this count is also interpreted as a parameter (the first and only one, {@code
     * {0}}). If this behavior annoys you, you can disable it using {@link
     * #addCountToParameters(boolean)}. </p>
     *
     * @param context         The translation context. {@code null} if no context defined.
     * @param messageId       The string to translate.
     * @param messageIdPlural The plural version of the string to translate. {@code null} if this
     *                        translation does not have a plural form.
     * @param count           The count of items to use to choose the singular or plural form.
     *                        {@code null} if this translation does not have a plural form.
     * @param parameters      The parameters, replacing values like {@code {0}} in the translated
     *                        string.
     *
     * @return The translated text, with the parameters replaced by their values.
     */
    public static String translate(String context, String messageId, String messageIdPlural, Integer count, Object... parameters)
    {
        return translate(null, context, messageId, messageIdPlural, count, parameters);
    }
    
    /**
     * Replaces some formatting codes into system codes.
     *
     * @param text The input text.
     *
     * @return The text with formatters replaced.
     * @see #setUserFriendlyFormatting(boolean) for details about the codes.
     */
    private static String replaceFormattingCodes(String text)
    {
        return text.replace("{black}", ChatColor.BLACK.toString())
                .replace("{darkblue}", ChatColor.DARK_BLUE.toString())
                .replace("{darkgreen}", ChatColor.DARK_GREEN.toString())
                .replace("{darkaqua}", ChatColor.DARK_AQUA.toString())
                .replace("{darkred}", ChatColor.DARK_RED.toString())
                .replace("{darkpurple}", ChatColor.DARK_PURPLE.toString())
                .replace("{gold}", ChatColor.GOLD.toString())
                .replace("{gray}", ChatColor.GRAY.toString())
                .replace("{darkgray}", ChatColor.DARK_GRAY.toString())
                .replace("{blue}", ChatColor.BLUE.toString())
                .replace("{green}", ChatColor.GREEN.toString())
                .replace("{aqua}", ChatColor.AQUA.toString())
                .replace("{red}", ChatColor.RED.toString())
                .replace("{lightpurple}", ChatColor.LIGHT_PURPLE.toString())
                .replace("{yellow}", ChatColor.YELLOW.toString())
                .replace("{white}", ChatColor.WHITE.toString())

                .replace("{bold}", ChatColor.BOLD.toString())
                .replace("{strikethrough}", ChatColor.STRIKETHROUGH.toString())
                .replace("{underline}", ChatColor.UNDERLINE.toString())
                .replace("{italic}", ChatColor.ITALIC.toString())
                .replace("{obfuscated}", ChatColor.MAGIC.toString())

                .replace("{reset}", ChatColor.RESET.toString())

                .replace("{ce}", errorColor)
                .replace("{cc}", commandColor)
                .replace("{ci}", noticeColor)
                .replace("{cs}", successColor)
                .replace("{cst}", statusColor);
    }



    /* **  METADATA METHODS  ** */

    /**
     * @return the primary locale set, or {@code null} if unset.
     */
    public static Locale getPrimaryLocale()
    {
        return primaryLocale;
    }

    /**
     * @return the fallback locale set, or {@code null} if unset.
     */
    public static Locale getFallbackLocale()
    {
        return fallbackLocale;
    }

    /**
     * Retrieves the last translator for a locale.
     *
     * @param locale A locale.
     *
     * @return The last translator for this locale, or null if either this locale is not loader or a
     * last translator is not available.
     */
    public static String getLastTranslator(Locale locale)
    {
        try
        {
            return translators.get(locale).getLastTranslator();
        }
        catch (NullPointerException e)  // Handles both null and unavailable locale
        {
            return null;
        }
    }

    /**
     * Retrieves the translation team for a locale.
     *
     * @param locale A locale.
     *
     * @return The last translator for this locale, or null if either this locale is not loader or a
     * translation team is not available.
     */
    public static String getTranslationTeam(Locale locale)
    {
        try
        {
            return translators.get(locale).getTranslationTeam();
        }
        catch (NullPointerException e)  // Handles both null and unavailable locale
        {
            return null;
        }
    }

    /**
     * Retrieves the person the error reports have to be sent to, for a locale.
     *
     * @param locale A locale.
     *
     * @return The receiver of the error reports for this locale, or null if either this locale is
     * not loader or a receiver is not available.
     */
    public static String getReportErrorsTo(Locale locale)
    {
        try
        {
            return translators.get(locale).getReportErrorsTo();
        }
        catch (NullPointerException e)  // Handles both null and unavailable locale
        {
            return null;
        }
    }
}
