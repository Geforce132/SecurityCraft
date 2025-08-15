package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedIronBarsBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedIronBarsBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CageTrapBlock extends DisguisableBlock {
	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public CageTrapBlock(Material material) {
		super(material);
		setSoundType(SoundType.METAL);
		destroyTimeForOwner = 5.0F;
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(DEACTIVATED))
			return super.getCollisionBoundingBox(state, world, pos);
		else
			return null;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof CageTrapBlockEntity) {
			CageTrapBlockEntity cageTrap = (CageTrapBlockEntity) tile;

			if (cageTrap.isDisabled()) {
				addCorrectShape(state, world, pos, entityBox, collidingBoxes, entity, isActualState, cageTrap);
				return;
			}

			if (entity instanceof EntityPlayer && ((cageTrap.isOwnedBy(entity) && cageTrap.ignoresOwner()) || cageTrap.isAllowed(entity)) || cageTrap.allowsOwnableEntity(entity))
				addCorrectShape(state, world, pos, entityBox, collidingBoxes, entity, isActualState, cageTrap);

			if (entity instanceof EntityLiving && !state.getValue(DEACTIVATED)) {
				if (cageTrap.capturesMobs())
					addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);
				else
					addCorrectShape(state, world, pos, entityBox, collidingBoxes, entity, isActualState, cageTrap);

				return;
			}
			else if (entity instanceof EntityItem) {
				addCorrectShape(state, world, pos, entityBox, collidingBoxes, entity, isActualState, cageTrap);
				return;
			}

			if (state.getValue(DEACTIVATED))
				addCorrectShape(state, world, pos, entityBox, collidingBoxes, entity, isActualState, cageTrap);
			else
				addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);
		}
		else
			addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);
	}

	private void addCorrectShape(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean isActualState, DisguisableBlockEntity disguisableTe) {
		if (disguisableTe.isModuleEnabled(ModuleType.DISGUISE)) {
			ItemStack moduleStack = disguisableTe.getModule(ModuleType.DISGUISE);

			if (!moduleStack.isEmpty() && ModuleItem.getBlockAddon(moduleStack) != null)
				super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, isActualState);
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!world.isRemote) {
			if (state.getValue(DEACTIVATED))
				return;

			CageTrapBlockEntity cageTrap = (CageTrapBlockEntity) world.getTileEntity(pos);
			boolean isPlayer = entity instanceof EntityPlayer;

			if (isPlayer || (entity instanceof EntityMob && cageTrap.capturesMobs())) {
				if (!state.getBoundingBox(world, pos).offset(pos).intersects(entity.getEntityBoundingBox()))
					return;

				if ((isPlayer && cageTrap.isOwnedBy(entity)) && cageTrap.ignoresOwner() || cageTrap.allowsOwnableEntity(entity))
					return;

				BlockPos topMiddle = pos.up(4);
				Owner owner = cageTrap.getOwner();
				String ownerUUID = owner.getUUID();
				String ownerName = owner.getName();

				loopIronBarPositions(new BlockPos.MutableBlockPos(pos), barPos -> {
					if (world.isAirBlock(barPos) || world.getBlockState(barPos).getMaterial().isReplaceable()) {
						if (barPos.equals(topMiddle))
							world.setBlockState(barPos, SCContent.horizontalReinforcedIronBars.getDefaultState());
						else
							world.setBlockState(barPos, ((ReinforcedIronBarsBlock) SCContent.reinforcedIronBars).getActualState(SCContent.reinforcedIronBars.getDefaultState(), world, barPos));

						TileEntity barBe = world.getTileEntity(barPos);

						if (barBe instanceof IOwnable)
							((IOwnable) barBe).setOwner(ownerUUID, ownerName);

						if (barBe instanceof ReinforcedIronBarsBlockEntity)
							((ReinforcedIronBarsBlockEntity) barBe).setCanDrop(false);
					}
				});
				world.setBlockState(pos, getDefaultState().withProperty(DEACTIVATED, true));
				world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 3.0F, 1.0F);

				if (isPlayer && PlayerUtils.isPlayerOnline(ownerName))
					PlayerUtils.sendMessageToPlayer(PlayerUtils.getPlayerFromName(ownerName), Utils.localize("tile.securitycraft:cageTrap.name"), Utils.localize("messages.securitycraft:cageTrap.captured", entity.getName(), pos), TextFormatting.BLACK);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		if (stack.getItem() == SCContent.wireCutters) {
			if (!state.getValue(DEACTIVATED)) {
				world.setBlockState(pos, state.withProperty(DEACTIVATED, true));

				if (!player.isCreative())
					stack.damageItem(1, player);

				world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return true;
			}
		}
		else if (stack.getItem() == Items.REDSTONE && state.getValue(DEACTIVATED)) {
			world.setBlockState(pos, state.withProperty(DEACTIVATED, false));

			if (!player.isCreative())
				stack.shrink(1);

			world.playSound(null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return true;
		}

		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(DEACTIVATED, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(DEACTIVATED, (meta == 1));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(DEACTIVATED) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DEACTIVATED);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new CageTrapBlockEntity();
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World level, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		TileEntity be = level.getTileEntity(pos);

		disassembleIronBars(state, level, pos, be instanceof CageTrapBlockEntity ? ((CageTrapBlockEntity) be).getOwner() : null);
		return super.removedByPlayer(state, level, pos, player, willHarvest);
	}

	public static void disassembleIronBars(IBlockState state, World level, BlockPos cageTrapPos, Owner cageTrapOwner) {
		if (cageTrapOwner != null && !level.isRemote && state.getValue(CageTrapBlock.DEACTIVATED)) {
			loopIronBarPositions(new MutableBlockPos(cageTrapPos), barPos -> {
				TileEntity barBe = level.getTileEntity(barPos);

				if (barBe instanceof IOwnable && cageTrapOwner.owns((IOwnable) barBe)) {
					Block barBlock = level.getBlockState(barPos).getBlock();

					if (barBlock == SCContent.reinforcedIronBars || barBlock == SCContent.horizontalReinforcedIronBars)
						level.destroyBlock(barPos, false);
				}
			});
		}
	}

	public static void loopIronBarPositions(MutableBlockPos pos, Consumer<MutableBlockPos> positionAction) {
		pos.setPos(pos.getX() - 1, pos.getY() + 1, pos.getZ() - 1);

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 3; x++) {
				for (int z = 0; z < 3; z++) {
					//skip the middle column above the cage trap, but not the place where the horizontal iron bars are
					if (!(x == 1 && z == 1 && y != 3))
						positionAction.accept(pos);

					pos.setPos(pos.getX(), pos.getY(), pos.getZ() + 1);
				}

				pos.setPos(pos.getX() + 1, pos.getY(), pos.getZ() - 3);
			}

			pos.setPos(pos.getX() - 3, pos.getY() + 1, pos.getZ());
		}
	}
}
