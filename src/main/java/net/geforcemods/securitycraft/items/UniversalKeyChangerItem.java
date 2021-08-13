package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class UniversalKeyChangerItem extends Item {

	public UniversalKeyChangerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx)
	{
		return onItemUseFirst(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getItemInHand(), ctx.getHand());
	}

	public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, ItemStack stack, InteractionHand hand) {
		InteractionResult briefcaseResult = handleBriefcase(player, hand).getResult();

		if(briefcaseResult != InteractionResult.PASS)
			return briefcaseResult;

		BlockEntity te = world.getBlockEntity(pos);

		if(te instanceof IPasswordProtected) {
			if(((IOwnable) te).getOwner().isOwner(player))
			{
				if(!world.isClientSide)
					NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new GenericTEContainer(SCContent.cTypeKeyChanger, windowId, world, pos);
						}

						@Override
						public Component getDisplayName()
						{
							return new TranslatableComponent(getDescriptionId());
						}
					}, pos);

				return InteractionResult.SUCCESS;
			}
			else if(!(te instanceof DisguisableBlockEntity) || (((BlockItem)((DisguisableBlock)((DisguisableBlockEntity)te).getBlockState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock)) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", ((IOwnable)world.getBlockEntity(pos)).getOwner().getName()), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		return handleBriefcase(player, hand);
	}

	private InteractionResultHolder<ItemStack> handleBriefcase(Player player, InteractionHand hand)
	{
		ItemStack keyChanger = player.getItemInHand(hand);

		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get()) {
			ItemStack briefcase = player.getOffhandItem();

			if (BriefcaseItem.isOwnedBy(briefcase, player)) {
				if (briefcase.hasTag() && briefcase.getTag().contains("passcode")) {
					briefcase.getTag().remove("passcode");
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
