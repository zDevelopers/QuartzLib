package fr.zcraft.ztoaster.core;

import static org.junit.Assert.assertSame;

import fr.zcraft.quartzlib.core.QuartzLib;
import fr.zcraft.ztoaster.MockedToasterTest;
import org.junit.Test;


public class QuartzLibTest extends MockedToasterTest {
    @Test
    public void getPluginTest() {
        assertSame(plugin, QuartzLib.getPlugin());
    }
}
