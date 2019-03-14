package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockBouncingBetty extends BlockExplosive implements IIntersectable {

	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

	public BlockBouncingBetty(Material material) {
		super(SoundType.STONE, material);
		setDefaultState(stateContainer.getBaseState().with(DEACTIVATED, false));
	}

	@Override
	public boolean isNormalCube(IBlockState state){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		return VoxelShapes.create(new AxisAlignedBB(0.200F, 0.000F, 0.200F, 0.800F, 0.200F, 0.800F));
	}

	/**
	 * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	 */
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos){
		return world.isTopSolid(pos.down());
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(entity instanceof EntityLivingBase)
			if(!PlayerUtils.isPlayerMountedOnCamera((EntityLivingBase)entity))
				explode(world, pos);
	}
	@Override
	public void onBlockClicked(IBlockState state, World world, BlockPos pos, EntityPlayer player){
		if(!player.isCreative())
			explode(world, pos);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
	public void explode(World world, BlockPos pos){
		if(world.isRemote)
			return;
		if(BlockUtils.getBlockPropertyAsBoolean(world, pos, DEACTIVATED))
			return;

		world.removeBlock(pos);
		EntityBouncingBetty entitytntprimed = new EntityBouncingBetty(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
		entitytntprimed.fuse = 15;
		entitytntprimed.motionY = 0.50D;
		WorldUtils.addScheduledTask(world, () -> world.spawnEntity(entitytntprimed));
		entitytntprimed.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("game.tnt.primed")), 1.0F, 1.0F);
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune)
	{
		return asItem();
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state){
		return new ItemStack(asItem());
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).get(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityOwnable().intersectsEntities();
	}

}
