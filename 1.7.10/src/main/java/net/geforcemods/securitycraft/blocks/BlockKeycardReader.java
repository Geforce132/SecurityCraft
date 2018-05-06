package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.items.ItemKeycardBase;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ItemUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockKeycardReader extends BlockOwnable {

	@SideOnly(Side.CLIENT)
	private IIcon keypadIconTop;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconFront;

	public BlockKeycardReader(Material material) {
		super(material);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		super.onBlockPlacedBy(world, x, y, z, entity, stack);

		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);

		if (entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);

		if (entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if (entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
	}

	public void insertCard(World world, int x, int y, int z, ItemStack stack, EntityPlayer player) {
		if(ModuleUtils.checkForModule(world, x, y, z, player, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(world, x, y, z, player, EnumCustomModules.BLACKLIST))
			return;

		if((((IPasswordProtected) world.getTileEntity(x, y, z)).getPassword() != null) && (!((TileEntityKeycardReader) world.getTileEntity(x, y, z)).doesRequireExactKeycard() && Integer.parseInt(((IPasswordProtected) world.getTileEntity(x, y, z)).getPassword()) <= ((ItemKeycardBase) stack.getItem()).getKeycardLV(stack) || ((TileEntityKeycardReader) world.getTileEntity(x, y, z)).doesRequireExactKeycard() && Integer.parseInt(((IPasswordProtected) world.getTileEntity(x, y, z)).getPassword()) == ((ItemKeycardBase) stack.getItem()).getKeycardLV(stack))){
			if(stack.getMetadata() == 3 && stack.stackTagCompound != null && !player.capabilities.isCreativeMode){
				stack.stackTagCompound.setInteger("Uses", stack.stackTagCompound.getInteger("Uses") - 1);

				if(stack.stackTagCompound.getInteger("Uses") <= 0)
					stack.stackSize--;
			}

			activate(world, x, y, z);
		}
		else if(((IPasswordProtected) world.getTileEntity(x, y, z)).getPassword() != null)
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.keycardReader.required").replace("#r", ((IPasswordProtected) world.getTileEntity(x, y, z)).getPassword()).replace("#c", "" + ((ItemKeycardBase) stack.getItem()).getKeycardLV(stack)), EnumChatFormatting.RED);
		else
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.keycardReader.name"), StatCollector.translateToLocal("messages.keycardReader.notSet"), EnumChatFormatting.RED);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;

		if(player.getCurrentEquippedItem() == null || (!(player.getCurrentEquippedItem().getItem() instanceof ItemKeycardBase) && player.getCurrentEquippedItem().getItem() != SCContent.adminTool))
			((TileEntityKeycardReader) world.getTileEntity(x, y, z)).openPasswordGUI(player);
		else if(player.getCurrentEquippedItem().getItem() == SCContent.adminTool)
			((BlockKeycardReader) world.getBlock(x, y, z)).insertCard(world, x, y, z, ItemUtils.toItemStack(SCContent.keycards, 3), player);
		else if(BlockUtils.isMetadataBetween(world, x, y, z, 2, 5))
			((BlockKeycardReader) world.getBlock(x, y, z)).insertCard(world, x, y, z, player.getCurrentEquippedItem(), player);

		return true;
	}

	public static void activate(World world, int x, int y, int z){
		world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 5, 3);
		world.notifyBlocksOfNeighborChange(x, y, z, SCContent.keycardReader);
		world.scheduleBlockUpdate(x, y, z, SCContent.keycardReader, 60);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random){
		if(!world.isRemote && world.getBlockMetadata(x, y, z) > 6 && world.getBlockMetadata(x, y, z) < 11)
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 5, 3);
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side){
		if(par1IBlockAccess.getBlockMetadata(x, y, z) > 6 && par1IBlockAccess.getBlockMetadata(x, y, z) < 11)
			return 15;
		else
			return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random){
		int meta = world.getBlockMetadata(x, y, z);

		if(meta > 6 && meta < 11){
			double d0 = x + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double d1 = y + 0.7F + (random.nextFloat() - 0.5F) * 0.2D;
			double d2 = z + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double d3 = 0.2199999988079071D;
			double d4 = 0.27000001072883606D;

			world.spawnParticle("reddust", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if(side == 3 && meta == 0)
			return keypadIconFront;

		if(meta == 7 || meta == 8 || meta == 9 || meta == 10)
			return side == 1 ? keypadIconTop : (side == 0 ? keypadIconTop : (side != (meta - 5) ? blockIcon : keypadIconFront));
		else
			return side == 1 ? keypadIconTop : (side == 0 ? keypadIconTop : (side != meta ? blockIcon : keypadIconFront));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		blockIcon = register.registerIcon("furnace_side");
		keypadIconTop = register.registerIcon("furnace_top");
		keypadIconFront = register.registerIcon("securitycraft:keycardReaderFront");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityKeycardReader();
	}

}
