package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockOwnable extends BlockContainer {

	private EnumBlockRenderType renderType = EnumBlockRenderType.MODEL;

	public BlockOwnable(Material material) {
		super(material);

		if(material == Material.GROUND)
			setSoundType(SoundType.GROUND);
		else if(material == Material.GLASS)
			setSoundType(SoundType.GLASS);
		else
			setSoundType(SoundType.STONE);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		if(placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (EntityPlayer)placer));

		if (!world.isRemote && stack.hasDisplayName()) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof INameSetter) {
				((INameSetter)te).setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return renderType;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable();
	}
}
