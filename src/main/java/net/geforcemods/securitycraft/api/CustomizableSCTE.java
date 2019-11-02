package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Iterator;

import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;

/**
 * Extend this class in your TileEntity to make it customizable. You will
 * be able to modify it with the various modules in SecurityCraft, and
 * have your block do different functions based on what modules are
 * inserted.
 *
 * @author Geforce
 */
public abstract class CustomizableSCTE extends TileEntityOwnable implements IInventory{

	private boolean linkable = false;
	public ArrayList<LinkedBlock> linkedBlocks = new ArrayList<LinkedBlock>();
	private NBTTagList nbtTagStorage = null;

	public NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getNumberOfCustomizableOptions(), ItemStack.EMPTY);
	public ItemStack[] itemStackss = new ItemStack[getNumberOfCustomizableOptions()];

	@Override
	public void update() {
		super.update();

		if(hasWorld() && nbtTagStorage != null) {
			readLinkedBlocks(nbtTagStorage);
			sync();
			nbtTagStorage = null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		NBTTagList list = tag.getTagList("Modules", 10);
		modules = NonNullList.withSize(getNumberOfCustomizableOptions(), ItemStack.EMPTY);

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			byte slot = stackTag.getByte("ModuleSlot");

			if (slot >= 0 && slot < modules.size())
				modules.set(slot, new ItemStack(stackTag));
		}

		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.readFromNBT(tag);

		if (tag.hasKey("linkable"))
			linkable = tag.getBoolean("linkable");

		if (linkable && tag.hasKey("linkedBlocks"))
		{
			if(!hasWorld()) {
				nbtTagStorage = tag.getTagList("linkedBlocks", Constants.NBT.TAG_COMPOUND);
				return;
			}

			readLinkedBlocks(tag.getTagList("linkedBlocks", Constants.NBT.TAG_COMPOUND));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		NBTTagList list = new NBTTagList();

		for(int i = 0; i < modules.size(); i++)
			if (!modules.get(i).isEmpty())
			{
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("ModuleSlot", (byte)i);
				modules.get(i).writeToNBT(stackTag);
				list.appendTag(stackTag);
			}

		tag.setTag("Modules", list);

		if(customOptions() != null)
			for(Option<?> option : customOptions())
				option.writeToNBT(tag);

		tag.setBoolean("linkable", linkable);

		if(linkable && hasWorld() && linkedBlocks.size() > 0) {
			NBTTagList tagList = new NBTTagList();

			WorldUtils.addScheduledTask(world, () -> {
				Iterator<LinkedBlock> iterator = linkedBlocks.iterator();

				while(iterator.hasNext()) {
					LinkedBlock block = iterator.next();
					NBTTagCompound toAppend = new NBTTagCompound();

					if(block != null) {
						if(!block.validate(world)) {
							linkedBlocks.remove(block);
							continue;
						}

						toAppend.setString("blockName", block.blockName);
						toAppend.setInteger("blockX", block.getX());
						toAppend.setInteger("blockY", block.getY());
						toAppend.setInteger("blockZ", block.getZ());
					}

					tagList.appendTag(toAppend);
				}

				tag.setTag("linkedBlocks", tagList);
			});
		}

		return tag;
	}

	private void readLinkedBlocks(NBTTagList list) {
		if(!linkable) return;

		for(int i = 0; i < list.tagCount(); i++) {
			String name = list.getCompoundTagAt(i).getString("blockName");
			int x = list.getCompoundTagAt(i).getInteger("blockX");
			int y = list.getCompoundTagAt(i).getInteger("blockY");
			int z = list.getCompoundTagAt(i).getInteger("blockZ");

			LinkedBlock block = new LinkedBlock(name, x, y, z);
			if(hasWorld() && !block.validate(world)) {
				list.removeTag(i);
				continue;
			}

			if(!linkedBlocks.contains(block))
				link(this, block.asTileEntity(world));
		}
	}

	@Override
	public int getSizeInventory() {
		return getNumberOfCustomizableOptions();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return modules.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (!modules.get(index).isEmpty())
		{
			ItemStack stack;

			if (modules.get(index).getCount() <= count)
			{
				stack = modules.get(index);
				modules.set(index, ItemStack.EMPTY);
				onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

				if(this instanceof TileEntitySecurityCamera)
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), true);

				return stack;
			}
			else
			{
				stack = modules.get(index).splitStack(count);

				if (modules.get(index).getCount() == 0)
					modules.set(index, ItemStack.EMPTY);

				onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

				if(this instanceof TileEntitySecurityCamera)
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), true);

				return stack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	/**
	 * Copy of decrStackSize which can't be overrided by subclasses.
	 */

	public ItemStack safeDecrStackSize(int index, int count)
	{
		if (!modules.get(index).isEmpty())
		{
			ItemStack stack;

			if (modules.get(index).getCount() <= count)
			{
				stack = modules.get(index);
				modules.set(index, ItemStack.EMPTY);
				onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

				if(this instanceof TileEntitySecurityCamera)
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), true);

				return stack;
			}
			else
			{
				stack = modules.get(index).splitStack(count);

				if (modules.get(index).getCount() == 0)
					modules.set(index, ItemStack.EMPTY);

				onModuleRemoved(stack, ((ItemModule) stack.getItem()).getModule());
				createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

				if(this instanceof TileEntitySecurityCamera)
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), true);

				return stack;
			}
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		if (!modules.get(index).isEmpty())
		{
			ItemStack stack = modules.get(index);
			modules.set(index, ItemStack.EMPTY);
			return stack;
		}
		else
			return ItemStack.EMPTY;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		modules.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack = new ItemStack(stack.getItem(), getInventoryStackLimit(), stack.getMetadata());

		if(!stack.isEmpty())
		{
			onModuleInserted(stack, ((ItemModule) stack.getItem()).getModule());

			if(this instanceof TileEntitySecurityCamera)
				world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), true);
		}
	}

	/**
	 * Copy of setInventorySlotContents which can't be overrided by subclasses.
	 */
	public void safeSetInventorySlotContents(int index, ItemStack stack) {
		modules.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack = new ItemStack(stack.getItem(), getInventoryStackLimit(), stack.getMetadata());

		if(!stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemModule){
			onModuleInserted(stack, ((ItemModule) stack.getItem()).getModule());
			createLinkedBlockAction(EnumLinkedAction.MODULE_INSERTED, new Object[]{ stack, ((ItemModule) stack.getItem()).getModule() }, this);

			if(this instanceof TileEntitySecurityCamera)
				world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), true);
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

	@Override
	public String getName(){
		return "Customize";
	}

	@Override
	public boolean hasCustomName() {
		return (getCustomName() != null && !getCustomName().equals("name"));
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() instanceof ItemModule ? true : false;
	}

	@Override
	public boolean isEmpty()
	{
		for(ItemStack stack : modules)
			if(!stack.isEmpty())
				return false;

		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for(int i = 0; i < modules.size(); i++)
			modules.set(i, ItemStack.EMPTY);
	}

	@Override
	public void onTileEntityDestroyed() {
		if(linkable)
			for(LinkedBlock block : linkedBlocks)
				CustomizableSCTE.unlink(block.asTileEntity(world), this);

		for(ItemStack module : modules)
		{
			Block.spawnAsEntity(world, pos, module);
		}
	}

	////////////////////////
	// MODULE STUFF //
	////////////////////////

	/**
	 * Called whenever a module is inserted into a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being inserted.
	 * @param module The EnumCustomModules variant of stack.
	 */
	public void onModuleInserted(ItemStack stack, EnumCustomModules module) {}

	/**
	 * Called whenever a module is removed from a slot in the "Customize" GUI.
	 *
	 * @param stack The raw ItemStack being removed.
	 * @param module The EnumCustomModules variant of stack.
	 */
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module) {}

	/**
	 * @return An ArrayList of all EnumCustomModules currently inserted in the TileEntity.
	 */
	public ArrayList<EnumCustomModules> getModules(){
		ArrayList<EnumCustomModules> modules = new ArrayList<EnumCustomModules>();

		for(ItemStack stack : this.modules)
			if(!stack.isEmpty() && stack.getItem() instanceof ItemModule)
				modules.add(((ItemModule) stack.getItem()).getModule());

		return modules;
	}

	/**
	 * @return The ItemStack for the given EnumCustomModules type.
	 * If there is no ItemStack for that type, returns null.
	 */
	public ItemStack getModule(EnumCustomModules module){
		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ItemModule && ((ItemModule) modules.get(i).getItem()).getModule() == module)
				return modules.get(i);

		return ItemStack.EMPTY;
	}

	/**
	 * Inserts a generic copy of the given module type into the Customization inventory.
	 */
	public void insertModule(EnumCustomModules module){
		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty())
				if(modules.get(i).getItem() == module.getItem())
					return;

		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty() && module != null){
				modules.set(i, new ItemStack(module.getItem()));
				break;
			}else if(!modules.get(i).isEmpty() && module == null)
				modules.set(i, ItemStack.EMPTY);
			else
				continue;
	}

	/**
	 * Inserts an exact copy of the given item into the Customization inventory.
	 */
	public void insertModule(ItemStack module){
		if(module.isEmpty() || !(module.getItem() instanceof ItemModule))
			return;

		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty())
				if(modules.get(i).getItem() == module.getItem())
					return;

		for(int i = 0; i < modules.size(); i++)
			if(modules.get(i).isEmpty()){
				modules.set(i, module.copy());
				break;
			}
			else
				continue;
	}

	/**
	 * Removes the first item with the given module type from the inventory.
	 */
	public void removeModule(EnumCustomModules module){
		for(int i = 0; i < modules.size(); i++)
			if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ItemModule && ((ItemModule) modules.get(i).getItem()).getModule() == module)
				modules.set(i, ItemStack.EMPTY);
	}

	/**
	 * Does this inventory contain a item with the given module type?
	 */
	public boolean hasModule(EnumCustomModules module){
		if(module == null){
			for(int i = 0; i < modules.size(); i++)
				if(modules.get(i).isEmpty())
					return true;
		}
		else
			for(int i = 0; i < modules.size(); i++)
				if(!modules.get(i).isEmpty() && modules.get(i).getItem() instanceof ItemModule && ((ItemModule) modules.get(i).getItem()).getModule() == module)
					return true;

		return false;
	}

	public int getNumberOfCustomizableOptions(){
		return acceptedModules().length;
	}

	public ArrayList<EnumCustomModules> getAcceptedModules(){
		ArrayList<EnumCustomModules> list = new ArrayList<EnumCustomModules>();

		for(EnumCustomModules module : acceptedModules())
			list.add(module);

		return list;
	}

	/**
	 * Checks to see if this TileEntity has an {@link Option}
	 * with the given name, and if so, returns it.
	 *
	 * @param name Option name
	 * @return The Option
	 */
	public Option<?> getOptionByName(String name) {
		for(Option<?> option : customOptions())
			if(option.getName().equals(name))
				return option;

		return null;
	}

	/**
	 * Sets this TileEntity able to be "linked" with other blocks,
	 * and being able to do things between them. Call CustomizableSCTE.link()
	 * to link two blocks together.
	 */
	public CustomizableSCTE linkable() {
		linkable = true;
		return this;
	}

	/**
	 * @return If this TileEntity is able to be linked with.
	 */
	public boolean canBeLinkedWith() {
		return linkable;
	}

	/**
	 * Links two blocks together. Calls onLinkedBlockAction()
	 * whenever certain events (found in {@link EnumLinkedAction}) occur.
	 */
	public static void link(CustomizableSCTE tileEntity1, CustomizableSCTE tileEntity2) {
		if(!tileEntity1.linkable || !tileEntity2.linkable) return;
		if(isLinkedWith(tileEntity1, tileEntity2)) return;

		LinkedBlock block1 = new LinkedBlock(tileEntity1);
		LinkedBlock block2 = new LinkedBlock(tileEntity2);

		if(!tileEntity1.linkedBlocks.contains(block2))
			tileEntity1.linkedBlocks.add(block2);

		if(!tileEntity2.linkedBlocks.contains(block1))
			tileEntity2.linkedBlocks.add(block1);
	}

	/**
	 * Unlinks the second TileEntity from the first.
	 *
	 * @param tileEntity1 The TileEntity to unlink from
	 * @param tileEntity2 The TileEntity to unlink
	 */
	public static void unlink(CustomizableSCTE tileEntity1, CustomizableSCTE tileEntity2) {
		if(tileEntity1 == null || tileEntity2 == null) return;
		if(!tileEntity1.linkable || !tileEntity2.linkable) return;

		LinkedBlock block = new LinkedBlock(tileEntity2);

		if(tileEntity1.linkedBlocks.contains(block))
			tileEntity1.linkedBlocks.remove(block);
	}

	/**
	 * @return Are the two blocks linked together?
	 */
	public static boolean isLinkedWith(CustomizableSCTE tileEntity1, CustomizableSCTE tileEntity2) {
		if(!tileEntity1.linkable || !tileEntity2.linkable) return false;

		return tileEntity1.linkedBlocks.contains(new LinkedBlock(tileEntity2)) && tileEntity2.linkedBlocks.contains(new LinkedBlock(tileEntity1));
	}

	/**
	 * Called whenever an {@link Option} in this TileEntity changes values.
	 *
	 * @param option The changed Option
	 */
	public void onOptionChanged(Option<?> option) {
		createLinkedBlockAction(EnumLinkedAction.OPTION_CHANGED, new Option[]{ option }, this);
	}

	/**
	 * Calls onLinkedBlockAction() for every block this TileEntity
	 * is linked to. <p>
	 *
	 * <b>NOTE:</b> Never use this method in onLinkedBlockAction(),
	 * use createLinkedBlockAction(EnumLinkedAction, Object[], ArrayList[CustomizableSCTE] instead.
	 *
	 * @param action The action that occurred
	 * @param parameters Action-specific parameters, see comments in {@link EnumLinkedAction}
	 * @param excludedTE The CustomizableSCTE which called this method, prevents infinite loops.
	 */
	public void createLinkedBlockAction(EnumLinkedAction action, Object[] parameters, CustomizableSCTE excludedTE) {
		ArrayList<CustomizableSCTE> list = new ArrayList<CustomizableSCTE>();

		list.add(excludedTE);

		createLinkedBlockAction(action, parameters, list);
	}

	/**
	 * Calls onLinkedBlockAction() for every block this TileEntity
	 * is linked to.
	 *
	 * @param action The action that occurred
	 * @param parameters Action-specific parameters, see comments in {@link EnumLinkedAction}
	 * @param excludedTEs CustomizableSCTEs that shouldn't have onLinkedBlockAction() called on them,
	 *        prevents infinite loops. Always add your TileEntity to the list whenever using this method
	 */
	public void createLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<CustomizableSCTE> excludedTEs) {
		if(!linkable) return;

		for(LinkedBlock block : linkedBlocks)
			if(excludedTEs.contains(block.asTileEntity(world)))
				continue;
			else {
				block.asTileEntity(world).onLinkedBlockAction(action, parameters, excludedTEs);
				block.asTileEntity(world).sync();
			}
	}

	/**
	 * Called whenever certain actions occur in blocks
	 * this TileEntity is linked to. See {@link EnumLinkedAction}
	 * for parameter descriptions. <p>
	 *
	 * @param action The {@link EnumLinkedAction} that occurred
	 * @param parameters Important variables related to the action
	 * @param excludedTEs CustomizableSCTEs that aren't going to have onLinkedBlockAction() called on them,
	 *        always add your TileEntity to the list if you're going to call createLinkedBlockAction() in this method to chain-link multiple blocks (i.e: like Laser Blocks)
	 */
	protected void onLinkedBlockAction(EnumLinkedAction action, Object[] parameters, ArrayList<CustomizableSCTE> excludedTEs) {}

	/**
	 * @return An array of what {@link EnumCustomModules} can be inserted
	 *         into this TileEntity.
	 */
	public abstract EnumCustomModules[] acceptedModules();

	/**
	 * @return An array of what custom {@link Option}s this
	 *         TileEntity has.
	 */
	public abstract Option<?>[] customOptions();

}
