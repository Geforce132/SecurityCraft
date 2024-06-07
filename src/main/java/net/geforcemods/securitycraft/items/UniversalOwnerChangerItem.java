package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class UniversalOwnerChangerItem extends Item {
	public UniversalOwnerChangerItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
		PlayerEntity player = ctx.getPlayer();

		//prioritize handling the briefcase
		if (ctx.getHand() == Hand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get())
			return handleBriefcase(player, stack).getResult();

		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		TileEntity be = level.getBlockEntity(pos);
		String newOwner = stack.getHoverName().getString();

		if (!(be instanceof IOwnable)) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), TextFormatting.RED);
			return ActionResultType.FAIL;
		}
		else if (be instanceof DisplayCaseBlockEntity && ((DisplayCaseBlockEntity) be).isOpen())
			return ActionResultType.PASS;

		IOwnable ownable = (IOwnable) be;
		Owner owner = ownable.getOwner();
		boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

		if (!ownable.isOwnedBy(player) && !isDefault) {
			if (!(block instanceof IBlockMine) && (!(be.getBlockState().getBlock() instanceof DisguisableBlock) || (((BlockItem) ((DisguisableBlock) be.getBlockState().getBlock()).getDisguisedStack(level, pos).getItem()).getBlock() instanceof DisguisableBlock))) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.notOwned"), TextFormatting.RED);
				return ActionResultType.FAIL;
			}

			return ActionResultType.PASS;
		}

		if (!stack.hasCustomHoverName() && !isDefault) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
			return ActionResultType.FAIL;
		}

		if (isDefault) {
			if (ConfigHandler.SERVER.allowBlockClaim.get())
				newOwner = player.getName().getString();
			else {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), TextFormatting.RED);
				return ActionResultType.FAIL;
			}
		}

		Owner oldOwner = ownable.getOwner().copy();

		ownable.setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID", newOwner);
		ownable.onOwnerChanged(state, level, pos, player, oldOwner, ownable.getOwner());

		if (!level.isClientSide)
			level.sendBlockUpdated(pos, state, state, 3);

		//disable this in a development environment
		if (FMLEnvironment.production && be instanceof IModuleInventory) {
			for (ModuleType moduleType : ((IModuleInventory) be).getInsertedModules()) {
				ItemStack moduleStack = ((IModuleInventory) be).getModule(moduleType);

				((IModuleInventory) be).removeModule(moduleType, false);
				Block.popResource(level, pos, moduleStack);
			}
		}

		PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
		ItemStack ownerChanger = player.getItemInHand(hand);

		if (!ownerChanger.hasCustomHoverName()) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
			return ActionResult.fail(ownerChanger);
		}

		if (hand == Hand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get())
			return handleBriefcase(player, ownerChanger);

		return ActionResult.pass(ownerChanger);
	}

	private ActionResult<ItemStack> handleBriefcase(PlayerEntity player, ItemStack ownerChanger) {
		ItemStack briefcase = player.getOffhandItem();

		if (BriefcaseItem.isOwnedBy(briefcase, player)) {
			String newOwner = ownerChanger.getHoverName().getString();

			if (!briefcase.hasTag())
				briefcase.setTag(new CompoundNBT());

			briefcase.getTag().putString("owner", newOwner);
			briefcase.getTag().putString("ownerUUID", PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID");
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
			return ActionResult.success(ownerChanger);
		}
		else
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.briefcase.notOwned"), TextFormatting.RED);

		return ActionResult.consume(ownerChanger);
	}
}