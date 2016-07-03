package intricateengineers.intricatemachinery.client.event

import intricateengineers.intricatemachinery.api.module.{MachineryFrame, Module}
import mcmultipart.raytrace.PartMOP
import net.minecraft.client.Minecraft
import net.minecraft.util.math.{RayTraceResult, Vec3d}
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object DebugRenderHandler {
    var instance: DebugRenderHandler = new DebugRenderHandler
}

class DebugRenderHandler {
  final private val mc: Minecraft = Minecraft.getMinecraft

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  def onRenderGameOverlayEvent(event: RenderGameOverlayEvent) {
    if ((event.getType eq RenderGameOverlayEvent.ElementType.TEXT) && event.isInstanceOf[RenderGameOverlayEvent.Text] && mc.gameSettings.showDebugInfo) {
      val ev: RenderGameOverlayEvent.Text = event.asInstanceOf[RenderGameOverlayEvent.Text]
      val hit: RayTraceResult = mc.objectMouseOver
      try {
        val mop: PartMOP = hit.asInstanceOf[PartMOP]
        mop.partHit match {
          case multipartHit: MachineryFrame =>
            ev.getLeft.add("")
            ev.getLeft.add(TextFormatting.BOLD.toString + TextFormatting.GREEN + "[Intricate Machinery]")
            val moduleMaybe: Option[Module] = multipartHit.moduleHitFromEyes() // 5 is the range that AABBs get highlighted (in blocks)

            // Machine Frame debug info
            ev.getLeft.add(TextFormatting.GOLD + "Frame:")
            for (entry <- multipartHit.debugInfo())
              ev.getLeft.add("  " + entry._1 + ": " + TextFormatting.GREEN + entry._2)

            // Module debug info
            ev.getLeft.add(TextFormatting.GOLD + "Module:")
            moduleMaybe.foreach(module =>
              for (entry <- module.debugInfo())
                ev.getLeft.add("  " + entry._1 + ": " + TextFormatting.GREEN + entry._2))
        }
      }
      catch {
          case e: ClassCastException =>
      }
    }
  }
}
