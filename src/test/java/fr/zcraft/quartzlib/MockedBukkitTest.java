package fr.zcraft.quartzlib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import fr.zcraft.ztoaster.Toaster;
import org.junit.After;
import org.junit.Before;

public abstract class MockedBukkitTest {
    protected ServerMock server;
    protected Toaster plugin;

    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Toaster.class);
    }

    @After
    public void tearDown()
    {
        MockBukkit.unload();
    }
}
