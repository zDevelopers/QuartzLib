/*
 * Copyright (C) 2013 Moribus
 * Copyright (C) 2015 ProkopyL <prokopylmc@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.zcraft.zlib;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

abstract public class PluginLogger 
{
    static private Thread mainThread;
    static private HashMap<Thread, PluginThreadLogger> loggers;
    
    static public void init()
    {
        mainThread = Thread.currentThread();
        loggers = new HashMap<>();
    }
    
    static public void exit()
    {
        mainThread = null;
        loggers = null;
    }
    
    static public void log(Level level, String message, Throwable ex)
    {
        getLogger().log(level, message, ex);
    }
    
    static public void log(Level level, String message, Object...args)
    {
        getLogger().log(level, message, args);
    }
        
    static public void log(Level level, String message, Throwable ex, Object... args)
    {
        log(level, message, args);
        log(level, "Exception : ", ex);
    }
    
    static public void info(String message, Object...args)
    {
        log(Level.INFO, message, args);
    }
    
    static public void warning(String message, Object... args)
    {
        log(Level.WARNING, message, args);
    }
    
    static public void warning(String message, Throwable ex)
    {
        warning(message + " : " + ex.getMessage());
    }
    
    static public void error(String message)
    {
        log(Level.SEVERE, message);
    }
    
    static public void error(String message, Throwable ex)
    {
        log(Level.SEVERE, message, ex);
    }
    
    static public void error(String message, Throwable ex, Object... args)
    {
        log(Level.SEVERE, message, ex, args);
    }
    
    static private Logger getLogger()
    {
        Thread currentThread = Thread.currentThread();
        if(currentThread.equals(mainThread)) return ZLib.getPlugin().getLogger();
        return getLogger(currentThread);
    }
    
    static private Logger getLogger(Thread thread)
    {
        PluginThreadLogger logger = loggers.get(thread);
        if(logger == null)
        {
            logger = new PluginThreadLogger(thread);
            loggers.put(thread, logger);
        }
        return logger;
    }
    
    static private class PluginThreadLogger extends Logger
    {
        private final String loggerName;
        public PluginThreadLogger(Thread thread)
        {
            super(ZLib.getPlugin().getClass().getCanonicalName(), null);
            setParent(ZLib.getPlugin().getLogger());
            setLevel(Level.ALL);
            loggerName = "[" + thread.getName() + "] ";
        }
        
        @Override
        public void log(LogRecord logRecord) 
        {
            logRecord.setMessage(loggerName + logRecord.getMessage());
            super.log(logRecord);
        }
    }
}
