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


package fr.zcraft.ztoaster;

import fr.zcraft.quartzlib.components.gui.Gui;
import fr.zcraft.quartzlib.components.i18n.I;
import fr.zcraft.quartzlib.components.worker.Worker;
import fr.zcraft.quartzlib.components.worker.WorkerCallback;
import fr.zcraft.quartzlib.components.worker.WorkerRunnable;
import fr.zcraft.quartzlib.tools.PluginLogger;
import org.bukkit.entity.Player;

public class ToasterWorker extends Worker
{
    /**
     * Optimal cooking time for making carefully baked toasts.
     */
    static public int TOAST_COOKING_TIME = 4269;
    
    static public Toast addToast(final Player cook)
    {
        return ToasterWorker.addToast(new WorkerCallback<Integer>()
        {
            @Override
            public void finished(Integer toastId)
            {
                I.sendT(cook, "DING! Toast {0} is ready !", toastId);
                Gui.update(ToastExplorer.class);
            }

            @Override
            public void errored(Throwable exception)
            {
                PluginLogger.error("Error while toasting", exception);
                I.sendT(cook, "{ce}Oh no! A toasted exception !");
                I.sendT(cook, "{ce}See toaster logs for details.");
            }
        });
    }
    
    /**
     * Creates a new toast, and adds it to the toaster queue.
     * @param callback The callback to call when a toast is cooked.
     * @return the newly created toast.
     */
    static public Toast addToast(WorkerCallback<Integer> callback)
    {
        final Toast newToast = Toaster.newToast();
        final int toastId = newToast.getToastId();
        
        submitQuery(new WorkerRunnable()
        {
            @Override
            public Object run() throws Throwable
            {
                PluginLogger.info("Cooking toast #{0} ...", toastId);
                
                Thread.sleep(TOAST_COOKING_TIME);
                
                PluginLogger.info("Toast #{0} cooked !", toastId);
                
                newToast.setStatus(Toast.CookingStatus.COOKED);
                
                return toastId;
            }
        }, callback);
        
        return newToast;
    }
}
