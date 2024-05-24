package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
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

public class ReinforcedHopperBlock extends BlockHopper implements IReinforcedBlock {
	public ReinforcedHopperBlock() {
		setSoundType(SoundType.METAL);
	}

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

			if (te instanceof ReinforcedHopperBlockEntity)
				((ReinforcedHopperBlockEntity) te).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity instanceof ReinforcedHopperBlockEntity) {
				ReinforcedHopperBlockEntity te = (ReinforcedHopperBlockEntity) tileEntity;

				//only allow the owner or players on the allowlist to access a reinforced hopper
				if (te.isOwnedBy(player) || te.isAllowed(player)) {
					player.displayGUIChest(te);
					player.addStat(StatList.HOPPER_INSPECTED);
				}
			}
		}

		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ReinforcedHopperBlockEntity();
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.HOPPER);
	}

	public static class ExtractionBlock implements IExtractionBlock, Function<Object, IExtractionBlock> {
		@Override
		public IExtractionBlock apply(Object o) {
			return this;
		}

		@Override
		public boolean canExtract(IOwnable ownable, World world, BlockPos pos, IBlockState state) {
			ReinforcedHopperBlockEntity hopperTe = (ReinforcedHopperBlockEntity) world.getTileEntity(pos);

			if (!hopperTe.getOwner().isValidated())
				return false;
			else if (!ownable.getOwner().owns(hopperTe)) {
				if (ownable instanceof IModuleInventory)
					return ((IModuleInventory) ownable).isAllowed(hopperTe.getOwner().getName()); //hoppers can extract out of e.g. chests if the hopper's owner is on the chest's allowlist module

				return false;
			}
			else
				return true;
		}

		@Override
		public Block getBlock() {
			return SCContent.reinforcedHopper;
		}
	}
}
