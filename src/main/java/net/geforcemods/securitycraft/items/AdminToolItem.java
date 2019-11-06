package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class AdminToolItem extends Item {

	public AdminToolItem() {
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1).maxStackSize(1));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		PlayerEntity player = ctx.getPlayer();
		if(!world.isRemote && CommonConfig.CONFIG.allowAdminTool.get()) {
			if(world.getTileEntity(pos) != null) {
				TileEntity te = world.getTileEntity(pos);
				boolean hasInfo = false;

				if(te instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.adminTool.getTranslationKey()), ClientUtils.localize("messages.securitycraft:adminTool.owner.name").replace("#", (((IOwnable) te).getOwner().getName() == null ? "????" : ((IOwnable) te).getOwner().getName())), TextFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.adminTool.getTranslationKey()), ClientUtils.localize("messages.securitycraft:adminTool.owner.uuid").replace("#", (((IOwnable) te).getOwner().getUUID() == null ? "????" : ((IOwnable) te).getOwner().getUUID())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof IPasswordProtected) {
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.adminTool.getTranslationKey()), ClientUtils.localize("messages.securitycraft:adminTool.password").replace("#", (((IPasswordProtected) te).getPassword() == null ? "????" : ((IPasswordProtected) te).getPassword())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if(te instanceof CustomizableTileEntity) {
					List<CustomModules> modules = ((CustomizableTileEntity) te).getModules();

					if(!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.adminTool.getTranslationKey()), ClientUtils.localize("messages.securitycraft:adminTool.equippedModules"), TextFormatting.DARK_PURPLE);

						for(CustomModules module : modules)
							PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.adminTool.getTranslationKey()), "-" + module.getName(), TextFormatting.DARK_PURPLE);

						hasInfo = true;
					}
				}

				if(te instanceof SecretSignTileEntity)
				{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.adminTool.getTranslationKey()), "", TextFormatting.DARK_PURPLE);

					for(int i = 0; i < 4; i++)
					{
						PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.adminTool.getTranslationKey()), ((SecretSignTileEntity)te).signText[i].getUnformattedComponentText(), TextFormatting.DARK_PURPLE);
					}

					hasInfo = true;
				}

				if(!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.adminTool.getTranslationKey()), ClientUtils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);

				return ActionResultType.FAIL;
			}

			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.adminTool.getTranslationKey()), ClientUtils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);
		}

		return ActionResultType.FAIL;
	}

}
