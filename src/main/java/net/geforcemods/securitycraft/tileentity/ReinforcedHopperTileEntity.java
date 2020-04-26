package net.geforcemods.securitycraft.tileentity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

//fuck vanilla for not making the hopper te extensible
public class ReinforcedHopperTileEntity extends LockableLootTileEntity implements IHopper, ITickableTileEntity, IOwnable
{
	private Owner owner = new Owner();
	private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long tickedGameTime;

	public ReinforcedHopperTileEntity()
	{
		super(SCContent.teTypeReinforcedHopper);
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);

		inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);

		if(!checkLootAndRead(tag))
			ItemStackHelper.loadAllItems(tag, inventory);

		if(tag.contains("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if(tag.contains("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));

		transferCooldown = tag.getInt("TransferCooldown");
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);

		if(!checkLootAndWrite(tag))
			ItemStackHelper.saveAllItems(tag, inventory);

		if(owner != null)
		{
			tag.putString("owner", owner.getName());
			tag.putString("ownerUUID", owner.getUUID());
		}

		tag.putInt("TransferCooldown", transferCooldown);
		return tag;
	}

	@Override
	public int getSizeInventory()
	{
		return inventory.size();
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		fillWithLoot(null);
		return ItemStackHelper.getAndSplit(getItems(), index, count);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		fillWithLoot(null);
		getItems().set(index, stack);

		if(stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());
	}

	@Override
	protected ITextComponent getDefaultName()
	{
		return new TranslationTextComponent("container.hopper");
	}

	@Override
	public void tick()
	{
		if(world != null && !world.isRemote)
		{
			--transferCooldown;
			tickedGameTime = world.getGameTime();

			if(!isOnTransferCooldown())
			{
				setTransferCooldown(0);
				updateHopper(() -> pullItems(this));
			}
		}
	}

	private boolean updateHopper(Supplier<Boolean> idk)
	{
		if(world != null && !world.isRemote)
		{
			if (!isOnTransferCooldown() && getBlockState().get(HopperBlock.ENABLED))
			{
				boolean hasChanged = false;

				if(!isInventoryEmpty())
					hasChanged = transferItemsOut();

				if(!isFull())
					hasChanged |= idk.get();

				if(hasChanged)
				{
					setTransferCooldown(8);
					markDirty();
					return true;
				}
			}

			return false;
		}
		else return false;
	}

	private boolean isInventoryEmpty()
	{
		for(ItemStack stack : inventory)
		{
			if(!stack.isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public boolean isEmpty()
	{
		return isInventoryEmpty();
	}

	private boolean isFull()
	{
		for(ItemStack stack : inventory)
		{
			if(stack.isEmpty() || stack.getCount() != stack.getMaxStackSize())
				return false;
		}

		return true;
	}

	private boolean transferItemsOut()
	{
		if(insertHook())
			return true;

		IInventory inv = getInventoryForHopperTransfer();

		if(inv != null)
		{
			Direction direction = getBlockState().get(HopperBlock.FACING).getOpposite();

			if(isInventoryFull(inv, direction))
				return false;
			else
			{
				for(int i = 0; i < getSizeInventory(); ++i)
				{
					if(!getStackInSlot(i).isEmpty())
					{
						ItemStack copy = getStackInSlot(i).copy();
						ItemStack remainder = putStackInInventoryAllSlots(this, inv, decrStackSize(i, 1), direction);

						if(remainder.isEmpty())
						{
							inv.markDirty();
							return true;
						}

						setInventorySlotContents(i, copy);
					}
				}
			}
		}

		return false;
	}

	private static IntStream getSlotStreamForSide(IInventory inv, Direction dir)
	{
		return inv instanceof ISidedInventory ? IntStream.of(((ISidedInventory)inv).getSlotsForFace(dir)) : IntStream.range(0, inv.getSizeInventory());
	}

	private boolean isInventoryFull(IInventory inventory, Direction side)
	{
		return getSlotStreamForSide(inventory, side).allMatch(slot -> {
			ItemStack stack = inventory.getStackInSlot(slot);
			return stack.getCount() >= stack.getMaxStackSize();
		});
	}

	private static boolean isInventoryEmpty(IInventory inventory, Direction side)
	{
		return getSlotStreamForSide(inventory, side).allMatch(slot -> inventory.getStackInSlot(slot).isEmpty());
	}

	public static boolean pullItems(IHopper hopper)
	{
		Boolean ret = VanillaInventoryCodeHooks.extractHook(hopper);

		if(ret != null)
			return ret;

		IInventory inv = getSourceInventory(hopper);

		if(inv != null)
		{
			Direction direction = Direction.DOWN;
			return isInventoryEmpty(inv, direction) ? false : getSlotStreamForSide(inv, direction).anyMatch(slot -> pullItemFromSlot(hopper, inv, slot, direction));
		}
		else
		{
			for(ItemEntity entity : getCaptureItems(hopper))
			{
				if (captureItem(hopper, entity))
					return true;
			}

			return false;
		}
	}

	private static boolean pullItemFromSlot(IHopper hopper, IInventory inventory, int index, Direction direction)
	{
		ItemStack stack = inventory.getStackInSlot(index);

		if(!stack.isEmpty() && canExtractItemFromSlot(inventory, stack, index, direction))
		{
			ItemStack copy = stack.copy();
			ItemStack remainder = putStackInInventoryAllSlots(inventory, hopper, inventory.decrStackSize(index, 1), null);

			if(remainder.isEmpty())
			{
				inventory.markDirty();
				return true;
			}

			inventory.setInventorySlotContents(index, copy);
		}

		return false;
	}

	public static boolean captureItem(IInventory inv, ItemEntity entity)
	{
		boolean capturedEverything = false;
		ItemStack copy = entity.getItem().copy();
		ItemStack remainder = putStackInInventoryAllSlots(null, inv, copy, null);

		if(remainder.isEmpty())
		{
			capturedEverything = true;
			entity.remove();
		}
		else
			entity.setItem(remainder);

		return capturedEverything;
	}

	public static ItemStack putStackInInventoryAllSlots(IInventory source, IInventory destination, ItemStack stack, Direction direction)
	{
		if(destination instanceof ISidedInventory && direction != null)
		{
			ISidedInventory inv = (ISidedInventory)destination;
			int[] slots = inv.getSlotsForFace(direction);

			for(int k = 0; k < slots.length && !stack.isEmpty(); ++k)
			{
				stack = insertStack(source, destination, stack, slots[k], direction);
			}
		}
		else
		{
			int destSize = destination.getSizeInventory();

			for(int j = 0; j < destSize && !stack.isEmpty(); ++j)
			{
				stack = insertStack(source, destination, stack, j, direction);
			}
		}

		return stack;
	}

	private static boolean canInsertItemInSlot(IInventory inventory, ItemStack stack, int index, Direction side)
	{
		if(!inventory.isItemValidForSlot(index, stack))
			return false;
		else return !(inventory instanceof ISidedInventory) || ((ISidedInventory)inventory).canInsertItem(index, stack, side);
	}

	private static boolean canExtractItemFromSlot(IInventory inventory, ItemStack stack, int index, Direction side)
	{
		return !(inventory instanceof ISidedInventory) || ((ISidedInventory)inventory).canExtractItem(index, stack, side);
	}

	private static ItemStack insertStack(IInventory source, IInventory destination, ItemStack stack, int index, Direction direction)
	{
		ItemStack destStack = destination.getStackInSlot(index);

		if(canInsertItemInSlot(destination, stack, index, direction))
		{
			boolean hasChanged = false;
			boolean isDestEmpty = destination.isEmpty();

			if(destStack.isEmpty())
			{
				destination.setInventorySlotContents(index, stack);
				stack = ItemStack.EMPTY;
				hasChanged = true;
			}
			else if (canCombine(destStack, stack))
			{
				int sizeDifference = stack.getMaxStackSize() - destStack.getCount();
				int minSize = Math.min(stack.getCount(), sizeDifference);
				stack.shrink(minSize);
				destStack.grow(minSize);
				hasChanged = minSize > 0;
			}

			if(hasChanged)
			{
				if (isDestEmpty && destination instanceof ReinforcedHopperTileEntity)
				{
					ReinforcedHopperTileEntity te = (ReinforcedHopperTileEntity)destination;

					if(!te.mayTransfer())
					{
						int k = 0;

						if(source instanceof ReinforcedHopperTileEntity) {
							ReinforcedHopperTileEntity te2 = (ReinforcedHopperTileEntity)source;

							if (te.tickedGameTime >= te2.tickedGameTime)
								k = 1;
						}

						te.setTransferCooldown(8 - k);
					}
				}

				destination.markDirty();
			}
		}

		return stack;
	}

	@Nullable
	private IInventory getInventoryForHopperTransfer()
	{
		Direction direction = getBlockState().get(HopperBlock.FACING);
		return getInventoryAtPosition(getWorld(), pos.offset(direction));
	}

	@Nullable
	public static IInventory getSourceInventory(IHopper hopper)
	{
		return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());
	}

	public static List<ItemEntity> getCaptureItems(IHopper hopper)
	{
		return hopper.getCollectionArea().toBoundingBoxList().stream().flatMap((box) -> {
			return hopper.getWorld().getEntitiesWithinAABB(ItemEntity.class, box.offset(hopper.getXPos() - 0.5D, hopper.getYPos() - 0.5D, hopper.getZPos() - 0.5D), EntityPredicates.IS_ALIVE).stream();
		}).collect(Collectors.toList());
	}

	@Nullable
	public static IInventory getInventoryAtPosition(World world, BlockPos pos)
	{
		return getInventoryAtPosition(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	@Nullable
	public static IInventory getInventoryAtPosition(World world, double x, double y, double z)
	{
		IInventory inv = null;
		BlockPos pos = new BlockPos(x, y, z);
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if(block instanceof ISidedInventoryProvider)
			inv = ((ISidedInventoryProvider)block).createInventory(state, world, pos);
		else if(state.hasTileEntity())
		{
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof IInventory)
			{
				inv = (IInventory)te;

				if(inv instanceof ChestTileEntity && block instanceof ChestBlock)
					inv = ChestBlock.func_226916_a_((ChestBlock)block, state, world, pos, true);
			}
		}

		if(inv == null)
		{
			List<Entity> list = world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntityPredicates.HAS_INVENTORY);

			if(!list.isEmpty())
				inv = (IInventory)list.get(world.rand.nextInt(list.size()));
		}

		return inv;
	}

	private static boolean canCombine(ItemStack stack1, ItemStack stack2)
	{
		if(stack1.getItem() != stack2.getItem())
			return false;
		else if(stack1.getDamage() != stack2.getDamage())
			return false;
		else if(stack1.getCount() > stack1.getMaxStackSize())
			return false;
		else return ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	@Override
	public double getXPos()
	{
		return pos.getX() + 0.5D;
	}

	@Override
	public double getYPos()
	{
		return pos.getY() + 0.5D;
	}

	@Override
	public double getZPos() {
		return pos.getZ() + 0.5D;
	}

	public void setTransferCooldown(int ticks)
	{
		transferCooldown = ticks;
	}

	private boolean isOnTransferCooldown()
	{
		return transferCooldown > 0;
	}

	public boolean mayTransfer()
	{
		return transferCooldown > 8;
	}

	@Override
	protected NonNullList<ItemStack> getItems()
	{
		return inventory;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items)
	{
		inventory = items;
	}

	public void onEntityCollision(Entity entity)
	{
		if(entity instanceof ItemEntity)
		{
			BlockPos pos = getPos();

			if(VoxelShapes.compare(VoxelShapes.create(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), getCollectionArea(), IBooleanFunction.AND))
				updateHopper(() -> captureItem(this, (ItemEntity)entity));
		}
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player)
	{
		return new HopperContainer(id, player, this);
	}

	public long getLastUpdateTime()
	{
		return tickedGameTime;
	}

	@Override
	public CompoundNBT getUpdateTag()
	{
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet)
	{
		read(packet.getNbtCompound());
	}

	@Override
	public Owner getOwner()
	{
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name)
	{
		owner.set(uuid, name);
	}

	@Override
	public void onLoad()
	{
		if(world.isRemote)
			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(getPos(), getWorld().dimension.getType().getId()));
	}

	//code from Forge, as it is hardcoded to the vanilla hopper
	private boolean insertHook()
	{
		return getItemHandler(this, Direction.UP)
				.map(itemHandlerResult -> {
					IItemHandler handler = itemHandlerResult.getKey();

					for (int i = 0; i < handler.getSlots(); i++)
					{
						ItemStack extractItem = handler.extractItem(i, 1, true);
						if (!extractItem.isEmpty())
						{
							for (int j = 0; j < getSizeInventory(); j++)
							{
								ItemStack destStack = getStackInSlot(j);
								if (isItemValidForSlot(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize() && destStack.getCount() < getInventoryStackLimit() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack)))
								{
									extractItem = handler.extractItem(i, 1, false);
									if (destStack.isEmpty())
										setInventorySlotContents(j, extractItem);
									else
									{
										destStack.grow(1);
										setInventorySlotContents(j, destStack);
									}
									markDirty();
									return true;
								}
							}
						}
					}

					return false;
				})
				.orElse(null); // TODO bad null
	}

	//this is private in forge's code, so it's copied here
	private LazyOptional<Pair<IItemHandler, Object>> getItemHandler(IHopper hopper, Direction hopperFacing)
	{
		double x = hopper.getXPos() + hopperFacing.getXOffset();
		double y = hopper.getYPos() + hopperFacing.getYOffset();
		double z = hopper.getZPos() + hopperFacing.getZOffset();
		return VanillaInventoryCodeHooks.getItemHandler(hopper.getWorld(), x, y, z, hopperFacing.getOpposite());
	}
}