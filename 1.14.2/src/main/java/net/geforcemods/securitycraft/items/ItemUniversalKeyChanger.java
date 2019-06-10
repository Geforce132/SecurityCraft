package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.TEInteractionObject;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemUniversalKeyChanger extends Item {

	public ItemUniversalKeyChanger() {
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1));
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, ItemUseContext ctx)
	{
		return onItemUseFirst(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getItem());
	}

	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, ItemStack stack) {
		if(world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof IPasswordProtected) {
			if(((IOwnable) world.getTileEntity(pos)).getOwner().isOwner(player))
			{
				if(player instanceof EntityPlayerMP)
					NetworkHooks.openGui((EntityPlayerMP)player, new TEInteractionObject(GuiHandler.KEY_CHANGER, world, pos), pos);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.universalKeyChanger.getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) world.getTileEntity(pos)).getOwner().getName()), TextFormatting.RED);

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}

}
