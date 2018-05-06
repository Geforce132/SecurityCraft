package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTaser extends Item {

	public ItemTaser(){
		super();
		setMaxDurability(151);
	}

	@Override
	public boolean isFull3D(){
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		if(!world.isRemote)
			if(!stack.isItemDamaged()){
				world.spawnEntityInWorld(new EntityTaserBullet(world, player));
				SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(player.posX, player.posY, player.posZ, SCSounds.TASERFIRED.path, 1.0F));

				if(!player.capabilities.isCreativeMode)
					stack.damageItem(150, player);
			}

		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean update){
		if(!world.isRemote)
			if(stack.getMetadata() >= 1)
				stack.setMetadata(stack.getMetadata() - 1);
	}

}
