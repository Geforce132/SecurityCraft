package net.geforcemods.securitycraft.compat.cyclic;

import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class CyclicCompat
{
	//blocks sack of holding from picking up blocks of other owners
	public static void onRightClickBlock(RightClickBlock event)
	{
		//		if(PlayerUtils.isHoldingItem(event.getPlayer(), ItemRegistry.tile_transporterempty))
		//		{
		//			TileEntity te = event.getWorld().getTileEntity(event.getPos());
		//
		//			if(te instanceof IOwnable && !((IOwnable)te).getOwner().isOwner(event.getPlayer()))
		//				event.setUseItem(Result.DENY);
		//		}
	}
}
