package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFakeWater extends BlockFlowingFluid
{
	public BlockFakeWater()
	{
		super(SCContent.fakeWater, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public void onEntityCollision(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote)
			if(entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative())
				((EntityPlayer) entity).attackEntityFrom(CustomDamageSources.fakeWater, 5F);
			else
				entity.attackEntityFrom(CustomDamageSources.fakeWater, 5F);
	}
}
