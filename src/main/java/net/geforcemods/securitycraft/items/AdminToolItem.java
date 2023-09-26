package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class AdminToolItem extends Item {
	public AdminToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		PlayerEntity player = ctx.getPlayer();
		IFormattableTextComponent adminToolName = Utils.localize(getDescriptionId());

		if (ConfigHandler.SERVER.allowAdminTool.get()) {
			if (!player.isCreative()) {
				PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.needCreative"), TextFormatting.DARK_PURPLE);
				return ActionResultType.FAIL;
			}

			ActionResultType briefcaseResult = handleBriefcase(player, ctx.getHand()).getResult();

			if (briefcaseResult != ActionResultType.PASS)
				return briefcaseResult;

			TileEntity be = level.getBlockEntity(pos);

			if (be != null) {
				if (be instanceof DisplayCaseBlockEntity && (((DisplayCaseBlockEntity) be).isOpen() && ((DisplayCaseBlockEntity) be).getDisplayedStack().isEmpty()))
					return ActionResultType.PASS;

				boolean hasInfo = false;

				if (be instanceof IOwnable) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", (((IOwnable) be).getOwner().getName() == null ? "????" : ((IOwnable) be).getOwner().getName())), TextFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", (((IOwnable) be).getOwner().getUUID() == null ? "????" : ((IOwnable) be).getOwner().getUUID())), TextFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if (be instanceof IModuleInventory) {
					List<ModuleType> modules = ((IModuleInventory) be).getInsertedModules();

					if (!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.equippedModules"), TextFormatting.DARK_PURPLE);

						for (ModuleType module : modules) {
							PlayerUtils.sendMessageToPlayer(player, adminToolName, new StringTextComponent("- ").append(new TranslationTextComponent(module.getTranslationKey())), TextFormatting.DARK_PURPLE);
						}

						hasInfo = true;
					}
				}

				if (be instanceof SecretSignBlockEntity) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, new StringTextComponent(""), TextFormatting.DARK_PURPLE); //EMPTY

					for (int i = 0; i < 4; i++) {
						ITextProperties text = ((SecretSignBlockEntity) be).messages[i];

						if (text instanceof IFormattableTextComponent)
							PlayerUtils.sendMessageToPlayer(player, adminToolName, (IFormattableTextComponent) text, TextFormatting.DARK_PURPLE);
					}

					hasInfo = true;
				}

				if (!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);

				return ActionResultType.SUCCESS;
			}

			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.noInfo"), TextFormatting.DARK_PURPLE);
		}
		else
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.disabled"), TextFormatting.DARK_PURPLE);

		return ActionResultType.FAIL;
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		if (!player.isCreative()) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:adminTool.needCreative"), TextFormatting.DARK_PURPLE);
			return ActionResult.fail(player.getItemInHand(hand));
		}
		else
			return handleBriefcase(player, hand);
	}

	private ActionResult<ItemStack> handleBriefcase(PlayerEntity player, Hand hand) {
		ItemStack adminTool = player.getItemInHand(hand);

		if (hand == Hand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get()) {
			ItemStack briefcase = player.getOffhandItem();
			IFormattableTextComponent adminToolName = Utils.localize(getDescriptionId());
			String ownerName = BriefcaseItem.getOwnerName(briefcase);
			String ownerUUID = BriefcaseItem.getOwnerUUID(briefcase);

			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", ownerName.isEmpty() ? "????" : ownerName), TextFormatting.DARK_PURPLE);
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", ownerUUID.isEmpty() ? "????" : ownerUUID), TextFormatting.DARK_PURPLE);
			return ActionResult.success(adminTool);
		}

		return ActionResult.pass(adminTool);
	}
}
