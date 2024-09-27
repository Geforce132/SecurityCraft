package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UniversalOwnerChangerItem extends Item {
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return true;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		//prioritize handling the briefcase
		if (hand == EnumHand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.briefcase)
			return handleBriefcase(player, stack).getType();

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		TileEntity te = world.getTileEntity(pos);
		String newOwner = stack.getDisplayName();

		if (!(te instanceof IOwnable)) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:universalOwnerChanger.name"), Utils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), TextFormatting.RED);
			return EnumActionResult.SUCCESS;
		}

		IOwnable ownable = (IOwnable) te;
		Owner owner = ownable.getOwner();
		boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

		if (!ownable.isOwnedBy(player) && !isDefault) {
			if (!(block instanceof IBlockMine) && (!(te.getBlockType() instanceof IDisguisable) || (((IDisguisable) te.getBlockType()).getDisguisedBlockState(world, pos).getBlock() instanceof IDisguisable))) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:universalOwnerChanger.name"), Utils.localize("messages.securitycraft:universalOwnerChanger.notOwned"), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}

			return EnumActionResult.PASS;
		}

		if (!stack.hasDisplayName() && !isDefault) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:universalOwnerChanger.name"), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
			return EnumActionResult.SUCCESS;
		}

		if (isDefault) {
			if (ConfigHandler.allowBlockClaim)
				newOwner = player.getName();
			else {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:universalOwnerChanger.name"), Utils.localize("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}
		}

		Owner oldOwner = ownable.getOwner().copy();

		ownable.setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
		ownable.onOwnerChanged(state, world, pos, player, oldOwner, ownable.getOwner());

		if (!world.isRemote)
			world.notifyBlockUpdate(pos, state, state, 3);

		if (!world.isRemote && te instanceof IModuleInventory) {
			for (ModuleType moduleType : ((IModuleInventory) te).getInsertedModules()) {
				ItemStack moduleStack = ((IModuleInventory) te).getModule(moduleType);

				((IModuleInventory) te).removeModule(moduleType, false);
				Block.spawnAsEntity(world, pos, moduleStack);
			}
		}

		PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:universalOwnerChanger.name"), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack ownerChanger = player.getHeldItem(hand);

		if (!ownerChanger.hasDisplayName()) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.universalOwnerChanger.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
			return ActionResult.newResult(EnumActionResult.SUCCESS, ownerChanger);
		}

		if (hand == EnumHand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.briefcase)
			return handleBriefcase(player, ownerChanger);

		return ActionResult.newResult(EnumActionResult.PASS, ownerChanger);
	}

	private ActionResult<ItemStack> handleBriefcase(EntityPlayer player, ItemStack ownerChanger) {
		ItemStack briefcase = player.getHeldItemOffhand();

		if (BriefcaseItem.isOwnedBy(briefcase, player)) {
			String newOwner = ownerChanger.getDisplayName();

			if (!briefcase.hasTagCompound())
				briefcase.setTagCompound(new NBTTagCompound());

			briefcase.getTagCompound().setString("owner", newOwner);
			briefcase.getTagCompound().setString("ownerUUID", PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID");
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.universalOwnerChanger.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
			return ActionResult.newResult(EnumActionResult.SUCCESS, ownerChanger);
		}
		else
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.universalOwnerChanger.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:universalOwnerChanger.briefcase.notOwned"), TextFormatting.RED);

		return ActionResult.newResult(EnumActionResult.SUCCESS, ownerChanger);
	}
}