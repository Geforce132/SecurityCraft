package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedDispenserBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedDropperBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedDispenserBlock extends BlockDispenser implements IReinforcedBlock {
	@Override
	public float getExplosionResistance(Entity exploder) {
		return Float.MAX_VALUE;
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return Float.MAX_VALUE;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer) placer));

		if (!world.isRemote && stack.hasDisplayName()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof ReinforcedDispenserBlockEntity)
				((ReinforcedDispenserBlockEntity) te).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof ReinforcedDispenserBlockEntity) {
				ReinforcedDispenserBlockEntity te = (ReinforcedDispenserBlockEntity) tileEntity;

				//only allow the owner or players on the allowlist to access a reinforced dispenser
				if (te.isOwnedBy(player) || te.isAllowed(player)) {
					player.displayGUIChest(te);

					if (te instanceof ReinforcedDropperBlockEntity)
						player.addStat(StatList.DROPPER_INSPECTED);
					else
						player.addStat(StatList.DISPENSER_INSPECTED);
				}
			}
		}

		return true;
	}

	@Override
	protected void dispense(World level, BlockPos pos) {
		if (level.getTileEntity(pos) instanceof ReinforcedDispenserBlockEntity)
			super.dispense(level, pos);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ReinforcedDispenserBlockEntity();
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.DISPENSER);
	}
}
