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

		World world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		TileEntity te = world.getBlockEntity(pos);
		String newOwner = stack.getHoverName().getString();

		if (!(te instanceof IOwnable)) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), TextFormatting.RED);
			return ActionResultType.FAIL;
		}
		else if (te instanceof DisplayCaseBlockEntity && ((DisplayCaseBlockEntity) te).isOpen())
			return ActionResultType.PASS;

		IOwnable ownable = (IOwnable) te;
		Owner owner = ownable.getOwner();
		boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

		if (!ownable.isOwnedBy(player) && !isDefault) {
			if (!(block instanceof IBlockMine) && (!(te.getBlockState().getBlock() instanceof DisguisableBlock) || (((BlockItem) ((DisguisableBlock) te.getBlockState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock))) {
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

		if (te instanceof IOwnable) {
			((IOwnable) te).setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID", newOwner);
			((IOwnable) te).onOwnerChanged(state, world, pos, player);
		}

		if (!world.isClientSide)
			world.getServer().getPlayerList().broadcastAll(te.getUpdatePacket());

		//disable this in a development environment
		if (FMLEnvironment.production && te instanceof IModuleInventory) {
			for (ModuleType moduleType : ((IModuleInventory) te).getInsertedModules()) {
				ItemStack moduleStack = ((IModuleInventory) te).getModule(moduleType);

				((IModuleInventory) te).removeModule(moduleType, false);
				Block.popResource(world, pos, moduleStack);
			}
		}

		PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
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