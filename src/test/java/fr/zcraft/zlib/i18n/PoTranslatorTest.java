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
package fr.zcraft.zlib.i18n;

import fr.zcraft.zlib.TestsUtils;
import fr.zcraft.zlib.components.i18n.translators.Translator;
import fr.zcraft.zlib.components.i18n.translators.gettext.GettextPOTranslator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;


public class PoTranslatorTest
{
    private final Translator translator;

    public PoTranslatorTest() throws IOException
    {
        translator = Translator.getInstance(Locale.FRANCE, TestsUtils.tempResource("i18n/fr_FR.po"));
    }

    @Test
    @Before
    public void testTranslatorTypeFromFileName()
    {
        Assert.assertEquals("Translator instance badly loaded from file name", GettextPOTranslator.class, translator.getClass());
    }

    @Test
    public void testAuthors()
    {
        Assert.assertEquals("Last translator badly retrieved", "Amaury Carrade", translator.getLastTranslator());
        Assert.assertEquals("Translation team badly retrieved", "Amaury Carrade", translator.getTranslationTeam());
        Assert.assertEquals("ReportErrorsTo badly retrieved", "AmauryCarrade", translator.getReportErrorsTo());
    }

    @Test
    public void testPluralIndex()
    {
        Assert.assertEquals("Bad index plural (count=0)", 0, ((int) translator.getPluralIndex(0)));
        Assert.assertEquals("Bad index plural (count=1)", 0, ((int) translator.getPluralIndex(1)));
        Assert.assertEquals("Bad index plural (count=2)", 1, ((int) translator.getPluralIndex(2)));
        Assert.assertEquals("Bad index plural (count=8)", 1, ((int) translator.getPluralIndex(8)));
    }

    @Test
    public void testLocale()
    {
        Assert.assertEquals("Bad exposed locale", Locale.FRANCE, translator.getLocale());
    }

    @Test
    public void testBasicTranslations()
    {
        Assert.assertEquals("Bad translation", "Il n'y a pas de toasts ici...", translator.translate(null, "There are no toasts here ...", null, null));
    }

    @Test
    public void testQuoteEscapement()
    {
        Assert.assertEquals("Quotes badly escaped", "Ce n'est qu'un \"toaster\"", translator.translate(null, "It's just a \"toaster\"", null, null));
        Assert.assertNull("Raw escaped quotes retrieved", translator.translate(null, "It's just a \\\"toaster\\\"", null, null));
    }

    @Test
    public void testContexts()
    {
        Assert.assertEquals("Bad translation with context", "{gold}{bold}Cuit", translator.translate("sidebar", "{gold}{bold}Cooked", null, null));
        Assert.assertEquals("Bad translation with context and UTF-8", "{red}{bold}♨ Toaster ♨", translator.translate("sidebar", "{red}{bold}♨ Toaster ♨", null, null));
        Assert.assertEquals("Bad translation with empty context", "  Toast no. {0}", translator.translate("", "  Toast #{0}", null, null));
    }

    @Test
    public void testPlurals()
    {
        Assert.assertEquals("Bad translation with plural (singular)", "Un toast ajouté.", translator.translate(null, "One toast added.", "{0} toasts added.", 1));
        Assert.assertEquals("Bad translation with plural (plural)", "{0} toasts ajoutés.", translator.translate(null, "One toast added.", "{0} toasts added.", 2));
    }

    @Test
    public void testUnknownTranslations()
    {
        Assert.assertNull("Non-null translation from unknown messageId, without context", translator.translate(null, "Unknown translation", null, null));
        Assert.assertNull("Non-null translation from unknown messageId, with context", translator.translate("context", "Unknown translation", null, null));
        Assert.assertNull("Non-null translation from unknown messageId, with empty context", translator.translate("", "Unknown translation", null, null));
        Assert.assertNull("Non-null translation from unknown messageId, without context, with plural (singular)", translator.translate(null, "Unknown translation", "Unknown translations", 0));
        Assert.assertNull("Non-null translation from unknown messageId, without context, with plural (plural)", translator.translate(null, "Unknown translation", "Unknown translations", 2));
        Assert.assertNull("Non-null translation from unknown messageId, with context, with plural (singular)", translator.translate("context", "Unknown translation", "Unknown translations", 0));
        Assert.assertNull("Non-null translation from unknown messageId, with context, with plural (plural)", translator.translate("context", "Unknown translation", "Unknown translations", 2));

        Assert.assertNull("Translation retrieved from bad context (null)", translator.translate(null, "{darkgreen}{bold}Cook", null, null));
        Assert.assertNull("Translation retrieved from bad context (non-null)", translator.translate("sidebar", "There are no toasts here ...", null, null));
        Assert.assertNull("Translation retrieved from bad context (empty)", translator.translate("", "{blue}Toaster", null, null));
        Assert.assertNull("Translation retrieved from bad context (unknown)", translator.translate("unknown-context", "{blue}Toaster", null, null));
    }
}
