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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class KeypadChestBlock extends ChestBlock {

	private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>> CONTAINER_MERGER = new TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>>() {
		@Override
		public Optional<INamedContainerProvider> acceptDouble(final ChestTileEntity chest1, final ChestTileEntity chest2) {
			final IInventory chestInventory = new DoubleSidedInventory(chest1, chest2);
			return Optional.of(new INamedContainerProvider() {
				@Override
				public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
					if (chest1.canOpen(player) && chest2.canOpen(player)) {
						chest1.unpackLootTable(inventory.player);
						chest2.unpackLootTable(inventory.player);
						return ChestContainer.sixRows(id, inventory, chestInventory);
					} else {
						return null;
					}
				}

				@Override
				public ITextComponent getDisplayName() {
					if (chest1.hasCustomName()) {
						return chest1.getDisplayName();
					} else {
						return chest2.hasCustomName() ? chest2.getDisplayName() : Utils.localize("block.securitycraft.keypad_chest_double");
					}
				}
			});
		}

		@Override
		public Optional<INamedContainerProvider> acceptSingle(ChestTileEntity te) {
			return Optional.of(te);
		}

		@Override
		public Optional<INamedContainerProvider> acceptNone() {
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
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(!world.isClientSide && !isBlocked(world, pos))
		{
			KeypadChestTileEntity te = (KeypadChestTileEntity)world.getBlockEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return ActionResultType.FAIL;
			}
			else if(ModuleUtils.isAllowed(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(world, pos, player);
			}
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
				te.openPasswordGUI(player);
		}

		return ActionResultType.SUCCESS;
	}

	public static void activate(World world, BlockPos pos, PlayerEntity player){
		if(!world.isClientSide) {
			BlockState state = world.getBlockState(pos);
			ChestBlock block = (ChestBlock)state.getBlock();
			INamedContainerProvider inamedcontainerprovider = block.getMenuProvider(state, world, pos);
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
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.setPlacedBy(world, pos, state, entity, stack);

		boolean isPlayer = entity instanceof PlayerEntity;

		if(isPlayer)
		{
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)entity));

			if(state.getValue(KeypadChestBlock.TYPE) != ChestType.SINGLE)
			{
				KeypadChestTileEntity thisTe = (KeypadChestTileEntity)world.getBlockEntity(pos);
				TileEntity otherTe = world.getBlockEntity(pos.relative(getConnectedDirection(state)));

				if(otherTe instanceof KeypadChestTileEntity && thisTe.getOwner().owns((KeypadChestTileEntity)otherTe))
					thisTe.setPassword(((KeypadChestTileEntity)otherTe).getPassword());
			}
		}
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		TileEntity te = world.getBlockEntity(pos);

		if (te instanceof KeypadChestTileEntity) {
			return ((KeypadChestTileEntity)te).hasModule(ModuleType.REDSTONE) ? MathHelper.clamp(((KeypadChestTileEntity)te).getNumPlayersUsing(), 0, 15) : 0;
		}

		return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == Direction.UP ? state.getSignal(world, pos, side) : 0;
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor){
		super.onNeighborChange(state, world, pos, neighbor);

		TileEntity tileEntity = world.getBlockEntity(pos);

		if (tileEntity instanceof KeypadChestTileEntity)
			((KeypadChestTileEntity) tileEntity).clearCache();
	}

	@Override
	public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
		return this.combine(state, world, pos, false).apply(CONTAINER_MERGER).orElse(null);
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity newBlockEntity(IBlockReader reader)
	{
		return new KeypadChestTileEntity();
	}

	public static boolean isBlocked(World world, BlockPos pos)
	{
		return isBelowSolidBlock(world, pos);
	}

	private static boolean isBelowSolidBlock(World world, BlockPos pos)
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
		public boolean convert(PlayerEntity player, World world, BlockPos pos)
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

		private void convertChest(PlayerEntity player, World world, BlockPos pos, Direction facing, ChestType type)
		{
			ChestTileEntity chest = (ChestTileEntity)world.getBlockEntity(pos);
			CompoundNBT tag = chest.save(new CompoundNBT());

			chest.clearContent();
			world.setBlockAndUpdate(pos, SCContent.KEYPAD_CHEST.get().defaultBlockState().setValue(FACING, facing).setValue(TYPE, type));
			((ChestTileEntity)world.getBlockEntity(pos)).load(world.getBlockState(pos), tag);
			((IOwnable) world.getBlockEntity(pos)).setOwner(player.getUUID().toString(), player.getName().getString());
		}
	}
}
