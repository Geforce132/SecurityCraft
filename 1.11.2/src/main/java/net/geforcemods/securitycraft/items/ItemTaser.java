package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemTaser extends Item {

	public ItemTaser(){
		super();
		setMaxDamage(151);
	}

	@Override
	public boolean isFull3D(){
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		ItemStack itemStackIn = playerIn.getHeldItem(hand);

		if(!worldIn.isRemote)
			if(!itemStackIn.isItemDamaged()){
				WorldUtils.addScheduledTask(worldIn, () -> worldIn.spawnEntity(new EntityTaserBullet(worldIn, playerIn)));
				mod_SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(playerIn.posX, playerIn.posY, playerIn.posZ, SCSounds.TASERFIRED.path, 1.0F, "player"));

				if(!playerIn.capabilities.isCreativeMode)
					itemStackIn.damageItem(150, playerIn);
			}

		return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5){
		if(!par2World.isRemote)
			if(par1ItemStack.getItemDamage() >= 1)
				par1ItemStack.setItemDamage(par1ItemStack.getItemDamage() - 1);
	}

}
