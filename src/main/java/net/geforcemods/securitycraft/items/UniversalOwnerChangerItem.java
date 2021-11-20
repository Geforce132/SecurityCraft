package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.SpecialDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
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
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class UniversalOwnerChangerItem extends Item
{
	public UniversalOwnerChangerItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx)
	{
		return onItemUseFirst(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), stack, ctx.getFace(), ctx.getHand());
	}

	public ActionResultType onItemUseFirst(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction side, Hand hand)
	{
		//prioritize handling the briefcase
		if (hand == Hand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.BRIEFCASE.get())
			return handleBriefcase(player, stack).getType();

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		TileEntity te = world.getTileEntity(pos);
		String newOwner = stack.getDisplayName().getString();

		if(!(te instanceof IOwnable))
		{
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), TextFormatting.RED);
			return ActionResultType.FAIL;
		}

		Owner owner = ((IOwnable)te).getOwner();
		boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

		if(!owner.isOwner(player) && !isDefault)
		{
			if(!(block instanceof IBlockMine) && (!(te instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)te).getBlockState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock))) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalOwnerChanger.notOwned"), TextFormatting.RED);
				return ActionResultType.FAIL;
			}

			return ActionResultType.PASS;
		}

		if(!stack.hasDisplayName() && !isDefault)
		{
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
			return ActionResultType.FAIL;
		}

		if(isDefault)
		{
			if(ConfigHandler.SERVER.allowBlockClaim.get())
				newOwner = player.getName().getString();
			else
			{
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), TextFormatting.RED);
				return ActionResultType.FAIL;
			}
		}

		if(te instanceof IOwnable) {
			((IOwnable)te).setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID", newOwner);
			((IOwnable)te).onOwnerChanged(state, world, pos, player);
		}

		if (!world.isRemote)
			world.getServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());

		//disable this in a development environment
		if(FMLEnvironment.production && te instanceof IModuleInventory)
		{
			for(ModuleType moduleType : ((IModuleInventory)te).getInsertedModules())
			{
				ItemStack moduleStack = ((IModuleInventory)te).getModule(moduleType);

				((IModuleInventory)te).removeModule(moduleType);
				((IModuleInventory)te).onModuleRemoved(moduleStack, moduleType);
				Block.spawnAsEntity(world, pos, moduleStack);
			}
		}

		PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack ownerChanger = player.getHeldItem(hand);

		if (!ownerChanger.hasDisplayName()) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), TextFormatting.RED);
			return ActionResult.resultFail(ownerChanger);
		}

		if (hand == Hand.MAIN_HAND && player.getHeldItemOffhand().getItem() == SCContent.BRIEFCASE.get())
			return handleBriefcase(player, ownerChanger);

		return ActionResult.resultPass(ownerChanger);
	}

	public static boolean tryUpdateBlock(World world, BlockPos pos, Owner newOwner)
	{
		Block block = world.getBlockState(pos).getBlock();

		if(block instanceof ReinforcedDoorBlock || block instanceof SpecialDoorBlock)
		{
			OwnableTileEntity te = (OwnableTileEntity)world.getTileEntity(pos);

			te.setOwner(newOwner.getName(), newOwner.getUUID());

			if(!world.isRemote)
				world.getServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());

			return true;
		}

		return false;
	}

	private ActionResult<ItemStack> handleBriefcase(PlayerEntity player, ItemStack ownerChanger)
	{
		ItemStack briefcase = player.getHeldItemOffhand();

		if (BriefcaseItem.isOwnedBy(briefcase, player)) {
			String newOwner = ownerChanger.getDisplayName().getString();

			if (!briefcase.hasTag())
				briefcase.setTag(new CompoundNBT());

			briefcase.getTag().putString("owner", newOwner);
			briefcase.getTag().putString("ownerUUID", PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUniqueID().toString() : "ownerUUID");
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), TextFormatting.GREEN);
			return ActionResult.resultSuccess(ownerChanger);
		}
		else
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalOwnerChanger.briefcase.notOwned"), TextFormatting.RED);

		return ActionResult.resultConsume(ownerChanger);
	}
}