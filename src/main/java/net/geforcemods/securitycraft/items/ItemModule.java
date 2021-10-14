package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModule extends Item{

	public static final int MAX_PLAYERS = 50;
	private final EnumModuleType module;
	private final boolean containsCustomData;
	private boolean canBeCustomized;
	private int guiToOpen;

	public ItemModule(EnumModuleType module, boolean containsCustomData){
		this(module, containsCustomData, false, -1);
	}

	public ItemModule(EnumModuleType module, boolean containsCustomData, boolean canBeCustomized, int guiToOpen){
		this.module = module;
		this.containsCustomData = containsCustomData;
		this.canBeCustomized = canBeCustomized;
		this.guiToOpen = guiToOpen;

		setMaxStackSize(1);
		setCreativeTab(SecurityCraft.tabSCTechnical);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof IModuleInventory)
		{
			IModuleInventory inv = (IModuleInventory)te;
			ItemStack stack = player.getHeldItem(hand);
			EnumModuleType type = ((ItemModule)stack.getItem()).getModuleType();

			if(te instanceof IOwnable && !((IOwnable)te).getOwner().isOwner(player))
				return EnumActionResult.PASS;

			if(inv.getAcceptedModules().contains(type) && !inv.hasModule(type))
			{
				inv.insertModule(stack);
				inv.onModuleInserted(stack, type);

				if(!player.isCreative())
					stack.shrink(1);

				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(!player.isSneaking())
		{
			if(!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());

			if(canBeCustomized()) {
				player.openGui(SecurityCraft.instance, guiToOpen, world, (int)player.posX, (int)player.posY, (int)player.posZ);
				return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
			}
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
		if(containsCustomData || canBeCustomized())
			list.add(Utils.localize("tooltip.securitycraft:module.modifiable").getFormattedText());
		else
			list.add(Utils.localize("tooltip.securitycraft:module.notModifiable").getFormattedText());

		if(canBeCustomized()) {
			Block addon = getBlockAddon(stack.getTagCompound());

			if(addon != null)
				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.added", Utils.localize(addon.getTranslationKey())).getFormattedText());
		}
	}

	public EnumModuleType getModuleType() {
		return module;
	}

	public Block getBlockAddon(NBTTagCompound tag){
		ItemStack stack = getAddonAsStack(tag);

		if(stack.getItem() instanceof ItemBlock)
			return ((ItemBlock)stack.getItem()).getBlock();
		else return null;
	}

	public ItemStack getAddonAsStack(NBTTagCompound tag){
		if(tag == null)
			return ItemStack.EMPTY;

		NBTTagList items = tag.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		if(items != null && !items.isEmpty())
			return new ItemStack(items.getCompoundTagAt(0));

		return ItemStack.EMPTY;
	}

	public boolean canBeCustomized(){
		return canBeCustomized;
	}

}
