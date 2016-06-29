package intricateengineers.intricatemachinery.api.util

import net.minecraft.client.Minecraft
import net.minecraft.crash.CrashReport
import net.minecraftforge.fml.common.FMLLog
import org.apache.logging.log4j.Level

object Logger {
  def debug(format: String, data: Any*) {
    if (true) FMLLog.log(Level.INFO, "[IM-DEBUG] " + format, data)
  }

  def info(format: String, data: Any*) {
    FMLLog.log(Level.INFO, "[IM-INFO] " + format, data)
  }

  def warn(format: String, data: Any*) {
    FMLLog.log(Level.WARN, "[IM-WARN] " + format, data)
  }

  def error(format: String, data: Any*) {
    FMLLog.log(Level.ERROR, "[IM-ERROR] " + format, data)
  }

  def fatal(throwable: Throwable, message: String, data: Any*) {
    FMLLog.log(Level.FATAL, "[IM-FATAL] " + message, data)
    Minecraft.getMinecraft.crashed(new CrashReport(message, throwable))
  }
}