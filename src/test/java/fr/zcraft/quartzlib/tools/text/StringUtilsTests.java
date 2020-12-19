package fr.zcraft.quartzlib.tools.text;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class StringUtilsTests {
    @Test
    public void canComputeLevenshteinDistance() {
        Assertions.assertEquals(0, StringUtils.levenshteinDistance("foo", "foo"));
        Assertions.assertEquals(1, StringUtils.levenshteinDistance("fooa", "foo"));
        Assertions.assertEquals(1, StringUtils.levenshteinDistance("foo", "fooa"));
        Assertions.assertEquals(1, StringUtils.levenshteinDistance("fao", "foo"));
        Assertions.assertEquals(6, StringUtils.levenshteinDistance("fooaaaaaa", "foo"));
        Assertions.assertEquals(2, StringUtils.levenshteinDistance("f", "foo"));
        Assertions.assertEquals(3, StringUtils.levenshteinDistance("a", "foo"));
    }

    @Test
    public void canFindLevenshteinNearest() {
        List<String> candidates = Arrays.asList("add", "list", "open");

        Assertions.assertEquals("add", StringUtils.levenshteinNearest("foo", candidates, 10));
        Assertions.assertEquals("add", StringUtils.levenshteinNearest("adf", candidates, 10));
        Assertions.assertEquals("open", StringUtils.levenshteinNearest("openn", candidates, 10));
        Assertions.assertNull(StringUtils.levenshteinNearest("kkkkkkkkkkkkkkkk", candidates, 10));
    }
}
