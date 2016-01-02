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
package fr.zcraft.zlib.components.worker;

import fr.zcraft.zlib.core.ZLib;
import fr.zcraft.zlib.core.ZLibComponent;
import fr.zcraft.zlib.tools.PluginLogger;
import fr.zcraft.zlib.tools.reflection.Reflection;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * The base class for workers.
 * A worker is a thread that can handle multiple tasks, which are executed in a queue.
 * 
 */
public abstract class Worker extends ZLibComponent
{
    /*===== Static API =====*/
    static private final HashMap<Class<? extends Worker>, Worker> runningWorkers = new HashMap();
    static private final HashMap<Class<? extends WorkerRunnable>, Worker> runnables = new HashMap();
    
    
    static protected <T> Future<T> submitToMainThread(Callable<T> callable)
    {
        return getCallerWorkerFromRunnable()._submitToMainThread(callable);
    }
    
    static protected void submitQuery(WorkerRunnable runnable)
    {
        getCallerWorker()._submitQuery(runnable);
    }
    
    static protected void submitQuery(WorkerRunnable runnable, WorkerCallback callback)
    {
        getCallerWorker()._submitQuery(runnable, callback);
    }
    
    static private Worker getCallerWorker()
    {
        Class<? extends Worker> caller = Reflection.getCallerClass(Worker.class);
        if(caller == null) 
            throw new IllegalAccessError("Queries must be submitted from a Worker class");
        
        return getWorker(caller);
    }
    
    static private Worker getWorker(Class<? extends Worker> workerClass)
    {
        Worker worker = runningWorkers.get(workerClass);
        
        if(worker == null)
            throw new IllegalStateException("Worker '" + workerClass.getName() + "' has not been correctly initialized");
        
        return worker;
    }
    
    static private Worker getCallerWorkerFromRunnable()
    {
        Class<? extends WorkerRunnable> caller = Reflection.getCallerClass(WorkerRunnable.class);
        if(caller == null) 
            throw new IllegalAccessError("Main thread queries must be submitted from a WorkerRunnable");
        
        Worker worker = runnables.get(caller);
        if(worker == null)
            throw new IllegalStateException("Caller runnable does not belong to any worker");
        
        return worker;
    }
    
    private final String name;
    private final ArrayDeque<WorkerRunnable> runQueue = new ArrayDeque<>();
    
    private final WorkerCallbackManager callbackManager;
    private final WorkerMainThreadExecutor mainThreadExecutor;
    private Thread thread;
    
    public Worker()
    {
        String tempName = null;
        WorkerAttributes attributes = getClass().getAnnotation(WorkerAttributes.class);
        
        if(attributes != null)
        {
            tempName = attributes.name();
            this.mainThreadExecutor = attributes.queriesMainThread() ? new WorkerMainThreadExecutor(tempName) : null;
        }
        else
        {
            this.mainThreadExecutor = null;
        }
        
        if(tempName == null || tempName.isEmpty())
            tempName = getClass().getSimpleName();
        
        this.name = tempName;
        this.callbackManager = new WorkerCallbackManager(tempName);
    }
    
    @Override
    public void onEnable()
    {
        if(thread != null && thread.isAlive())
        {
            PluginLogger.warning("Restarting thread '{0}'.", name);
            onDisable();
        }
        callbackManager.init();
        if(mainThreadExecutor != null) mainThreadExecutor.init();
        runningWorkers.put(getClass(), this);
        thread = createThread();
        thread.start();
    }
    
    @Override
    public void onDisable()
    {
        thread.interrupt();
        callbackManager.exit();
        if(mainThreadExecutor != null) mainThreadExecutor.exit();
        thread = null;
        runningWorkers.remove(getClass());
    }
    
    private void run()
    {
        WorkerRunnable currentRunnable;
        
        while(!Thread.interrupted())
        {
            synchronized(runQueue)
            {
                try
                {
                    while(runQueue.isEmpty()) runQueue.wait();
                }
                catch(InterruptedException ex)
                {
                    break;
                }
                currentRunnable = runQueue.pop();
            }
            
            try
            {
                callbackManager.callback(currentRunnable, currentRunnable.run());
            }
            catch(Throwable ex)
            {
                callbackManager.callback(currentRunnable, null, ex);
            }
            runnables.remove(currentRunnable.getClass());
        }
    }
    
    private void _submitQuery(WorkerRunnable runnable)
    {
        attachRunnable(runnable);
        synchronized(runQueue)
        {
            runQueue.add(runnable);
            runQueue.notify();
        }
    }
    
    private void _submitQuery(WorkerRunnable runnable, WorkerCallback callback)
    {
        callbackManager.setupCallback(runnable, callback);
        _submitQuery(runnable);
    }
    
    private <T> Future<T> _submitToMainThread(Callable<T> callable)
    {
        if(mainThreadExecutor != null) return mainThreadExecutor.submit(callable);
        return null;
    }
    
    private Thread createThread()
    {
        return new Thread(getName())
        {
            @Override
            public void run()
            {
                Worker.this.run();
            }
        };
    }
    
    private void attachRunnable(WorkerRunnable runnable)
    {
        if(runnable.getWorker() != null && runnable.getWorker() != this)
            throw new IllegalArgumentException("This runnable is already attached to another worker");
        runnable.setWorker(this);
        runnables.put(runnable.getClass(), this);
    }
    
    public String getName()
    {
        return ZLib.getPlugin().getName() + "-" + name;
    }
    
}
