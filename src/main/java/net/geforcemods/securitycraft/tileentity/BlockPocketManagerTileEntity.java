package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.blocks.BlockPocketManagerBlock;
import net.geforcemods.securitycraft.blocks.BlockPocketWallBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedRotatedPillarBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.AssembleBlockPocket;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

public class BlockPocketManagerTileEntity extends CustomizableTileEntity implements INamedContainerProvider
{
	public static final int RENDER_DISTANCE = 100;

	public boolean enabled = false;
	public boolean showOutline = false;
	public int size = 5;
	private List<BlockPos> blocks = new ArrayList<>();
	private List<BlockPos> walls = new ArrayList<>();
	private List<BlockPos> floor = new ArrayList<>();

	public BlockPocketManagerTileEntity()
	{
		super(SCContent.teTypeBlockPocketManager);
	}

	/**
	 * Enables the block pocket
	 * @return The feedback message. null if none should be sent.
	 */

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(RENDER_DISTANCE);
	}

	public TranslationTextComponent enableMultiblock()
	{
		if(!enabled) //multiblock detection
		{
			if(world.isRemote)
				SecurityCraft.channel.sendToServer(new ToggleBlockPocketManager(this, true, size));

			List<BlockPos> blocks = new ArrayList<>();
			List<BlockPos> sides = new ArrayList<>();
			List<BlockPos> floor = new ArrayList<>();
			final Direction managerFacing = world.getBlockState(pos).get(BlockPocketManagerBlock.FACING);
			final Direction left = managerFacing.rotateY();
			final Direction right = left.getOpposite();
			final Direction back = left.rotateY();
			final BlockPos startingPos;
			final int lowest = 0;
			final int highest = size - 1;
			BlockPos pos = getPos().toImmutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;

			while(world.getBlockState(pos = pos.offset(left)).getBlock() instanceof IBlockPocket) //find the bottom left corner
				;

			pos = pos.offset(right); //pos got offset one too far (which made the while loop above stop) so it needs to be corrected
			startingPos = pos.toImmutable();

			//looping through cube level by level
			while(yi < size)
			{
				while(zi < size)
				{
					while(xi < size)
					{
						//skip the blocks in the middle
						if(xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest)
						{
							xi++;
							continue;
						}

						BlockPos currentPos = pos.offset(right, xi);
						BlockState currentState = world.getBlockState(currentPos);

						if(currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getPos()))
							return new TranslationTextComponent("messages.securitycraft:blockpocket.multipleManagers");

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(currentState.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get())
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get().getTranslationKey()));
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.get(BlockStateProperties.AXIS) != typeToCheckFor)
								{
									if(currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
										return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock.rotation", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getTranslationKey()));
								}
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.get(BlockStateProperties.AXIS) != typeToCheckFor)
								{
									if (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
										return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock.rotation", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getTranslationKey()));
								}
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.BLOCK_POCKET_WALL.get().getTranslationKey()));

								floor.add(currentPos);
								sides.add(currentPos);
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.get(BlockStateProperties.AXIS) != Axis.Y)
							{
								if (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock.rotation", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
								return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getTranslationKey()));
							}
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.BLOCK_POCKET_WALL.get().getTranslationKey()));

								sides.add(currentPos);
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()), new TranslationTextComponent(SCContent.BLOCK_POCKET_WALL.get().getTranslationKey()));

								sides.add(currentPos);
							}
						}

						OwnableTileEntity te = (OwnableTileEntity)world.getTileEntity(currentPos);

						if(!getOwner().owns(te))
							return new TranslationTextComponent("messages.securitycraft:blockpocket.unowned", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
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
			enabled = true;

			for(BlockPos blockPos : blocks)
			{
				TileEntity te = world.getTileEntity(blockPos);

				if(te instanceof BlockPocketTileEntity)
					((BlockPocketTileEntity)te).setManager(this);
			}

			for(BlockPos blockPos : floor)
			{
				world.setBlockState(blockPos, world.getBlockState(blockPos).with(BlockPocketWallBlock.SOLID, true));
			}

			setWalls(!hasModule(ModuleType.DISGUISE));
			return new TranslationTextComponent("messages.securitycraft:blockpocket.activated");
		}

		return null;
	}

	/**
	 * Auto-assembles the Block Pocket for a player.
	 * First it makes sure that the space isn't occupied, then it checks the inventory of the player for the required items, then it places the blocks.
	 * @param player The player that opened the screen, used to check if the player is in creative or not
	 * @return The feedback message. null if none should be sent.
	 */
	public TranslationTextComponent autoAssembleMultiblock(PlayerEntity player)
	{
		if(!enabled) //multiblock assembling in three steps
		{
			if(world.isRemote)
				SecurityCraft.channel.sendToServer(new AssembleBlockPocket(this, size));

			final Direction managerFacing = world.getBlockState(pos).get(BlockPocketManagerBlock.FACING);
			final Direction left = managerFacing.rotateY();
			final Direction right = left.getOpposite();
			final Direction back = left.rotateY();
			final BlockPos startingPos;
			final int lowest = 0;
			final int half = (size - 1) / 2;
			final int highest = size - 1;
			BlockPos pos = getPos().toImmutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;
			int wallsNeeded = 0;
			int pillarsNeeded = 0;
			int chiseledNeeded = 0;

			pos = pos.offset(right, -half);
			startingPos = pos.toImmutable();

			//Step 1: looping through cube level by level to make sure the space where the BP should go to isn't occupied
			while(yi < size)
			{
				while(zi < size)
				{
					while(xi < size)
					{
						//skip the blocks in the middle
						if(xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest)
						{
							xi++;
							continue;
						}

						BlockPos currentPos = pos.offset(right, xi);
						BlockState currentState = world.getBlockState(currentPos);

						currentState.getMaterial().isReplaceable();

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(currentState.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get() && !(currentState.getMaterial().isReplaceable()))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(currentState.getMaterial().isReplaceable()) chiseledNeeded++;
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !(currentState.getMaterial().isReplaceable()) || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.get(BlockStateProperties.AXIS) != typeToCheckFor))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(currentState.getMaterial().isReplaceable()) pillarsNeeded++;
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								Axis typeToCheckFor = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;

								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !(currentState.getMaterial().isReplaceable()) || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.get(BlockStateProperties.AXIS) != typeToCheckFor))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(currentState.getMaterial().isReplaceable()) pillarsNeeded++;
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock) && !(currentState.getMaterial().isReplaceable()))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(currentState.getMaterial().isReplaceable()) wallsNeeded++;
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && !(currentState.getMaterial().isReplaceable()) || (currentState.getBlock() == SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() && currentState.get(BlockStateProperties.AXIS) != Axis.Y))
								return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

							if(currentState.getMaterial().isReplaceable()) pillarsNeeded++;
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock) && !(currentState.getMaterial().isReplaceable()))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(currentState.getMaterial().isReplaceable()) wallsNeeded++;
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock) && !(currentState.getMaterial().isReplaceable()))
									return new TranslationTextComponent("messages.securitycraft:blockpocket.blockInWay", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));

								if(currentState.getMaterial().isReplaceable()) wallsNeeded++;
							}
						}

						if(world.getTileEntity(currentPos) instanceof OwnableTileEntity)
						{
							OwnableTileEntity te = (OwnableTileEntity)world.getTileEntity(currentPos);

							if(!getOwner().owns(te))
								return new TranslationTextComponent("messages.securitycraft:blockpocket.unowned", currentPos, new TranslationTextComponent(currentState.getBlock().asItem().getTranslationKey()));
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
			} //if the code comes to this place, the space is either clear or occupied by blocks that would have been placed either way.

			if(chiseledNeeded + pillarsNeeded + wallsNeeded == 0) //this applies when no blocks are missing, so when the BP is already in place
				return new TranslationTextComponent("messages.securitycraft:blockpocket.alreadyAssembled");
			if(!player.isCreative()) //Step 2: if the player isn't in creative, it is checked if he has enough items to build the BP
			{
				int chiseledFound = 0, pillarsFound = 0, wallsFound = 0;
				NonNullList<ItemStack> inventory = player.inventory.mainInventory;
				for(int i = 1; i <= inventory.size(); i++)
				{
					ItemStack itemStackChecking = inventory.get(i - 1);

					if(!itemStackChecking.isEmpty())
					{
						if (Block.getBlockFromItem(itemStackChecking.getItem()) instanceof ShulkerBoxBlock) //there has to be a check for shulker boxes, otherwise the huge BP's that take 4000 blocks to build couldn't be auto-assembled due to lack of inventory space
						{
							ListNBT list = itemStackChecking.getTag().getCompound("BlockEntityTag").getList("Items", Constants.NBT.TAG_COMPOUND);

							for(int j = 0; j < list.size(); j++) {
								ItemStack itemInChest = ItemStack.read(list.getCompound(j));
								if(itemInChest.getItem() == new ItemStack(SCContent.BLOCK_POCKET_WALL.get()).getItem())
								{
									wallsFound+=itemInChest.getCount();
								}
								else if(itemInChest.getItem() == new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get()).getItem())
								{
									chiseledFound+=itemInChest.getCount();

								}
								else if(itemInChest.getItem() == new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()).getItem())
								{
									pillarsFound+=itemInChest.getCount();

								}
							}
						}

						if(itemStackChecking.getItem() == new ItemStack(SCContent.BLOCK_POCKET_WALL.get()).getItem())
						{
							wallsFound+=itemStackChecking.getCount();
						}
						else if(itemStackChecking.getItem() == new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get()).getItem())
						{
							chiseledFound+=itemStackChecking.getCount();
						}
						else if(itemStackChecking.getItem() == new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()).getItem())
						{
							pillarsFound+=itemStackChecking.getCount();
						}
					}
				}
				if (chiseledNeeded > chiseledFound || pillarsNeeded > pillarsFound || wallsNeeded > wallsFound)
					return new TranslationTextComponent("messages.securitycraft:blockpocket.notEnoughItems");

				for(int i = 1; i <= inventory.size(); i++) //actually take the items out that are used for completing the BP
				{
					ItemStack itemStackChecking = inventory.get(i - 1);

					if(!itemStackChecking.isEmpty())
					{
						if (Block.getBlockFromItem(itemStackChecking.getItem()) instanceof ShulkerBoxBlock)
						{
							ListNBT list = itemStackChecking.getTag().getCompound("BlockEntityTag").getList("Items", Constants.NBT.TAG_COMPOUND);

							for(int j = 0; j < list.size(); j++) {
								CompoundNBT compound = list.getCompound(j);
								ItemStack itemInChest = ItemStack.read(compound);
								int count = itemInChest.getCount();

								if(itemInChest.getItem() == new ItemStack(SCContent.BLOCK_POCKET_WALL.get()).getItem())
								{
									if (count <= wallsNeeded)
									{
										list.remove(j);
										wallsNeeded-=count;
									}
									else
									{
										while (wallsNeeded != 0)
										{
											count = itemInChest.getCount(); //update count
											itemInChest.setCount(count-1);
											wallsNeeded--;
										}
										compound.putInt("Count", count-1);
									}
								}
								else if(itemInChest.getItem() == new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get()).getItem())
								{
									if (count <= chiseledNeeded)
									{
										list.remove(j);
										chiseledNeeded-=count;
									}
									else
									{
										while (chiseledNeeded != 0)
										{
											count = itemInChest.getCount(); //update count
											itemInChest.setCount(count-1);
											chiseledNeeded--;
										}
										compound.putInt("Count", count-1);
									}
								}
								else if(itemInChest.getItem() == new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()).getItem())
								{
									if (count <= pillarsNeeded)
									{
										list.remove(j);
										pillarsNeeded-=count;
									}
									else
									{
										while (pillarsNeeded != 0)
										{
											count = itemInChest.getCount(); //update count
											itemInChest.setCount(count-1);
											pillarsNeeded--;
										}
										compound.putInt("Count", count-1);
									}
								}
							}
						}

						int count = itemStackChecking.getCount();

						if(itemStackChecking.getItem() == new ItemStack(SCContent.BLOCK_POCKET_WALL.get()).getItem())
						{
							if (count <= wallsNeeded)
							{
								inventory.set(i - 1, ItemStack.EMPTY);
								wallsNeeded-=count;
							}
							else
							{
								while (wallsNeeded != 0)
								{
									count = itemStackChecking.getCount();
									itemStackChecking.setCount(count-1);
									wallsNeeded--;
								}
								inventory.set(i - 1, itemStackChecking);
							}
						}
						else if(itemStackChecking.getItem() == new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get()).getItem())
						{
							if (count <= chiseledNeeded)
							{
								inventory.set(i - 1, ItemStack.EMPTY);
								chiseledNeeded-=count;
							}
							else
							{
								while (chiseledNeeded != 0)
								{
									count = itemStackChecking.getCount();
									itemStackChecking.setCount(count-1);
									chiseledNeeded--;
								}
								inventory.set(i - 1, itemStackChecking);
							}
						}
						else if(itemStackChecking.getItem() == new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()).getItem())
						{
							if (count <= pillarsNeeded)
							{
								inventory.set(i - 1, ItemStack.EMPTY);
								pillarsNeeded-=count;
							}
							else
							{
								while (pillarsNeeded != 0)
								{
									count = itemStackChecking.getCount();
									itemStackChecking.setCount(count-1);
									pillarsNeeded--;
								}
								inventory.set(i - 1, itemStackChecking);
							}
						}
					}
				}
			}
			pos = getPos().toImmutable().offset(right, -half);
			xi = lowest;
			yi = lowest;
			zi = lowest;

			while(yi < size) //Step 3: placing the blocks and giving them the right owner
			{
				while(zi < size)
				{
					while(xi < size)
					{
						//skip the blocks in the middle again
						if(xi > lowest && yi > lowest && zi > lowest && xi < highest && yi < highest && zi < highest)
						{
							xi++;
							continue;
						}
						BlockPos currentPos = pos.offset(right, xi);
						BlockState currentState = world.getBlockState(currentPos);
						if(currentState.getBlock() instanceof BlockPocketManagerBlock && !currentPos.equals(getPos()))
							return new TranslationTextComponent("messages.securitycraft:blockpocket.multipleManagers");
						//placing the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//placing the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(currentState.getBlock() != SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get())
									world.setBlockState(currentPos, SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get().getDefaultState());
							}
							//placing the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								Axis typeToPlace = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.X : Axis.Z;
								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
									world.setBlockState(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDefaultState().with(ReinforcedRotatedPillarBlock.AXIS, typeToPlace));
							}
							//placing the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								Axis typeToPlace = managerFacing == Direction.NORTH || managerFacing == Direction.SOUTH ? Axis.Z : Axis.X;
								if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get())
									world.setBlockState(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDefaultState().with(ReinforcedRotatedPillarBlock.AXIS, typeToPlace));
							}
							//placing the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									world.setBlockState(currentPos, SCContent.BLOCK_POCKET_WALL.get().getDefaultState());
							}
						}
						//placing the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(currentState.getBlock() != SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get() || currentState.get(BlockStateProperties.AXIS) != Axis.Y)
								world.setBlockState(currentPos, SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get().getDefaultState().with(ReinforcedRotatedPillarBlock.AXIS, Axis.Y));
						}
						//placing the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									world.setBlockState(currentPos, SCContent.BLOCK_POCKET_WALL.get().getDefaultState());
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockPocketWallBlock))
									world.setBlockState(currentPos, SCContent.BLOCK_POCKET_WALL.get().getDefaultState());
							}
						}
						//assigning the owner
						if(world.getTileEntity(currentPos) instanceof OwnableTileEntity)
						{
							OwnableTileEntity te = (OwnableTileEntity)world.getTileEntity(currentPos);
							te.getOwner().set(getOwner());
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
			}
			return new TranslationTextComponent("messages.securitycraft:blockpocket.assembled");
		}
		return null;
	}

	public void disableMultiblock()
	{
		if(enabled)
		{
			if(world.isRemote)
			{
				SecurityCraft.channel.sendToServer(new ToggleBlockPocketManager(this, false, size));
				PlayerUtils.sendMessageToPlayer(SecurityCraft.proxy.getClientPlayer(), ClientUtils.localize(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:blockpocket.deactivated"), TextFormatting.DARK_AQUA);
			}

			enabled = false;

			for(BlockPos pos : blocks)
			{
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof BlockPocketTileEntity)
					((BlockPocketTileEntity)te).removeManager();
			}

			for(BlockPos pos : floor)
			{
				BlockState state = world.getBlockState(pos);

				if(state.has(BlockPocketWallBlock.SOLID))
					world.setBlockState(pos, state.with(BlockPocketWallBlock.SOLID, false));
			}

			if(hasModule(ModuleType.DISGUISE))
				setWalls(true);

			blocks.clear();
			walls.clear();
			floor.clear();
		}
	}

	public void toggleOutline()
	{
		showOutline = !showOutline;
	}

	public void setWalls(boolean seeThrough)
	{
		for(BlockPos pos : walls)
		{
			BlockState state = world.getBlockState(pos);

			if(state.getBlock() instanceof BlockPocketWallBlock)
				world.setBlockState(pos, state.with(BlockPocketWallBlock.SEE_THROUGH, seeThrough));
		}
	}

	@Override
	public void onTileEntityDestroyed()
	{
		super.onTileEntityDestroyed();
		if (world.getBlockState(pos).getBlock() != SCContent.BLOCK_POCKET_MANAGER.get())
			disableMultiblock();
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		if(enabled && module == ModuleType.DISGUISE)
			setWalls(false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		if(enabled && module == ModuleType.DISGUISE)
			setWalls(true);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		tag.putBoolean("BlockPocketEnabled", enabled);
		tag.putBoolean("ShowOutline", showOutline);
		tag.putInt("Size", size);

		for(int i = 0; i < blocks.size(); i++)
		{
			tag.putLong("BlocksList" + i, blocks.get(i).toLong());
		}

		for(int i = 0; i < walls.size(); i++)
		{
			tag.putLong("WallsList" + i, walls.get(i).toLong());
		}

		for(int i = 0; i < floor.size(); i++)
		{
			tag.putLong("FloorList" + i, floor.get(i).toLong());
		}

		return super.write(tag);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		int i = 0;

		super.read(tag);
		enabled = tag.getBoolean("BlockPocketEnabled");
		showOutline = tag.getBoolean("ShowOutline");
		size = tag.getInt("Size");

		while(tag.contains("BlocksList" + i))
		{
			blocks.add(BlockPos.fromLong(tag.getLong("BlocksList" + i)));
			i++;
		}

		i = 0;

		while(tag.contains("WallsList" + i))
		{
			walls.add(BlockPos.fromLong(tag.getLong("WallsList" + i)));
			i++;
		}

		i = 0;

		while(tag.contains("FloorList" + i))
		{
			floor.add(BlockPos.fromLong(tag.getLong("FloorList" + i)));
			i++;
		}
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {
				ModuleType.DISGUISE,
				ModuleType.WHITELIST
		};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new GenericTEContainer(SCContent.cTypeBlockPocketManager, windowId, world, pos);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(SCContent.BLOCK_POCKET_MANAGER.get().getTranslationKey());
	}
}
