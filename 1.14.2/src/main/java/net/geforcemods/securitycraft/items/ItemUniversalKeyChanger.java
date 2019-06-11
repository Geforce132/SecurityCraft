package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.TEInteractionObject;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemUniversalKeyChanger extends Item {

	public ItemUniversalKeyChanger() {
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1));
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx)
	{
		return onItemUseFirst(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.func_221532_j().x, ctx.func_221532_j().y, ctx.func_221532_j().z, ctx.getItem());
	}

	public ActionResultType onItemUseFirst(PlayerEntity player, World world, BlockPos pos, Direction side, double hitX, double hitY, double hitZ, ItemStack stack) {
		if(world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof IPasswordProtected) {
			if(((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player))
			{
				if(player instanceof ServerPlayerEntity)
					NetworkHooks.openGui((ServerPlayerEntity)player, new TEInteractionObject(GuiHandler.KEY_CHANGER, world, pos), pos);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.universalKeyChanger.getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) world.getTileEntity(pos)).getOwner().getName()), TextFormatting.RED);

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

}
