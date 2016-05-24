package intricateengineers.intricatemachinery.api.module;

import intricateengineers.intricatemachinery.api.util.VectorUtils;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.MultipartHelper;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * @author topisani
 */
public class IMModuleItem extends ItemMultiPart {

    public final Class<? extends IMModule> module;

    public IMModuleItem(Class<? extends IMModule> module) {
        this.module = module;
    }

    @Override
    public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
        try {
            IMModule instance = module.newInstance();
            instance.setLocalPos(VectorUtils.modulus(hit, 1));
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean place(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {

        if (!player.canPlayerEdit(pos, side, stack)) return false;

        IMultipart part = createPart(world, pos, side, hit, stack, player);

        if (part != null && MultipartHelper.canAddPart(world, pos, part)) {
            if (!world.isRemote) MultipartHelper.addPart(world, pos, part);
            consumeItem(stack);

            SoundType sound = getPlacementSound(stack);
            if (sound != null)
                world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());

            return true;
        }

        return false;
    }

}
