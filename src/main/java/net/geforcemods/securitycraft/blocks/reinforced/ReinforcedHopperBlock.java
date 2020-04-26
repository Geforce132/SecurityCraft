package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.ReinforcedHopperTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedHopperBlock extends HopperBlock implements IReinforcedBlock
{
	public ReinforcedHopperBlock()
	{
		super(Block.Properties.create(Material.IRON, MaterialColor.STONE).hardnessAndResistance(-1.0F, 60000000.0F).sound(SoundType.METAL).notSolid());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));

		if(stack.hasDisplayName())
		{
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
				((ReinforcedHopperTileEntity)te).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if (!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
				player.openContainer((ReinforcedHopperTileEntity)te);
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
		{
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof ReinforcedHopperTileEntity)
			{
				InventoryHelper.dropInventoryItems(world, pos, (ReinforcedHopperTileEntity)te);
				world.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof ReinforcedHopperTileEntity)
			((ReinforcedHopperTileEntity)te).onEntityCollision(entity);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new ReinforcedHopperTileEntity();
	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.HOPPER;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(ENABLED, vanillaState.get(ENABLED)).with(FACING, vanillaState.get(FACING));
	}
}
