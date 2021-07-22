package net.geforcemods.securitycraft.tileentity;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

//fuck vanilla for not making the hopper te extensible
public class ReinforcedHopperTileEntity extends RandomizableContainerBlockEntity implements Hopper, TickableBlockEntity, IOwnable, IModuleInventory
{
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private Owner owner = new Owner();
	private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long tickedGameTime;

	public ReinforcedHopperTileEntity()
	{
		super(SCContent.teTypeReinforcedHopper);
	}

	@Override
	public void load(BlockState state, CompoundTag tag)
	{
		super.load(state, tag);

		inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);

		if(!tryLoadLootTable(tag))
			ContainerHelper.loadAllItems(tag, inventory);

		owner.setOwnerName(tag.getString("owner"));
		owner.setOwnerUUID(tag.getString("ownerUUID"));
		transferCooldown = tag.getInt("TransferCooldown");
		modules = readModuleInventory(tag);
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);

		if(!trySaveLootTable(tag))
			ContainerHelper.saveAllItems(tag, inventory);

		if(owner != null)
		{
			tag.putString("owner", owner.getName());
			tag.putString("ownerUUID", owner.getUUID());
		}

		tag.putInt("TransferCooldown", transferCooldown);
		writeModuleInventory(tag);
		return tag;
	}

	@Override
	public int getContainerSize()
	{
		return inventory.size();
	}

	@Override
	public ItemStack removeItem(int index, int count)
	{
		unpackLootTable(null);
		return ContainerHelper.removeItem(getItems(), index, count);
	}

	@Override
	public void setItem(int index, ItemStack stack)
	{
		unpackLootTable(null);
		getItems().set(index, stack);

		if(stack.getCount() > getMaxStackSize())
			stack.setCount(getMaxStackSize());
	}

	@Override
	protected Component getDefaultName()
	{
		return new TranslatableComponent("container.hopper");
	}

	@Override
	public void tick()
	{
		if(level != null && !level.isClientSide)
		{
			--transferCooldown;
			tickedGameTime = level.getGameTime();

			if(!isOnTransferCooldown())
			{
				setTransferCooldown(0);
				updateHopper(() -> pullItems(this));
			}
		}
	}

	private boolean updateHopper(Supplier<Boolean> idk)
	{
		if(level != null && !level.isClientSide)
		{
			if (!isOnTransferCooldown() && getBlockState().getValue(HopperBlock.ENABLED))
			{
				boolean hasChanged = false;

				if(!isInventoryEmpty())
					hasChanged = transferItemsOut();

				if(!isFull())
					hasChanged |= idk.get();

				if(hasChanged)
				{
					setTransferCooldown(8);
					setChanged();
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

		Container inv = getInventoryForHopperTransfer();

		if(inv != null)
		{
			Direction direction = getBlockState().getValue(HopperBlock.FACING).getOpposite();

			if(isInventoryFull(inv, direction))
				return false;
			else
			{
				for(int i = 0; i < getContainerSize(); ++i)
				{
					if(!getItem(i).isEmpty())
					{
						ItemStack copy = getItem(i).copy();
						ItemStack remainder = putStackInInventoryAllSlots(this, inv, removeItem(i, 1), direction);

						if(remainder.isEmpty())
						{
							inv.setChanged();
							return true;
						}

						setItem(i, copy);
					}
				}
			}
		}

		return false;
	}

	private static IntStream getSlotStreamForSide(Container inv, Direction dir)
	{
		return inv instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer)inv).getSlotsForFace(dir)) : IntStream.range(0, inv.getContainerSize());
	}

	private boolean isInventoryFull(Container inventory, Direction side)
	{
		return getSlotStreamForSide(inventory, side).allMatch(slot -> {
			ItemStack stack = inventory.getItem(slot);
			return stack.getCount() >= stack.getMaxStackSize();
		});
	}

	private static boolean isInventoryEmpty(Container inventory, Direction side)
	{
		return getSlotStreamForSide(inventory, side).allMatch(slot -> inventory.getItem(slot).isEmpty());
	}

	public static boolean pullItems(Hopper hopper)
	{
		Boolean ret = VanillaInventoryCodeHooks.extractHook(hopper);

		if(ret != null)
			return ret;

		Container inv = getSourceInventory(hopper);

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

	private static boolean pullItemFromSlot(Hopper hopper, Container inventory, int index, Direction direction)
	{
		ItemStack stack = inventory.getItem(index);

		if(!stack.isEmpty() && canExtractItemFromSlot(inventory, stack, index, direction))
		{
			ItemStack copy = stack.copy();
			ItemStack remainder = putStackInInventoryAllSlots(inventory, hopper, inventory.removeItem(index, 1), null);

			if(remainder.isEmpty())
			{
				inventory.setChanged();
				return true;
			}

			inventory.setItem(index, copy);
		}

		return false;
	}

	public static boolean captureItem(Container inv, ItemEntity entity)
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

	public static ItemStack putStackInInventoryAllSlots(Container source, Container destination, ItemStack stack, Direction direction)
	{
		if(destination instanceof WorldlyContainer && direction != null)
		{
			WorldlyContainer inv = (WorldlyContainer)destination;
			int[] slots = inv.getSlotsForFace(direction);

			for(int k = 0; k < slots.length && !stack.isEmpty(); ++k)
			{
				stack = insertStack(source, destination, stack, slots[k], direction);
			}
		}
		else
		{
			int destSize = destination.getContainerSize();

			for(int j = 0; j < destSize && !stack.isEmpty(); ++j)
			{
				stack = insertStack(source, destination, stack, j, direction);
			}
		}

		return stack;
	}

	private static boolean canInsertItemInSlot(Container inventory, ItemStack stack, int index, Direction side)
	{
		if(!inventory.canPlaceItem(index, stack))
			return false;
		else return !(inventory instanceof WorldlyContainer) || ((WorldlyContainer)inventory).canPlaceItemThroughFace(index, stack, side);
	}

	private static boolean canExtractItemFromSlot(Container inventory, ItemStack stack, int index, Direction side)
	{
		return !(inventory instanceof WorldlyContainer) || ((WorldlyContainer)inventory).canTakeItemThroughFace(index, stack, side);
	}

	private static ItemStack insertStack(Container source, Container destination, ItemStack stack, int index, Direction direction)
	{
		ItemStack destStack = destination.getItem(index);

		if(canInsertItemInSlot(destination, stack, index, direction))
		{
			boolean hasChanged = false;
			boolean isDestEmpty = destination.isEmpty();

			if(destStack.isEmpty())
			{
				destination.setItem(index, stack);
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

				destination.setChanged();
			}
		}

		return stack;
	}

	@Nullable
	private Container getInventoryForHopperTransfer()
	{
		Direction direction = getBlockState().getValue(HopperBlock.FACING);
		return getInventoryAtPosition(getLevel(), worldPosition.relative(direction));
	}

	@Nullable
	public static Container getSourceInventory(Hopper hopper)
	{
		return getInventoryAtPosition(hopper.getLevel(), hopper.getLevelX(), hopper.getLevelY() + 1.0D, hopper.getLevelZ());
	}

	public static List<ItemEntity> getCaptureItems(Hopper hopper)
	{
		return hopper.getSuckShape().toAabbs().stream().flatMap((box) -> {
			return hopper.getLevel().getEntitiesOfClass(ItemEntity.class, box.move(hopper.getLevelX() - 0.5D, hopper.getLevelY() - 0.5D, hopper.getLevelZ() - 0.5D), EntitySelector.ENTITY_STILL_ALIVE).stream();
		}).collect(Collectors.toList());
	}

	@Nullable
	public static Container getInventoryAtPosition(Level world, BlockPos pos)
	{
		return getInventoryAtPosition(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	@Nullable
	public static Container getInventoryAtPosition(Level world, double x, double y, double z)
	{
		Container inv = null;
		BlockPos pos = new BlockPos(x, y, z);
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if(block instanceof WorldlyContainerHolder)
			inv = ((WorldlyContainerHolder)block).getContainer(state, world, pos);
		else if(state.hasTileEntity())
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(te instanceof Container)
			{
				inv = (Container)te;

				if(inv instanceof ChestBlockEntity && block instanceof ChestBlock)
					inv = ChestBlock.getContainer((ChestBlock)block, state, world, pos, true);
			}
		}

		if(inv == null)
		{
			List<Entity> list = world.getEntities((Entity)null, new AABB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntitySelector.CONTAINER_ENTITY_SELECTOR);

			if(!list.isEmpty())
				inv = (Container)list.get(world.random.nextInt(list.size()));
		}

		return inv;
	}

	private static boolean canCombine(ItemStack stack1, ItemStack stack2)
	{
		if(stack1.getItem() != stack2.getItem())
			return false;
		else if(stack1.getDamageValue() != stack2.getDamageValue())
			return false;
		else if(stack1.getCount() > stack1.getMaxStackSize())
			return false;
		else return ItemStack.tagMatches(stack1, stack2);
	}

	@Override
	public double getLevelX()
	{
		return worldPosition.getX() + 0.5D;
	}

	@Override
	public double getLevelY()
	{
		return worldPosition.getY() + 0.5D;
	}

	@Override
	public double getLevelZ() {
		return worldPosition.getZ() + 0.5D;
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
			BlockPos pos = getBlockPos();

			if(Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ())), getSuckShape(), BooleanOp.AND))
				updateHopper(() -> captureItem(this, (ItemEntity)entity));
		}
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player)
	{
		return new HopperMenu(id, player, this);
	}

	public long getLastUpdateTime()
	{
		return tickedGameTime;
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		return save(new CompoundTag());
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		load(getBlockState(), packet.getTag());
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
		if(level.isClientSide)
			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(getBlockPos()));
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public ItemStack getItem(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : super.getItem(slot);
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {ModuleType.ALLOWLIST};
	}

	@Override
	public NonNullList<ItemStack> getInventory()
	{
		return modules;
	}

	@Override
	public BlockEntity getTileEntity()
	{
		return this;
	}

	//code from Forge, as it is hardcoded to the vanilla hopper
	private boolean insertHook()
	{
		Direction hopperFacing = getBlockState().getValue(HopperBlock.FACING);
		return getItemHandler(this, hopperFacing)
				.map(destinationResult -> {
					IItemHandler itemHandler = destinationResult.getKey();
					Object destination = destinationResult.getValue();
					if (isFull())
					{
						return false;
					}
					else
					{
						for (int i = 0; i < getContainerSize(); ++i)
						{
							if (!getItem(i).isEmpty())
							{
								ItemStack originalSlotContents = getItem(i).copy();
								ItemStack insertStack = removeItem(i, 1);
								ItemStack remainder = putStackInInventoryAllSlots(this, destination, itemHandler, insertStack);

								if (remainder.isEmpty())
								{
									return true;
								}

								setItem(i, originalSlotContents);
							}
						}

						return false;
					}
				})
				.orElse(false);
	}

	//these are private in forge's code, so it's copied here
	private Optional<Pair<IItemHandler, Object>> getItemHandler(Hopper hopper, Direction hopperFacing)
	{
		double x = hopper.getLevelX() + hopperFacing.getStepX();
		double y = hopper.getLevelY() + hopperFacing.getStepY();
		double z = hopper.getLevelZ() + hopperFacing.getStepZ();
		return VanillaInventoryCodeHooks.getItemHandler(hopper.getLevel(), x, y, z, hopperFacing.getOpposite());
	}

	private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack)
	{
		for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++)
		{
			stack = insertStack(source, destination, destInventory, stack, slot);
		}
		return stack;
	}

	private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot)
	{
		ItemStack itemstack = destInventory.getStackInSlot(slot);

		if (destInventory.insertItem(slot, stack, true).isEmpty())
		{
			boolean insertedItem = false;
			boolean inventoryWasEmpty = isEmpty(destInventory);

			if (itemstack.isEmpty())
			{
				destInventory.insertItem(slot, stack, false);
				stack = ItemStack.EMPTY;
				insertedItem = true;
			}
			else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack))
			{
				int originalSize = stack.getCount();
				stack = destInventory.insertItem(slot, stack, false);
				insertedItem = originalSize < stack.getCount();
			}

			if (insertedItem)
			{
				if (inventoryWasEmpty && destination instanceof HopperBlockEntity)
				{
					HopperBlockEntity destinationHopper = (HopperBlockEntity)destination;

					if (!destinationHopper.isOnCustomCooldown())
					{
						destinationHopper.setCooldown(8);
					}
				}
			}
		}

		return stack;
	}

	private static boolean isEmpty(IItemHandler itemHandler)
	{
		for (int slot = 0; slot < itemHandler.getSlots(); slot++)
		{
			ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
			if (stackInSlot.getCount() > 0)
			{
				return false;
			}
		}
		return true;
	}
}