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

package fr.zcraft.quartzlib.components.rawtext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import junit.framework.Assert;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;


public class RawTextTest {
    private static void assertJson(RawText part, String jsonMessage) {
        JSONObject obj;
        try {
            obj = (JSONObject) new JSONParser().parse(jsonMessage);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(obj.toJSONString(), part.toJSONString());
    }

    @Test
    public void simpleMessageTest() {
        RawText text = new RawText("Hello world !");
        assertJson(text, "{\"text\":\"Hello world !\"}");
    }

    @Test
    public void extraedMessageTest() {
        RawText text = new RawText("Hello")
                .then("world")
                .then("!")
                .build();
        assertJson(text, "{\"text\":\"Hello\", \"extra\":[{\"text\":\"world\"}, {\"text\":\"!\"}]}");
    }

    @Test
    public void styleNameTest() {
        HashMap<ChatColor, String> styleNames = new HashMap<>();
        styleNames.put(ChatColor.AQUA, "aqua");
        styleNames.put(ChatColor.BLACK, "black");
        styleNames.put(ChatColor.BLUE, "blue");
        styleNames.put(ChatColor.BOLD, "bold");
        styleNames.put(ChatColor.DARK_AQUA, "dark_aqua");
        styleNames.put(ChatColor.DARK_BLUE, "dark_blue");
        styleNames.put(ChatColor.DARK_GRAY, "dark_gray");
        styleNames.put(ChatColor.DARK_GREEN, "dark_green");
        styleNames.put(ChatColor.DARK_PURPLE, "dark_purple");
        styleNames.put(ChatColor.DARK_RED, "dark_red");
        styleNames.put(ChatColor.GOLD, "gold");
        styleNames.put(ChatColor.GRAY, "gray");
        styleNames.put(ChatColor.GREEN, "green");
        styleNames.put(ChatColor.ITALIC, "italic");
        styleNames.put(ChatColor.LIGHT_PURPLE, "light_purple");
        styleNames.put(ChatColor.MAGIC, "obfuscated");
        styleNames.put(ChatColor.RED, "red");
        styleNames.put(ChatColor.STRIKETHROUGH, "strikethrough");
        styleNames.put(ChatColor.UNDERLINE, "underline");
        styleNames.put(ChatColor.WHITE, "white");
        styleNames.put(ChatColor.YELLOW, "yellow");

        Assert.assertEquals("All values (except reset) are covered",
                ChatColor.values().length - 1, styleNames.size());

        for (ChatColor color : styleNames.keySet()) {
            Assert.assertEquals(RawText.toStyleName(color), styleNames.get(color));
        }
    }

    @Test
    public void colorTest() {
        RawText text = new RawText("test")
                .color(ChatColor.RED);

        assertJson(text, "{\"text\":\"test\", \"color\": \"red\"}");
    }

    @Test
    public void styleTest() {
        final String styleMessage =
                "{\"text\":\"test\", "
                        + "\"bold\": true, "
                        + "\"italic\": true, "
                        + "\"underlined\": true, "
                        + "\"strikethrough\": true, "
                        + "\"obfuscated\": true}";

        RawText text = new RawText("test")
                .style(ChatColor.BOLD)
                .style(ChatColor.ITALIC)
                .style(ChatColor.UNDERLINE)
                .style(ChatColor.STRIKETHROUGH)
                .style(ChatColor.MAGIC);

        assertJson(text, styleMessage);

        RawText text2 = new RawText("test")
                .style(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH, ChatColor.MAGIC);

        assertJson(text2, styleMessage);
    }

    @Test
    public void clickTest() throws URISyntaxException {
        final String urlTest =
                "{\"text\":\"test\", \"clickEvent\": {\"action\": \"open_url\", \"value\": \"https://www.zcraft.fr\"} }";
        final String commandTest =
                "{\"text\":\"test\", \"clickEvent\": {\"action\": \"run_command\", \"value\": \"/say hi\"} }";
        final String suggestTest =
                "{\"text\":\"test\", \"clickEvent\": {\"action\": \"suggest_command\", \"value\": \"hello\"} }";

        RawText textUrl = new RawText("test")
                .uri("https://www.zcraft.fr");
        assertJson(textUrl, urlTest);

        RawText textUrl2 = new RawText("test")
                .uri(new URI("https://www.zcraft.fr"));
        assertJson(textUrl2, urlTest);

        RawText textCommand = new RawText("test")
                .command("/say hi");
        assertJson(textCommand, commandTest);

        RawText textSuggest = new RawText("test")
                .suggest("hello");
        assertJson(textSuggest, suggestTest);
    }

    /*@Test
    public void hoverAchievementTest()
    {
       final String HOVER_ACHIEVEMENT_TEST = "{\"text\":\"test\",\"hoverEvent\":
       {\"action\":\"show_achievement\",\"value\":\"achievement.theEnd\"}}";
        RawText text = new RawText("test")
            .hover(Bukkit.getAdvancement(NamespacedKey.minecraft("END_PORTAL")));
        
        assertJSON(text, HOVER_ACHIEVEMENT_TEST);
    }
    
    @Test
    public void hoverStatisticTest()
    {
        final String HOVER_STATISTIC_TEST = "{\"text\":\"test\",\"hoverEvent\":
        {\"action\":\"show_achievement\",\"value\":\"stat.walkOneCm\"}}";
        RawText text = new RawText("test")
            .hover(Statistic.WALK_ONE_CM);
        
        assertJSON(text, HOVER_STATISTIC_TEST);
    }
    */

    @Test
    public void insertionTest() {
        final String insertionTest = "{\"text\":\"test\",\"insertion\":\"/say Hello\",\"bold\":false}";
        final RawText text = new RawText("test")
                .insert("/say Hello");

        assertJson(text, insertionTest);
    }

    @Test
    public void insertionWithOtherStylesTest() {
        final String insertionWithBoldTest = "{\"text\":\"test\",\"insertion\":\"/say Hello\",\"bold\":true}";
        final String insertionWithColorTest =
                "{\"text\":\"test\",\"insertion\":\"/say Hello\",\"color\":\"dark_green\"}";

        final RawText boldText = new RawText("test")
                .insert("/say Hello")
                .style(ChatColor.BOLD);

        final RawText colorText = new RawText("test")
                .insert("/say Hello")
                .style(ChatColor.DARK_GREEN);

        assertJson(boldText, insertionWithBoldTest);
        assertJson(colorText, insertionWithColorTest);
    }

    @Test
    public void mapDeleteTest() {
        final int mapTestId = 42;
        final String mapDeleteMessage = "{\"text\":\"You are going to delete \",\"extra\":[{\"text\":\"" + mapTestId
                + "\",\"color\":\"gold\"},{\"text\":\". Are you sure ? \",\"color\":\"white\"},"
                + "{\"text\":\"[Confirm]\", \"color\":\"green\", \"clickEvent\":"
                + "{\"action\":\"run_command\",\"value\":\"/maptool delete-noconfirm "
                + mapTestId + "\"}, "
                + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"This map will be deleted \","
                + "\"extra\":[{\"text\":\"forever\",\"color\":\"red\",\"bold\":true,\"italic\":true,"
                + "\"underlined\":true}, {\"text\":\" !\", \"underlined\":true}],\"underlined\":true}}}]}";


        RawText hoverText = new RawText("This map will be deleted ")
                .style(ChatColor.UNDERLINE)
                .then("forever")
                .style(ChatColor.RED, ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.BOLD)
                .then(" !")
                .style(ChatColor.UNDERLINE)
                .build();

        RawText text = new RawText("You are going to delete ")
                .then("" + mapTestId)
                .color(ChatColor.GOLD)
                .then(". Are you sure ? ")
                .color(ChatColor.WHITE)
                .then("[Confirm]")
                .color(ChatColor.GREEN)
                .hover(hoverText)
                .command("/maptool delete-noconfirm " + mapTestId)
                .build();

        assertJson(text, mapDeleteMessage);
    }

    @Test
    public void plainTextTest() {
        final String plainText = "Hello world !";

        RawText text = new RawText("Hello ")
                .then("world")
                .then(" !")
                .build();

        Assert.assertEquals(plainText, text.toPlainText());
    }

    @Test
    public void formattedTextTest() {
        final String formattedText = ChatColor.RED + "" + ChatColor.BOLD + "Hello " + ChatColor.RESET
                + ChatColor.BLUE + "world" + ChatColor.RESET;

        RawText text = new RawText("Hello ")
                .style(ChatColor.RED, ChatColor.BOLD)
                .then("world")
                .color(ChatColor.BLUE)
                .build();

        Assert.assertEquals(formattedText, text.toFormattedText());
    }

    @Test
    public void fromFormattedTextTest() {
        final String helloworldTest =
                "{\"text\":\"Hello\", \"color\":\"red\", \"extra\":[{\"text\":\" world !\", \"color\":\"green\"}]}";

        RawText text = RawText.fromFormattedString(ChatColor.RED + "Hello" + ChatColor.GREEN + " world !");

        assertJson(text, helloworldTest);
    }

    @Test
    public void fromFormattedTextWithBaseTest() {
        final String helloWorldTest =
                "{\"text\":\"\", \"color\":\"gold\", \"insertion\":\"test\", \"extra\":[{\"text\":\"Hello\", "
                        + "\"color\":\"red\"}, {\"text\":\" world !\", \"color\":\"green\"}]}";

        RawText text = RawText.fromFormattedString(ChatColor.RED + "Hello" + ChatColor.GREEN + " world !",
                new RawText().color(ChatColor.GOLD).insert("test"));

        assertJson(text, helloWorldTest);
    }
}
