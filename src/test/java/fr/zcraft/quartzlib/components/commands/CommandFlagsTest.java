/*
 * Copyright or © or Copr. AmauryCarrade (2015)
 *
 * http://amaury.carrade.eu
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package fr.zcraft.quartzlib.components.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

public class CommandFlagsTest {
    private final Method parseArgsMethod;

    public CommandFlagsTest() throws ReflectiveOperationException {
        parseArgsMethod =
                Command.class.getDeclaredMethod("parseArgs", String[].class, Set.class, List.class, Set.class);
        parseArgsMethod.setAccessible(true);
    }

    private void parseArgs(final String[] args, final Set<String> acceptedFlags, List<String> realArgs,
                           Set<String> flags) {
        try {
            parseArgsMethod.invoke(null, args, acceptedFlags, realArgs, flags);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Assert.fail("Cannot invoke parseArgs method");
        }
    }

    private void assertArgs(final String[] args, final String[] acceptedFlags, final String[] expectedArgs,
                            final String[] expectedFlags) {
        final Set<String> acceptedFlagsSet = acceptedFlags != null ? new HashSet<>(Arrays.asList(acceptedFlags)) : null;

        final List<String> actualArgs = new ArrayList<>(args.length);
        final Set<String> actualFlags = new HashSet<>();

        parseArgs(args, acceptedFlagsSet, actualArgs, actualFlags);

        final TreeSet<String> expectedFlagsSorted = new TreeSet<>(Arrays.asList(expectedFlags));
        final TreeSet<String> actualFlagsSorted = new TreeSet<>(actualFlags);

        Assert.assertEquals("Expected and actual arguments differs", StringUtils.join(expectedArgs, ","),
                StringUtils.join(actualArgs, ","));
        Assert.assertEquals("Expected and actual flags differs", StringUtils.join(expectedFlagsSorted, ","),
                StringUtils.join(actualFlagsSorted, ","));
    }

    @Test
    public void flagsDisabledTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "arg2"},
                null,
                new String[] {"arg0", "arg1", "arg2"},
                new String[] {}
        );
    }

    @Test
    public void flagsDisabledWithFlagLikeTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "-flag", "arg2", "--flag2"},
                null,
                new String[] {"arg0", "arg1", "-flag", "arg2", "--flag2"},
                new String[] {}
        );
    }

    @Test
    public void simpleFlagsTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "-f"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "-l"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"f", "l"}
        );
    }

    @Test
    public void simpleMultipleFlagsTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "-fl"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"f", "l"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-flop"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"f", "l", "o", "p"}
        );
    }

    @Test
    public void longFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--flop"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "--flop", "--pomf"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop", "pomf"}
        );
    }

    @Test
    public void longFlagWithDashTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--flop-pomf"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop-pomf"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "--flop-pomf", "--pomf"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop-pomf", "pomf"}
        );
    }

    @Test
    public void mixedFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--flop", "-fl", "--pomf"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop", "pomf", "f", "l"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "--flop", "--pomf", "-l"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop", "pomf", "f", "l"}
        );
    }

    @Test
    public void mixedCaseFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--FLOP", "-fL", "--poMf"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop", "pomf", "f", "l"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-F", "--flop", "--POMf", "-L"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop", "pomf", "f", "l"}
        );
    }

    @Test
    public void middleFlagTest() {
        assertArgs(
                new String[] {"arg0", "--flop", "-fl", "arg1", "--pomf"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop", "pomf", "f", "l"}
        );

        assertArgs(
                new String[] {"arg0", "-f", "--flop", "arg1", "--pomf", "-l"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"flop", "pomf", "f", "l"}
        );
    }

    @Test
    public void duplicatedFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--pomf", "--pomf"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"pomf"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "-f"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-ff"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );
    }

    @Test
    public void duplicatedMixedCaseFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--pomf", "--POMF"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"pomf"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "-F"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-fF"},
                new String[] {},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );
    }


    // Constrained


    @Test
    public void simpleConstrainedFlagsTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "-f"},
                new String[] {"f"},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "-l"},
                new String[] {"f"},
                new String[] {"arg0", "arg1", "-l"},
                new String[] {"f"}
        );
    }

    @Test
    public void simpleMultipleConstrainedFlagsTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "-fl"},
                new String[] {"f"},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-flop"},
                new String[] {"f", "o"},
                new String[] {"arg0", "arg1"},
                new String[] {"f", "o"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-fl"},
                new String[] {"k"},
                new String[] {"arg0", "arg1", "-fl"},
                new String[] {}
        );
    }

    @Test
    public void longConstrainedFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--flop"},
                new String[] {"flop"},
                new String[] {"arg0", "arg1"},
                new String[] {"flop"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "--flop", "--pomf"},
                new String[] {"pomf"},
                new String[] {"arg0", "arg1", "--flop"},
                new String[] {"pomf"}
        );
    }

    @Test
    public void mixedConstrainedFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--flop", "-fl", "--pomf"},
                new String[] {"flop", "f"},
                new String[] {"arg0", "arg1", "--pomf"},
                new String[] {"flop", "f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "--flop", "--pomf", "-l"},
                new String[] {"flop", "f"},
                new String[] {"arg0", "arg1", "--pomf", "-l"},
                new String[] {"flop", "f"}
        );
    }

    @Test
    public void mixedCaseConstrainedFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--FLOP", "-fL", "--poMf"},
                new String[] {"flop", "l"},
                new String[] {"arg0", "arg1", "--poMf"},
                new String[] {"flop", "l"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-F", "--flop", "--POMf", "-L"},
                new String[] {"flop", "l"},
                new String[] {"arg0", "arg1", "-F", "--POMf"},
                new String[] {"flop", "l"}
        );
    }

    @Test
    public void middleConstrainedFlagTest() {
        assertArgs(
                new String[] {"arg0", "--flop", "-fl", "arg1", "--pomf"},
                new String[] {"pomf", "l"},
                new String[] {"arg0", "--flop", "arg1"},
                new String[] {"pomf", "l"}
        );

        assertArgs(
                new String[] {"arg0", "-f", "--flop", "arg1", "--pomf", "-l"},
                new String[] {"pomf", "l"},
                new String[] {"arg0", "-f", "--flop", "arg1"},
                new String[] {"pomf", "l"}
        );
    }

    @Test
    public void duplicatedConstrainedFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--pomf", "--pomf"},
                new String[] {"pomf"},
                new String[] {"arg0", "arg1"},
                new String[] {"pomf"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "-f"},
                new String[] {"f"},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-ff"},
                new String[] {"f"},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "--pomf", "--pomf"},
                new String[] {"flop"},
                new String[] {"arg0", "arg1", "--pomf", "--pomf"},
                new String[] {}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "-f"},
                new String[] {"l"},
                new String[] {"arg0", "arg1", "-f", "-f"},
                new String[] {}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-ff"},
                new String[] {"l"},
                new String[] {"arg0", "arg1", "-ff"},
                new String[] {}
        );
    }

    @Test
    public void duplicatedMixedCaseConstrainedFlagTest() {
        assertArgs(
                new String[] {"arg0", "arg1", "--pomf", "--POMF"},
                new String[] {"pomf"},
                new String[] {"arg0", "arg1"},
                new String[] {"pomf"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "-F"},
                new String[] {"f"},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-fF"},
                new String[] {"f"},
                new String[] {"arg0", "arg1"},
                new String[] {"f"}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "--pomf", "--POMF"},
                new String[] {"flop"},
                new String[] {"arg0", "arg1", "--pomf", "--POMF"},
                new String[] {}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-f", "-F"},
                new String[] {"l"},
                new String[] {"arg0", "arg1", "-f", "-F"},
                new String[] {}
        );

        assertArgs(
                new String[] {"arg0", "arg1", "-fF"},
                new String[] {"l"},
                new String[] {"arg0", "arg1", "-fF"},
                new String[] {}
        );
    }
}
