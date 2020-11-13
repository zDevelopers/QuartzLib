package fr.zcraft.quartzlib.core;

import fr.zcraft.quartzlib.MockedToasterTest;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertSame;

public class QuartzLibTest extends MockedToasterTest {
    @Test
    public void getPluginTest() {
        assertSame(plugin, QuartzLib.getPlugin());
    }
}
