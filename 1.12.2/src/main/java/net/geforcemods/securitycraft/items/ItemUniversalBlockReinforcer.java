package net.geforcemods.securitycraft.items;

import java.util.HashSet;

import com.google.common.collect.Sets;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemUniversalBlockReinforcer extends ItemTool
{
	public ItemUniversalBlockReinforcer(int damage)
	{
		super(2.0F, 5.0F, ToolMaterial.GOLD, getBreakableBlocks());

		setMaxDamage(damage);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote)
			player.openGui(SecurityCraft.MODID, GuiHandler.BLOCK_REINFORCER, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return super.onItemRightClick( world, player, hand);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		if(!player.capabilities.isCreativeMode)
		{
			World world = player.getEntityWorld();
			IBlockState state = world.getBlockState(pos);
			Block block = world.getBlockState(pos).getBlock();

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
								world.setBlockState(pos, rb.getStateFromMeta(i));
						}
					}
					else
						world.setBlockState(pos, rb.getStateFromMeta(block.getMetaFromState(block.getActualState(state, world, pos))));

					((IOwnable)world.getTileEntity(pos)).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
					stack.damageItem(1, player);
					return true;
				}
			}
		}

		return false;
	}

	private static HashSet<Block> getBreakableBlocks()
	{
		HashSet<Block> set = Sets.newHashSet();

		IReinforcedBlock.BLOCKS.forEach((reinforcedBlock) -> {
			set.addAll(((IReinforcedBlock)reinforcedBlock).getVanillaBlocks());
		});

		return set;
	}
}
