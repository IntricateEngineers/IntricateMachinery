/*
 * Copyright (c) 2016 IntricateEngineers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
            val eyes: Vec3d = mc.thePlayer.getPositionEyes(1)
            val module: Module = multipartHit.moduleHit(eyes, eyes.add(mc.thePlayer.getLookVec.scale(5))) // the range that AABBs get highlighted (in blocks)

            // Machine Frame debug info
            ev.getLeft.add(TextFormatting.GOLD + "Frame:")
            for (entry <- multipartHit.debugInfo.get)
              ev.getLeft.add("  " + entry._1 + ": " + TextFormatting.GREEN + entry._2)

            // Module debug info
            ev.getLeft.add(TextFormatting.GOLD + "Module:")
            if (module != null)
              for (entry <- module.debugInfo.get)
                ev.getLeft.add("  " + entry._1 + ": " + TextFormatting.GREEN + entry._2)
            case _ =>
          }
      }
      catch {
          case e: ClassCastException =>
      }
    }
  }
}