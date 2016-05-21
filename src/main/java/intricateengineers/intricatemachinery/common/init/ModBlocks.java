package intricateengineers.intricatemachinery.common.init;

import intricateengineers.intricatemachinery.api.IMModules;
import intricateengineers.intricatemachinery.api.module.IMModule;
import intricateengineers.intricatemachinery.common.block.FurnaceModule;
import intricateengineers.intricatemachinery.common.util.gui.IMCreativeTab;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author topisani
 */
public final class ModBlocks {

    public static void init() {
        IMModules.FURNACE = registerModule(new FurnaceModule());
    }

    private static IMModule registerModule(IMModule module) {
        ItemMultiPart item = new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
                return module;
            }
        };
        item.setUnlocalizedName(module.getType().getResourcePath());
        item.setCreativeTab(IMCreativeTab.INSTANCE);
        GameRegistry.<Item>register(item, module.getType());
        MultipartRegistry.registerPart(module.getClass(), module.getType().toString());
        return module;
    }
}
