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

import intricateengineers.intricatemachinery.core.ModInfo;
import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.raytrace.RayTraceUtils;
import intricateengineers.intricatemachinery.api.module.ModelBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;


public abstract class Module implements ICapabilitySerializable<NBTTagCompound> {
    public static final Property PROPERTY = new Property();
    private final ResourceLocation name;
    private IMultipart container;
    private final ModelBase model;
    public byte posX, posY, posZ, rotation;
    public Set<HashMap<String, ?>> debugInfo;
    private List<AxisAlignedBB> selectionBoxes = new ArrayList<>();

    public Module(String name, ModelBase model) {
        this.name = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), name);
        this.model = model;
        this.debugInfo = initDebugInfo();
        setLocalPos(new Vec3d(8/16d, 8/16d, 8/16d), (byte) 2);
    }

    public World getWorld() {
        return getContainerMultipart() != null ? getContainerMultipart().getWorld() : null;
    }

    public BlockPos getPos() {

        return getContainerMultipart() != null ? getContainerMultipart().getPos() : null;
    }

    public IMultipart getContainerMultipart() {

        return container;
    }

    public void setContainer(IMultipart container) {

        this.container = container;
    }

    public ModelBase getModel() {
        return model;
    }

    public void setLocalPos(Vec3d localPos, byte rotation) {
        this.posX = (byte) (localPos.xCoord * 16f);
        this.posY = (byte) (localPos.yCoord * 16f);
        this.posZ = (byte) (localPos.zCoord * 16f);
        this.rotation = (byte) RandomUtils.nextInt(0, 3);
        this.debugInfo = initDebugInfo();
    }

    public ResourceLocation getType() {
        return name;
    }

    public RayTraceUtils.AdvancedRayTraceResultPart collisionRayTrace(Vec3d start, Vec3d end) {
        if (selectionBoxes.isEmpty())
        {
            addSelectionBoxes(selectionBoxes);
        }
        RayTraceUtils.AdvancedRayTraceResult result = RayTraceUtils.collisionRayTrace(getWorld(), getPos(), start, end, selectionBoxes);
        return result == null ? null : null; //TODO: Raytracing
        // new RayTraceUtils.AdvancedRayTraceResultPart(result, this);
    }

    /**
     * Adds the selection boxes used to ray trace this part.
     * Called only once when module is placed
     */
    public void addSelectionBoxes(List<AxisAlignedBB> list) {
        list.add(model.mainBox.toAABB(this.posX, this.posY, this.posZ));
    }

    //@Override
    public void addOcclusionBoxes(List<AxisAlignedBB> list) {
        list.add(model.mainBox.toAABB(this.posX, this.posY, this.posZ));
    }

    //@Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        //super.writeToNBT(tag);

        NBTTagCompound pos = new NBTTagCompound();
        pos.setByte("x", posX);
        pos.setByte("y", posY);
        pos.setByte("z", posZ);
        pos.setByte("rot", rotation);
        tag.setTag("module_pos", pos);

        return tag;
    }

    //@Override
    public void readFromNBT(NBTTagCompound tag) {
        //super.readFromNBT(tag);

        NBTTagCompound pos = tag.getCompoundTag("module_pos");
        this.posX = pos.getByte("x");
        this.posY = pos.getByte("y");
        this.posZ = pos.getByte("z");
        this.rotation = pos.getByte("rot");
    }

    //@Override
    public void writeUpdatePacket(PacketBuffer buf) {
        buf.setBytes(9384, new byte[]{posX, posY, posZ, rotation});
    }

    //@Override
    public void readUpdatePacket(PacketBuffer buf) {
        this.posX = buf.getBytes(9384, new byte[]{posX, posY, posZ, rotation}).getByte(0);
        this.posY = buf.getBytes(9384, new byte[]{posX, posY, posZ, rotation}).getByte(1);
        this.posZ = buf.getBytes(9384, new byte[]{posX, posY, posZ, rotation}).getByte(2);
        this.rotation = buf.getBytes(9384, new byte[]{posX, posY, posZ, rotation}).getByte(3);
    }

    //@Override
    public BlockStateContainer createBlockState() {

        return new ExtendedBlockState(MCMultiPartMod.multipart, new IProperty[0], new IUnlistedProperty[] {PROPERTY});
    }

    //@Override
    public IBlockState getActualState(IBlockState state) {

        return state;
    }

    //@Override
    public IBlockState getExtendedState(IBlockState state) {
        return ((IExtendedBlockState) state).withProperty(PROPERTY, this);
    }

    // TODO: Use SortedMap for correct ordering
    public Set<HashMap<String, ?>> getDebugInfo()
    {
        return debugInfo;
    }

    protected Set<HashMap<String, ?>> initDebugInfo() {
        // Name of the module
        HashMap<String, String> hashMapName = new HashMap<>(1);
        hashMapName.put("Name", name.getResourcePath());

        // Position in pixels in relation to current block
        HashMap<String, Byte> hashMapPosAndRot = new HashMap<>(4);
        hashMapPosAndRot.put("posX", this.posX);
        hashMapPosAndRot.put("posY", this.posY);
        hashMapPosAndRot.put("posZ", this.posZ);
        hashMapPosAndRot.put("rotation", this.rotation);

        Set<HashMap<String, ?>> debugInfo = new HashSet<>();
        debugInfo.add(hashMapName);
        debugInfo.add(hashMapPosAndRot);
        return debugInfo;
    }

    private static class Property implements IUnlistedProperty<Module> {

        @Override
        public String getName() {
            return "im_module";
        }

        @Override
        public boolean isValid(Module value) {
            return true;
        }

        @Override
        public Class<Module> getType() {
            return Module.class;
        }

        @Override
        public String valueToString(Module value) {
            return value.toString();
        }
    }
}
