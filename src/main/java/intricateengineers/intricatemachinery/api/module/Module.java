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
import mcmultipart.multipart.IMultipart;
import mcmultipart.raytrace.RayTraceUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
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

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        NBTTagCompound pos = new NBTTagCompound();
        pos.setByte("x", posX);
        pos.setByte("y", posY);
        pos.setByte("z", posZ);
        pos.setByte("rot", rotation);
        tag.setTag("module_pos", pos);

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        NBTTagCompound pos = tag.getCompoundTag("module_pos");
        this.posX = pos.getByte("x");
        this.posY = pos.getByte("y");
        this.posZ = pos.getByte("z");
        this.rotation = pos.getByte("rot");
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

    /**
     * Determines if this object has support for the capability in question on the specific side.
     * The return value of this MIGHT change during runtime if this object gains or looses support
     * for a capability.
     * <p>
     * Example:
     * A Pipe getting a cover placed on one side causing it loose the Inventory attachment function for that side.
     * <p>
     * This is a light weight version of getCapability, intended for metadata uses.
     * @param capability The capability to check
     * @param facing     The Side to check from:
     *                   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @return True if this object supports the capability.
     */
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return false;
    }

    /**
     * Retrieves the handler for the capability requested on the specific side.
     * The return value CAN be null if the object does not support the capability.
     * The return value CAN be the same for multiple faces.
     * @param capability The capability to check
     * @param facing     The Side to check from:
     *                   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @return True if this object supports the capability.
     */
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return null;
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
