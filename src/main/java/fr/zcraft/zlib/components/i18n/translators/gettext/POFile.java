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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Parser for Gettext {@code *.po} resources files.
 *
 * <p>Note: this parser does not support PO file without a blank line between each translation entry
 * currently.</p>
 */
public class POFile
{
    private BufferedReader rawReader;

    private Set<Translation> translations = new HashSet<>();

    private String lastTranslator = null;
    private String translationTeam = null;
    private String reportErrorsTo = null;

    private Integer pluralCount = 2;
    private String pluralFormScript = "";


    /**
     * @param reader The string this parser will have to parse.
     */
    public POFile(BufferedReader reader)
    {
        this.rawReader = reader;
    }

    /**
     * @param reader The string this parser will have to parse.
     */
    public POFile(Reader reader)
    {
        this.rawReader = new BufferedReader(reader);
    }

    /**
     * @param file The file this parser will have to parse.
     *
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a
     *                               regular file, or for some other reason cannot be opened for
     *                               reading.
     */
    public POFile(File file) throws FileNotFoundException
    {
        this(new FileReader(file));
    }

    /**
     * @param raw The string this parser will have to parse.
     */
    public POFile(String raw)
    {
        this(new StringReader(raw));
    }


    /**
     * Parses the string and extracts translations and metadata.
     *
     * The PO file is only computed one time (because the buffer is consumed). Other calls does
     * nothing.
     *
     * @throws CannotParsePOException if the PO file cannot be parsed.
     */
    public void parse() throws CannotParsePOException
    {
        if (rawReader == null)
            return;

        try
        {
            String line;
            Integer lineNumber = 0;

            // For each section, we first collect the keys defined (they may expand on several lines);
            // then we extract and save them when we hit a blank line, acting as a separator between
            // translations.

            Map<String, String> tokens    = new HashMap<>();
            String              lastToken = null;

            while ((line = rawReader.readLine()) != null)
            {
                lineNumber++;

                // We don't care about trailing whitespaces
                line = line.trim();

                // File parsing
                if (!line.isEmpty())
                {
                    // Comments
                    if (line.startsWith("#"))
                        continue;

                        // Continued values of tokens on another line
                    else if (line.startsWith("\""))
                    {
                        if (lastToken == null)
                            throw new CannotParsePOException("Unnamed token value", lineNumber);

                        String value = extractTokenValue(line);
                        String currentTokenValue = tokens.get(lastToken);

                        tokens.put(lastToken, currentTokenValue + value);
                    }

                    // Beginning of a new token
                    else
                    {
                        String[] lineParts = line.split(" ", 2);
                        if (line.length() < 2)
                            throw new CannotParsePOException("Malformed token line", lineNumber);

                        // The first string before a space is the token name, according to the spec (like
                        // msgid, msgstr...). The other parts are the token value.
                        tokens.put(lineParts[0], extractTokenValue(lineParts[1]));
                        lastToken = lineParts[0];
                    }
                }

                // Analysis
                else
                {
                    if (!tokens.isEmpty())
                        analyseEntry(tokens);

                    tokens.clear();
                    lastToken = null;
                }
            }

            // If the file doesn't ends with an blank line
            if (!tokens.isEmpty())
                analyseEntry(tokens);
        }
        catch (IOException e)
        {
            throw new CannotParsePOException("An IO exception occurred while parsing the file", e);
        }
        finally
        {
            try
            {
                if (rawReader != null)
                {
                    rawReader.close();
                    rawReader = null;
                }
            }
            catch (IOException ignored) {}
        }
    }

    /**
     * From a token value in quotes, extract the raw value.
     *
     * <p>As example, « {@code "Raw \"value\""} » is converted into « {@code Raw "value"} ».</p>
     *
     * @param raw The raw token value.
     *
     * @return The extracted value.
     */
    private String extractTokenValue(String raw)
    {
        final StringBuilder extracted = new StringBuilder();
        Boolean inString = false;

        for (int i = 0; i < raw.length(); i++)
        {
            char character = raw.charAt(i);
            if (character == '"')
            {
                if (i == 0 || raw.charAt(i - 1) != '\\')
                {
                    inString = !inString;
                    if (!inString)
                        break;
                }
                else
                {
                    extracted.append('"');
                }
            }
            else if (inString && !(character == '\\' && i != raw.length() - 1 && raw.charAt(i + 1) == '"'))
            {
                extracted.append(character);
            }
        }

        return extracted.toString();
    }

    private void analyseEntry(Map<String, String> tokens)
    {
        // If there isn't any `msgid` token, the section is invalid and skipped.
        if (!tokens.containsKey("msgid"))
            return;

        String msgid = tokens.get("msgid");

        // Translation entry
        if (!msgid.isEmpty())
        {
            String msgid_plural = tokens.get("msgid_plural");  // Null if unset—that's exactly what we want.
            String msgctxt      = tokens.get("msgctxt");       // Same.

            // msgstr can be in two different formats:
            // - msgstr: then there is only one translation;
            // - msgstr[i]: then multiple translations are available (for plurals).
            List<String> msgstr;

            if (tokens.containsKey("msgstr"))
            {
                msgstr = Collections.singletonList(tokens.get("msgstr"));
            }
            else
            {
                msgstr = new ArrayList<>();
                for (int i = 0; ; i++)
                {
                    String tokenValue = tokens.get("msgstr[" + i + "]");
                    if (tokenValue == null)
                        break;

                    // Elements are added ordered, so the index is the good one.
                    msgstr.add(tokenValue);
                }
            }

            // No translation available, skipped.
            if (msgstr.isEmpty() || msgstr.get(0).trim().isEmpty())
                return;

            translations.add(new Translation(msgctxt, msgid, msgid_plural, msgstr));
        }

        // Metadata
        else
        {
            String rawMetadata = tokens.get("msgstr");
            if (rawMetadata == null)
                return;

            // Ensures both interpreted and written line breaks are interpreted
            String[] metadata = rawMetadata.split("(\\\\n)|\n");
            for (String meta : metadata)
            {
                String[] metaParts = meta.split(":");
                if (metaParts.length < 2)
                    continue;

                String value = metaParts[1].trim();

                switch (metaParts[0].trim().toLowerCase())
                {
                    case "last-translator":
                        lastTranslator = value;
                        break;

                    case "language-team":
                        translationTeam = value;
                        break;

                    case "report-msgid-bugs-to":
                        reportErrorsTo = value;
                        break;

                    case "plural-forms":
                        String[] parts = value.split(";", 2);
                        if (parts.length < 2)
                            break;

                        try
                        {
                            pluralCount = Integer.valueOf(parts[0].split("=")[1]);
                            pluralFormScript = parts[1];

                            if (pluralFormScript.endsWith(";"))
                                pluralFormScript = pluralFormScript.substring(0, pluralFormScript.length() - 1);

                            // Converts “plural=<script>” to “<script>”
                            if (pluralFormScript.contains("="))
                                pluralFormScript = pluralFormScript.split("=")[1];
                        }
                        catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored)
                        {
                            // Well, invalid.
                        }
                        break;
                }
            }
        }
    }

    /**
     * @return The translations extracted from the PO file.
     */
    public Set<Translation> getTranslations()
    {
        return translations;
    }

    /**
     * @return The last translator.
     */
    public String getLastTranslator()
    {
        return lastTranslator;
    }

    /**
     * @return The name of the translation team.
     */
    public String getTranslationTeam()
    {
        return translationTeam;
    }

    /**
     * @return The person to contact if there is an error in the translations.
     */
    public String getReportErrorsTo()
    {
        return reportErrorsTo;
    }

    /**
     * @return The number of plural forms in this PO/language.
     */
    public Integer getPluralCount()
    {
        return pluralCount;
    }

    /**
     * @return The script to execute to determine which translation has to be used for a given
     * number.
     */
    public String getPluralFormScript()
    {
        return pluralFormScript;
    }


    /**
     * Thrown if the file cannot be parsed.
     */
    public class CannotParsePOException extends RuntimeException
    {
        public CannotParsePOException(String message)
        {
            super(message);
        }

        public CannotParsePOException(String message, Integer line)
        {
            super(message + " [" + line + "]");
        }

        public CannotParsePOException(Throwable cause)
        {
            super(cause);
        }

        public CannotParsePOException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public CannotParsePOException(String message, Integer line, Throwable cause)
        {
            super(message + " [" + line + "]", cause);
        }
    }
}
