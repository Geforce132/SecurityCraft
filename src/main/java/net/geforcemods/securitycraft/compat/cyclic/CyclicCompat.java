package net.geforcemods.securitycraft.compat.cyclic;

import com.lothrazar.cyclicmagic.item.tiletransporter.ItemChestSackEmpty;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CyclicCompat
{
	//blocks sack of holding from picking up blocks of other owners
	@SubscribeEvent
	public void onRightClickBlock(RightClickBlock event)
	{
		if(event.getEntityPlayer().inventory.getCurrentItem().getItem() instanceof ItemChestSackEmpty)
		{
			TileEntity te = event.getWorld().getTileEntity(event.getPos());

			if(te instanceof IOwnable && !((IOwnable)te).getOwner().isOwner(event.getEntityPlayer()))
				event.setUseItem(Result.DENY);
		}
	}
}
