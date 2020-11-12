package fr.zcraft.quartzlib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import fr.zcraft.ztoaster.Toaster;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * A simple base class for tests that just set up a Mock Bukkit server, without any plugin associated.
 *
 * This is useful for testing simple, non-plugin-related APIs (such as ItemStack) that however require a Server to be
 * instantiated.
 */
public abstract class MockedBukkitTest {
    protected ServerMock server;

    @BeforeEach
    public void setUp()
    {
        server = MockBukkit.mock();
    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }
}
