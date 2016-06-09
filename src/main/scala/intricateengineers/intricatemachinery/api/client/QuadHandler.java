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
import intricateengineers.intricatemachinery.api.module.ModelBase;
import intricateengineers.intricatemachinery.api.module.Module;
import intricateengineers.intricatemachinery.api.util.Logger;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class QuadHandler {

    protected static final FaceBakery faceBakery = new FaceBakery();
    protected final List<BakedQuad> quads = new ArrayList<>();
    protected ModelBase model;

    public QuadHandler(ModelBase model) {
        this.model = model;
    }

    public void initQuads() {
        quads.clear();
        for (ModelBase.Box box : this.model.getBoxes()) {
            for (EnumFacing face : EnumFacing.values()) {
                Pair<Vector3f, Vector3f> vecs = box.getFace(face);
                if (vecs.getLeft() == null) {
                    continue;
                }
                TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(box.faces.get(face).getLeft().toString());
                BlockPartFace partFace = new BlockPartFace(null, 0, "", box.faces.get(face).getRight());
                ModelRotation mr = ModelRotation.X0_Y0;
                BlockPartRotation blockPartRotation = null;
                BakedQuad quad = faceBakery.makeBakedQuad(vecs.getLeft(), vecs.getRight(), partFace, texture, face, mr, blockPartRotation, true, true);
                quads.add(quad);
            }
        }
    }

    public void initTextures() {
        for (ModelBase.Box box : this.model.getBoxes()) {
            for (EnumFacing face : EnumFacing.values()) {
                if (box.faces.get(face) != null) {
                    Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(box.faces.get(face).getLeft());
                }
            }
        }
    }

    @MethodsReturnNonnullByDefault
    public List<BakedQuad> getQuads(MachineryFrame frame, Module module, long rand) {

        if (module == null) {
            return this.quads;
        }

        int[] vertexData;
        List<BakedQuad> quads1 = new ArrayList<>();

        for (BakedQuad quad : quads) {
            vertexData = quad.getVertexData().clone();
            for (int i = 0; i < 4 * 7; i += 7)
            {
                float xFloat = Float.intBitsToFloat(vertexData[i]);
                float yFloat = Float.intBitsToFloat(vertexData[i+1]);
                float zFloat = Float.intBitsToFloat(vertexData[i+2]);

                vertexData[i] = Float.floatToRawIntBits((xFloat)+(module.posX / 16f));
                vertexData[(i)+1] = Float.floatToRawIntBits((yFloat)+(module.posY / 16f));
                vertexData[(i)+2] = Float.floatToRawIntBits((zFloat)+(module.posZ / 16f));
            }
            BakedQuad quad1 = new BakedQuad(vertexData, quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), DefaultVertexFormats.ITEM);
            quads1.add(quad1);
        }

        return quads1; 
    }
}