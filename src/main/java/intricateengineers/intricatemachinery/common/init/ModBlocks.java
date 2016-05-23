package intricateengineers.intricatemachinery.common.init;

import intricateengineers.intricatemachinery.api.IMModules;
import intricateengineers.intricatemachinery.api.module.IMModule;
import intricateengineers.intricatemachinery.common.block.FurnaceModule;
import intricateengineers.intricatemachinery.common.util.gui.IMCreativeTab;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
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
                System.out.println("world = [" + world + "], pos = [" + pos + "], side = [" + side + "], hit = [" + hit + "], stack = [" + stack + "], player = [" + player + "]");
                module.setLocalPos(hit);
                return module;
            }
            @Override
            public boolean place(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
                if(!player.canPlayerEdit(pos, side, stack)) {
                    return false;
                } else {
                    IMultipart part = this.createPart(world, pos, side, hit, stack, player);
                    if(part != null && MultipartHelper.canAddPart(world, pos, part)) {
                        if(!world.isRemote) {
                            MultipartHelper.addPart(world, pos, part);
                        }
                        this.consumeItem(stack);
                        SoundType sound = this.getPlacementSound(stack);
                        if(sound != null) {
                            world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
                        }
                        return true;

                    } else {
                        return false;
                    }
                }
            }
        };
        item.setUnlocalizedName(module.getType().getResourcePath());
        item.setCreativeTab(IMCreativeTab.INSTANCE);
        GameRegistry.<Item>register(item, module.getType());
        MultipartRegistry.registerPart(module.getClass(), module.getType().toString());
        return module;
    }
}
