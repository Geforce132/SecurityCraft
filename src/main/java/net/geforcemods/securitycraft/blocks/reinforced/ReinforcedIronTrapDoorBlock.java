package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedIronTrapDoorBlock extends TrapDoorBlock implements IReinforcedBlock {

	public ReinforcedIronTrapDoorBlock(Material material) {
		super(Block.Properties.create(material).sound(SoundType.METAL).hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean flag)
	{
		boolean hasActiveSCBlock = BlockUtils.hasActiveSCBlockNextTo(world, pos);

		if(hasActiveSCBlock != state.get(OPEN))
		{
			world.setBlockState(pos, state.with(OPEN, hasActiveSCBlock), 2);
			playSound((PlayerEntity)null, world, pos, hasActiveSCBlock);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		return false;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);

		if(!(newState.getBlock() instanceof ReinforcedIronTrapDoorBlock))
			world.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param)
	{
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableTileEntity();
	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.IRON_TRAPDOOR;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(HORIZONTAL_FACING, vanillaState.get(HORIZONTAL_FACING)).with(OPEN, false).with(HALF, vanillaState.get(HALF)).with(POWERED, false).with(WATERLOGGED, vanillaState.get(WATERLOGGED));
	}
}