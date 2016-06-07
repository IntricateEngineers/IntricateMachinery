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

import intricateengineers.intricatemachinery.api.module.BlockModel.IMBakedModel;
import intricateengineers.intricatemachinery.api.module.MachineryFrame;
import intricateengineers.intricatemachinery.api.module.ModelBase;
import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author topisani
 */
public class BakedModelFrame implements IMBakedModel {

    protected final List<BakedQuad> quads = new ArrayList<>();

    public BakedModelFrame() {
    }

    public void initQuads() {
        quads.clear();
        for (ModelBase.Box box : MachineryFrame.MODEL.getBoxes()) {
            for (EnumFacing face : EnumFacing.values()) {
                Pair<Vector3f, Vector3f> vecs = box.getFace(face);
                if (vecs.getLeft() == null) {
                    continue;
                }
                TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(box.faces.get(face).getLeft().toString());
                BlockPartFace partFace = new BlockPartFace(null, 0, "", box.faces.get(face).getRight());
                ModelRotation mr = ModelRotation.X0_Y0;
                BlockPartRotation blockPartRotation = null;
                BakedQuad quad = QuadHandler.faceBakery.makeBakedQuad(vecs.getLeft(), vecs.getRight(), partFace, texture, face, mr, blockPartRotation, true, true);
                quads.add(quad);
            }
        }
    }

    public void initTextures() {
        for (ModelBase.Box box : MachineryFrame.MODEL.getBoxes()) {
            for (EnumFacing face : EnumFacing.values()) {
                if (box.faces.get(face) != null) {
                    Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(box.faces.get(face).getLeft());
                }
            }
        }
    }
    
    // TODO: Cache the displaced quads so this doesn't run for every side on placement. Hack it up for now (it works)
    @Override
    @MethodsReturnNonnullByDefault
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        // Hack in question. Only run when when side is null (ie. once for each box)
        if (side != null) {
            return new ArrayList<>();
        }
        if (state instanceof IExtendedBlockState) {
            MachineryFrame frame = ((IExtendedBlockState) state).getValue(MachineryFrame.PROPERTY);
            if (frame != null) {
                List<BakedQuad> quads1 = new ArrayList<>();
                quads1.addAll(quads);
                frame.getModules().forEach((module) -> quads1.addAll(module.getModel().getQuadHandler().getQuads(frame, module, rand)));
                return quads1;
            }
            return quads;
        }
        else
            return new ArrayList<>();
    }

    @Override
    public boolean isAmbientOcclusion() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isGui3d() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemOverrideList getOverrides() {
        // TODO Auto-generated method stub
        return null;
    }
}
