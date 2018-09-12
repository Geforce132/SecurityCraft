package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemUniversalBlockReinforcer extends Item
{
	public ItemUniversalBlockReinforcer(int damage)
	{
		setMaxDurability(damage);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
			player.openGui(SecurityCraft.MODID, GuiHandler.BLOCK_REINFORCER, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		if(!player.capabilities.isCreativeMode)
		{
			World world = player.getEntityWorld();
			Block block = world.getBlock(x, y, z);

			for(Block rb : IReinforcedBlock.BLOCKS)
			{
				IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

				if(reinforcedBlock.getVanillaBlocks().contains(block))
				{
					if(reinforcedBlock.getVanillaBlocks().size() == reinforcedBlock.getAmount())
					{
						for(int i = 0; i < reinforcedBlock.getAmount(); i++)
						{
							if(block.equals(reinforcedBlock.getVanillaBlocks().get(i)))
								world.setBlock(x, y, z, rb, i, 3);
						}
					}
					else
						world.setBlock(x, y, z, rb, block.getDamageValue(world, x, y, z), 2);

					((IOwnable)world.getTileEntity(x, y, z)).getOwner().set(player.getGameProfile().getId().toString(), player.getCommandSenderName());
					stack.damageItem(1, player);
					return true;
				}
			}
		}

		return false;
	}
}
