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
import fr.zcraft.quartzlib.components.i18n.translators.Translator;
import fr.zcraft.quartzlib.components.i18n.translators.properties.PropertiesTranslator;
import fr.zcraft.quartzlib.tools.reflection.Reflection;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;


public class PropertiesTranslatorTest
{
    private final Translator translator;

    public PropertiesTranslatorTest() throws IOException
    {
        translator = Translator.getInstance(Locale.FRANCE, TestsUtils.tempResource("i18n/fr_FR.properties"));

        try
        {
            Reflection.call(translator, "load");
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            Assert.fail("Unable to load translations");
        }
    }

    @Test
    @BeforeEach
    public void testTranslatorTypeFromFileName()
    {
        Assert.assertEquals("Translator instance badly loaded from file name", PropertiesTranslator.class, translator.getClass());
    }

    @Test
    public void testAuthors()
    {
        Assert.assertEquals("Last translator badly retrieved", "Amaury Carrade", translator.getLastTranslator());
        Assert.assertEquals("Translation team badly retrieved", "Amaury Carrade", translator.getTranslationTeam());
        Assert.assertEquals("ReportErrorsTo badly retrieved", "AmauryCarrade", translator.getReportErrorsTo());
    }

    @Test
    public void testAuthorsWithoutMeta() throws IOException
    {
        PropertiesTranslator noMetaTranslator = new PropertiesTranslator(Locale.UK, TestsUtils.tempResource("i18n/en_GB.no-meta.properties"));

        try
        {
            Reflection.call(noMetaTranslator, "load");
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            Assert.fail("Unable to load translations");
        }

        Assert.assertNull("Last translator retrieved in no-meta mode", noMetaTranslator.getLastTranslator());
        Assert.assertNull("Translation team retrieved in no-meta mode", noMetaTranslator.getTranslationTeam());
        Assert.assertNull("ReportErrorsTo retrieved in no-meta mode", noMetaTranslator.getReportErrorsTo());
    }

    @Test
    public void testPluralIndex()
    {
        Assert.assertEquals("Bad index plural (count=0)", 0, ((int) translator.getPluralIndex(0)));
        Assert.assertEquals("Bad index plural (count=1)", 0, ((int) translator.getPluralIndex(1)));
        Assert.assertEquals("Bad index plural (count=2)", 0, ((int) translator.getPluralIndex(2)));
        Assert.assertEquals("Bad index plural (count=8)", 0, ((int) translator.getPluralIndex(8)));
    }

    @Test
    public void testLocale()
    {
        Assert.assertEquals("Bad exposed locale", Locale.FRANCE, translator.getLocale());
    }

    @Test
    public void testBasicTranslations()
    {
        Assert.assertEquals("Bad translation", "{darkgreen}{bold}Cuisinier", translator.translate(null, "sidebar.cook", null, null));
        Assert.assertEquals("Bad translation with spaces", "{yellow}{bold}Dans le toaster", translator.translate(null, "sidebar.inside-the-toaster", null, null));
        Assert.assertEquals("Bad translation with UTF-8", "{red}{bold}\u2668 Toaster \u2668", translator.translate(null, "sidebar.toaster-cooking", null, null));
    }

    @Test
    public void testContexts()
    {
        Assert.assertEquals("Bad translation: context not ignored", "{gold}{bold}Cuit", translator.translate("context", "sidebar.cooked", null, null));
        Assert.assertEquals("Bad translation with spaces: context not ignored", "{yellow}{bold}Dans le toaster", translator.translate("context", "sidebar.inside-the-toaster", null, null));
        Assert.assertEquals("Bad translation with UTF-8: context not ignored", "{red}{bold}\u2668 Toaster \u2668", translator.translate("context", "sidebar.toaster-cooking", null, null));
    }

    @Test
    public void testPlurals()
    {
        Assert.assertEquals("Bad translation with plural (singular)", "Un toast ajout\u00E9.", translator.translate(null, "one-toast-added", "toasts-added", 1));
        Assert.assertEquals("Bad translation with plural (plural, should be ignored)", "Un toast ajout\u00E9.", translator.translate(null, "one-toast-added", "toasts-added", 2));
    }

    @Test
    public void testUnknownTranslations()
    {
        Assert.assertNull("Non-null translation from unknown messageId, without context", translator.translate(null, "unknown.translation", null, null));
        Assert.assertNull("Non-null translation from unknown messageId, with context", translator.translate("context", "unknown.translation", null, null));
        Assert.assertNull("Non-null translation from unknown messageId, with empty context", translator.translate("", "unknown.translation", null, null));
        Assert.assertNull("Non-null translation from unknown messageId, without context, with plural (singular)", translator.translate(null, "unknown.translation", "unknown.translations", 0));
        Assert.assertNull("Non-null translation from unknown messageId, without context, with plural (plural, should be ignored)", translator.translate(null, "unknown.translation", "unknown.translations", 2));
        Assert.assertNull("Non-null translation from unknown messageId, with context, with plural (singular)", translator.translate("context", "unknown.translation", "unknown.translations", 0));
        Assert.assertNull("Non-null translation from unknown messageId, with context, with plural (plural, should be ignored)", translator.translate("context", "unknown.translation", "unknown.translations", 2));
    }
}
