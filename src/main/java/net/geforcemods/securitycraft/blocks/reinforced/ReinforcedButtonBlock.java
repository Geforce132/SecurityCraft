package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.WhitelistOnlyTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedButtonBlock extends AbstractButtonBlock implements IReinforcedBlock
{
	private final Block vanillaBlock;
	public boolean isWoodenButton;

	public ReinforcedButtonBlock(boolean isWooden, Block.Properties properties, Block vb)
	{
		super(isWooden, properties);
		this.isWoodenButton = isWooden;
		this.vanillaBlock = vb;
	}

	@Override
	public SoundEvent getSoundEvent(boolean powered)
	{
		if (isWoodenButton) return powered ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
		else return powered ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace) {
		if(isAllowedToPress(world, pos, (WhitelistOnlyTileEntity)world.getTileEntity(pos), player))
			return super.onBlockActivated(state, world, pos, player, hand, rayTrace);
		return ActionResultType.FAIL;
	}

	public boolean isAllowedToPress(World world, BlockPos pos, WhitelistOnlyTileEntity te, PlayerEntity entity)
	{
		return te.getOwner().isOwner(entity) || ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(entity.getName().getUnformattedComponentText().toLowerCase());
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlock;
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
