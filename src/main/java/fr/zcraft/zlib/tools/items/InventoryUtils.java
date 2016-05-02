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

package fr.zcraft.zlib.tools.items;

import fr.zcraft.zlib.tools.runners.RunTask;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This class provides various utilities for inventory management.
 */
abstract public class InventoryUtils 
{
    private InventoryUtils() {}
    
    /**
     * Checks if these inventories are equal.
     *
     * @param inventory1 The first inventory.
     * @param inventory2 The other inventory.
     * @return {@code true} if the two inventories are the same one.
     */
    static public boolean sameInventories(Inventory inventory1, Inventory inventory2)
    {
        if (inventory1 == inventory2)
        {
            return true;
        }

        if (inventory1 == null || inventory2 == null
                || inventory1.getSize() != inventory2.getSize()
                || inventory1.getType() != inventory2.getType()
                || !inventory1.getName().equals(inventory2.getName())
                || !inventory1.getTitle().equals(inventory2.getTitle()))
        {
            return false;
        }

            for (int slot = 0; slot < inventory1.getSize(); slot++)
            {
                ItemStack item1 = inventory1.getItem(slot);
                ItemStack item2 = inventory2.getItem(slot);

                if (item1 == null || item2 == null)
                {
                    if (item1 == item2)
                    {
                        continue;
                    }
                    else
                    {
                        return false;
                    }
                }

                if (!item1.equals(item2))
                {
                    return false;
                }
            }

            return true;
        
    }
    
    static public void updateInventoryLater(final Inventory inventory)
    {
        RunTask.nextTick(new UpdateInventoryTask(inventory));
    }
    
    static private class UpdateInventoryTask implements Runnable
    {
        private final Inventory inventory;
        
        public UpdateInventoryTask(Inventory inventory)
        {
            this.inventory = inventory;
        }

        @Override
        public void run()
        {
            for(HumanEntity entity : inventory.getViewers())
            {
                if(entity instanceof Player)
                {
                    ((Player)entity).updateInventory();
                }
            }
        }
    }
}
