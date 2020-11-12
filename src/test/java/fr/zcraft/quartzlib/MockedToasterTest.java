package fr.zcraft.quartzlib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import fr.zcraft.ztoaster.Toaster;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class MockedToasterTest {
    protected ServerMock server;
    protected Toaster plugin;

    @BeforeEach
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Toaster.class);
    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }
}
