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

package intricateengineers.intricatemachinery.client.event;

import intricateengineers.intricatemachinery.api.module.MachineryFrame;
import intricateengineers.intricatemachinery.api.module.Module;

import intricateengineers.intricatemachinery.api.util.Logger;
import mcmultipart.raytrace.PartMOP;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VelocityRa
 */
public class DebugRenderHandler {
    public static DebugRenderHandler instance = new DebugRenderHandler();
    private static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT && event instanceof RenderGameOverlayEvent.Text
                && mc.gameSettings.showDebugInfo) {
            RenderGameOverlayEvent.Text ev = (RenderGameOverlayEvent.Text) event;
            RayTraceResult hit = mc.objectMouseOver;
            try {
                PartMOP mop = (PartMOP) hit;
                if (mop.partHit instanceof MachineryFrame) {
                    ev.getLeft().add(TextFormatting.BOLD.toString() + TextFormatting.GREEN + "[Intricate Machinery]");

                    Vec3d eyes = mc.thePlayer.getPositionEyes(1);
                    Module module = ((MachineryFrame) mop.partHit).moduleHit(eyes, eyes.add(mc.thePlayer.getLookVec()));

                    if (module != null) {
                        for (HashMap<String, ?> hashMap : module.getDebugInfo())
                            for (Map.Entry<String, ?> entry : hashMap.entrySet()) {
                                ev.getLeft().add(entry.getKey() + ": " + TextFormatting.GREEN + entry.getValue());
                            }
                    }
                }
            }
            catch(ClassCastException e)
            {
                e.printStackTrace();
            }
        }
    }
}

