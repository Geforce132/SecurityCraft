package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.OwnableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class OwnableBlock extends ContainerBlock {

	private BlockRenderType renderType = BlockRenderType.MODEL;

	public OwnableBlock(Block.Properties properties) {
		this(SoundType.STONE, properties);
	}

	public OwnableBlock(SoundType soundType, Block.Properties properties) {
		super(properties.sound(soundType));
	}

	public OwnableBlock(SoundType soundType, Block.Properties properties, BlockRenderType renderType) {
		this(soundType, properties);
		this.renderType = renderType;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return renderType;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new OwnableTileEntity();
	}
}
