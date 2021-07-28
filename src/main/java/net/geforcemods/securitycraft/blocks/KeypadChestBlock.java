package net.geforcemods.securitycraft.blocks;

import java.util.Optional;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;

public class KeypadChestBlock extends ChestBlock {

	private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> CONTAINER_MERGER = new DoubleBlockCombiner.Combiner<>() {
		@Override
		public Optional<MenuProvider> acceptDouble(final ChestBlockEntity chest1, final ChestBlockEntity chest2) {
			final Container chestInventory = new CompoundContainer(chest1, chest2);
			return Optional.of(new MenuProvider() {
				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					if (chest1.canOpen(player) && chest2.canOpen(player)) {
						chest1.unpackLootTable(inventory.player);
						chest2.unpackLootTable(inventory.player);
						return ChestMenu.sixRows(id, inventory, chestInventory);
					} else {
						return null;
					}
				}

				@Override
				public Component getDisplayName() {
					if (chest1.hasCustomName()) {
						return chest1.getDisplayName();
					} else {
						return chest2.hasCustomName() ? chest2.getDisplayName() : Utils.localize("block.securitycraft.keypad_chest_double");
					}
				}
			});
		}

		@Override
		public Optional<MenuProvider> acceptSingle(ChestBlockEntity te) {
			return Optional.of(te);
		}

		@Override
		public Optional<MenuProvider> acceptNone() {
			return Optional.empty();
		}
	};

	public KeypadChestBlock(Block.Properties properties){
		super(properties, () -> SCContent.teTypeKeypadChest);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(!world.isClientSide && !isBlocked(world, pos))
		{
			KeypadChestTileEntity te = (KeypadChestTileEntity)world.getBlockEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);

				return InteractionResult.FAIL;
			}
			else if(ModuleUtils.isAllowed(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

				activate(world, pos, player);
			}
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
				te.openPasswordGUI(player);
		}

		return InteractionResult.SUCCESS;
	}

	public static void activate(Level world, BlockPos pos, Player player){
		if(!world.isClientSide) {
			BlockState state = world.getBlockState(pos);
			ChestBlock block = (ChestBlock)state.getBlock();
			MenuProvider inamedcontainerprovider = block.getMenuProvider(state, world, pos);
			if (inamedcontainerprovider != null) {
				player.openMenu(inamedcontainerprovider);
				player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
			}
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.setPlacedBy(world, pos, state, entity, stack);

		if(entity instanceof Player player)
		{
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, player));

			if(state.getValue(KeypadChestBlock.TYPE) != ChestType.SINGLE)
			{
				KeypadChestTileEntity thisTe = (KeypadChestTileEntity)world.getBlockEntity(pos);
				BlockEntity otherTe = world.getBlockEntity(pos.relative(getConnectedDirection(state)));

				if(otherTe instanceof KeypadChestTileEntity te && thisTe.getOwner().owns(te))
					thisTe.setPassword(te.getPassword());
			}
		}
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof KeypadChestTileEntity te)
			return te.hasModule(ModuleType.REDSTONE) ? Mth.clamp(te.getNumPlayersUsing(), 0, 15) : 0;
		else return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return side == Direction.UP ? state.getSignal(world, pos, side) : 0;
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor){
		super.onNeighborChange(state, world, pos, neighbor);

		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof KeypadChestTileEntity te)
			te.setBlockState(state);
	}

	@Override
	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
		return combine(state, world, pos, false).apply(CONTAINER_MERGER).orElse(null);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new KeypadChestTileEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return world.isClientSide ? createTickerHelper(type, SCContent.teTypeKeypadChest, KeypadChestTileEntity::lidAnimateTick) : null;
	}

	public static boolean isBlocked(Level world, BlockPos pos)
	{
		return isBelowSolidBlock(world, pos);
	}

	private static boolean isBelowSolidBlock(Level world, BlockPos pos)
	{
		return world.getBlockState(pos.above()).isRedstoneConductor(world, pos.above());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public static class Convertible implements IPasswordConvertible
	{
		@Override
		public Block getOriginalBlock()
		{
			return Blocks.CHEST;
		}

		@Override
		public boolean convert(Player player, Level world, BlockPos pos)
		{
			BlockState state = world.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			ChestType type = state.getValue(TYPE);

			convertChest(player, world, pos, facing, type);

			if(type != ChestType.SINGLE)
			{
				BlockPos newPos = pos.relative(getConnectedDirection(state));
				BlockState newState = world.getBlockState(newPos);
				Direction newFacing = newState.getValue(FACING);
				ChestType newType = newState.getValue(TYPE);

				convertChest(player, world, newPos, newFacing, newType);
			}

			return true;
		}

		private void convertChest(Player player, Level world, BlockPos pos, Direction facing, ChestType type)
		{
			ChestBlockEntity chest = (ChestBlockEntity)world.getBlockEntity(pos);
			CompoundTag tag = chest.save(new CompoundTag());

			chest.clearContent();
			world.setBlockAndUpdate(pos, SCContent.KEYPAD_CHEST.get().defaultBlockState().setValue(FACING, facing).setValue(TYPE, type));
			((ChestBlockEntity)world.getBlockEntity(pos)).load(tag);
			((IOwnable) world.getBlockEntity(pos)).setOwner(player.getUUID().toString(), player.getName().getString());
		}
	}
}
