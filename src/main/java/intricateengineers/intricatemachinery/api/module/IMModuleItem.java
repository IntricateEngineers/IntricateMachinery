package intricateengineers.intricatemachinery.api.module;

import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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
            instance.setLocalPos(hit);
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
