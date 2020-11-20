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
package fr.zcraft.quartzlib.components.worker;

import fr.zcraft.quartzlib.core.QuartzComponent;
import fr.zcraft.quartzlib.core.QuartzLib;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * The base class for workers.
 * A worker is a thread that can handle multiple tasks, which are executed in a queue.
 * 
 */
public class Worker extends QuartzComponent implements ExecutorService {
    private ForkJoinPool forkJoinPool = null;
    private final int threadCount;

    public Worker () {
        this(0);
    }

    public Worker (int threadCount) {
        this.threadCount = threadCount;
        QuartzLib.loadComponent(this);
    }

    @Override
    protected void onEnable() {
        if (this.threadCount <= 0) {
            this.forkJoinPool = new ForkJoinPool();
        } else {
            this.forkJoinPool = new ForkJoinPool(threadCount);
        }
    }

    @Override
    protected void onDisable() {
        this.shutdownNow();
    }

    /* All of the overrides of the world */

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return forkJoinPool.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return forkJoinPool.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return forkJoinPool.invokeAll(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable task) {
        forkJoinPool.execute(task);
    }

    @Override
    public <T> ForkJoinTask<T> submit(Callable<T> task) {
        return forkJoinPool.submit(task);
    }

    @Override
    public <T> ForkJoinTask<T> submit(Runnable task, T result) {
        return forkJoinPool.submit(task, result);
    }

    @Override
    public ForkJoinTask<?> submit(Runnable task) {
        return forkJoinPool.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
        return forkJoinPool.invokeAll(tasks);
    }

    @Override
    public void shutdown() {
        forkJoinPool.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return forkJoinPool.shutdownNow();
    }

    @Override
    public boolean isTerminated() {
        return forkJoinPool.isTerminated();
    }

    @Override
    public boolean isShutdown() {
        return forkJoinPool.isShutdown();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return forkJoinPool.awaitTermination(timeout, unit);
    }
}
