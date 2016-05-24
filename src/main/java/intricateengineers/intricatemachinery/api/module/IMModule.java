package intricateengineers.intricatemachinery.api.module;

import intricateengineers.intricatemachinery.core.ModInfo;
import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.Multipart;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;

/**
 * @author topisani
 */
public abstract class IMModule extends Multipart {
    public static final Property PROPERTY = new Property();
    private final ResourceLocation name;
    private final IMModel model;
    public byte posX, posY, posZ;

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

    /**
     * Adds the selection boxes used to ray trace this part.
     */
    @Override
    public void addSelectionBoxes(List<AxisAlignedBB> list) {
            list.add(model.mainBox.toAABB(this.posX, this.posY, this.posZ));
    }

    @Override
    public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {

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
