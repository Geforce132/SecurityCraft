package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.SpecialDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.fml.loading.FMLEnvironment;

public class UniversalOwnerChangerItem extends Item
{
	public UniversalOwnerChangerItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx)
	{
		return onItemUseFirst(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), stack, ctx.getClickedFace(), ctx.getHand());
	}

	public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, ItemStack stack, Direction side, InteractionHand hand)
	{
		//prioritize handling the briefcase
		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get())
			return handleBriefcase(player, stack).getResult();

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		BlockEntity te = world.getBlockEntity(pos);
		String newOwner = stack.getHoverName().getString();

		if(!(te instanceof IOwnable ownable))
		{
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.cantChange"), ChatFormatting.RED);
			return InteractionResult.FAIL;
		}

		Owner owner = ownable.getOwner();
		boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

		if(!owner.isOwner(player) && !isDefault)
		{
			if(!(block instanceof IBlockMine) && (!(te instanceof DisguisableBlockEntity) || (((BlockItem)((DisguisableBlock)((DisguisableBlockEntity)te).getBlockState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock))) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.notOwned"), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}

			return InteractionResult.PASS;
		}

		if(!stack.hasCustomHoverName() && !isDefault)
		{
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), ChatFormatting.RED);
			return InteractionResult.FAIL;
		}

		if(isDefault)
		{
			if(ConfigHandler.SERVER.allowBlockClaim.get())
				newOwner = player.getName().getString();
			else
			{
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.noBlockClaiming"), ChatFormatting.RED);
				return InteractionResult.FAIL;
			}
		}

		ownable.setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID", newOwner);
		ownable.onOwnerChanged(state, world, pos, player);

		if (!world.isClientSide)
			world.getServer().getPlayerList().broadcastAll(te.getUpdatePacket());

		//disable this in a development environment
		if(FMLEnvironment.production && te instanceof IModuleInventory inv)
		{
			for(ModuleType moduleType : inv.getInsertedModules())
			{
				ItemStack moduleStack = inv.getModule(moduleType);

				inv.removeModule(moduleType);
				inv.onModuleRemoved(moduleStack, moduleType);
				Block.popResource(world, pos, moduleStack);
			}
		}

		PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), ChatFormatting.GREEN);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack ownerChanger = player.getItemInHand(hand);

		if (!ownerChanger.hasCustomHoverName()) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.noName"), ChatFormatting.RED);
			return InteractionResultHolder.fail(ownerChanger);
		}

		if (hand == InteractionHand.MAIN_HAND && player.getOffhandItem().getItem() == SCContent.BRIEFCASE.get())
			return handleBriefcase(player, ownerChanger);

		return InteractionResultHolder.pass(ownerChanger);
	}

	public static boolean tryUpdateBlock(Level world, BlockPos pos, Owner newOwner)
	{
		Block block = world.getBlockState(pos).getBlock();

		if(block instanceof ReinforcedDoorBlock || block instanceof SpecialDoorBlock)
		{
			OwnableBlockEntity te = (OwnableBlockEntity)world.getBlockEntity(pos);

			te.setOwner(newOwner.getUUID(), newOwner.getName());

			if(!world.isClientSide)
				world.getServer().getPlayerList().broadcastAll(te.getUpdatePacket());

			return true;
		}

		return false;
	}

	private InteractionResultHolder<ItemStack> handleBriefcase(Player player, ItemStack ownerChanger)
	{
		ItemStack briefcase = player.getOffhandItem();

		if (BriefcaseItem.isOwnedBy(briefcase, player)) {
			String newOwner = ownerChanger.getHoverName().getString();

			if (!briefcase.hasTag())
				briefcase.setTag(new CompoundTag());

			briefcase.getTag().putString("owner", newOwner);
			briefcase.getTag().putString("ownerUUID", PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID");
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), ChatFormatting.GREEN);
			return InteractionResultHolder.success(ownerChanger);
		}
		else
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.briefcase.notOwned"), ChatFormatting.RED);

		return InteractionResultHolder.consume(ownerChanger);
	}
}