package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.blocks.BlockBlockPocketManager;
import net.geforcemods.securitycraft.blocks.BlockBlockPocketWall;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedCrystalQuartz;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.packets.PacketCToggleBlockPocketManager;
import net.geforcemods.securitycraft.network.packets.PacketSAssembleBlockPocket;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TileEntityBlockPocketManager extends CustomizableSCTE
{
	public static final int RENDER_DISTANCE = 100;
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 1);
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 2);
	public boolean enabled = false;
	public boolean showOutline = false;
	public int size = 5;
	private List<BlockPos> blocks = new ArrayList<>();
	private List<BlockPos> walls = new ArrayList<>();
	private List<BlockPos> floor = new ArrayList<>();

	/**
	 * Enables the block pocket
	 * @return The feedback message. null if none should be sent.
	 */
	public TextComponentTranslation enableMultiblock()
	{
		if(!enabled) //multiblock detection
		{
			if(world.isRemote)
				SecurityCraft.network.sendToServer(new PacketCToggleBlockPocketManager(this, true, size));

			List<BlockPos> blocks = new ArrayList<>();
			List<BlockPos> sides = new ArrayList<>();
			List<BlockPos> floor = new ArrayList<>();
			final EnumFacing managerFacing = world.getBlockState(pos).getValue(BlockBlockPocketManager.FACING);
			final EnumFacing left = managerFacing.rotateY();
			final EnumFacing right = left.getOpposite();
			final EnumFacing back = left.rotateY();
			final BlockPos startingPos;
			final int lowest = 0;
			final int highest = size - 1;
			BlockPos pos = getPos().toImmutable();
			int xi = lowest;
			int yi = lowest;
			int zi = lowest;

			while(world.getBlockState(pos = pos.offset(left)).getBlock() instanceof BlockReinforcedCrystalQuartz) //find the bottom left corner
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
						IBlockState currentState = world.getBlockState(currentPos);

						if(currentState.getBlock() instanceof BlockBlockPocketManager && !currentPos.equals(getPos()))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.multipleManagers");

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz) || currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != EnumType.CHISELED)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CHISELED_CRYSTAL_QUARTZ.getTranslationKey() + ".name"));
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								if(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz)
								{
									if(currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor)
										return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.rotation", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
								}
								else
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CRYSTAL_QUARTZ_PILLAR.getTranslationKey() + ".name"));
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								if(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz)
								{
									if(currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor)
										return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.rotation", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
								}
								else
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CRYSTAL_QUARTZ_PILLAR.getTranslationKey() + ".name"));
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(SCContent.blockPocketWall.getTranslationKey() + ".name"));

								floor.add(currentPos);
								sides.add(currentPos);
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz)
							{
								if(currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != EnumType.LINES_Y)
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock.rotation", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
							}
							else
								return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(REINFORCED_CRYSTAL_QUARTZ_PILLAR.getTranslationKey() + ".name"));
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(SCContent.blockPocketWall.getTranslationKey() + ".name"));

								sides.add(currentPos);
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.invalidBlock", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"), new TextComponentTranslation(SCContent.blockPocketWall.getTranslationKey() + ".name"));

								sides.add(currentPos);
							}
						}

						TileEntityOwnable te = (TileEntityOwnable)world.getTileEntity(currentPos);

						if(!getOwner().owns(te))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.unowned", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
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

				if(te instanceof TileEntityBlockPocket)
					((TileEntityBlockPocket)te).setManager(this);
			}

			for(BlockPos blockPos : floor)
			{
				world.setBlockState(blockPos, world.getBlockState(blockPos).withProperty(BlockBlockPocketWall.SOLID, true));
			}

			setWalls(!hasModule(EnumModuleType.DISGUISE));
			return new TextComponentTranslation("messages.securitycraft:blockpocket.activated");
		}

		return null;
	}

	/**
	 * Auto-assembles the Block Pocket for a player.
	 * First it makes sure that the space isn't occupied, then it checks the inventory of the player for the required items, then it places the blocks.
	 * @param player The player that opened the screen, used to check if the player is in creative or not
	 * @return The feedback message. null if none should be sent.
	 */
	public TextComponentTranslation autoAssembleMultiblock(EntityPlayer player)
	{
		if(!enabled) //multiblock assembling in three steps
		{
			if(world.isRemote)
				SecurityCraft.network.sendToServer(new PacketSAssembleBlockPocket(this, size));

			final EnumFacing managerFacing = world.getBlockState(pos).getValue(BlockBlockPocketManager.FACING);
			final EnumFacing left = managerFacing.rotateY();
			final EnumFacing right = left.getOpposite();
			final EnumFacing back = left.rotateY();
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
						IBlockState currentState = world.getBlockState(currentPos);

						currentState.getMaterial().isReplaceable();

						//checking the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//checking the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz && currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) == EnumType.CHISELED) && !(currentState.getMaterial().isReplaceable()))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(currentState.getMaterial().isReplaceable())
									chiseledNeeded++;
							}
							//checking the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								if(!isReinforcedCrystalQuartzPillar(currentState) && !(currentState.getMaterial().isReplaceable()) || (currentState.getBlock() instanceof BlockReinforcedCrystalQuartz && currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(currentState.getMaterial().isReplaceable())
									pillarsNeeded++;
							}
							//checking the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								EnumType typeToCheckFor = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								if(!isReinforcedCrystalQuartzPillar(currentState) && !(currentState.getMaterial().isReplaceable()) || (currentState.getBlock() instanceof BlockReinforcedCrystalQuartz && currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != typeToCheckFor))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(currentState.getMaterial().isReplaceable())
									pillarsNeeded++;
							}
							//checking the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall) && !(currentState.getMaterial().isReplaceable()))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(currentState.getMaterial().isReplaceable())
									wallsNeeded++;
							}
						}
						//checking the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(!isReinforcedCrystalQuartzPillar(currentState) && !(currentState.getMaterial().isReplaceable()) || (currentState.getBlock() instanceof BlockReinforcedCrystalQuartz && currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != EnumType.LINES_Y))
								return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

							if(currentState.getMaterial().isReplaceable())
								pillarsNeeded++;
						}
						//checking the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall) && !(currentState.getMaterial().isReplaceable()))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(currentState.getMaterial().isReplaceable())
									wallsNeeded++;
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall) && !(currentState.getMaterial().isReplaceable()))
									return new TextComponentTranslation("messages.securitycraft:blockpocket.blockInWay", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));

								if(currentState.getMaterial().isReplaceable())
									wallsNeeded++;
							}
						}

						if(world.getTileEntity(currentPos) instanceof TileEntityOwnable)
						{
							TileEntityOwnable te = (TileEntityOwnable)world.getTileEntity(currentPos);

							if(!getOwner().owns(te))
								return new TextComponentTranslation("messages.securitycraft:blockpocket.unowned", currentPos, new TextComponentTranslation(currentState.getBlock().getItem(world, currentPos, currentState).getTranslationKey() + ".name"));
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

			if(chiseledNeeded + pillarsNeeded + wallsNeeded == 0) //this applies when no blocks are missing, so when the BP is already in place
				return new TextComponentTranslation("messages.securitycraft:blockpocket.alreadyAssembled");

			//Step 2: if the player isn't in creative, it is checked if they have enough items to build the BP. If so, they're removed
			if(!player.isCreative())
			{
				int chiseledFound = 0;
				int pillarsFound = 0;
				int wallsFound = 0;
				NonNullList<ItemStack> inventory = player.inventory.mainInventory;

				for(int i = 1; i <= inventory.size(); i++)
				{
					ItemStack stackToCheck = inventory.get(i - 1);

					if(!stackToCheck.isEmpty() && stackToCheck.getItem() instanceof ItemBlock)
					{
						Block block = ((ItemBlock)stackToCheck.getItem()).getBlock();

						if(block instanceof BlockShulkerBox && stackToCheck.hasTagCompound()) //there has to be a check for shulker boxes, otherwise the huge BPs that take 4000 blocks to build couldn't be auto-assembled due to lack of inventory space
						{
							NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);

							ItemStackHelper.loadAllItems(stackToCheck.getTagCompound().getCompoundTag("BlockEntityTag"), contents);

							for(ItemStack boxStack : contents)
							{
								if(!(boxStack.getItem() instanceof ItemBlock))
									continue;

								block = ((ItemBlock)boxStack.getItem()).getBlock();

								if(block == SCContent.blockPocketWall)
									wallsFound += boxStack.getCount();
								else if(block == SCContent.reinforcedCrystalQuartz && boxStack.getMetadata() == 1)
									chiseledFound += boxStack.getCount();
								else if(block == SCContent.reinforcedCrystalQuartz && boxStack.getMetadata() == 2)
									pillarsFound += boxStack.getCount();
							}
						}
						else if(block == SCContent.blockPocketWall)
							wallsFound += stackToCheck.getCount();
						else if(block == SCContent.reinforcedCrystalQuartz && stackToCheck.getMetadata() == 1)
							chiseledFound += stackToCheck.getCount();
						else if(block == SCContent.reinforcedCrystalQuartz && stackToCheck.getMetadata() == 2)
							pillarsFound += stackToCheck.getCount();
					}
				}

				if(chiseledNeeded > chiseledFound || pillarsNeeded > pillarsFound || wallsNeeded > wallsFound)
					return new TextComponentTranslation("messages.securitycraft:blockpocket.notEnoughItems");

				for(int i = 1; i <= inventory.size(); i++) //actually take out the items that are used for assembling the BP
				{
					ItemStack stackToCheck = inventory.get(i - 1);

					if(!stackToCheck.isEmpty() && stackToCheck.getItem() instanceof ItemBlock)
					{
						Block block = ((ItemBlock)stackToCheck.getItem()).getBlock();
						int count = stackToCheck.getCount();

						if(block instanceof BlockShulkerBox && stackToCheck.hasTagCompound())
						{
							NBTTagCompound stackTag = stackToCheck.getTagCompound();
							NBTTagCompound blockEntityTag = stackTag.getCompoundTag("BlockEntityTag");
							NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);

							ItemStackHelper.loadAllItems(blockEntityTag, contents);

							for(int j = 0; j < contents.size(); j++)
							{
								ItemStack boxStack = contents.get(j);

								if(!(boxStack.getItem() instanceof ItemBlock))
									continue;

								block = ((ItemBlock)boxStack.getItem()).getBlock();
								count = boxStack.getCount();

								if(block == SCContent.blockPocketWall)
								{
									if(count <= wallsNeeded)
									{
										contents.set(j, ItemStack.EMPTY);
										wallsNeeded -= count;
									}
									else
									{
										while(wallsNeeded != 0)
										{
											boxStack.shrink(1);
											wallsNeeded--;
										}
									}
								}
								else if(block == SCContent.reinforcedCrystalQuartz && boxStack.getMetadata() == 1)
								{
									if(count <= chiseledNeeded)
									{
										contents.set(j, ItemStack.EMPTY);
										chiseledNeeded -= count;
									}
									else
									{
										while(chiseledNeeded != 0)
										{
											boxStack.shrink(1);
											chiseledNeeded--;
										}
									}
								}
								else if(block == SCContent.reinforcedCrystalQuartz && boxStack.getMetadata() == 2)
								{
									if(count <= pillarsNeeded)
									{
										contents.set(j, ItemStack.EMPTY);
										pillarsNeeded -= count;
									}
									else
									{
										while(pillarsNeeded != 0)
										{
											boxStack.shrink(1);
											pillarsNeeded--;
										}
									}
								}
							}

							ItemStackHelper.saveAllItems(blockEntityTag, contents);
							stackTag.setTag("BlockEntityTag", blockEntityTag);
							stackToCheck.setTagCompound(stackTag);
						} //shulker box end
						else if(block == SCContent.blockPocketWall)
						{
							if(count <= wallsNeeded)
							{
								inventory.set(i - 1, ItemStack.EMPTY);
								wallsNeeded -= count;
							}
							else
							{
								while(wallsNeeded != 0)
								{
									stackToCheck.shrink(1);
									wallsNeeded--;
								}

								inventory.set(i - 1, stackToCheck);
							}
						}
						else if(block == SCContent.reinforcedCrystalQuartz && stackToCheck.getMetadata() == 1)
						{
							if(count <= chiseledNeeded)
							{
								inventory.set(i - 1, ItemStack.EMPTY);
								chiseledNeeded -= count;
							}
							else
							{
								while(chiseledNeeded != 0)
								{
									stackToCheck.shrink(1);
									chiseledNeeded--;
								}

								inventory.set(i - 1, stackToCheck);
							}
						}
						else if(block == SCContent.reinforcedCrystalQuartz && stackToCheck.getMetadata() == 2)
						{
							if(count <= pillarsNeeded)
							{
								inventory.set(i - 1, ItemStack.EMPTY);
								pillarsNeeded -= count;
							}
							else
							{
								while(pillarsNeeded != 0)
								{
									stackToCheck.shrink(1);
									pillarsNeeded--;
								}

								inventory.set(i - 1, stackToCheck);
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
						IBlockState currentState = world.getBlockState(currentPos);

						if(currentState.getBlock() instanceof BlockBlockPocketManager && !currentPos.equals(getPos()))
							return new TextComponentTranslation("messages.securitycraft:blockpocket.multipleManagers");

						//placing the lowest and highest level of the cube
						if((yi == lowest && !currentPos.equals(getPos())) || yi == highest) //if (y level is lowest AND it's not the block pocket manager's position) OR (y level is highest)
						{
							//placing the corners
							if(((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
							{
								if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz) || currentState.getValue(BlockReinforcedCrystalQuartz.VARIANT) != EnumType.CHISELED)
									world.setBlockState(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockReinforcedCrystalQuartz.VARIANT, EnumType.CHISELED));
							}
							//placing the sides parallel to the block pocket manager
							else if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								EnumType typeToPlace = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_X : EnumType.LINES_Z;

								if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz))
									world.setBlockState(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockReinforcedCrystalQuartz.VARIANT, typeToPlace));
							}
							//placing the sides orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								EnumType typeToPlace = managerFacing == EnumFacing.NORTH || managerFacing == EnumFacing.SOUTH ? EnumType.LINES_Z : EnumType.LINES_X;

								if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz))
									world.setBlockState(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockReinforcedCrystalQuartz.VARIANT, typeToPlace));
							}
							//placing the middle plane
							else if(xi > lowest && zi > lowest && xi < highest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									world.setBlockState(currentPos, SCContent.blockPocketWall.getDefaultState());
							}
						}
						//placing the corner edges
						else if(yi != lowest && yi != highest && ((xi == lowest && zi == lowest) || (xi == lowest && zi == highest) || (xi == highest && zi == lowest) || (xi == highest && zi == highest)))
						{
							if(!(currentState.getBlock() instanceof BlockReinforcedCrystalQuartz))
								world.setBlockState(currentPos, SCContent.reinforcedCrystalQuartz.getDefaultState().withProperty(BlockReinforcedCrystalQuartz.VARIANT, EnumType.LINES_Y));
						}
						//placing the walls
						else if(yi > lowest && yi < highest)
						{
							//checking the walls parallel to the block pocket manager
							if((zi == lowest || zi == highest) && xi > lowest && xi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									world.setBlockState(currentPos, SCContent.blockPocketWall.getDefaultState());
							}
							//checking the walls orthogonal to the block pocket manager
							else if((xi == lowest || xi == highest) && zi > lowest && zi < highest)
							{
								if(!(currentState.getBlock() instanceof BlockBlockPocketWall))
									world.setBlockState(currentPos, SCContent.blockPocketWall.getDefaultState());
							}
						}

						//assigning the owner
						if(world.getTileEntity(currentPos) instanceof TileEntityOwnable)
						{
							TileEntityOwnable te = (TileEntityOwnable)world.getTileEntity(currentPos);

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

			return new TextComponentTranslation("messages.securitycraft:blockpocket.assembled");
		}

		return null;
	}

	public void disableMultiblock()
	{
		if(enabled)
		{
			if(world.isRemote)
			{
				SecurityCraft.network.sendToServer(new PacketCToggleBlockPocketManager(this, false, size));
				PlayerUtils.sendMessageToPlayer(SecurityCraft.proxy.getClientPlayer(), ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), ClientUtils.localize("messages.securitycraft:blockpocket.deactivated"), TextFormatting.DARK_AQUA);
			}

			enabled = false;

			for(BlockPos pos : blocks)
			{
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof TileEntityBlockPocket)
					((TileEntityBlockPocket)te).removeManager();
			}

			for(BlockPos pos : floor)
			{
				IBlockState state = world.getBlockState(pos);

				if(state.getProperties().containsKey(BlockBlockPocketWall.SOLID))
					world.setBlockState(pos, state.withProperty(BlockBlockPocketWall.SOLID, false));
			}

			if(hasModule(EnumModuleType.DISGUISE))
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
			IBlockState state = world.getBlockState(pos);

			if(state.getBlock() instanceof BlockBlockPocketWall)
				world.setBlockState(pos, state.withProperty(BlockBlockPocketWall.SEE_THROUGH, seeThrough));
		}
	}

	@Override
	public void onTileEntityDestroyed()
	{
		super.onTileEntityDestroyed();
		if (world.getBlockState(pos).getBlock() != SCContent.blockPocketManager)
			disableMultiblock();
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module)
	{
		super.onModuleInserted(stack, module);

		if(enabled && module == EnumModuleType.DISGUISE)
			setWalls(false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(enabled && module == EnumModuleType.DISGUISE)
			setWalls(true);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setBoolean("BlockPocketEnabled", enabled);
		tag.setBoolean("ShowOutline", showOutline);
		tag.setInteger("Size", size);

		for(int i = 0; i < blocks.size(); i++)
		{
			tag.setLong("BlocksList" + i, blocks.get(i).toLong());
		}

		for(int i = 0; i < walls.size(); i++)
		{
			tag.setLong("WallsList" + i, walls.get(i).toLong());
		}

		for(int i = 0; i < floor.size(); i++)
		{
			tag.setLong("FloorList" + i, floor.get(i).toLong());
		}

		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		int i = 0;

		super.readFromNBT(tag);
		enabled = tag.getBoolean("BlockPocketEnabled");
		showOutline = tag.getBoolean("ShowOutline");
		size = tag.getInteger("Size");

		while(tag.hasKey("BlocksList" + i))
		{
			blocks.add(BlockPos.fromLong(tag.getLong("BlocksList" + i)));
			i++;
		}

		i = 0;

		while(tag.hasKey("WallsList" + i))
		{
			walls.add(BlockPos.fromLong(tag.getLong("WallsList" + i)));
			i++;
		}

		i = 0;

		while(tag.hasKey("FloorList" + i))
		{
			floor.add(BlockPos.fromLong(tag.getLong("FloorList" + i)));
			i++;
		}
	}

	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[] {
				EnumModuleType.DISGUISE,
				EnumModuleType.WHITELIST
		};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return new AxisAlignedBB(getPos()).grow(RENDER_DISTANCE);
	}

	private boolean isReinforcedCrystalQuartzPillar(IBlockState state)
	{
		if(state.getBlock() instanceof BlockReinforcedCrystalQuartz)
		{
			EnumType type = state.getValue(BlockReinforcedCrystalQuartz.VARIANT);

			return type == EnumType.LINES_X || type == EnumType.LINES_Y || type == EnumType.LINES_Z;
		}
		else return false;
	}
}
