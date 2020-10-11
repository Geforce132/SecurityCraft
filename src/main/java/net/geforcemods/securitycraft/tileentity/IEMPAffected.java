package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public interface IEMPAffected
{
	public default void shutDown()
	{
		World world = getTileEntity().getWorld();
		BlockPos pos = getTileEntity().getPos();
		IBlockState state = world.getBlockState(pos);

		setShutDown(true);

		if(state.getProperties().containsKey(BlockSecurityCamera.POWERED) && state.getValue(BlockSecurityCamera.POWERED)) //it's just a boolean property with the name "powered", this is one of the many fields that could be used
			world.setBlockState(pos, state.withProperty(BlockSecurityCamera.POWERED, false));

		if(!world.isRemote)
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(getTileEntity().getUpdatePacket());
	}

	public default void reactivate()
	{
		setShutDown(false);

		if(!getTileEntity().getWorld().isRemote)
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(getTileEntity().getUpdatePacket());
	}

	public boolean isShutDown();

	public void setShutDown(boolean shutDown);

	public TileEntity getTileEntity();
}
