/*
 * Copyright or © or Copr. QuartzLib contributors (2015 - 2020)
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
package fr.zcraft.quartzlib.i18n;

import fr.zcraft.quartzlib.TestsUtils;
import fr.zcraft.quartzlib.components.i18n.translators.Translation;
import fr.zcraft.quartzlib.components.i18n.translators.gettext.POFile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;


public class POParserTest
{
    private final POFile po = new POFile(new InputStreamReader(TestsUtils.getResource("i18n/fr_FR.po")));

    @Test
    @Before
    public void testPoParsing()
    {
        try
        {
            po.parse();
        }
        catch (POFile.CannotParsePOException e)
        {
            Assert.fail("PO file parsing throws exception for a valid PO file");
        }
    }

    @Test
    public void testAuthors()
    {
        Assert.assertEquals("Last translator badly retrieved", "Amaury Carrade", po.getLastTranslator());
        Assert.assertEquals("Translation team badly retrieved", "Amaury Carrade", po.getTranslationTeam());
        Assert.assertEquals("ReportErrorsTo badly retrieved", "AmauryCarrade", po.getReportErrorsTo());
    }

    @Test
    public void testPlurals()
    {
        Assert.assertEquals("Bad plural count", 2, (int) po.getPluralCount());
        Assert.assertEquals("Plural script badly retrieved", "n>1", po.getPluralFormScript());
    }

    @Test
    public void testTranslationsCount()
    {
        Assert.assertFalse("Translations from PO file missing", po.getTranslations().size() < 13);
        Assert.assertFalse("Translations from PO file in a too high count", po.getTranslations().size() > 13);
    }

    @Test
    public void testTranslations()
    {
        for (Translation translation : po.getTranslations())
        {
            switch (translation.getOriginal())
            {
                case "{darkgreen}{bold}Cook":
                    Assert.assertEquals("Bad translations count for single translation", 1, translation.getTranslations().size());
                    Assert.assertEquals("Bad translation", "{darkgreen}{bold}Cuisinier", translation.getTranslations().get(0));
                    Assert.assertEquals("Bad context", "sidebar", translation.getContext());
                    break;

                case "{red}{bold}♨ Toaster ♨":
                    Assert.assertEquals("Bad translations count for single translation with UTF-8", 1, translation.getTranslations().size());
                    Assert.assertEquals("Bad translation with UTF-8", "{red}{bold}♨ Toaster ♨", translation.getTranslations().get(0));
                    Assert.assertEquals("Bad context with UTF-8 messageId", "sidebar", translation.getContext());
                    break;

                case "One toast added.":
                    Assert.assertEquals("Bad translations count for translation with plural", 2, translation.getTranslations().size());
                    Assert.assertEquals("Bad extracted plural messageId", "{0} toasts added.", translation.getOriginalPlural());
                    Assert.assertEquals("Bad singular translation", "Un toast ajouté.", translation.getTranslations().get(0));
                    Assert.assertEquals("Bad plural translation", "{0} toasts ajoutés.", translation.getTranslations().get(1));
                    Assert.assertEquals("Bad null context with plurals", null, translation.getContext());
                    break;

                case "There are no toasts here ...":
                    Assert.assertEquals("Bad translations count for single translation without context", 1, translation.getTranslations().size());
                    Assert.assertEquals("Bad translation without context", "Il n'y a pas de toasts ici...", translation.getTranslations().get(0));
                    Assert.assertEquals("Bad null context", null, translation.getContext());
                    break;

                case "  Toast #{0}":
                    Assert.assertEquals("Bad translations count for single translation with empty context", 1, translation.getTranslations().size());
                    Assert.assertEquals("Bad translation with empty context", "  Toast no. {0}", translation.getTranslations().get(0));
                    Assert.assertEquals("Bad empty context", "", translation.getContext());
                    break;

                case "It's just a \"toaster\"":
                    Assert.assertEquals("Bad translations count with escaped quotes", 1, translation.getTranslations().size());
                    Assert.assertEquals("Bad translation with escaped quotes", "Ce n'est qu'un \"toaster\"", translation.getTranslations().get(0));
                    Assert.assertFalse("Translation retrieved with raw escaped quotes", "Ce n'est qu'un \\\"toaster\\\"".equals(translation.getTranslations().get(0)));
                    break;

                case "It's just a \\\"toaster\\\"":
                    Assert.fail("Translation retrieved from raw escaped quotes");
                    break;

                case "Multi-lines message":
                    Assert.assertEquals("Bad translations count for multi-lines messages", 1, translation.getTranslations().size());
                    Assert.assertEquals("Bad translations for multi-lines messages", "Message multi-ligne", translation.getTranslations().get(0));
                    break;

                case "Multi-lines message with trailing space":
                    Assert.assertEquals("Bad translations count for multi-lines messages", 1, translation.getTranslations().size());
                    Assert.assertEquals("Bad translations for multi-lines messages", "Message multi-ligne avec une espace séparatrice explicite", translation.getTranslations().get(0));
                    break;

                case "Multi-lines message  with multiple trailing spaces":
                    Assert.assertEquals("Bad translations count for multi-lines messages with multiple trailing spaces", 1, translation.getTranslations().size());
                    Assert.assertEquals("Bad translations for multi-lines messages with multiple trailing spaces", "Message  multi-ligne avec plusieurs espaces séparatrices explicites", translation.getTranslations().get(0));
                    Assert.assertFalse("Bad translations for multi-lines messages with multiple trailing spaces: trailing spaces skipped", "Message multi-ligne avec plusieurs espaces séparatrices explicites".equals(translation.getTranslations().get(0)));
                    break;

                case "Multi-lines message with multiple trailing spaces":
                    Assert.fail("Bad translations for multi-lines messages with multiple trailing spaces: trailing spaces skipped in messageId");
                    break;

                case "Multi-lines message  with trailing space":
                    Assert.fail("Bad translations for multi-lines messages with one trailing spaces: trailing spaces duplicated in messageId");
                    break;

                case " {gray}It will shrink by one block every {0} second(s) until {1} blocks in diameter.":
                    Assert.fail("Bad translation for multi-line messages with empty lines: a trailing space was incorrectly added");
                    break;

                case "{gray}When clicked, a sign will open; write the name of the team inside.":
                    Assert.fail("Translation with commented out msgid and msgstr retrieved");
            }
        }
    }
}
