/*
 * Copyright or © or Copr. QuartzLib contributors (2015 - 2020)
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

package fr.zcraft.quartzlib.components.configuration;

import fr.zcraft.quartzlib.core.QuartzComponent;
import fr.zcraft.quartzlib.core.QuartzLib;
import fr.zcraft.quartzlib.tools.Callback;


public abstract class Configuration extends QuartzComponent {
    /* ===== Static API ===== */
    private static ConfigurationInstance instance;

    /**
     * Initializes the configuration based on a given class.
     */
    public static void init(final Class<?> configurationClass) {
        instance = new ConfigurationInstance(QuartzLib.getPlugin().getConfig());
        instance.init(configurationClass);
        instance.onEnable();
    }

    /**
     * Saves the config.yml configuration to the disk.
     */
    public static void save() {
        instance.save(true);
    }

    /**
     * Reloads the config.yml configuration from the disk.
     */
    public static void reload() {
        instance.reload(true);
    }

    /**
     * Registers a callback called when the configuration is updated someway.
     *
     * <p>Callbacks are not called when the configuration is reloaded fully.</p>
     *
     * @param callback The callback.
     */
    public static void registerConfigurationUpdateCallback(final Callback<ConfigurationItem<?>> callback) {
        instance.registerConfigurationUpdateCallback(callback);
    }

    @Override
    protected void onEnable() {
        init(this.getClass());
    }
}
