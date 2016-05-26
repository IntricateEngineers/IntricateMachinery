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

import java.util.*;

/**
 * @author topisani
 */
public abstract class IMModule extends Multipart {
    public static final Property PROPERTY = new Property();
    private final ResourceLocation name;
    private final IMModel model;
    public byte posX, posY, posZ;
    private List<AxisAlignedBB> selectionBoxes = new ArrayList<>();

    public IMModule(String name, IMModel model) {
        this.name = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), name);
        this.model = model;
    }

    public IMModel getModel() {
        return model;
    }

    public void setLocalPos(Vec3d localPos) {
        this.posX = (byte) (localPos.xCoord * 16f);
        this.posY = (byte) (localPos.yCoord * 16f);
        this.posZ = (byte) (localPos.zCoord * 16f);
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
        HashMap<String, Byte> hashMapLocalPos = new HashMap<>(3);
        hashMapLocalPos.put("posX", this.posX);
        hashMapLocalPos.put("posY", this.posY);
        hashMapLocalPos.put("posZ", this.posZ);

        Set<HashMap<String, ?>> setHashMaps = new HashSet<>();
        setHashMaps.add(hashMapLocalPos);
        setHashMaps.add(hashMapName);

        return setHashMaps;
    }

    private static class Property implements IUnlistedProperty<IMModule> {

        @Override
        public String getName() {
            return "position";
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

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagCompound pos = tag.getCompoundTag("module_pos");
        this.posX = pos.getByte("x");
        this.posY = pos.getByte("y");
        this.posZ = pos.getByte("z");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagCompound pos = new NBTTagCompound();
        pos.setByte("x", posX);
        pos.setByte("y", posY);
        pos.setByte("z", posZ);
        tag.setTag("module_pos", pos);

        return tag;
    }
}
