package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLEnvironment;

public class UniversalOwnerChangerItem extends Item {
	public UniversalOwnerChangerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
		InteractionHand hand = ctx.getHand();
		Player player = ctx.getPlayer();

		//prioritize handling the briefcase
		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get())
			return handleBriefcase(player, stack).getResult();

		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		BlockEntity be = level.getBlockEntity(pos);
		String newOwner = stack.getHoverName().getString();

		if (!(be instanceof IOwnable ownable)) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), ChatFormatting.RED);
			return InteractionResult.FAIL;
		}
		else if (be instanceof DisplayCaseBlockEntity displayCase && displayCase.isOpen())
			return InteractionResult.PASS;

		Owner owner = ownable.getOwner();
		boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

		if (!ownable.isOwnedBy(player) && !isDefault) {
			if (!(block instanceof IBlockMine) && (!(be.getBlockState().getBlock() instanceof DisguisableBlock db) || (((BlockItem) db.getDisguisedStack(level, pos).getItem()).getBlock() instanceof DisguisableBlock))) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.notOwned"), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}

			return InteractionResult.PASS;
		}

		if (!stack.has(DataComponents.CUSTOM_NAME) && !isDefault) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), ChatFormatting.RED);
			return InteractionResult.FAIL;
		}

		if (isDefault) {
			if (ConfigHandler.SERVER.allowBlockClaim.get())
				newOwner = player.getName().getString();
			else {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}
		}

		Owner oldOwner = ownable.getOwner().copy();

		ownable.setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID", newOwner);
		ownable.onOwnerChanged(state, level, pos, player, oldOwner, ownable.getOwner());

		if (!level.isClientSide)
			level.sendBlockUpdated(pos, state, state, 3);

		//disable this in a development environment
		if (FMLEnvironment.production && be instanceof IModuleInventory inv) {
			for (ModuleType moduleType : inv.getInsertedModules()) {
				ItemStack moduleStack = inv.getModule(moduleType);

				inv.removeModule(moduleType, false);
				Block.popResource(level, pos, moduleStack);
			}
		}

		PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), ChatFormatting.GREEN);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack ownerChanger = player.getItemInHand(hand);

		if (!ownerChanger.has(DataComponents.CUSTOM_NAME)) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), ChatFormatting.RED);
			return InteractionResultHolder.fail(ownerChanger);
		}

		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get())
			return handleBriefcase(player, ownerChanger);

		return InteractionResultHolder.pass(ownerChanger);
	}

	private InteractionResultHolder<ItemStack> handleBriefcase(Player player, ItemStack ownerChanger) {
		ItemStack briefcase = player.getOffhandItem();

		if (BriefcaseItem.isOwnedBy(briefcase, player)) {
			String newOwner = ownerChanger.getHoverName().getString();

			briefcase.set(SCContent.OWNER_DATA, new OwnerData(newOwner, PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID", true));
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), ChatFormatting.GREEN);
			return InteractionResultHolder.success(ownerChanger);
		}
		else
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.briefcase.notOwned"), ChatFormatting.RED);

		return InteractionResultHolder.consume(ownerChanger);
	}
}