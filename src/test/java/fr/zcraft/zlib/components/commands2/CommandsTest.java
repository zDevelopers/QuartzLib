/*
 * Copyright or © or Copr. ZLib contributors (2015 - 2016)
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

package fr.zcraft.zlib.components.commands2;

import fr.zcraft.zlib.components.commands2.bb.BBCommand;
import fr.zcraft.zlib.components.commands2.exceptions.CommandException;
import fr.zcraft.zlib.components.commands2.iom.CreateCommand;
import fr.zcraft.zlib.components.commands2.iom.IoMCommand;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class CommandsTest
{
    static private final CommandSender sender = new CommandSender();
    @Test
    public void iomTest() throws CommandException, URISyntaxException {
        Commands.register(IoMCommand.class, "maptool");
        Commands.register(CreateCommand.class, "tomap");

        Context<?> mapToolContext = Commands.makeContext("maptool", sender, new String[]{"list"});
        Assert.assertEquals(Optional.of(IoMCommand.LIST), mapToolContext.getParentContext().map(Context::getCommandRunnable));
        Assert.assertTrue(mapToolContext.getCommandRunnable() instanceof IoMCommand.ListCommand);

        Context<?> createContext = Commands.makeContext("maptool", sender, new String[]{"create", "http://example.com/test.png", "-w", "42", "--stretch"});
        Assert.assertTrue(createContext.getCommandRunnable() instanceof CreateCommand);
        CreateCommand createRunnable = (CreateCommand) createContext.getCommandRunnable();

        Assert.assertEquals(new URI("http://example.com/test.png"), createRunnable.imageURI);
        Assert.assertEquals(Optional.of(42), createRunnable.width);
        Assert.assertEquals(Optional.empty(), createRunnable.height);
        Assert.assertEquals(true, createRunnable.stretch);
        Assert.assertEquals(false, createRunnable.cover);

    }

    @Test
    public void bbTest() throws CommandException {
        Commands.registerParameterTypeConverter(new BBCommand.BBItemParamConverter());
        Commands.register(BBCommand.class, "bb");

        Context<?> bbContext = Commands.makeContext("bb", sender, new String[]{"saw"});
        Assert.assertTrue(bbContext.getCommandRunnable() instanceof BBCommand);
        BBCommand bbRunnable = (BBCommand) bbContext.getCommandRunnable();
        Assert.assertEquals("saw", bbRunnable.item.itemType);
        Assert.assertEquals(Optional.empty(), bbRunnable.amount);
    }
}
