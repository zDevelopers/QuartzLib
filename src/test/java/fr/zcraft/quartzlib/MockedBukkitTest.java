package fr.zcraft.quartzlib;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * A simple base class for tests that just set up a Mock Bukkit server, without any plugin associated.
 * <p> This is useful for testing simple, non-plugin-related APIs (such as ItemStack) that however require a Server to
 * be instantiated.</p>
 */
public abstract class MockedBukkitTest {
    protected final ServerMock server = MockBukkit.mock();

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
