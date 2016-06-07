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

package intricateengineers.intricatemachinery.api.module;

import intricateengineers.intricatemachinery.api.client.BakedModelFrame;
import intricateengineers.intricatemachinery.api.client.util.UV;
import intricateengineers.intricatemachinery.common.module.DummyModule;
import intricateengineers.intricatemachinery.common.module.FurnaceModule;
import intricateengineers.intricatemachinery.core.ModInfo;

import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.INormallyOccludingPart;
import mcmultipart.multipart.Multipart;
import mcmultipart.raytrace.RayTraceUtils;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.*;

/**
 * @author topisani
 */
public class MachineryFrame extends Multipart implements INormallyOccludingPart {

    public static final Property PROPERTY = new Property();
    public static final BlockModel MODEL = new Model();
    public static final ResourceLocation NAME = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "machinery_frame");
    private final Module[][][] modulePositions = new Module[16][16][16];
    public Set<Map<String, ?>> debugInfo;
    private List<AxisAlignedBB> selectionBoxes = new ArrayList<>();
    private List<Module> modules = new ArrayList<>();

    public MachineryFrame() {
        modules.add(new FurnaceModule(this){{setLocalPos(new Vec3d(3 / 16f, 0, 1 / 16f), (byte) 1);}});
        modules.add(new DummyModule(this){{setLocalPos(new Vec3d(0, 5 / 16f, 1 / 16f), (byte) 3);}});
    }

    public List<Module> getModules() {
        return modules;
    }

    public boolean addModule(Module module) {
        //TODO: check occlusion
        modules.add(module);
        return true;
    }

    @Override
    public ResourceLocation getType() {
        return NAME;
    }

    @Override
    public RayTraceUtils.AdvancedRayTraceResultPart collisionRayTrace(Vec3d start, Vec3d end) {
        if (selectionBoxes.isEmpty()) {
            addSelectionBoxes(selectionBoxes);
        }
        RayTraceUtils.AdvancedRayTraceResult result = RayTraceUtils.collisionRayTrace(getWorld(), getPos(), start, end, selectionBoxes);
        return result == null ? null : new RayTraceUtils.AdvancedRayTraceResultPart(result, this);
    }

    @Nullable
    public Module modleHit(Vec3d start, Vec3d end) {
        Vec3d framePos = new Vec3d(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
        start = start.add(framePos);
        end = end.add(framePos);
        for (Module module : this.modules) {
            for (AxisAlignedBB bounds : module.getAABBs()) {
                RayTraceResult rt = bounds.offset(module.posX / 16f, module.posY / 16f, module.posZ / 16f).calculateIntercept(start, end);
                if (rt != null) {
                    return module;
                }
            }
        }
        return null;
    }

    /**
     * Adds the selection boxes used to ray trace this part.
     * Called only once when module is placed
     */
    @Override
    public void addSelectionBoxes(List<AxisAlignedBB> list) {
        MODEL.getBoxes().forEach((box) -> list.add(box.toAABB(0, 0, 0)));
        modules.forEach((module) -> module.addSelectionBoxes(list));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagCompound modules = new NBTTagCompound();

        for (int i = 0; i < this.modules.size(); i++) { 
            modules.setTag(String.valueOf(i), this.modules.get(i).serializeNBT());
        }

        tag.setTag("modules", modules);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        
        NBTTagList modules = tag.getTagList("modules", 0);

        for (int i = 0; i < modules.tagCount(); i++) {
            //this.modules.add(modules.get(i).
            //TODO: read module
        }

    }

    @Override
    public void writeUpdatePacket(PacketBuffer buf) {
        // TODO: Write modules to packets
    }

    @Override
    public void readUpdatePacket(PacketBuffer buf) {
        // TODO: Read modules from packets
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new ExtendedBlockState(MCMultiPartMod.multipart, new IProperty[0], new IUnlistedProperty[] {PROPERTY});
    }

    @Override
    public IBlockState getActualState(IBlockState state) {

        return state;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state) {
        return ((IExtendedBlockState) state).withProperty(PROPERTY, this);
    }

    @Override
    public void addOcclusionBoxes(List<AxisAlignedBB> list) {
        // TODO: Add boxes from all modules
        list.add(MODEL.mainBox.toAABB(0, 0, 0));
    }

    public Set<Map<String, ?>> getDebugInfo()
    {
        return debugInfo;
    }

    private static class Property implements IUnlistedProperty<MachineryFrame> {

        @Override
        public String getName() {
            return "machinery_frame";
        }

        @Override
        public boolean isValid(MachineryFrame value) {
            return true;
        }

        @Override
        public Class<MachineryFrame> getType() {
            return MachineryFrame.class;
        }

        @Override
        public String valueToString(MachineryFrame value) {
            return value.toString();
        }
    }

    private static class Model extends BlockModel {

        @Override
        public void init() {
            ResourceLocation frameTexture = new ResourceLocation("minecraft", "blocks/furnace_top");
            // Corners
            addBox(vec(0, 0, 0), vec(1, 16, 1))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(0, 0, 15), vec(1, 16, 16))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(15, 0, 0), vec(16, 16, 1))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(15, 0, 15), vec(16, 16, 16))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            // Horizontals
            addBox(vec(1, 0, 0), vec(15, 1, 1))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(1, 15, 0), vec(15, 16, 1))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(0, 0, 1), vec(1, 1, 15))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(0, 15, 1), vec(1, 16, 15))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(15, 0, 1), vec(16, 1, 15))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(15, 15, 1), vec(16, 16, 15))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(1, 0, 15), vec(15, 1, 16))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
            addBox(vec(1, 15, 15), vec(15, 16, 16))
                .setFace(NORTH, frameTexture, UV.auto(16))
                .setFace(EAST, frameTexture, UV.auto(16))
                .setFace(SOUTH, frameTexture, UV.auto(16))
                .setFace(WEST, frameTexture, UV.auto(16))
                .setFace(UP, frameTexture, UV.auto(16))
                .setFace(DOWN, frameTexture, UV.auto(16));
        }

        @Override
        public BakedModelFrame initBakedModel() {
            return new BakedModelFrame();
        }
    }
}
