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

import intricateengineers.intricatemachinery.api.module.IMModule;
import mcmultipart.client.multipart.MultipartSpecialRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

/**
 * @author topisani
 */
public class IMModuleSpecialRenderer extends MultipartSpecialRenderer<IMModule> {

    private final Minecraft mc = Minecraft.getMinecraft();
    private IBlockState baseState = null;
    private BlockModelRenderer renderer = null;
    private Tessellator tess = null;
    private VertexBuffer buff = null;

    @Override
    public void renderMultipartAt(IMModule part, double x, double y, double z, float partialTicks, int destroyStage) {
        if (this.baseState == null) {
            this.baseState = part.createBlockState().getBaseState();
            renderer = mc.getBlockRendererDispatcher().getBlockModelRenderer();
            tess = Tessellator.getInstance();
            buff = tess.getBuffer();
        }
        GL11.glPushMatrix();
        //This will move our renderer so that it will be on proper place in the world
        GL11.glTranslatef((float)x, (float)y, (float)z);
        BlockPos pos = part.getPos().add(part.posX, part.posY, part.posZ);
        //int l = part.getWorld().getCombinedLight(pos, 0);
        //int l1 = l % 65536;
        //int l2 = l / 65536;
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)l1, (float)l2);

        int dir = part.rotation;

        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 0, 0.5F);
        //This line actually rotates the renderer.
        GL11.glRotatef(dir * (-90F), 0F, 1F, 0F);
        GL11.glTranslatef(-0.5F, 0, -0.5F);
        GL11.glTranslatef(part.posX, part.posY, part.posZ);

        renderer.renderModel(part.getWorld(), part.getModel().getBakedModel(), part.getExtendedState(this.baseState), pos, buff, false, 52L );

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
