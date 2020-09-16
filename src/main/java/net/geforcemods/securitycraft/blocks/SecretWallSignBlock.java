package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class SecretWallSignBlock extends WallSignBlock
{
	public SecretWallSignBlock(Block.Properties properties, WoodType woodType)
	{
		super(properties, woodType);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		return VoxelShapes.empty();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(!world.isRemote && player.getHeldItem(hand).getItem() == SCContent.ADMIN_TOOL.get())
			return SCContent.ADMIN_TOOL.get().onItemUse(new ItemUseContext(player, hand, hit));

		SecretSignTileEntity te = (SecretSignTileEntity)world.getTileEntity(pos);

		if (te != null && te.isPlayerAllowedToSeeText(player))
			return super.onBlockActivated(state, world, pos, player, hand, hit);

		return ActionResultType.FAIL;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new SecretSignTileEntity();
	}

	@Override
	public String getTranslationKey() {
		return Util.makeTranslationKey("block", this.getRegistryName()).replace("_wall", "");
	}
}