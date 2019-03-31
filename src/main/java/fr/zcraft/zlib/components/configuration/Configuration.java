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

package fr.zcraft.zlib.components.configuration;

import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.core.ZLibComponent;
import fr.zcraft.zlib.tools.Callback;


public abstract class Configuration extends ZLibComponent
{
    /* ===== Static API ===== */
    static private ConfigurationInstance instance;
    
    @Override
    protected void onEnable() 
    {
        init(this.getClass());
    }
    @SuppressWarnings("rawtypes")
    static public void init(Class configurationClass)
    {
        instance = new ConfigurationInstance(ZLib.getPlugin().getConfig());
        instance.init(configurationClass);
        instance.onEnable();
    }

    /**
     * Saves the config.yml configuration to the disk.
     */
    static public void save()
    {
        instance.save(true);
    }

    /**
     * Reloads the config.yml configuration from the disk.
     */
    static public void reload()
    {
        instance.reload(true);
    }

    /**
     * Registers a callback called when the configuration is updated someway.
     *
     * Callbacks are not called when the configuration is reloaded fully.
     *
     * @param callback The callback.
     */
    static public void registerConfigurationUpdateCallback(Callback<ConfigurationItem<?>> callback)
    {
        instance.registerConfigurationUpdateCallback(callback);
    }
}
