package io.github.kosmx.emotes.executor;

import java.util.logging.Level;

public interface Logger {
     default void log(Level level, String msg){
         log(level, msg, false);
     }

     default void log(Level level, String msg, boolean bl){
         if(bl || EmoteInstance.config != null && EmoteInstance.config.showDebug.get()){
            writeLog(level, msg);
         }
     }

     void writeLog(Level level, String msg);

     void log(Level level, String msg, Throwable exc);

    /**
     * Logs error with stacktrace if debug is enabled, else without
     * @param level level
     * @param msg message
     * @param exc exception
     */
     default void logDebug(Level level, String msg, Throwable exc){
         if (EmoteInstance.config != null && EmoteInstance.config.showDebug.get()) {
             log(level, msg, exc);
         } else {
             log(level, msg+": "+exc.getMessage());
         }
     }

}
