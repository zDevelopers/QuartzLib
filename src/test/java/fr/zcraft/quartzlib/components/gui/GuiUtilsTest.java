/*
 * Copyright or © or Copr. AmauryCarrade (2015)
 *
 * http://amaury.carrade.eu
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

package fr.zcraft.quartzlib.components.gui;

import com.google.common.collect.Lists;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;


public class GuiUtilsTest {
    private static void assertLists(List<String> expected, List<String> actual) {
        assertLists(null, expected, actual);
    }

    private static void assertLists(String message, List<String> expected, List<String> actual) {
        Assert.assertEquals(message, StringUtils.join(expected, ","), StringUtils.join(actual, ","));
    }

    @Test
    public void generateLoreTest() {
        final String text =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                        + "Aliquam sit amet malesuada enim. Sed ac ultricies eros.";
        final List<String> expected = Lists.newArrayList(
                "Lorem ipsum dolor sit amet,",
                "consectetur adipiscing elit.",
                "Aliquam sit amet malesuada",
                "enim. Sed ac ultricies eros."
        );

        assertLists(expected, GuiUtils.generateLore(text, 28));
    }

    @Test
    public void generateLoreWithLineBreakTest() {
        final String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam sit\n"
                        + "amet malesuada enim. Sed ac ultricies eros.";
        final List<String> expected = Lists.newArrayList(
                "Lorem ipsum dolor sit amet,",
                "consectetur adipiscing elit.",
                "Aliquam sit",
                "amet malesuada enim. Sed ac",
                "ultricies eros."
        );

        assertLists(expected, GuiUtils.generateLore(text, 28));
    }

    @Test
    public void generateLoreWithColorsTest() {
        final String text =
                "§cLorem ipsum dolor sit amet, consectetur §aadipiscing elit. "
                        + "Aliquam sit amet malesuada §renim. Sed ac ultricies eros.";
        final List<String> expected = Lists.newArrayList(
                "§cLorem ipsum dolor sit amet,",
                "§cconsectetur §aadipiscing elit.",
                "§aAliquam sit amet malesuada",
                "§a§renim. Sed ac ultricies eros."
        );

        assertLists(expected, GuiUtils.generateLore(text, 28));
    }

    @Test
    public void generateLoreWithColorsAndLineBreakTest() {
        final String text =
                "§cLorem ipsum dolor sit amet, consectetur §aadipiscing \nelit. "
                        + "Aliquam sit amet malesuada §renim. Sed ac ultricies eros.";
        final List<String> expected = Lists.newArrayList(
                "§cLorem ipsum dolor sit amet,",
                "§cconsectetur §aadipiscing",
                "§aelit. Aliquam sit amet",
                "§amalesuada §renim. Sed ac",
                "§rultricies eros."
        );

        assertLists(expected, GuiUtils.generateLore(text, 28));
    }

    @Test
    public void generateLoreWithWordLongerThanTheLimitTest() {
        final String text =
                "Loremipsumdolorsitametconsecteturadipiscing elit. "
                        + "Aliquam sit amet malesuada enim. Sed ac ultricies eros.";
        final List<String> expected = Lists.newArrayList(
                "Loremipsumdolorsitametconsecteturadipiscing",
                "elit. Aliquam sit amet",
                "malesuada enim. Sed ac",
                "ultricies eros."
        );

        assertLists(expected, GuiUtils.generateLore(text, 28));
    }

    @Test
    public void generatePrefixedFixedLengthTextTest() {
        final String text =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                        + "Pellentesque posuere sagittis pulvinar. Vestibulum nec ultricies urna. "
                        + "Integer malesuada lacus eget ornare ultricies. Donec dapibus eget enim blandit luctus. "
                        + "Praesent ut tortor a urna ornare ornare. Proin luctus dictum arcu, "
                        + "at pellentesque eros tincidunt non. "
                        + "Nam ac orci pharetra, tincidunt elit finibus, congue lacus. Suspendisse a arcu dui. "
                        + "Curabitur ac malesuada enim. Phasellus et imperdiet velit. "
                        + "Pellentesque dignissim lectus et laoreet placerat. Cras sed diam ut nulla cursus cursus. "
                        + "Sed tempus rhoncus dui, ac imperdiet ex dictum id.";
        final String expected =
                "| Lorem ipsum dolor sit amet, consectetur adipiscing\n"
                        + "| elit. Pellentesque posuere sagittis pulvinar.\n"
                        + "| Vestibulum nec ultricies urna. Integer malesuada lacus\n"
                        + "| eget ornare ultricies. Donec dapibus eget enim blandit\n"
                        + "| luctus. Praesent ut tortor a urna ornare ornare. Proin\n"
                        + "| luctus dictum arcu, at pellentesque eros tincidunt non.\n"
                        + "| Nam ac orci pharetra, tincidunt elit finibus, congue\n"
                        + "| lacus. Suspendisse a arcu dui. Curabitur ac malesuada\n"
                        + "| enim. Phasellus et imperdiet velit. Pellentesque\n"
                        + "| dignissim lectus et laoreet placerat. Cras sed diam ut\n"
                        + "| nulla cursus cursus. Sed tempus rhoncus dui, ac\n"
                        + "| imperdiet ex dictum id.";

        Assert.assertEquals(expected, GuiUtils.generatePrefixedFixedLengthString("| ", text, 55));
    }

    @Test
    public void generatePrefixedFixedLengthTextWithColorsTest() {
        final String text =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. §cPellentesque posuere sagittis pulvinar. "
                        + "§d§lVestibulum nec ultricies urna. Integer malesuada lacus eget ornare ultricies. "
                        + "Donec dapibus eget enim blandit luctus. Praesent ut tortor a urna ornare ornare. "
                        + "Proin luctus dictum arcu, at pellentesque eros tincidunt non. "
                        + "§rNam ac orci pharetra, tincidunt elit finibus, congue lacus. Suspendisse a arcu dui. "
                        + "Curabitur ac malesuada enim. Phasellus et imperdiet velit. "
                        + "Pellentesque dignissim lectus et laoreet placerat. Cras sed diam ut nulla cursus cursus. "
                        + "Sed tempus rhoncus dui, ac imperdiet ex dictum id.";
        final String expected =
                "| Lorem ipsum dolor sit amet, consectetur adipiscing\n"
                        + "| elit. §cPellentesque posuere sagittis pulvinar.\n"
                        + "| §c§d§lVestibulum nec ultricies urna. Integer malesuada lacus\n"
                        + "| §d§leget ornare ultricies. Donec dapibus eget enim blandit\n"
                        + "| §d§lluctus. Praesent ut tortor a urna ornare ornare. Proin\n"
                        + "| §d§lluctus dictum arcu, at pellentesque eros tincidunt non.\n"
                        + "| §d§l§rNam ac orci pharetra, tincidunt elit finibus, congue\n"
                        + "| §rlacus. Suspendisse a arcu dui. Curabitur ac malesuada\n"
                        + "| §renim. Phasellus et imperdiet velit. Pellentesque\n"
                        + "| §rdignissim lectus et laoreet placerat. Cras sed diam ut\n"
                        + "| §rnulla cursus cursus. Sed tempus rhoncus dui, ac\n"
                        + "| §rimperdiet ex dictum id.";

        Assert.assertEquals(expected, GuiUtils.generatePrefixedFixedLengthString("| ", text, 55));
    }

    @Test
    public void generatePrefixedFixedLengthTextWithLineBreaksTest() {
        final String text =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. \n"
                        + "Pellentesque posuere sagittis pulvinar. Vestibulum nec ultricies urna. "
                        + "Integer malesuada lacus eget ornare ultricies. \n"
                        + "Donec dapibus eget enim blandit luctus. Praesent ut tortor a urna ornare ornare. "
                        + "Proin luctus dictum arcu, at pellentesque eros tincidunt non. "
                        + "Nam ac orci pharetra, tincidunt elit finibus, congue lacus. \n"
                        + "Suspendisse a arcu dui. Curabitur ac malesuada enim. Phasellus et imperdiet velit. "
                        + "Pellentesque dignissim lectus et laoreet placerat. Cras sed diam ut nulla cursus cursus. \n"
                        + "Sed tempus rhoncus dui, ac imperdiet ex dictum id.";
        final String expected =
                "| Lorem ipsum dolor sit amet, consectetur adipiscing\n"
                        + "| elit.\n"
                        + "| Pellentesque posuere sagittis pulvinar. Vestibulum nec\n"
                        + "| ultricies urna. Integer malesuada lacus eget ornare\n"
                        + "| ultricies.\n"
                        + "| Donec dapibus eget enim blandit luctus. Praesent ut\n"
                        + "| tortor a urna ornare ornare. Proin luctus dictum arcu,\n"
                        + "| at pellentesque eros tincidunt non. Nam ac orci\n"
                        + "| pharetra, tincidunt elit finibus, congue lacus.\n"
                        + "| Suspendisse a arcu dui. Curabitur ac malesuada enim.\n"
                        + "| Phasellus et imperdiet velit. Pellentesque dignissim\n"
                        + "| lectus et laoreet placerat. Cras sed diam ut nulla\n"
                        + "| cursus cursus.\n"
                        + "| Sed tempus rhoncus dui, ac imperdiet ex dictum id.";

        Assert.assertEquals(expected, GuiUtils.generatePrefixedFixedLengthString("| ", text, 55));
    }

    @Test
    public void generatePrefixedFixedLengthTextWithLineBreaksAndColorsTest() {
        final String text =
                "Lorem ipsum dolor sit amet, §aconsectetur adipiscing elit. \n"
                        + "Pellentesque posuere §b§l§osagittis pulvinar. Vestibulum nec ultricies urna. "
                        + "Integer malesuada lacus eget ornare ultricies. \nDonec dapibus eget enim blandit luctus. "
                        + "Praesent ut tortor a urna ornare ornare. "
                        + "Proin luctus dictum arcu, at pellentesque eros tincidunt non. "
                        + "§rNam ac orci pharetra, tincidunt elit finibus, congue lacus. \n"
                        + "Suspendisse a arcu dui. Curabitur ac malesuada enim. Phasellus et imperdiet velit. "
                        + "Pellentesque dignissim lectus et laoreet placerat. Cras sed diam ut nulla cursus cursus. \n"
                        + "§6§lSed tempus rhoncus dui, ac imperdiet ex dictum id.";
        final String expected =
                "| Lorem ipsum dolor sit amet, §aconsectetur adipiscing\n"
                        + "| §aelit.\n"
                        + "| §aPellentesque posuere §b§l§osagittis pulvinar. Vestibulum nec\n"
                        + "| §b§l§oultricies urna. Integer malesuada lacus eget ornare\n"
                        + "| §b§l§oultricies.\n"
                        + "| §b§l§oDonec dapibus eget enim blandit luctus. Praesent ut\n"
                        + "| §b§l§otortor a urna ornare ornare. Proin luctus dictum arcu,\n"
                        + "| §b§l§oat pellentesque eros tincidunt non. §rNam ac orci\n"
                        + "| §rpharetra, tincidunt elit finibus, congue lacus.\n"
                        + "| §rSuspendisse a arcu dui. Curabitur ac malesuada enim.\n"
                        + "| §rPhasellus et imperdiet velit. Pellentesque dignissim\n"
                        + "| §rlectus et laoreet placerat. Cras sed diam ut nulla\n"
                        + "| §rcursus cursus.\n"
                        + "| §r§6§lSed tempus rhoncus dui, ac imperdiet ex dictum id.";

        Assert.assertEquals(expected, GuiUtils.generatePrefixedFixedLengthString("| ", text, 55));
    }
}
