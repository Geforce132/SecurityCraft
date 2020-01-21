package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.KeycardReaderBlock;
import net.geforcemods.securitycraft.blocks.KeypadBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.OwnableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedFenceGateBlock extends FenceGateBlock implements IIntersectable {

	public ReinforcedFenceGateBlock(){
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(-1.0F, 6000000.0F).sound(SoundType.METAL));
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) //onBlockActivated
	{
		return ActionResultType.FAIL;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);
		world.removeTileEntity(pos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(BlockUtils.getBlockPropertyAsBoolean(world, pos, OPEN))
			return;

		if(entity instanceof ItemEntity)
			return;
		else if(entity instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)entity;

			if(((OwnableTileEntity)world.getTileEntity(pos)).getOwner().isOwner(player))
				return;
		}
		else if(entity instanceof CreeperEntity)
		{
			CreeperEntity creeper = (CreeperEntity)entity;
			LightningBoltEntity lightning = new LightningBoltEntity(world, pos.getX(), pos.getY(), pos.getZ(), true);

			creeper.onStruckByLightning(lightning);
			return;
		}

		entity.attackEntityFrom(CustomDamageSources.ELECTRICITY, 6.0F);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if(!world.isRemote) {
			boolean isPoweredSCBlock = isSCBlock(block) && world.isBlockPowered(pos);

			if (isPoweredSCBlock || block.getDefaultState().canProvidePower())
				if (isPoweredSCBlock && !state.get(OPEN).booleanValue() && !state.get(POWERED).booleanValue()) {
					world.setBlockState(pos, state.with(OPEN, Boolean.valueOf(true)).with(POWERED, Boolean.valueOf(true)), 2);
					world.playEvent((PlayerEntity)null, 1008, pos, 0);
				}
				else if (!isPoweredSCBlock && state.get(OPEN).booleanValue() && state.get(POWERED).booleanValue()) {
					world.setBlockState(pos, state.with(OPEN, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)), 2);
					world.playEvent((PlayerEntity)null, 1014, pos, 0);
				}
				else if (isPoweredSCBlock != state.get(POWERED).booleanValue())
					world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(isPoweredSCBlock)), 2);
		}
	}

	private boolean isSCBlock(Block block) {
		return (block instanceof LaserBlock || block instanceof RetinalScannerBlock ||
				block instanceof KeypadBlock || block instanceof KeycardReaderBlock || block instanceof InventoryScannerBlock);
	}

	@Override
	public boolean eventReceived(BlockState state, World world, BlockPos pos, int par5, int par6){
		super.eventReceived(state, world, pos, par5, par6);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableTileEntity().intersectsEntities();
	}

}
