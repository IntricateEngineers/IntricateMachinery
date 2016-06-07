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

package intricateengineers.intricatemachinery.api.client;

import intricateengineers.intricatemachinery.api.module.MachineryFrame;
import mcmultipart.client.multipart.MultipartSpecialRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

/**
 * @author topisani
 */
public class FrameRenderer extends MultipartSpecialRenderer<MachineryFrame> {
    private final Minecraft mc = Minecraft.getMinecraft();
    private IBlockState baseState = null;
    private BlockModelRenderer renderer = null;

    @Override
    public void renderMultipartAt(MachineryFrame part, double x, double y, double z, float partialTicks, int destroyStage) {
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();

        VertexBuffer buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        renderMultipartFast(part, x, y, z, partialTicks, destroyStage, buffer);
        Tessellator.getInstance().draw();

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public void renderMultipartFast(MachineryFrame frame, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer) {
        if (this.baseState == null) {
            this.baseState = frame.createBlockState().getBaseState();
            renderer = mc.getBlockRendererDispatcher().getBlockModelRenderer();
        }
        if (frame == null) {
            return;
        }

        BlockPos pos = frame.getPos();
        buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
        renderer.renderModel(frame.getWorld(), MachineryFrame.MODEL.getBakedModel(), frame.getExtendedState(this.baseState), pos, buffer, false, 52L );
        buffer.setTranslation(0, 0, 0);
    }
}
