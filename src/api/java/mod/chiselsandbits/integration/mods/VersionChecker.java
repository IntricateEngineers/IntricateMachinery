package mod.chiselsandbits.integration.mods;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.integration.ChiselsAndBitsIntegration;
import mod.chiselsandbits.integration.IntegrationBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

@ChiselsAndBitsIntegration( "VersionChecker" )
public class VersionChecker extends IntegrationBase
{

	@Override
	public void init()
	{
		final NBTTagCompound compound = new NBTTagCompound();
		compound.setString( "curseProjectName", "chisels-bits" );
		compound.setString( "curseFilenameParser", "chiselsandbits-[].jar" );
		FMLInterModComms.sendRuntimeMessage( ChiselsAndBits.MODID, "VersionChecker", "addCurseCheck", compound );
	}

}
