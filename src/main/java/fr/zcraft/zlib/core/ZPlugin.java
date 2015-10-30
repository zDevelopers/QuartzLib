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
package fr.zcraft.zlib.core;

import fr.zcraft.zlib.ZLib;
import fr.zcraft.zlib.tools.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * The base class of any plugin using the ZLib.
 *
 * To use the ZLib, you have to use this class instead of {@link JavaPlugin}, and to add calls to
 * the {@code super} methods of {@link JavaPlugin#onEnable()} and {@link JavaPlugin#onDisable()} (if
 * you use them).
 */
public abstract class ZPlugin extends JavaPlugin
{

	@Override
	public void onEnable()
	{
		ZLib.init(this);
	}

	/**
	 * Load the given ZLib's components.
	 *
	 * @param components The base classes of the components to load.
	 */
	@SafeVarargs
	public final void loadComponents(Class<? extends ZLibComponent>... components)
	{
		for (Class<? extends ZLibComponent> componentClass : components)
		{
			try
			{
				ZLib.loadComponent(componentClass.newInstance());
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				PluginLogger.error("Cannot instantiate the ZLib component " + componentClass.getName(), e);
			}
		}
	}

	@Override
	public void onDisable()
	{
		ZLib.unloadComponents();
	}
}
