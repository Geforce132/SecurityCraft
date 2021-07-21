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
		public Optional<INamedContainerProvider> func_225539_a_(final ChestTileEntity chest1, final ChestTileEntity chest2) {
			final IInventory chestInventory = new DoubleSidedInventory(chest1, chest2);
			return Optional.of(new INamedContainerProvider() {
				@Override
				public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
					if (chest1.canOpen(player) && chest2.canOpen(player)) {
						chest1.fillWithLoot(inventory.player);
						chest2.fillWithLoot(inventory.player);
						return ChestContainer.createGeneric9X6(id, inventory, chestInventory);
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
		public Optional<INamedContainerProvider> func_225538_a_(ChestTileEntity te) {
			return Optional.of(te);
		}

		@Override
		public Optional<INamedContainerProvider> func_225537_b_() {
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
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(!world.isRemote && !isBlocked(world, pos))
		{
			KeypadChestTileEntity te = (KeypadChestTileEntity)world.getTileEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return ActionResultType.FAIL;
			}
			else if(ModuleUtils.isAllowed(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(world, pos, player);
			}
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
				te.openPasswordGUI(player);
		}

		return ActionResultType.SUCCESS;
	}

	public static void activate(World world, BlockPos pos, PlayerEntity player){
		if(!world.isRemote) {
			BlockState state = world.getBlockState(pos);
			ChestBlock block = (ChestBlock)state.getBlock();
			INamedContainerProvider inamedcontainerprovider = block.getContainer(state, world, pos);
			if (inamedcontainerprovider != null) {
				player.openContainer(inamedcontainerprovider);
				player.addStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
			}
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		boolean isPlayer = entity instanceof PlayerEntity;

		if(isPlayer)
		{
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)entity));

			if(state.get(KeypadChestBlock.TYPE) != ChestType.SINGLE)
			{
				KeypadChestTileEntity thisTe = (KeypadChestTileEntity)world.getTileEntity(pos);
				TileEntity otherTe = world.getTileEntity(pos.offset(getDirectionToAttached(state)));

				if(otherTe instanceof KeypadChestTileEntity && thisTe.getOwner().owns((KeypadChestTileEntity)otherTe))
					thisTe.setPassword(((KeypadChestTileEntity)otherTe).getPassword());
			}
		}
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof KeypadChestTileEntity) {
			return ((KeypadChestTileEntity)te).hasModule(ModuleType.REDSTONE) ? MathHelper.clamp(ChestTileEntity.getPlayersUsing(world, pos), 0, 15) : 0;
		}

		return 0;
	}

	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == Direction.UP ? state.getWeakPower(world, pos, side) : 0;
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor){
		super.onNeighborChange(state, world, pos, neighbor);

		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof KeypadChestTileEntity)
			((KeypadChestTileEntity) tileEntity).updateContainingBlockInfo();
	}

	@Override
	public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
		return this.combine(state, world, pos, false).apply(CONTAINER_MERGER).orElse(null);
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(IBlockReader reader)
	{
		return new KeypadChestTileEntity();
	}

	public static boolean isBlocked(World world, BlockPos pos)
	{
		return isBelowSolidBlock(world, pos);
	}

	private static boolean isBelowSolidBlock(World world, BlockPos pos)
	{
		return world.getBlockState(pos.up()).isNormalCube(world, pos.up());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.toRotation(state.get(FACING)));
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
			Direction facing = state.get(FACING);
			ChestType type = state.get(TYPE);

			convertChest(player, world, pos, facing, type);

			if(type != ChestType.SINGLE)
			{
				BlockPos newPos = pos.offset(getDirectionToAttached(state));
				BlockState newState = world.getBlockState(newPos);
				Direction newFacing = newState.get(FACING);
				ChestType newType = newState.get(TYPE);

				convertChest(player, world, newPos, newFacing, newType);
			}

			return true;
		}

		private void convertChest(PlayerEntity player, World world, BlockPos pos, Direction facing, ChestType type)
		{
			ChestTileEntity chest = (ChestTileEntity)world.getTileEntity(pos);
			CompoundNBT tag = chest.write(new CompoundNBT());

			chest.clear();
			world.setBlockState(pos, SCContent.KEYPAD_CHEST.get().getDefaultState().with(FACING, facing).with(TYPE, type));
			((ChestTileEntity)world.getTileEntity(pos)).read(world.getBlockState(pos), tag);
			((IOwnable) world.getTileEntity(pos)).setOwner(player.getUniqueID().toString(), player.getName().getString());
		}
	}
}
