package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.containers.ProjectorContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ProjectorTileEntity extends DisguisableTileEntity implements Container, MenuProvider {

	public static final int MIN_WIDTH = 1; //also for height
	public static final int MAX_WIDTH = 10; //also for height
	public static final int MIN_RANGE = 1;
	public static final int MAX_RANGE = 30;
	public static final int MIN_OFFSET = -10;
	public static final int MAX_OFFSET = 10;

	public static final int RENDER_DISTANCE = 100;

	private int projectionWidth = 1;
	private int projectionHeight = 1;
	private int projectionRange = 5;
	private int projectionOffset = 0;
	public boolean activatedByRedstone = false;
	public boolean active = false;
	private boolean horizontal = false;

	private ItemStack projectedBlock = ItemStack.EMPTY;

	public ProjectorTileEntity()
	{
		super(SCContent.teTypeProjector);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(getBlockPos()).inflate(RENDER_DISTANCE);
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);

		tag.putInt("width", projectionWidth);
		tag.putInt("height", projectionHeight);
		tag.putInt("range", projectionRange);
		tag.putInt("offset", projectionOffset);
		tag.putBoolean("active", active);
		tag.putBoolean("horizontal", horizontal);
		tag.put("storedItem", projectedBlock.save(new CompoundTag()));
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundTag tag)
	{
		super.load(state, tag);

		projectionWidth = tag.getInt("width");
		projectionHeight = tag.getInt("height");
		projectionRange = tag.getInt("range");
		projectionOffset = tag.getInt("offset");
		activatedByRedstone = hasModule(ModuleType.REDSTONE);
		active = tag.getBoolean("active");
		horizontal = tag.getBoolean("horizontal");
		projectedBlock = ItemStack.of(tag.getCompound("storedItem"));
	}

	public int getProjectionWidth()
	{
		return projectionWidth;
	}

	public void setProjectionWidth(int width)
	{
		projectionWidth = width;
	}

	public int getProjectionHeight()
	{
		return projectionHeight;
	}

	public void setProjectionHeight(int projectionHeight)
	{
		this.projectionHeight = projectionHeight;
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

	public boolean isHorizontal()
	{
		return horizontal;
	}

	public void setHorizontal(boolean horizontal)
	{
		this.horizontal = horizontal;
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
		return Block.byItem(projectedBlock.getItem());
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		super.onModuleInserted(stack, module);

		if(module == ModuleType.REDSTONE)
			setActivatedByRedstone(true);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(module == ModuleType.REDSTONE)
			setActivatedByRedstone(false);
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.DISGUISE, ModuleType.REDSTONE};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
	{
		return new ProjectorContainer(windowId, level, worldPosition, inv);
	}

	@Override
	public Component getDisplayName()
	{
		return new TranslatableComponent(SCContent.PROJECTOR.get().getDescriptionId());
	}

	@Override
	public void clearContent()
	{
		projectedBlock = ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count)
	{
		ItemStack stack = projectedBlock;

		if(count >= 1)
			projectedBlock = ItemStack.EMPTY;

		return stack;
	}

	@Override
	public int getContainerSize()
	{
		return ProjectorContainer.SIZE;
	}

	@Override
	public int getMaxStackSize()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : (slot == 36 ? projectedBlock : ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItem(int slot)
	{
		return getStackInSlot(slot);
	}

	@Override
	public boolean isEmpty()
	{
		return projectedBlock.isEmpty();
	}

	@Override
	public boolean stillValid(Player arg0)
	{
		return true;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index)
	{
		ItemStack stack = projectedBlock;
		projectedBlock = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack)
	{
		if(!stack.isEmpty() && stack.getCount() > getMaxStackSize())
			stack = new ItemStack(stack.getItem(), getMaxStackSize());

		projectedBlock = stack;
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}
}
