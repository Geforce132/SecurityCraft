package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

public class UniversalBlockModifierItem extends Item {
	public UniversalBlockModifierItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockEntity be = level.getBlockEntity(pos);
		Player player = ctx.getPlayer();

		if (be instanceof DisplayCaseBlockEntity displayCase && (displayCase.isOpen() && displayCase.getDisplayedStack().isEmpty()))
			return InteractionResult.PASS;
		else if (be instanceof IModuleInventory) {
			if (be instanceof IOwnable ownable && !ownable.isOwnedBy(player)) {
				if (!(be.getBlockState().getBlock() instanceof IDisguisable db) || (((BlockItem) db.getDisguisedStack(level, pos).getItem()).getBlock() instanceof IDisguisable))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(ownable.getOwner())), ChatFormatting.RED);

				return InteractionResult.FAIL;
			}
			else if (!ctx.getLevel().isClientSide) {
				NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
						return new CustomizeBlockMenu(windowId, level, pos, inv);
					}

					@Override
					public Component getDisplayName() {
						if (be instanceof Nameable nameable)
							return nameable.getDisplayName();
						else
							return Component.translatable(be.getBlockState().getBlock().getDescriptionId());
					}
				}, pos);
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}
}
