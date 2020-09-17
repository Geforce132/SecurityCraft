package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.ScannerDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class UniversalOwnerChangerItem extends Item
{
	public UniversalOwnerChangerItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx)
	{
		return onItemUseFirst(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), stack, ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z);
	}

	public ActionResultType onItemUseFirst(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction side, double hitX, double hitY, double hitZ)
	{
		Block block = world.getBlockState(pos).getBlock();
		TileEntity te = world.getTileEntity(pos);
		String newOwner = stack.getDisplayName().getString();

		if(!world.isRemote)
		{
			if(!(te instanceof IOwnable))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), TextFormatting.RED);
				return ActionResultType.FAIL;
			}

			Owner owner = ((IOwnable)te).getOwner();
			boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

			if(!owner.isOwner(player) && !isDefault)
			{
				if(!(block instanceof IBlockMine) && (!(te instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)te).getBlockState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock)))
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.notOwned"), TextFormatting.RED);

				return ActionResultType.FAIL;
			}

			if(!stack.hasDisplayName() && !isDefault)
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
				return ActionResultType.FAIL;
			}

			if(isDefault)
			{
				if(ConfigHandler.CONFIG.allowBlockClaim.get())
					newOwner = player.getName().getString();
				else
				{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), TextFormatting.RED);
					return ActionResultType.FAIL;
				}
			}

			boolean door = false;
			boolean updateTop = true;

			if(BlockUtils.getBlock(world, pos) instanceof ReinforcedDoorBlock || BlockUtils.getBlock(world, pos) instanceof ScannerDoorBlock)
			{
				door = true;
				((IOwnable)world.getTileEntity(pos)).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);

				if(BlockUtils.getBlock(world, pos.up()) instanceof ReinforcedDoorBlock || BlockUtils.getBlock(world, pos.up()) instanceof ScannerDoorBlock)
					((IOwnable)world.getTileEntity(pos.up())).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
				else if(BlockUtils.getBlock(world, pos.down()) instanceof ReinforcedDoorBlock || BlockUtils.getBlock(world, pos.down()) instanceof ScannerDoorBlock)
				{
					((IOwnable)world.getTileEntity(pos.down())).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
					updateTop = false;
				}
			}

			if(te instanceof IOwnable)
				((IOwnable)te).getOwner().set(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);

			world.getServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());

			if(door)
				world.getServer().getPlayerList().sendPacketToAllPlayers(((OwnableTileEntity)world.getTileEntity(updateTop ? pos.up() : pos.down())).getUpdatePacket());

			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack ownerChanger = player.getHeldItem(hand);

		if (!world.isRemote) {
			if (!ownerChanger.hasDisplayName()) {
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
				return ActionResult.resultFail(ownerChanger);
			}

			if (hand == Hand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.BRIEFCASE.get()) {
				ItemStack briefcase = player.getHeldItemOffhand();

				if (BriefcaseItem.isOwnedBy(briefcase, player)) {
					String newOwner = ownerChanger.getDisplayName().getString();

					if (!briefcase.hasTag())
						briefcase.setTag(new CompoundNBT());

					briefcase.getTag().putString("owner", newOwner);
					briefcase.getTag().putString("ownerUUID", PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID");
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
					return ActionResult.resultSuccess(ownerChanger);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalOwnerChanger.briefcase.notOwned"), TextFormatting.RED);
			}
		}

		return ActionResult.resultFail(ownerChanger);
	}
}