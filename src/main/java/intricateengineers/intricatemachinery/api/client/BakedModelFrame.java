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
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author topisani
 */
public class BakedModelFrame extends BakedModelIM {

    public BakedModelFrame(ModelBase model) {
        super(model);
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
                frame.getModules().forEach((module) -> quads1.addAll(module.getModel().getBakedModel().getQuads(state, side, rand)));
                return quads1;
            }
        }
        return quads;
    }
}
