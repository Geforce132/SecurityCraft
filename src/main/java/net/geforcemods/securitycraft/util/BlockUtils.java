package net.geforcemods.securitycraft.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.SecurityCraftAPI;
import net.geforcemods.securitycraft.blocks.KeycardReaderBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedButtonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLeverBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class BlockUtils{
	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> new EmptyHandler());
	private static final List<Block> PRESSURE_PLATES = Arrays.asList(new Block[] {
			SCContent.REINFORCED_STONE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_OAK_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_WARPED_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE.get()
	});
	private static final List<Block> BUTTONS = Arrays.asList(new Block[]{
			SCContent.REINFORCED_STONE_BUTTON.get(),
			SCContent.REINFORCED_OAK_BUTTON.get(),
			SCContent.REINFORCED_SPRUCE_BUTTON.get(),
			SCContent.REINFORCED_BIRCH_BUTTON.get(),
			SCContent.REINFORCED_JUNGLE_BUTTON.get(),
			SCContent.REINFORCED_ACACIA_BUTTON.get(),
			SCContent.REINFORCED_DARK_OAK_BUTTON.get(),
			SCContent.REINFORCED_CRIMSON_BUTTON.get(),
			SCContent.REINFORCED_WARPED_BUTTON.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get()
	});

	public static boolean isSideSolid(IWorldReader world, BlockPos pos, Direction side)
	{
		return world.getBlockState(pos).isSolidSide(world, pos, side);
	}

	public static void updateAndNotify(World world, BlockPos pos, Block block, int delay, boolean shouldUpdate){
		if(shouldUpdate)
			world.getPendingBlockTicks().scheduleTick(pos, block, delay);

		world.notifyNeighborsOfStateChange(pos, block);
	}

	public static Block getBlock(IBlockReader world, BlockPos pos){
		return world.getBlockState(pos).getBlock();
	}

	public static Block getBlock(World world, int x, int y, int z){
		return world.getBlockState(toPos(x, y, z)).getBlock();
	}

	public static BlockPos toPos(int x, int y, int z){
		return new BlockPos(x, y, z);
	}

	public static int[] fromPos(BlockPos pos){
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}

	/**
	 * Returns the number of blocks between two positions<br>
	 * Credit to https://zonide.angelfire.com/minecraftbdc.shtml for the formula
	 */
	public static int getDistanceBetweenBlocks(BlockPos pos1, BlockPos pos2)
	{
		int xAdjusted = (int) Math.pow((pos1.getX() - pos2.getX()), 2);
		int yAdjusted = (int) Math.pow((pos1.getY() - pos2.getY()), 2);
		int zAdjusted = (int) Math.pow((pos1.getZ() - pos2.getZ()), 2);

		return (int) Math.sqrt(xAdjusted + yAdjusted + zAdjusted);
	}

	public static boolean hasActiveSCBlockNextTo(World world, BlockPos pos)
	{
		TileEntity thisTile = world.getTileEntity(pos);

		return hasActiveSCBlockNextTo(world, pos, thisTile, SCContent.LASER_BLOCK.get(), true, (state, te) -> state.get(LaserBlock.POWERED)) ||
				hasActiveSCBlockNextTo(world, pos, thisTile, SCContent.RETINAL_SCANNER.get(), true, (state, te) -> state.get(RetinalScannerBlock.POWERED)) ||
				hasActiveSCBlockNextTo(world, pos, thisTile, SCContent.KEYPAD.get(), true, (state, te) -> state.get(KeypadBlock.POWERED)) ||
				hasActiveSCBlockNextTo(world, pos, thisTile, SCContent.KEYCARD_READER.get(), true, (state, te) -> state.get(KeycardReaderBlock.POWERED)) ||
				hasActiveSCBlockNextTo(world, pos, thisTile, SCContent.INVENTORY_SCANNER.get(), true, (state, te) -> ((InventoryScannerTileEntity)te).hasModule(ModuleType.REDSTONE) && ((InventoryScannerTileEntity)te).shouldProvidePower()) ||
				hasActiveSCBlockNextTo(world, pos, thisTile, null, false, (state, te) -> PRESSURE_PLATES.contains(state.getBlock()) && state.get(ReinforcedPressurePlateBlock.POWERED)) ||
				hasActiveSCBlockNextTo(world, pos, thisTile, null, false, (state, te) -> BUTTONS.contains(state.getBlock()) && state.get(ReinforcedButtonBlock.POWERED)) ||
				hasActiveSCBlockNextTo(world, pos, thisTile, SCContent.REINFORCED_LEVER.get(), true, (state, te) -> state.get(ReinforcedLeverBlock.POWERED));
	}

	private static boolean hasActiveSCBlockNextTo(World world, BlockPos pos, TileEntity te, Block block, boolean checkForBlock, BiFunction<BlockState,TileEntity,Boolean> extraCondition)
	{
		for(Direction dir : Direction.values())
		{
			BlockPos offsetPos = pos.offset(dir);
			BlockState offsetState = world.getBlockState(offsetPos);

			if(!checkForBlock || offsetState.getBlock() == block)
			{
				TileEntity offsetTe = world.getTileEntity(offsetPos);

				if(extraCondition.apply(offsetState, offsetTe))
					return ((IOwnable)offsetTe).getOwner().owns((IOwnable)te);
			}

			if(world.getRedstonePower(offsetPos, dir) == 15 && !offsetState.canProvidePower())
			{
				for(Direction dirOffset : Direction.values())
				{
					if(dirOffset.getOpposite() == dir) //skip this, as it would just go back to the original position
						continue;

					BlockPos newOffsetPos = offsetPos.offset(dirOffset);

					offsetState = world.getBlockState(newOffsetPos);

					if(!checkForBlock || offsetState.getBlock() == block)
					{
						//checking that e.g. a lever/button is correctly attached to the block
						if(offsetState.hasProperty(BlockStateProperties.FACE) && offsetState.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
						{
							Axis offsetAxis = dirOffset.getAxis();
							Direction offsetFacing = offsetState.get(BlockStateProperties.HORIZONTAL_FACING);
							AttachFace offsetAttachFace = offsetState.get(BlockStateProperties.FACE);

							switch(offsetAxis)
							{
								case X: case Z:
									if(offsetAttachFace != AttachFace.WALL || dirOffset != offsetFacing)
										return false;
									break;
								case Y:
									if((dirOffset == Direction.UP && offsetAttachFace != AttachFace.FLOOR) || (dirOffset == Direction.DOWN && offsetAttachFace != AttachFace.CEILING))
										return false;
									break;
							}
						}

						TileEntity offsetTe = world.getTileEntity(newOffsetPos);

						if(extraCondition.apply(offsetState, offsetTe))
							return ((IOwnable)offsetTe).getOwner().owns((IOwnable) te);
					}
				}
			}
		}

		return false;
	}

	public static LazyOptional<?> getProtectedCapability(Direction side, TileEntity te, Supplier<LazyOptional<?>> extractionPermittedHandler, Supplier<LazyOptional<?>> insertOnlyHandler)
	{
		if(side == null)
			return EMPTY_INVENTORY;

		BlockPos offsetPos = te.getPos().offset(side);
		BlockState offsetState = te.getWorld().getBlockState(offsetPos);

		for(IExtractionBlock extractionBlock : SecurityCraftAPI.getRegisteredExtractionBlocks())
		{
			if(offsetState.getBlock() == extractionBlock.getBlock())
			{
				if(!extractionBlock.canExtract((IOwnable)te, te.getWorld(), offsetPos, offsetState))
					return EMPTY_INVENTORY;
				else return extractionPermittedHandler.get();
			}
		}

		return insertOnlyHandler.get();
	}
}
