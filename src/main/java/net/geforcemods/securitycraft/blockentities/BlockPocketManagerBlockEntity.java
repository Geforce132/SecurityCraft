package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketWallBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCrystalQuartzBlock;
import net.geforcemods.securitycraft.inventory.InsertOnlyItemStackHandler;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BlockPocketManagerBlockEntity extends CustomizableBlockEntity implements ITickable, ILockable {
	public static final int RENDER_DISTANCE = 100;
	private static final int BLOCK_PLACEMENTS_PER_TICK = 4;
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 1);
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 2);
	private boolean enabled = false;
	private boolean showOutline = false;
	private int color = 0xFF0000FF;
	private int autoBuildOffset = 0;
	private int size = 5;
	private List<BlockPos> blocks = new ArrayList<>();
	private List<BlockPos> walls = new ArrayList<>();
	private List<BlockPos> floor = new ArrayList<>();
	protected NonNullList<ItemStack> storage = NonNullList.withSize(56, ItemStack.EMPTY);
	private IItemHandler storageHandler;
	private IItemHandler insertOnlyHandler;
	private List<Pair<BlockPos, IBlockState>> placeQueue = new ArrayList<>();
	private boolean shouldPlaceBlocks = false;

	@Override
	public void update() {
		if (!world.isRemote && shouldPlaceBlocks) {
			EntityPlayer owner = PlayerUtils.getPlayerFromName(getOwner().getName());

			//if the owner left the server, stop building the block pocket
			if (owner == null) {
				placeQueue.clear();
				shouldPlaceBlocks = false;
				return;
			}

			boolean isCreative = owner.isCreative();
			boolean placed4 = true;

			//place 4 blocks per tick
			//only place the next block if the previous one was placed
			//if any block failed to place, either the end was reached, or a block was in the way
			placeLoop:
			for (int i = 0; i < BLOCK_PLACEMENTS_PER_TICK; i++) {
				Pair<BlockPos, IBlockState> toPlace;
				IBlockState stateInWorld;

				do {
					if (placeQueue.isEmpty()) {
						placed4 = false;
						break placeLoop;
					}

					toPlace = placeQueue.remove(0);

					Block block = toPlace.getRight().getBlock();

					//only allow block pocket walls and reinforced crystal quartz that is not default (so either chiseled or any of the lines types)
					if (block != SCContent.blockPocketWall && (block != SCContent.reinforcedCrystalQuartz || toPlace.getRight().getValue(BlockQuartz.VARIANT) == EnumType.DEFAULT))
						throw new IllegalStateException(String.format("Tried to automatically place non-block pocket block \"%s\"! This mustn't happen!", toPlace.getRight()));
				}
				//reach the next block that is missing for the block pocket
				while ((stateInWorld = world.getBlockState(toPlace.getLeft())) == toPlace.getRight());

				if (stateInWorld.getMaterial().isReplaceable()) {
					BlockPos pos = toPlace.getLeft();
					IBlockState stateToPlace = toPlace.getRight();
					SoundType soundType = stateToPlace.getBlock().getSoundType(stateToPlace, world, pos, owner);
					TileEntity te;

					//queue blocks for removal from the inventory
					if (!isCreative) {
						//remove blocks from inventory
						invLoop:
						for (int k = 0; k < storage.size(); k++) {
							ItemStack stackToCheck = storage.get(k);

							if (!stackToCheck.isEmpty() && ((ItemBlock) stackToCheck.getItem()).getBlock() == stateToPlace.getBlock()) {
								stackToCheck.shrink(1);
								break invLoop;
							}
						}
					}

					world.setBlockState(pos, stateToPlace);
					world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, soundType.getVolume(), soundType.getPitch());
					te = world.getTileEntity(pos);

					//assigning the owner
					if (te instanceof OwnableBlockEntity)
						((OwnableBlockEntity) te).setOwner(getOwner().getUUID(), getOwner().getName());

					continue;
				}

				//when an invalid block is in the way
				PlayerUtils.sendMessageToPlayer(owner, Utils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), new TextComponentTranslation("messages.securitycraft:blockpocket.assemblyFailed", getFormattedRelativeCoordinates(toPlace.getLeft(), world.getBlockState(pos).getValue(BlockPocketManagerBlock.FACING)), new TextComponentTranslation(stateInWorld.getBlock().getTranslationKey())), TextFormatting.DARK_AQUA);
				placed4 = false;
				break placeLoop;
			}

			if (!placed4) {
				if (!placeQueue.isEmpty()) //there are still blocks left to place, so a different block is blocking (heh) a space
					placeQueue.clear();
				else { //no more blocks left to place, assembling must be done
					setWalls(!isModuleEnabled(ModuleType.DISGUISE));
					PlayerUtils.sendMessageToPlayer(owner, Utils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), new TextComponentTranslation("messages.securitycraft:blockpocket.assembled"), TextFormatting.DARK_AQUA);
				}

				shouldPlaceBlocks = false;
			}
		}
	}

	/**
	 * Enables the block pocket
	 *
	 * @return The feedback message. null if none should be sent.
	 */
	public TextComponentTranslation enableMultiblock() {
		if (world.isRemote)
			return new TextComponentTranslation("enableMultiblock called on client! Send a ToggleBlockPocketManager packet instead.");

		if (!isEnabled()) { //multiblock detection
			List<BlockPos> blocks = new ArrayList<>();
			List<BlockPos> sides = new ArrayList<>();
			List<BlockPos> floor = new ArrayList<>();
			final EnumFacing managerFacing = world.getBlockState(pos).getValue(BlockPocketManagerBlock.FACING);
			final EnumFacing left = managerFacing.rotateY();
			final EnumFacing right = left.getOpposite();
			final EnumFacing back = left.rotateY();
			final BlockPos startingPos;
			final int lowest = 0;
			final int highest = getSize() - 1;
			BlockPos pos = getPos().toImmutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;
			int offset = 0;

			if (!(world.getBlockState(pos.offset(left)).getBlock() instanceof ReinforcedCrystalQuartzBlock)) {
				offset = -getAutoBuildOffset() + (getSize() / 2);
				pos = pos.offset(left, offset);
			}
			else {
				for (int i = 1; i < getSize() - 1; i++) { //find the bottom left corner
					if (world.getBlockState(pos.offset(left, i)).getValue(BlockQuartz.VARIANT).getMetadata() < 2) { //pillars
						offset = i;
						pos = pos.offset(left, offset);
						break;
					}
				}

				if (offset == 0) {
					offset = -getAutoBuildOffset() + (getSize() / 2);
					pos = pos.offset(left, offset);
				}
			}

			startingPos = pos.toImmutable();

			//looping through cube level by level
			while (yi < getSize()) {
				while (zi < getSize()) {
					while (xi < getSize()) {
						//skip the blocks in the middle
						if (xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest) {
							xi++;
							continue;
						}

						BlockPos currentPos = pos.offset(right, xi);
						IBlockState currentState = world.getBlockState(currentPos);

						if (currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getPos()))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.multipleManagers");

						//checking the lowest and highest level of the cube
						//if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						if ((yi == lowest && !currentPos.equals(getPos())) || yi == highest) { //checking the corners
							if (((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest))) {
								if (!(currentState.getBlock() instanceof ReinforcedCrystalQuartzBlock) || currentState.getValue(BlockQuartz.VARIANT) != EnumType.CHISELED)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CHISELED_CRYSTAL_QUARTZ.getTranslationKey() + ".name"));
							}
							//checking the sides parallel to the block pocket manager
							else if ((zi == lowest || zi == highest) && xi > lowest && xi < highest) {
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								if (currentState.getBlock() instanceof ReinforcedCrystalQuartzBlock) {
									if (currentState.getValue(BlockQuartz.VARIANT) != typeToCheckFor)
										return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
								}
								else
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CRYSTAL_QUARTZ_PILLAR.getTranslationKey() + ".name"));
							}
							//checking the sides orthogonal to the block pocket manager
							else if ((xi == lowest || xi == highest) && zi > lowest && zi < highest) {
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								if (currentState.getBlock() instanceof ReinforcedCrystalQuartzBlock) {
									if (currentState.getValue(BlockQuartz.VARIANT) != typeToCheckFor)
										return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
								}
								else
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CRYSTAL_QUARTZ_PILLAR.getTranslationKey() + ".name"));
							}
							//checking the middle plane
							else if (xi > lowest && zi > lowest && xi < highest && zi < highest) {
								if (!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(SCContent.blockPocketWall.getTranslationKey() + ".name"));

								floor.add(currentPos);
								sides.add(currentPos);
							}
						}
						//checking the corner edges
						else if (yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest))) {
							if (currentState.getBlock() instanceof ReinforcedCrystalQuartzBlock) {
								if (currentState.getValue(BlockQuartz.VARIANT) != EnumType.LINES_Y)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.rotation", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
							}
							else
								return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CRYSTAL_QUARTZ_PILLAR.getTranslationKey() + ".name"));
						}
						//checking the walls parallel and orthogonal to the block pocket manager
						else if (yi > lowest && yi < highest && (((zi == lowest || zi == highest) && xi > lowest && xi < highest) || ((xi == lowest || xi == highest) && zi > lowest && zi < highest))) {
							if (!(currentState.getBlock() instanceof BlockPocketWallBlock))
								return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(SCContent.blockPocketWall.getTranslationKey() + ".name"));

							sides.add(currentPos);
						}

						OwnableBlockEntity te = (OwnableBlockEntity) world.getTileEntity(currentPos);

						if (!getOwner().owns(te))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
						else
							blocks.add(currentPos);

						xi++;
					}

					xi = 0;
					zi++;
					pos = startingPos.up(yi).offset(back, zi);
				}

				zi = 0;
				yi++;
				pos = startingPos.up(yi);
			}

			this.blocks = blocks;
			this.walls = sides;
			this.floor = floor;
			setEnabled(true);
			setAutoBuildOffset(-offset + (getSize() / 2));

			for (BlockPos blockPos : blocks) {
				TileEntity te = world.getTileEntity(blockPos);

				if (te instanceof BlockPocketBlockEntity)
					((BlockPocketBlockEntity) te).setManager(this);
			}

			for (BlockPos blockPos : floor) {
				world.setBlockState(blockPos, world.getBlockState(blockPos).withProperty(BlockPocketWallBlock.SOLID, true));
			}

			setWalls(!isModuleEnabled(ModuleType.DISGUISE));
			return new TextComponentTranslation("messages.securitycraft:blockpocket.activated");
		}

		return null;
	}

	/**
	 * Auto-assembles the Block Pocket for a player. First it makes sure that the space isn't occupied, then it checks its
	 * inventory for the required items, then it places the blocks.
	 *
	 * @return The feedback message. null if none should be sent.
	 */

	public TextComponentTranslation autoAssembleMultiblock() {
		if (world.isRemote)
			return new TextComponentTranslation("autoAssembleMultiblock called on client! Send an AssembleBlockPocket packet instead.");

		if (!isEnabled()) {
			final EnumFacing managerFacing = world.getBlockState(pos).getValue(BlockPocketManagerBlock.FACING);
			final EnumFacing left = managerFacing.rotateY();
			final EnumFacing right = left.getOpposite();
			final EnumFacing back = left.rotateY();
			final BlockPos startingPos;
			final int lowest = 0;
			final int half = (getSize() - 1) / 2 - getAutoBuildOffset();
			final int highest = getSize() - 1;
			BlockPos pos = getPos().toImmutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;
			int wallsNeeded = 0;
			int pillarsNeeded = 0;
			int chiseledNeeded = 0;

			pos = pos.offset(right, -half);
			startingPos = pos.toImmutable();

			//loop through the cube level by level to make sure the building space isn't occupied
			while (yi < getSize()) {
				while (zi < getSize()) {
					while (xi < getSize()) {
						//skip the blocks in the middle
						if (xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest) {
							xi++;
							continue;
						}

						BlockPos currentPos = pos.offset(right, xi);
						IBlockState currentState = world.getBlockState(currentPos);
						boolean replaceable = currentState.getMaterial().isReplaceable();

						//checking the lowest and highest level of the cube
						//if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						if ((yi == lowest && !currentPos.equals(getPos())) || yi == highest) {
							//checking the corners
							if (((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest))) {
								if (!(currentState.getBlock() instanceof ReinforcedCrystalQuartzBlock && currentState.getValue(BlockQuartz.VARIANT) == EnumType.CHISELED) && !replaceable)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if (replaceable)
									chiseledNeeded++;
							}
							//checking the sides parallel to the block pocket manager
							else if ((zi == lowest || zi == highest) && xi > lowest && xi < highest) {
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								if (!isReinforcedCrystalQuartzPillar(currentState) && !replaceable || (currentState.getBlock() instanceof ReinforcedCrystalQuartzBlock && currentState.getValue(BlockQuartz.VARIANT) != typeToCheckFor))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if (replaceable)
									pillarsNeeded++;
							}
							//checking the sides orthogonal to the block pocket manager
							else if ((xi == lowest || xi == highest) && zi > lowest && zi < highest) {
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								if (!isReinforcedCrystalQuartzPillar(currentState) && !replaceable || (currentState.getBlock() instanceof ReinforcedCrystalQuartzBlock && currentState.getValue(BlockQuartz.VARIANT) != typeToCheckFor))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if (replaceable)
									pillarsNeeded++;
							}
							//checking the middle plane
							else if (xi > lowest && zi > lowest && xi < highest && zi < highest) {
								if (!(currentState.getBlock() instanceof BlockPocketWallBlock) && !replaceable)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if (replaceable)
									wallsNeeded++;
							}
						}
						//checking the corner edges
						else if (yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest))) {
							if (!isReinforcedCrystalQuartzPillar(currentState) && !replaceable || (currentState.getBlock() instanceof ReinforcedCrystalQuartzBlock && currentState.getValue(BlockQuartz.VARIANT) != EnumType.LINES_Y))
								return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

							if (replaceable)
								pillarsNeeded++;
						}
						//checking the walls parallel and orthogonal to the block pocket manager
						else if (yi > lowest && yi < highest && (((zi == lowest || zi == highest) && xi > lowest && xi < highest) || ((xi == lowest || xi == highest) && zi > lowest && zi < highest))) {
							if (!(currentState.getBlock() instanceof BlockPocketWallBlock) && !replaceable)
								return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

							if (replaceable)
								wallsNeeded++;
						}

						if (world.getTileEntity(currentPos) instanceof OwnableBlockEntity) {
							OwnableBlockEntity te = (OwnableBlockEntity) world.getTileEntity(currentPos);

							if (!getOwner().owns(te))
								return new TextComponentTranslation("messages.securitycraft:blockpocket.unowned", getFormattedRelativeCoordinates(currentPos, managerFacing), new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
						}

						xi++;
					}

					xi = 0;
					zi++;
					pos = startingPos.up(yi).offset(back, zi);
				}

				zi = 0;
				yi++;
				pos = startingPos.up(yi);
			} //if the code comes to this place, the space is either clear or occupied by blocks that would have been placed either way, or existing blocks can be replaced (like grass)

			if (chiseledNeeded + pillarsNeeded + wallsNeeded == 0) //this applies when no blocks are missing, so when the BP is already in place
				return new TextComponentTranslation("messages.securitycraft:blockpocket.alreadyAssembled");

			pos = getPos().toImmutable().offset(right, -half);
			xi = lowest;
			yi = lowest;
			zi = lowest;

			//add blocks to the auto building queue
			while (yi < getSize()) {
				while (zi < getSize()) {
					while (xi < getSize()) {
						//skip the blocks in the middle again
						if (xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest) {
							xi++;
							continue;
						}

						BlockPos currentPos = pos.offset(right, xi);
						IBlockState currentState = world.getBlockState(currentPos);

						if (currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getPos()))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.multipleManagers");

						//placing the lowest and highest level of the cube
						//if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						if ((yi == lowest && !currentPos.equals(getPos())) || yi == highest) {
							//placing the corners
							if (((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
								placeQueue.add(Pair.of(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockQuartz.VARIANT, EnumType.CHISELED)));
							else if ((zi == lowest || zi == highest) && xi > lowest && xi < highest) { //placing the sides parallel to the block pocket manager
								EnumType typeToPlace = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								placeQueue.add(Pair.of(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockQuartz.VARIANT, typeToPlace)));
							}
							//placing the sides orthogonal to the block pocket manager
							else if ((xi == lowest || xi == highest) && zi > lowest && zi < highest) {
								EnumType typeToPlace = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								placeQueue.add(Pair.of(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockQuartz.VARIANT, typeToPlace)));
							}
							//placing the middle plane
							else if (xi > lowest && zi > lowest && xi < highest && zi < highest)
								placeQueue.add(Pair.of(currentPos, SCContent.blockPocketWall.getDefaultState()));
						}
						//placing the corner edges
						else if (yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							placeQueue.add(Pair.of(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockQuartz.VARIANT, EnumType.LINES_Y)));
						//placing the walls parallel and orthogonal to the block pocket manager
						else if (yi > lowest && yi < highest && (((zi == lowest || zi == highest) && xi > lowest && xi < highest) || ((xi == lowest || xi == highest) && zi > lowest && zi < highest)))
							placeQueue.add(Pair.of(currentPos, SCContent.blockPocketWall.getDefaultState()));

						xi++;
					}

					xi = 0;
					zi++;
					pos = startingPos.up(yi).offset(back, zi);
				}

				zi = 0;
				yi++;
				pos = startingPos.up(yi);
			}

			shouldPlaceBlocks = true;
			return null;
		}

		return null;
	}

	public TextComponentBase disableMultiblock() {
		if (world.isRemote)
			return new TextComponentString("disableMultiblock called on client! Send a ToggleBlockPocketManager packet instead.");

		if (isEnabled()) {
			IBlockState state = world.getBlockState(pos);

			setEnabled(false);

			for (BlockPos pos : blocks) {
				TileEntity te = world.getTileEntity(pos);

				if (te instanceof BlockPocketBlockEntity)
					((BlockPocketBlockEntity) te).removeManager();
			}

			for (BlockPos pos : floor) {
				IBlockState floorState = world.getBlockState(pos);

				if (floorState.getProperties().containsKey(BlockPocketWallBlock.SOLID))
					world.setBlockState(pos, floorState.withProperty(BlockPocketWallBlock.SOLID, false));
			}

			if (isModuleEnabled(ModuleType.DISGUISE))
				setWalls(true);

			blocks.clear();
			walls.clear();
			floor.clear();
			markDirty();
			world.notifyBlockUpdate(pos, state, state, 3);
			return Utils.localize("messages.securitycraft:blockpocket.deactivated");
		}

		return null;
	}

	private TextComponentTranslation getFormattedRelativeCoordinates(BlockPos pos, EnumFacing managerFacing) {
		BlockPos difference = pos.subtract(this.pos);
		int offsetLeft;
		int offsetBehind;
		int offsetAbove = difference.getY();
		List<TextComponentTranslation> components = new ArrayList<>();

		switch (managerFacing) {
			case NORTH:
				offsetBehind = difference.getZ();
				offsetLeft = difference.getX();
				break;
			case SOUTH:
				offsetBehind = -difference.getZ();
				offsetLeft = -difference.getX();
				break;
			case WEST:
				offsetBehind = difference.getX();
				offsetLeft = -difference.getZ();
				break;
			case EAST:
				offsetBehind = -difference.getX();
				offsetLeft = difference.getZ();
				break;
			default:
				throw new IllegalArgumentException("Invalid Block Pocket Manager direction: " + managerFacing.name());
		}

		if (offsetLeft > 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksLeft", offsetLeft));
		else if (offsetLeft < 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksRight", -offsetLeft));

		if (offsetBehind > 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksBehind", offsetBehind));

		if (offsetAbove > 0)
			components.add(Utils.localize("messages.securitycraft:blockpocket.position.blocksAbove", offsetAbove));

		return Utils.localize("messages.securitycraft:blockpocket.position." + components.size(), components.toArray());
	}

	public void toggleOutline() {
		setShowOutline(!showsOutline());
	}

	public void setWalls(boolean seeThrough) {
		for (BlockPos pos : walls) {
			IBlockState state = world.getBlockState(pos);

			if (state.getBlock() instanceof BlockPocketWallBlock)
				world.setBlockState(pos, state.withProperty(BlockPocketWallBlock.SEE_THROUGH, seeThrough));
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side) {
		//"!isPlacingBlocks()" prevents extracting while auto building the block pocket
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return !isPlacingBlocks() && BlockUtils.isAllowedToExtractFromProtectedObject(side, this) ? (T) getStorageHandler() : (T) getInsertOnlyHandler();
		else
			return super.getCapability(cap, side);
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if (world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() != SCContent.blockPocketManager)
			disableMultiblock();
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (isEnabled() && module == ModuleType.DISGUISE)
			setWalls(false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (isEnabled() && module == ModuleType.DISGUISE)
			setWalls(true);
		else if (module == ModuleType.STORAGE) {
			IItemHandler handler = getStorageHandler();

			for (int i = 0; i < handler.getSlots(); i++) {
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("BlockPocketEnabled", isEnabled());
		tag.setBoolean("ShowOutline", showsOutline());
		tag.setInteger("Size", getSize());
		tag.setInteger("AutoBuildOffset", getAutoBuildOffset());
		tag.setInteger("Color", color);
		ItemStackHelper.saveAllItems(tag, storage);

		for (int i = 0; i < blocks.size(); i++) {
			tag.setLong("BlocksList" + i, blocks.get(i).toLong());
		}

		for (int i = 0; i < walls.size(); i++) {
			tag.setLong("WallsList" + i, walls.get(i).toLong());
		}

		for (int i = 0; i < floor.size(); i++) {
			tag.setLong("FloorList" + i, floor.get(i).toLong());
		}

		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		int i = 0;

		super.readFromNBT(tag);
		setEnabled(tag.getBoolean("BlockPocketEnabled"));
		setShowOutline(tag.getBoolean("ShowOutline"));
		setSize(tag.getInteger("Size"));
		setAutoBuildOffset(tag.getInteger("AutoBuildOffset"));
		setColor(tag.getInteger("Color"));
		ItemStackHelper.loadAllItems(tag, storage);

		while (tag.hasKey("BlocksList" + i)) {
			blocks.add(BlockPos.fromLong(tag.getLong("BlocksList" + i)));
			i++;
		}

		i = 0;

		while (tag.hasKey("WallsList" + i)) {
			walls.add(BlockPos.fromLong(tag.getLong("WallsList" + i)));
			i++;
		}

		i = 0;

		while (tag.hasKey("FloorList" + i)) {
			floor.add(BlockPos.fromLong(tag.getLong("FloorList" + i)));
			i++;
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST, ModuleType.STORAGE
		};
	}

	@Override
	public String getModuleDescriptionId(String denotation, ModuleType module) {
		return IModuleInventory.getBaseModuleDescriptionId(denotation, module); //the disguise module description is generic and doesn't apply to the block pocket manager
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[0];
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(RENDER_DISTANCE);
	}

	private boolean isReinforcedCrystalQuartzPillar(IBlockState state) {
		if (state.getBlock() instanceof ReinforcedCrystalQuartzBlock) {
			EnumType type = state.getValue(BlockQuartz.VARIANT);

			return type == EnumType.LINES_X || type == EnumType.LINES_Y || type == EnumType.LINES_Z;
		}
		else
			return false;
	}

	public IItemHandler getStorageHandler() {
		if (storageHandler == null) {
			storageHandler = new ItemStackHandler(storage) {
				@Override
				public boolean isItemValid(int slot, ItemStack stack) {
					return BlockPocketManagerBlockEntity.isItemValid(stack);
				}
			};
		}

		return storageHandler;
	}

	private IItemHandler getInsertOnlyHandler() {
		if (insertOnlyHandler == null) {
			insertOnlyHandler = new InsertOnlyItemStackHandler(storage) {
				@Override
				public boolean isItemValid(int slot, ItemStack stack) {
					return BlockPocketManagerBlockEntity.isItemValid(stack);
				}
			};
		}

		return insertOnlyHandler;
	}

	public boolean isPlacingBlocks() {
		return shouldPlaceBlocks;
	}

	public static boolean isItemValid(ItemStack stack) {
		if (stack.getItem() instanceof ItemBlock) {
			Block block = ((ItemBlock) stack.getItem()).getBlock();

			return block == SCContent.blockPocketWall || (block == SCContent.reinforcedCrystalQuartz && stack.getMetadata() >= 1);
		}

		return false;
	}

	public void setColor(int color) {
		this.color = MathHelper.clamp(color, 0xFF000000, 0xFFFFFFFF);
	}

	public int getColor() {
		return color;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean showsOutline() {
		return showOutline;
	}

	public void setShowOutline(boolean showOutline) {
		this.showOutline = showOutline;
	}

	public int getAutoBuildOffset() {
		return autoBuildOffset;
	}

	public void setAutoBuildOffset(int autoBuildOffset) {
		this.autoBuildOffset = autoBuildOffset;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
