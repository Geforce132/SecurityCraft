package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.containers.ProjectorContainer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ProjectorTileEntity extends SecurityCraftTileEntity implements IInventory, INamedContainerProvider {

	public static final int MIN_WIDTH = 1;
	public static final int MAX_WIDTH = 10;
	public static final int MIN_RANGE = 1;
	public static final int MAX_RANGE = 30;
	public static final int MIN_OFFSET = -10;
	public static final int MAX_OFFSET = 10;

	private int projectionWidth = 1;
	private int projectionRange = 5;
	private int projectionOffset = 0;

	private ItemStack projectedBlock = ItemStack.EMPTY;

	public ProjectorTileEntity() 
	{
		super(SCContent.teTypeProjector);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(100);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) 
	{
		super.write(tag);

		tag.putInt("width", projectionWidth);
		tag.putInt("range", projectionRange);
		tag.putInt("offset", projectionOffset);

		if(!isEmpty()) 
		{
			CompoundNBT itemTag = new CompoundNBT();
			projectedBlock.write(itemTag);
			tag.put("storedItem", itemTag);
		}

		return tag;
	}

	@Override 
	public void read(CompoundNBT tag) 
	{
		super.read(tag);

		if(tag.contains("width"))
			projectionWidth = tag.getInt("width");

		if(tag.contains("range"))
			projectionRange = tag.getInt("range");

		if(tag.contains("offset"))
			projectionOffset = tag.getInt("offset");

		if(tag.contains("storedItem"))
			projectedBlock = ItemStack.read(tag.getCompound("storedItem"));
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

	public Block getProjectedBlock() {
		return Block.getBlockFromItem(projectedBlock.getItem());
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new ProjectorContainer(windowId, world, pos, inv);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		// return new TranslationTextComponent(SCContent.PROJECTOR.get().getTranslationKey());
		return new TranslationTextComponent("Projector");
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
		return ProjectorContainer.SIZE;
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) 
	{
		return slot == 9 ? projectedBlock : ItemStack.EMPTY;
	}

	@Override
	public boolean isEmpty()
	{
		return projectedBlock.isEmpty();
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity arg0) 
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

		if(!(stack.getItem() instanceof BlockItem)) 
			return;

		projectedBlock = stack;
	}

}
