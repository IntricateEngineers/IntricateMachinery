package intricateengineers.intricatemachinery.api.util;

import intricateengineers.intricatemachinery.core.IntricateMachinery;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public class Logger {

    public static void debug(String format, Object... data) {
        if (true) FMLLog.log(Level.INFO, "[IM-DEBUG] " + format, data);
    }

    public static void info(String format, Object... data) {
        FMLLog.log(Level.INFO, "[IM-INFO] " + format, data);
    }

    public static void warn(String format, Object... data) {
        FMLLog.log(Level.WARN, "[RTG-WARN] " + format, data);
    }

    public static void error(String format, Object... data) {
        FMLLog.log(Level.ERROR, "[IM-ERROR] " + format, data);
    }

    public static void fatal(Throwable throwable, String message, Object... data) {
        FMLLog.log(Level.FATAL, "[IM-FATAL] " + message, data);
        Minecraft.getMinecraft().crashed(new CrashReport(message, throwable));
    }
}