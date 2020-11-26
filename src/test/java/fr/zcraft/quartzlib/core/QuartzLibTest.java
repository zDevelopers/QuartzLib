package fr.zcraft.quartzlib.core;

import static org.junit.Assert.assertSame;

import fr.zcraft.quartzlib.MockedToasterTest;
import org.junit.Test;


public class QuartzLibTest extends MockedToasterTest {
    @Test
    public void getPluginTest() {
        assertSame(plugin, QuartzLib.getPlugin());
    }
}
