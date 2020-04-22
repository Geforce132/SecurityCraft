package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.WhitelistOnlyTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ReinforcedLeverBlock extends LeverBlock implements IReinforcedBlock {

	public ReinforcedLeverBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		if(isAllowedToPress(world, pos, (WhitelistOnlyTileEntity)world.getTileEntity(pos), player))
			return super.onBlockActivated(state, world, pos, player, hand, result);
		return ActionResultType.FAIL;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, WhitelistOnlyTileEntity te, PlayerEntity entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(entity.getName().getUnformattedComponentText().toLowerCase());
	}

	private static void addParticles(BlockState state, IWorld world, BlockPos pos, float alpha) {
		Direction dir1 = ((Direction)state.get(HORIZONTAL_FACING)).getOpposite();
		Direction dir2 = getFacing(state).getOpposite();
		double newX = (double)pos.getX() + 0.5D + 0.1D * (double)dir1.getXOffset() + 0.2D * (double)dir2.getXOffset();
		double newY = (double)pos.getY() + 0.5D + 0.1D * (double)dir1.getYOffset() + 0.2D * (double)dir2.getYOffset();
		double newZ = (double)pos.getZ() + 0.5D + 0.1D * (double)dir1.getZOffset() + 0.2D * (double)dir2.getZOffset();
		world.addParticle(new RedstoneParticleData(1.0F, 0.0F, 0.0F, alpha), newX, newY, newZ, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public Block getVanillaBlock()
	{
		return Blocks.LEVER;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState().with(FACE, vanillaState.get(FACE)).with(HORIZONTAL_FACING, vanillaState.get(HORIZONTAL_FACING)).with(POWERED, vanillaState.get(POWERED));
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		return NonNullList.from(ItemStack.EMPTY, new ItemStack(this));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new WhitelistOnlyTileEntity();
	}
}
