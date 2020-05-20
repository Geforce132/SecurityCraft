package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.containers.ProjectorContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ProjectorTileEntity extends SecurityCraftTileEntity implements INamedContainerProvider {

	public static final int MIN_WIDTH = 1;
	public static final int MAX_WIDTH = 10;
	public static final int MIN_RANGE = 1;
	public static final int MAX_RANGE = 30;
	public static final int MIN_OFFSET = -10;
	public static final int MAX_OFFSET = 10;

	private int projectionWidth = 1;
	private int projectionRange = 5;
	private int projectionOffset = 0;

	public ProjectorTileEntity() 
	{
		super(SCContent.teTypeProjector);
	}

	@Override
	public void tick() 
	{
		super.tick();
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

}
