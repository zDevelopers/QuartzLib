/*
 * Plugin UHCReloaded : Alliances
 *
 * Copyright ou © ou Copr. Amaury Carrade (2016)
 * Idées et réflexions : Alexandre Prokopowicz, Amaury Carrade, "Vayan".
 *
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA
 * sur le site "http://www.cecill.info".
 *
 * En contrepartie de l'accessibilité au code source et des droits de copie,
 * de modification et de redistribution accordés par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
 * seule une responsabilité restreinte pèse sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les concédants successifs.
 *
 * A cet égard  l'attention de l'utilisateur est attirée sur les risques
 * associés au chargement,  à l'utilisation,  à la modification et/ou au
 * développement et à la reproduction du logiciel par l'utilisateur étant
 * donné sa spécificité de logiciel libre, qui peut le rendre complexe à
 * manipuler et qui le réserve donc à des développeurs et des professionnels
 * avertis possédant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
 * logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement,
 * à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
 *
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez
 * pris connaissance de la licence CeCILL, et que vous en avez accepté les
 * termes.
 */

package fr.zcraft.quartzlib.components.i18n.translators.gettext;

import fr.zcraft.quartzlib.tools.PluginLogger;
import java.util.Locale;
import java.util.function.Function;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluralForms {
    /**
     * The default plural forms function when we cannot do anything else. It's the English plural rule.
     */
    private static final Function<Long, Integer> FORMS_FUNCTION_FALLBACK = n -> n != 1 ? 1 : 0;

    /**
     * The number of plural forms for this script.
     */
    private final int formsCount;

    /**
     * The form script, as written in the PO/MO file.
     */
    @NotNull
    private final String formsScript;

    /**
     * A function to call to compute the correct plural form
     * from a count.
     */
    private final Function<Long, Integer> formsFunction;

    /**
     * Constructs a new Gettext plural form.
     *
     * <p>The script will be matched against a database of known script. If one match, it will be implemented in
     * pure Java for better performances. Else, we'll fallback on a JavaScript Engine to do the work. If no
     * engine can be loaded, we'll fallback on hardcoded English rules.
     *
     * @param formsCount  The number of plural forms.
     * @param formsScript The raw script (without “plural=” prefix).
     */
    public PluralForms(int formsCount, @Nullable String formsScript) {
        this.formsCount = formsCount;
        this.formsScript = formsScript;

        this.formsFunction = computeFormsFunction();
    }

    public int computePluralForm(long count) {
        final int index = this.formsFunction.apply(count);

        if (index < 0 || index > formsCount) {
            return 0;
        } else {
            return index;
        }
    }

    private Function<Long, Integer> computeFormsFunction() {
        if (formsScript == null) {
            return formsFunctionFallback();
        }

        // We first try if this script is known
        Function<Long, Integer> function = formsFunctionFromKnownScripts();

        // Else we try two JS engines, and fallback to English rules.
        if (function == null) {
            function = formsFunctionFromNashorn();
        }
        if (function == null) {
            function = formsFunctionFromGraalVM();
        }
        if (function == null) {
            function = formsFunctionFallback();
        }

        return function;
    }

    private String normalizeFormsScript(final String script) {
        return script.toLowerCase(Locale.ROOT).replace(" ", "").trim();
    }

    /**
     * Some plural scripts are very commons. For them, we hardcode native functions.
     * We then do not depend on a JavaScript engine, and it's order of magnitude faster.
     *
     * <p>This evaluate method can only work correctly with Plural-Forms listed at:
     * http://www.gnu.org/software/gettext/manual/html_node/Plural-forms.html#Plural-forms
     *
     * @return a Function to compute the plural index from the given count.
     */
    private Function<Long, Integer> formsFunctionFromKnownScripts() {
        switch (normalizeFormsScript(formsScript)) {
            // Only one form
            // Japanese, Vietnamese, Korean, Thai
            case "0":
                return n -> 0;

            // Two forms, singular used for one only
            // English, German, Dutch, Swedish, Danish, Norwegian, Faroese, Spanish, Portuguese, Italian,
            // Greek, Bulgarian, Finnish, Estonian, Hebrew, ahasa Indonesian, Esperanto, Hungarian, Turkish
            case "n!=1":
                return n -> n != 1 ? 1 : 0;

            case "n!=0":
                return n -> n != 0 ? 1 : 0;

            // Two forms, singular used for zero and one
            // Brazilian Portuguese, French
            case "n>0":
                return n -> n > 0 ? 1 : 0;

            case "n>1":
                return n -> n > 1 ? 1 : 0;

            // Three forms, special case for zero
            // Latvian
            case "n%10==1&&n%100!=11?0:n!=0?1:2":
                return n -> n % 10 == 1 && n % 100 != 11 ? 0 : (n != 0 ? 1 : 2);

            // Three forms, special cases for one and two
            // Gaeilge (Irish)
            case "n==1?0:n==2?1:2":
                return n -> n == 1 ? 0 : (n == 2 ? 1 : 2);

            // Three forms, special case for numbers ending in 00 or [2-9][0-9]
            // Romanian
            case "n==1?0:(n==0||(n%100>0&&n%100<20))?1:2":
                return n -> n == 1 ? 0 : ((n == 0 || (n % 100 > 0 && n % 100 < 20)) ? 1 : 2);

            // Three forms, special case for numbers ending in 1[2-9]
            // Lithuanian
            case "n%10==1&&n%100!=11?0:n%10>=2&&(n%100<10||n%100>=20)?1:2":
                return n -> n % 10 == 1 && n % 100 != 11 ? 0 : (n % 10 >= 2 && (n % 100 < 10 || n % 100 >= 20) ? 1 : 2);

            // Three forms, special cases for numbers ending in 1 and 2, 3, 4, except those ending in 1[1-4]
            // Russian, Ukrainian, Belarusian, Serbian, Croatian
            case "n%10==1&&n%100!=11?0:n%10>=2&&n%10<=4&&(n%100<10||n%100>=20)?1:2":
                return n -> n % 10 == 1 && n % 100 != 11 ? 0 :
                        (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20) ? 1 : 2);

            // Three forms, special cases for 1 and 2, 3, 4
            // Czech, Slovak
            case "(n==1)?0:(n>=2&&n<=4)?1:2":
            case "n==1?0:(n>=2&&n<=4)?1:2":
            case "n==1?0:n>=2&&n<=4?1:2":
                return n -> n == 1 ? 0 : ((n >= 2 && n <= 4) ? 1 : 2);

            // Three forms, special case for one and some numbers ending in 2, 3, or 4
            // Polish
            case "n==1?0:n%10>=2&&n%10<=4&&(n%100<10||n%100>=20)?1:2":
                return n -> n == 1 ? 0 : (n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20) ? 1 : 2);

            // Four forms, special case for one and all numbers ending in 02, 03, or 04
            // Slovenian
            case "n%100==1?0:n%100==2?1:n%100==3||n%100==4?2:3":
                return n -> n % 100 == 1 ? 0 : (n % 100 == 2 ? 1 : (n % 100 == 3 || n % 100 == 4 ? 2 : 3));

            // Six forms, special cases for one, two, all numbers ending in 02, 03, … 10, all numbers ending in 11 … 99,
            // and others
            // Arabic
            case "n==0?0:n==1?1:n==2?2:n%100>=3&&n%100<=10?3:n%100>=11?4:5":
                return n -> n == 0 ? 0 :
                        (n == 1 ? 1 : (n == 2 ? 2 : (n % 100 >= 3 && n % 100 <= 10 ? 3 : (n % 100 >= 11 ? 4 : 5))));

            default:
                return null;
        }
    }

    /**
     * If the script is non-standard, we fallback on the buiilt-in Nashorn (available before Java 15).
     *
     * @return a Function to compute the plural index from the given count.
     */
    private Function<Long, Integer> formsFunctionFromNashorn() {
        return formsFunctionFromJSEngine("JavaScript");
    }

    /**
     * In Java 15+, we fallback on GraalVM.
     *
     * @return a Function to compute the plural index from the given count.
     */
    private Function<Long, Integer> formsFunctionFromGraalVM() {
        return formsFunctionFromJSEngine("graal.js");
    }

    /**
     * Generic computation from a JS engine.
     *
     * <p>The official documentation mentions that the plural determination script is in C format, but
     * the JavaScript format is the same for these scripts (containing only basic mathematics and
     * ternary operators), excepted for the booleans, but this case is handled manually.
     *
     * @return a Function to compute the plural index from the given count.
     */
    private Function<Long, Integer> formsFunctionFromJSEngine(final String engine) {
        final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(engine);
        if (scriptEngine == null) {
            return null;
        }

        return n -> {
            try {
                scriptEngine.put("n", n);
                Object rawPluralIndex = scriptEngine.eval(formsScript);

                // If the index is a boolean, as some po files use the C handling of booleans, we convert them
                // into the appropriate numbers.
                // Else, we try to convert the output to an integer.
                return rawPluralIndex instanceof Boolean ? (((Boolean) rawPluralIndex) ? 1 : 0) :
                        (rawPluralIndex instanceof Number ? ((Number) rawPluralIndex).intValue() :
                                Integer.valueOf(rawPluralIndex.toString()));
            } catch (ScriptException | NumberFormatException e) {
                PluginLogger.error("Invalid plural forms script “{1}”", e, formsScript);
                return 0;
            }
        };
    }

    /**
     * If nothing work, we fallback on hardcoded English rules.
     *
     * @return a Function to compute the plural index from the given count.
     */
    private Function<Long, Integer> formsFunctionFallback() {
        return FORMS_FUNCTION_FALLBACK;
    }
}
