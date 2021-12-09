package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
import net.geforcemods.securitycraft.inventory.ModuleItemInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

public class ModuleItem extends Item{

	public static final Style GRAY_STYLE = Style.EMPTY.setFormatting(TextFormatting.GRAY);
	public static final int MAX_PLAYERS = 50;
	private final ModuleType module;
	private final boolean containsCustomData;
	private boolean canBeCustomized;

	public ModuleItem(Item.Properties properties, ModuleType module, boolean containsCustomData){
		this(properties, module, containsCustomData, false);
	}

	public ModuleItem(Item.Properties properties, ModuleType module, boolean containsCustomData, boolean canBeCustomized){
		super(properties);
		this.module = module;
		this.containsCustomData = containsCustomData;
		this.canBeCustomized = canBeCustomized;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		TileEntity te = ctx.getWorld().getTileEntity(ctx.getPos());
		ItemStack stack = ctx.getItem();

		if(te instanceof IModuleInventory)
		{
			IModuleInventory inv = (IModuleInventory)te;
			ModuleType type = ((ModuleItem)stack.getItem()).getModuleType();

			if(te instanceof IOwnable && !((IOwnable)te).getOwner().isOwner(ctx.getPlayer()))
				return ActionResultType.PASS;

			if(inv.getAcceptedModules().contains(type) && !inv.hasModule(type))
			{
				inv.insertModule(stack);
				inv.onModuleInserted(stack, type);

				if(!ctx.getPlayer().isCreative())
					stack.shrink(1);

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(canBeCustomized())
		{
			if(module == ModuleType.ALLOWLIST || module == ModuleType.DENYLIST) {
				if(world.isRemote)
					ClientHandler.displayEditModuleGui(stack);

				return ActionResult.resultConsume(stack);
			}
			else if(module == ModuleType.DISGUISE)
			{
				if (!world.isRemote) {
					NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
						{
							return new DisguiseModuleContainer(windowId, inv, new ModuleItemInventory(player.getHeldItem(hand)));
						}

						@Override
						public ITextComponent getDisplayName()
						{
							return new TranslationTextComponent(getTranslationKey());
						}
					});
				}

				return ActionResult.resultConsume(stack);
			}
		}

		return ActionResult.resultPass(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		if(containsCustomData || canBeCustomized())
			list.add(new TranslationTextComponent("tooltip.securitycraft:module.modifiable").setStyle(GRAY_STYLE));
		else
			list.add(new TranslationTextComponent("tooltip.securitycraft:module.notModifiable").setStyle(GRAY_STYLE));

		if(canBeCustomized()) {
			Block addon = getBlockAddon(stack.getTag());

			if(addon != null)
				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.added", Utils.localize(addon.getTranslationKey())).setStyle(GRAY_STYLE));
		}
	}

	public ModuleType getModuleType() {
		return module;
	}

	public Block getBlockAddon(CompoundNBT tag){
		if(tag == null)
			return null;

		ListNBT items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		if(items != null && !items.isEmpty())
		{
			Item item = ItemStack.read(items.getCompound(0)).getItem();

			if(item instanceof BlockItem)
				return ((BlockItem)item).getBlock();
		}

		return null;
	}

	public boolean canBeCustomized(){
		return canBeCustomized;
	}

}
