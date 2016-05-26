package intricateengineers.intricatemachinery.client.event;

import intricateengineers.intricatemachinery.api.module.IMModule;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
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
            if (hit != null && hit instanceof PartMOP) {
                PartMOP mop = (PartMOP) hit;
                if (mop.partHit != null) {
                    ev.getRight().add("");
                    //ev.getRight().add(mop.partHit.getType().toString());

                    for (HashMap<String, ?> hashMap : ((IMModule)mop.partHit).getDebugInfo()) {
                        for (Map.Entry<String, ?> entry: hashMap.entrySet()){
                            ev.getRight().add(entry.getKey() + ": " + TextFormatting.GREEN + entry.getValue());
                        }
                    }
                }
            }
        }
    }
}
