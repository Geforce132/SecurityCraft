package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class UniversalBlockModifierItem extends Item {
	public UniversalBlockModifierItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		TileEntity be = level.getBlockEntity(pos);
		PlayerEntity player = ctx.getPlayer();

		if (be instanceof DisplayCaseBlockEntity && (((DisplayCaseBlockEntity) be).isOpen() && ((DisplayCaseBlockEntity) be).getDisplayedStack().isEmpty()))
			return ActionResultType.PASS;
		else if (be instanceof IModuleInventory) {
			if (be instanceof IOwnable && !((IOwnable) be).isOwnedBy(player)) {
				if (!(be.getBlockState().getBlock() instanceof IDisguisable) || (((BlockItem) ((IDisguisable) be.getBlockState().getBlock()).getDisguisedStack(level, pos).getItem()).getBlock() instanceof IDisguisable))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) be).getOwner())), TextFormatting.RED);

				return ActionResultType.FAIL;
			}
			else if (!ctx.getLevel().isClientSide) {
				NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
						return new CustomizeBlockMenu(windowId, level, pos, inv);
					}

					@Override
					public ITextComponent getDisplayName() {
						if (be instanceof INameable)
							return ((INameable) be).getDisplayName();
						else
							return new TranslationTextComponent(be.getBlockState().getBlock().getDescriptionId());
					}
				}, pos);
			}

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}
}
