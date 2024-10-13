package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.components.PasscodeData;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public class UniversalKeyChangerItem extends Item {
	public UniversalKeyChangerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
		Player player = ctx.getPlayer();
		InteractionHand hand = ctx.getHand();
		Level level = ctx.getLevel();
		InteractionResult briefcaseResult = handleBriefcase(level, player, hand);

		if (briefcaseResult != InteractionResult.PASS)
			return briefcaseResult;

		BlockPos pos = ctx.getClickedPos();
		BlockEntity be = level.getBlockEntity(pos);

		if (be instanceof DisplayCaseBlockEntity displayCase && (displayCase.isOpen() && displayCase.getDisplayedStack().isEmpty()))
			return InteractionResult.PASS;
		else if (be instanceof IPasscodeProtected) {
			if (((IOwnable) be).isOwnedBy(player) || player.isCreative()) {
				if (!level.isClientSide)
					PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(DataType.CHANGE_PASSCODE, pos));

				return InteractionResult.SUCCESS;
			}
			else if (!(be.getBlockState().getBlock() instanceof IDisguisable db) || (((BlockItem) db.getDisguisedStack(level, pos).getItem()).getBlock() instanceof IDisguisable)) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) be).getOwner())), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		return handleBriefcase(level, player, hand);
	}

	private InteractionResult handleBriefcase(Level level, Player player, InteractionHand hand) {
		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get()) {
			ItemStack briefcase = player.getOffhandItem();

			if (BriefcaseItem.isOwnedBy(briefcase, player) || player.isCreative()) {
				PasscodeData passcodeData = briefcase.get(SCContent.PASSCODE_DATA);

				if (passcodeData != null) {
					if (!level.isClientSide)
						SaltData.removeSalt(passcodeData.saltKey());

					briefcase.remove(SCContent.PASSCODE_DATA);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.passcodeReset"), ChatFormatting.GREEN);
					return InteractionResult.SUCCESS_SERVER;
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.noPasscode"), ChatFormatting.RED);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.notOwned"), ChatFormatting.RED);

			return InteractionResult.CONSUME;
		}

		return InteractionResult.PASS;
	}
}
