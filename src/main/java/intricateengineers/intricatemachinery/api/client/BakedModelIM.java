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

import intricateengineers.intricatemachinery.api.module.ModelBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class BakedModelIM implements IBakedModel {

    protected static final FaceBakery faceBakery = new FaceBakery();
    protected final ResourceLocation particle;
    protected final List<BakedQuad> quads = new ArrayList<>();
    protected ModelBase model;

    public BakedModelIM(ModelBase model) {
        this.model = model;
        this.particle = TextureMap.LOCATION_MISSING_TEXTURE;
    }

    public void initQuads() {
        quads.clear();
        for (ModelBase.Box box : model.getBoxes()) {
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
        for (ModelBase.Box box : model.getBoxes()) {
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
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(particle.toString());
    }

    @Override
    @Deprecated
    @MethodsReturnNonnullByDefault
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}