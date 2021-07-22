package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedWallBlock extends WallBlock implements IReinforcedBlock
{
	private final Supplier<Block> vanillaBlockSupplier;

	public ReinforcedWallBlock(Block.Properties properties, Block vanillaBlock)
	{
		super(properties);

		this.vanillaBlockSupplier = () -> vanillaBlock;
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlockSupplier.get();
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return defaultBlockState()
				.setValue(UP, vanillaState.getValue(UP))
				.setValue(NORTH_WALL, vanillaState.getValue(NORTH_WALL))
				.setValue(EAST_WALL, vanillaState.getValue(EAST_WALL))
				.setValue(SOUTH_WALL, vanillaState.getValue(SOUTH_WALL))
				.setValue(WEST_WALL, vanillaState.getValue(WEST_WALL))
				.setValue(WATERLOGGED, vanillaState.getValue(WATERLOGGED));
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (Player)placer));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world)
	{
		return new OwnableTileEntity();
	}
}
