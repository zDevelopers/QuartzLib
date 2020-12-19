package fr.zcraft.quartzlib.components.commands;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DiscoveryUtilsTests {
    @Test
    public void canGenerateArgumentNames() throws NoSuchMethodException {
        class Foo {
            public void add(int foo1, String foo2) {

            }

            public void add(String foo1, String foo2) {

            }
        }

        Method addMethod = Foo.class.getMethod("add", int.class, String.class);
        Assertions.assertEquals("int",
                DiscoveryUtils.generateArgumentName(addMethod, addMethod.getParameters()[0]));
        Assertions.assertEquals("string",
                DiscoveryUtils.generateArgumentName(addMethod, addMethod.getParameters()[1]));

        Method add2Method = Foo.class.getMethod("add", String.class, String.class);
        Assertions.assertEquals("string1",
                DiscoveryUtils.generateArgumentName(add2Method, add2Method.getParameters()[0]));
        Assertions.assertEquals("string2",
                DiscoveryUtils.generateArgumentName(add2Method, add2Method.getParameters()[1]));
    }
}
