package fr.zcraft.quartzlib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.After;
import org.junit.Before;

/**
 * A simple base class for tests that just set up a Mock Bukkit server, without any plugin associated.
 * <p> This is useful for testing simple, non-plugin-related APIs (such as ItemStack) that however require a Server to
 * be instantiated.</p>
 */
public abstract class MockedBukkitTest {
    protected ServerMock server;

    @Before
    public void setup() {
        server = MockBukkit.mock();
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
