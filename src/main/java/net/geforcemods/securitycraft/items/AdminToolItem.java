package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class AdminToolItem extends Item {

	public AdminToolItem() {
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1).maxStackSize(1));
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		PlayerEntity player = ctx.getPlayer();
		if(!world.isRemote && ConfigHandler.CONFIG.allowAdminTool.get()) {
			IFormattableTextComponent adminToolName = ClientUtils.localize(SCContent.ADMIN_TOOL.get().getTranslationKey());

			if(world.getTileEntity(pos) != null) {
				TileEntity te = world.getTileEntity(pos);
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.owner.name", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), TextFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.owner.uuid", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.password", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IModuleInventory) {
					List<ModuleType> modules = ((IModuleInventory) te).getInsertedModules();

					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.equippedModules"), TextFormatting.DARK_PURPLE);

						for(ModuleType module : modules)
							PlayerUtils.sendMessageToPlayer(player, adminToolName, new StringTextComponent("- ").append(new TranslationTextComponent(module.getTranslationKey())), TextFormatting.DARK_PURPLE);

						hasInfo = true;
					}
				}

				if(te instanceof SecretSignTileEntity)
				{
					PlayerUtils.sendMessageToPlayer(player, adminToolName, new StringTextComponent(""), TextFormatting.DARK_PURPLE); //EMPTY

					for(int i = 0; i < 4; i++)
					{
						ITextProperties text = ((SecretSignTileEntity)te).func_235677_a_(i, tc -> tc);

						if(text instanceof IFormattableTextComponent)
							PlayerUtils.sendMessageToPlayer(player, adminToolName, (IFormattableTextComponent)text, TextFormatting.DARK_PURPLE);
					}

					hasInfo = true;
				}

				if(!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);

				return ActionResultType.FAIL;
			}

			PlayerUtils.sendMessageToPlayer(player, adminToolName, ClientUtils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);
		}

		return ActionResultType.FAIL;
	}

}
