package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.components.PasscodeData;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class BriefcaseItem extends Item {
	public BriefcaseItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		handle(ctx.getItemInHand(), ctx.getLevel(), ctx.getPlayer());
		return InteractionResult.CONSUME;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		handle(stack, level, player);
		return InteractionResult.CONSUME;
	}

	private void handle(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide)
			PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(stack.has(SCContent.PASSCODE_DATA) ? OpenScreen.DataType.CHECK_PASSCODE_FOR_BRIEFCASE : OpenScreen.DataType.SET_PASSCODE_FOR_BRIEFCASE));
	}

	public static void checkPasscode(Player player, ItemStack briefcase, String incomingCode, PasscodeData passcodeData) {
		PasscodeUtils.setOnCooldown(player);
		passcodeData.checkPasscode(briefcase, incomingCode, () -> {
			if (!briefcase.has(SCContent.OWNER_DATA)) //If the briefcase doesn't have an owner (that usually gets set when assigning a new passcode), set the player that first enters the correct passcode as the owner
				briefcase.set(SCContent.OWNER_DATA, OwnerData.fromPlayer(player, true));

			player.openMenu(new MenuProvider() {
				@Override
				public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
					return new BriefcaseMenu(windowId, inv, ItemContainer.briefcase(PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get())));
				}

				@Override
				public Component getDisplayName() {
					return briefcase.getHoverName();
				}
			}, player.blockPosition());
		});
	}

	public static boolean isOwnedBy(ItemStack briefcase, Player player) {
		OwnerData owner = briefcase.get(SCContent.OWNER_DATA);

		if (owner != null) {
			String ownerName = owner.name();
			String ownerUUID = owner.uuid();

			return ownerName.isEmpty() || ownerUUID.equals(player.getUUID().toString()) || (ownerUUID.equals("ownerUUID") && ownerName.equals(player.getName().getString()));
		}

		return false;
	}
}
