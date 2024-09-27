package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedButtonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLeverBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AllowlistOnlyBlockEntity extends CustomizableBlockEntity {
	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[0];
	}

	@Override
	public void onOwnerChanged(IBlockState state, World world, BlockPos pos, EntityPlayer player, Owner oldOwner, Owner newOwner) {
		Block block = state.getBlock();

		if (block instanceof ReinforcedButtonBlock)
			turnOff(world, pos, state, block, BlockButton.POWERED, state.getValue(BlockDirectional.FACING).getOpposite());
		else if (block instanceof ReinforcedLeverBlock)
			turnOff(world, pos, state, block, BlockLever.POWERED, state.getValue(BlockLever.FACING).getFacing().getOpposite());
		else if (block instanceof ReinforcedPressurePlateBlock)
			turnOff(world, pos, state, block, BlockPressurePlate.POWERED, EnumFacing.DOWN);

		super.onOwnerChanged(state, world, pos, player, oldOwner, newOwner);
	}

	private void turnOff(World world, BlockPos pos, IBlockState state, Block block, PropertyBool poweredProperty, EnumFacing additionalUpdateOffset) {
		world.setBlockState(pos, state.withProperty(poweredProperty, false));
		world.notifyNeighborsOfStateChange(pos, block, false);
		world.notifyNeighborsOfStateChange(pos.offset(additionalUpdateOffset), block, false);
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
}
