package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.screen.ScreenHandler;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class KeycardReaderBlock extends DisguisableBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public KeycardReaderBlock(Material material) {
		super(material);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		IBlockState north = world.getBlockState(pos.north());
		IBlockState south = world.getBlockState(pos.south());
		IBlockState west = world.getBlockState(pos.west());
		IBlockState east = world.getBlockState(pos.east());
		EnumFacing facing = state.getValue(FACING);

		if (facing == EnumFacing.NORTH && north.isFullBlock() && !south.isFullBlock())
			facing = EnumFacing.SOUTH;
		else if (facing == EnumFacing.SOUTH && south.isFullBlock() && !north.isFullBlock())
			facing = EnumFacing.NORTH;
		else if (facing == EnumFacing.WEST && west.isFullBlock() && !east.isFullBlock())
			facing = EnumFacing.EAST;
		else if (facing == EnumFacing.EAST && east.isFullBlock() && !west.isFullBlock())
			facing = EnumFacing.WEST;

		world.setBlockState(pos, state.withProperty(FACING, facing), 2);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			KeycardReaderBlockEntity te = (KeycardReaderBlockEntity) world.getTileEntity(pos);

			if (te.isDisabled())
				player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (te.isDenied(player)) {
				if (te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, new TextComponentTranslation(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
			}
			else {
				ItemStack stack = player.getHeldItem(hand);
				Item item = stack.getItem();
				boolean isCodebreaker = item == SCContent.codebreaker;
				boolean isKeycardHolder = item == SCContent.keycardHolder;

				//either no keycard, or an unlinked keycard, or an admin tool
				if (!isKeycardHolder && (!(item instanceof KeycardItem) || !stack.hasTagCompound() || !stack.getTagCompound().getBoolean("linked")) && !isCodebreaker) {
					//only allow the owner and whitelisted players to open the gui
					if (te.isOwnedBy(player) || te.isAllowed(player))
						player.openGui(SecurityCraft.instance, ScreenHandler.KEYCARD_READER_ID, world, pos.getX(), pos.getY(), pos.getZ());
				}
				else if (item != SCContent.limitedUseKeycard) { //limited use keycards are only crafting components now
					if (isCodebreaker) {
						double chance = ConfigHandler.codebreakerChance;

						if (chance < 0.0D)
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
						else {
							if (!player.isCreative())
								stack.damageItem(1, player);

							if (player.isCreative() || new Random().nextDouble() < chance)
								activate(world, pos, state, te.getSignalLength());
							else
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.codebreaker), Utils.localize("messages.securitycraft:codebreaker.failed"), TextFormatting.RED);
						}
					}
					else {
						if (isKeycardHolder) {
							ItemContainer holderInventory = ItemContainer.keycardHolder(stack);
							ITextComponent feedback = null;

							for (int i = 0; i < holderInventory.getSizeInventory(); i++) {
								ItemStack keycardStack = holderInventory.getStackInSlot(i);

								if (keycardStack.getItem() instanceof KeycardItem && keycardStack.hasTagCompound()) {
									feedback = insertCard(world, pos, state, te, keycardStack, player);

									if (feedback == null)
										return true;
								}
							}

							if (feedback == null)
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:keycard_holder.no_keycards"), TextFormatting.RED);
							else
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:keycard_holder.fail"), TextFormatting.RED);
						}
						else {
							ITextComponent feedback = insertCard(world, pos, state, te, stack, player);

							if (feedback != null)
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), feedback, TextFormatting.RED);
						}
					}
				}
			}
		}

		return true;
	}

	public ITextComponent insertCard(World world, BlockPos pos, IBlockState state, KeycardReaderBlockEntity te, ItemStack stack, EntityPlayer player) {
		NBTTagCompound tag = stack.getTagCompound();
		Owner keycardOwner = new Owner(tag.getString("ownerName"), tag.getString("ownerUUID"));

		//owner of this keycard reader and the keycard reader the keycard got linked to do not match
		if ((ConfigHandler.enableTeamOwnership && !PlayerUtils.areOnSameTeam(te.getOwner(), keycardOwner)) || !te.getOwner().getUUID().equals(keycardOwner.getUUID()))
			return new TextComponentTranslation("messages.securitycraft:keycardReader.differentOwner");

		//the keycard's signature does not match this keycard reader's
		if (te.getSignature() != tag.getInteger("signature"))
			return new TextComponentTranslation("messages.securitycraft:keycardReader.wrongSignature");

		int level = ((KeycardItem) stack.getItem()).getLevel();

		//the keycard's level
		if (!te.getAcceptedLevels()[level]) //both are 0 indexed, so it's ok
			return new TextComponentTranslation("messages.securitycraft:keycardReader.wrongLevel", level + 1); //level is 0-indexed, so it has to be increased by one to match with the item name

		boolean powered = world.getBlockState(pos).getValue(POWERED);

		if (tag.getBoolean("limited")) {
			int uses = tag.getInteger("uses");

			if (uses <= 0)
				return new TextComponentTranslation("messages.securitycraft:keycardReader.noUses");

			if (!player.isCreative() && !powered)
				tag.setInteger("uses", --uses);
		}

		if (!powered)
			activate(world, pos, state, te.getSignalLength());

		return null;
	}

	public void activate(World world, BlockPos pos, IBlockState state, int signalLength) {
		world.setBlockState(pos, state.withProperty(POWERED, true));
		BlockUtils.updateIndirectNeighbors(world, pos, SCContent.keycardReader);
		world.scheduleUpdate(pos, SCContent.keycardReader, signalLength);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if (!world.isRemote) {
			world.setBlockState(pos, state.withProperty(POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, SCContent.keycardReader);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(POWERED)) {
			world.notifyNeighborsOfStateChange(pos, this, false);
			BlockUtils.updateIndirectNeighbors(world, pos, this);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if ((state.getValue(POWERED))) {
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;

			world.spawnParticle(EnumParticleTypes.REDSTONE, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.REDSTONE, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.REDSTONE, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.REDSTONE, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if ((blockState.getValue(POWERED)))
			return 15;
		else
			return 0;
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getProperties().containsKey(POWERED) && state.getValue(POWERED))
			return (state.getValue(FACING).getIndex() + 6);
		else if (state.getProperties().containsKey(FACING))
			return state.getValue(FACING).getIndex();
		else
			return 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new KeycardReaderBlockEntity();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirror) {
		return state.withRotation(mirror.toRotation(state.getValue(FACING)));
	}
}
