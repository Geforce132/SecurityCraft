package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.containers.ContainerProjector;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityProjector extends TileEntityDisguisable implements IInventory {

	public static final int MIN_WIDTH = 1;
	public static final int MAX_WIDTH = 10;
	public static final int MIN_RANGE = 1;
	public static final int MAX_RANGE = 30;
	public static final int MIN_OFFSET = -10;
	public static final int MAX_OFFSET = 10;

	public static final int RENDER_DISTANCE = 100;

	private int projectionWidth = 1;
	private int projectionRange = 5;
	private int projectionOffset = 0;
	public boolean activatedByRedstone = false;
	public boolean active = false;

	private ItemStack projectedBlock = ItemStack.EMPTY;

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(RENDER_DISTANCE);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setInteger("width", projectionWidth);
		tag.setInteger("range", projectionRange);
		tag.setInteger("offset", projectionOffset);
		activatedByRedstone = hasModule(EnumModuleType.REDSTONE);
		tag.setBoolean("active", active);

		if(!isEmpty())
		{
			NBTTagCompound itemTag = new NBTTagCompound();
			projectedBlock.writeToNBT(itemTag);
			tag.setTag("storedItem", itemTag);
		}

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if(tag.hasKey("width"))
			projectionWidth = tag.getInteger("width");

		if(tag.hasKey("range"))
			projectionRange = tag.getInteger("range");

		if(tag.hasKey("offset"))
			projectionOffset = tag.getInteger("offset");

		activatedByRedstone = hasModule(EnumModuleType.REDSTONE);

		if(tag.hasKey("active"))
			active = tag.getBoolean("active");

		if(tag.hasKey("storedItem"))
			projectedBlock = new ItemStack(tag.getCompoundTag("storedItem"));
	}

	public int getProjectionWidth()
	{
		return projectionWidth;
	}

	public void setProjectionWidth(int width)
	{
		projectionWidth = width;
	}

	public int getProjectionRange()
	{
		return projectionRange;
	}

	public void setProjectionRange(int range)
	{
		projectionRange = range;
	}

	public int getProjectionOffset()
	{
		return projectionOffset;
	}

	public void setProjectionOffset(int offset)
	{
		projectionOffset = offset;
	}

	public boolean isActivatedByRedstone()
	{
		return activatedByRedstone;
	}

	public void setActivatedByRedstone(boolean redstone)
	{
		activatedByRedstone = redstone;
	}

	public boolean isActive()
	{
		return activatedByRedstone ? active : true;
	}

	public void setActive(boolean isOn)
	{
		active = isOn;
	}

	public Block getProjectedBlock() {
		return Block.getBlockFromItem(projectedBlock.getItem());
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module)
	{
		super.onModuleInserted(stack, module);

		if(module == EnumModuleType.REDSTONE)
			setActivatedByRedstone(true);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(module == EnumModuleType.REDSTONE)
			setActivatedByRedstone(false);
	}

	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[]{EnumModuleType.DISGUISE, EnumModuleType.REDSTONE};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public void clear()
	{
		projectedBlock = ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		ItemStack stack = projectedBlock;

		if(count >= 1)
			projectedBlock = ItemStack.EMPTY;

		return stack;
	}

	@Override
	public int getSizeInventory()
	{
		return ContainerProjector.SIZE;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : (slot == 36 ? projectedBlock : ItemStack.EMPTY);
	}

	@Override
	public boolean isEmpty()
	{
		return projectedBlock.isEmpty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer arg0)
	{
		return true;
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack stack = projectedBlock;
		projectedBlock = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		if(!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack = new ItemStack(stack.getItem(), getInventoryStackLimit());

		projectedBlock = stack;
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "Projector";
	}

	@Override
	public void openInventory(EntityPlayer player)
	{}

	@Override
	public void closeInventory(EntityPlayer player)
	{}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return stack.getItem() instanceof ItemBlock;
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{}

	@Override
	public int getFieldCount()
	{
		return 0;
	}
}
