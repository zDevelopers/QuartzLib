/*
 * Copyright or © or Copr. ZLib contributors (2015)
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

package fr.zcraft.ztoaster.commands;

import fr.zcraft.quartzlib.components.commands.Command;
import fr.zcraft.quartzlib.components.commands.CommandException;
import fr.zcraft.quartzlib.components.commands.CommandInfo;
import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.ztoaster.Toast;
import fr.zcraft.ztoaster.Toaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@CommandInfo(name = "list", usageParameters = "[cooked|not_cooked]")
public class ListCommand extends Command
{
    @Override
    protected void run() throws CommandException
    {
        if(args.length == 0)
        {
            showToasts(Arrays.asList(Toaster.getToasts()));
        }
        else
        {
            ArrayList<Toast> toasts = new ArrayList<Toast>();
            Toast.CookingStatus status = getEnumParameter(0, Toast.CookingStatus.class);
            
            for(Toast toast : Toaster.getToasts())
            {
                if(toast.getStatus().equals(status))
                    toasts.add(toast);
            }
            
            showToasts(toasts);
        }
    }
    
    private void showToasts(Collection<Toast> toasts)
    {
        if(toasts.isEmpty())
        {
            // Output of the command /toaster list, without toasts.
            info(I.t("There are no toasts here ..."));
        }
        
        for(Toast toast : toasts)
        {
            sender.sendMessage(I.t("  Toast #{0}", toast.getToastId()));
        }
    }
}
