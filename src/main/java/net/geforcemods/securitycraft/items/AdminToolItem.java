package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

public class AdminToolItem extends Item {
	public AdminToolItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Player player = ctx.getPlayer();
		MutableComponent adminToolName = Utils.localize(getDescriptionId());

		if (ConfigHandler.SERVER.allowAdminTool.get()) {
			if (!player.isCreative()) {
				PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.needCreative"), ChatFormatting.DARK_PURPLE);
				return InteractionResult.FAIL;
			}

			InteractionResult briefcaseResult = handleBriefcase(player, ctx.getHand()).getResult();

			if (briefcaseResult != InteractionResult.PASS)
				return briefcaseResult;

			BlockEntity be = level.getBlockEntity(pos);

			if (be != null) {
				if (be instanceof DisplayCaseBlockEntity displayCase && (displayCase.isOpen() && displayCase.getDisplayedStack().isEmpty()))
					return InteractionResult.PASS;

				boolean hasInfo = false;
				boolean isOwnable = be instanceof IOwnable;

				if (isOwnable) {
					Owner owner = ((IOwnable) be).getOwner();

					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", (owner.getName() == null ? "????" : owner.getName())), ChatFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", (owner.getUUID() == null ? "????" : owner.getUUID())), ChatFormatting.DARK_PURPLE);
					hasInfo = true;
				}

				if (be instanceof IModuleInventory inv) {
					List<ModuleType> modules = inv.getInsertedModules();

					if (!modules.isEmpty()) {
						PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.equippedModules"), ChatFormatting.DARK_PURPLE);

						for (ModuleType module : modules) {
							PlayerUtils.sendMessageToPlayer(player, adminToolName, Component.literal("- ").append(Component.translatable(module.getTranslationKey())), ChatFormatting.DARK_PURPLE);
						}

						hasInfo = true;
					}
				}

				if (isOwnable && be instanceof SignBlockEntity signBe) {
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Component.literal(""), ChatFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.signFrontText"), ChatFormatting.DARK_PURPLE);
					sendSignText(signBe.getFrontText(), player, adminToolName);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Component.literal(""), ChatFormatting.DARK_PURPLE);
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.signBackText"), ChatFormatting.DARK_PURPLE);
					sendSignText(signBe.getBackText(), player, adminToolName);
					hasInfo = true;
				}

				if (!hasInfo)
					PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.noInfo"), ChatFormatting.DARK_PURPLE);

				return InteractionResult.SUCCESS;
			}

			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.noInfo"), ChatFormatting.DARK_PURPLE);
		}
		else
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.disabled"), ChatFormatting.DARK_PURPLE);

		return InteractionResult.FAIL;
	}

	private void sendSignText(SignText signText, Player player, MutableComponent adminToolName) {
		for (int i = 0; i < 4; i++) {
			Component text = signText.getMessage(i, false);

			if (text instanceof MutableComponent mutableComponent)
				PlayerUtils.sendMessageToPlayer(player, adminToolName, mutableComponent, ChatFormatting.DARK_PURPLE);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!player.isCreative()) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:adminTool.needCreative"), ChatFormatting.DARK_PURPLE);
			return InteractionResultHolder.fail(player.getItemInHand(hand));
		}
		else
			return handleBriefcase(player, hand);
	}

	private InteractionResultHolder<ItemStack> handleBriefcase(Player player, InteractionHand hand) {
		ItemStack adminTool = player.getItemInHand(hand);

		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get()) {
			ItemStack briefcase = player.getOffhandItem();
			MutableComponent adminToolName = Utils.localize(getDescriptionId());
			OwnerData ownerData = briefcase.getOrDefault(SCContent.OWNER_DATA, OwnerData.DEFAULT);
			String ownerName = ownerData.name();
			String ownerUUID = ownerData.uuid();

			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.name", ownerName.isEmpty() ? "????" : ownerName), ChatFormatting.DARK_PURPLE);
			PlayerUtils.sendMessageToPlayer(player, adminToolName, Utils.localize("messages.securitycraft:adminTool.owner.uuid", ownerUUID.isEmpty() ? "????" : ownerUUID), ChatFormatting.DARK_PURPLE);
			return InteractionResultHolder.success(adminTool);
		}

		return InteractionResultHolder.pass(adminTool);
	}
}
