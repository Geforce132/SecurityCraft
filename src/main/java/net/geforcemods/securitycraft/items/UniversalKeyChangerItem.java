package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
		InteractionResult briefcaseResult = handleBriefcase(level, player, hand).getResult();

		if (briefcaseResult != InteractionResult.PASS)
			return briefcaseResult;

		BlockPos pos = ctx.getClickedPos();
		BlockEntity be = level.getBlockEntity(pos);

		if (be instanceof DisplayCaseBlockEntity displayCase && (displayCase.isOpen() && displayCase.getDisplayedStack().isEmpty()))
			return InteractionResult.PASS;
		else if (be instanceof IPasscodeProtected) {
			if (((IOwnable) be).isOwnedBy(player) || player.isCreative()) {
				if (!level.isClientSide)
					PacketDistributor.PLAYER.with((ServerPlayer) player).send(new OpenScreen(DataType.UNIVERSAL_KEY_CHANGER, pos));

				return InteractionResult.SUCCESS;
			}
			else if (!(be.getBlockState().getBlock() instanceof DisguisableBlock db) || (((BlockItem) db.getDisguisedStack(level, pos).getItem()).getBlock() instanceof DisguisableBlock)) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) be).getOwner())), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return handleBriefcase(level, player, hand);
	}

	private InteractionResultHolder<ItemStack> handleBriefcase(Level level, Player player, InteractionHand hand) {
		ItemStack keyChanger = player.getItemInHand(hand);

		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get()) {
			ItemStack briefcase = player.getOffhandItem();

			if (BriefcaseItem.isOwnedBy(briefcase, player) || player.isCreative()) {
				CompoundTag tag = briefcase.getTag();

				if (tag != null && tag.contains("passcode")) {
					if (tag.contains("saltKey") && !level.isClientSide)
						SaltData.removeSalt(tag.getUUID("saltKey"));

					PasscodeUtils.filterPasscodeAndSaltFromTag(tag);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.passcodeReset"), ChatFormatting.GREEN);
					return InteractionResultHolder.success(keyChanger);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.noPasscode"), ChatFormatting.RED);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.briefcase.notOwned"), ChatFormatting.RED);

			return InteractionResultHolder.consume(keyChanger);
		}

		return InteractionResultHolder.pass(keyChanger);
	}
}
