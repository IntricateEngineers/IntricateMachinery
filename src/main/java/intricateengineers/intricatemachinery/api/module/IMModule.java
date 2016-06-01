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
import mcmultipart.multipart.Multipart;
import mcmultipart.raytrace.RayTraceUtils;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;

/**
 * @author topisani
 */
public abstract class IMModule extends Multipart {
    public static final Property PROPERTY = new Property();
    private final ResourceLocation name;
    private final IMModel model;
    public byte posX, posY, posZ, rotation;
    private List<AxisAlignedBB> selectionBoxes = new ArrayList<>();

    public IMModule(String name, IMModel model) {
        this.name = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), name);
        this.model = model;
    }

    public IMModel getModel() {
        return model;
    }

    public void setLocalPos(Vec3d localPos, byte rotation) {
        this.posX = (byte) (localPos.xCoord * 16f);
        this.posY = (byte) (localPos.yCoord * 16f);
        this.posZ = (byte) (localPos.zCoord * 16f);
        this.rotation = rotation;
    }

    // For debugging only, probably won't be very useful in practice
    // since parts can take many sizes inside the same machine frame
    public void setLocalPosCenteredSnapped(Vec3d localPos, byte rotation) {

        Vector3f modelSize = model.getMainBox().getSize();

        // Center
        this.posX -= modelSize.x/2;
        this.posY -= modelSize.y/2;
        this.posZ -= modelSize.z/2;

        // Snap
        this.posX = (byte) (((int)(localPos.xCoord * 16) / (int)modelSize.x) * modelSize.x);
        this.posY = (byte) (((int)(localPos.yCoord * 16) / (int)modelSize.y) * modelSize.y);
        this.posZ = (byte) (((int)(localPos.zCoord * 16) / (int)modelSize.z) * modelSize.z);
        this.rotation = rotation;
    }

    @Override
    public ResourceLocation getType() {
        return name;
    }

    @Override
    public RayTraceUtils.AdvancedRayTraceResultPart collisionRayTrace(Vec3d start, Vec3d end) {
        if (selectionBoxes.isEmpty())
        {
            addSelectionBoxes(selectionBoxes);
        }
        RayTraceUtils.AdvancedRayTraceResult result = RayTraceUtils.collisionRayTrace(getWorld(), getPos(), start, end, selectionBoxes);
        return result == null ? null : new RayTraceUtils.AdvancedRayTraceResultPart(result, this);
    }

    /**
     * Adds the selection boxes used to ray trace this part.
     * Called only once when module is placed
     */
    public void addSelectionBoxes(List<AxisAlignedBB> list) {
        list.add(model.mainBox.toAABB(this.posX, this.posY, this.posZ));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagCompound pos = new NBTTagCompound();
        pos.setByte("x", posX);
        pos.setByte("y", posY);
        pos.setByte("z", posZ);
        tag.setTag("module_pos", pos);

        NBTTagCompound rot = new NBTTagCompound();
        rot.setByte("rot", rotation);
        tag.setTag("module_rot", pos);

        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagCompound pos = tag.getCompoundTag("module_pos");
        this.posX = pos.getByte("x");
        this.posY = pos.getByte("y");
        this.posZ = pos.getByte("z");

        NBTTagCompound rot = tag.getCompoundTag("module_rot");
        this.rotation = rot.getByte("rot");
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

    // TODO: Use SortedMap for correct ordering
    public Set<HashMap<String, ?>> getDebugInfo()
    {
        // Name of the module
        HashMap<String, String> hashMapName = new HashMap<>(1);
        hashMapName.put("Name", name.getResourcePath());

        // Position in pixels in relation to current block
        HashMap<String, Byte> hashMapPosAndRot = new HashMap<>(4);
        hashMapPosAndRot.put("posX", this.posX);
        hashMapPosAndRot.put("posY", this.posY);
        hashMapPosAndRot.put("posZ", this.posZ);
        hashMapPosAndRot.put("rotation", this.rotation);

        Set<HashMap<String, ?>> setHashMaps = new HashSet<>();
        setHashMaps.add(hashMapPosAndRot);
        setHashMaps.add(hashMapName);

        return setHashMaps;
    }

    private static class Property implements IUnlistedProperty<IMModule> {

        @Override
        public String getName() {
            return "im_module";
        }

        @Override
        public boolean isValid(IMModule value) {
            return true;
        }

        @Override
        public Class<IMModule> getType() {
            return IMModule.class;
        }

        @Override
        public String valueToString(IMModule value) {
            return value.toString();
        }
    }
}
