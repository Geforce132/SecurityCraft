package org.freeforums.geforce.securitycraft.main;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityPortableRadar;

public class Utils {
	
	public static String getFormattedCoordinates(BlockPos pos){
		return "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ();
	}
	
	/**
	 * Prints "Method running" whenever this method is called.
	 * Good for checking if methods are being called.
	 */
	public static void checkIfRunning(){
		System.out.println("Method running.");
	}
	
	/**
	 * Prints "Method running" whenever this method is called.
	 * Good for checking if methods are being called.
	 */	
	public static void checkIfRunning(Object... objects){
		String string = "Method running. Args: ";
		
		for(int i = 0; i < objects.length; i++){
			if(i == (objects.length - 1)){
				string += objects[i];
			}else{
				string += objects[i] + " | ";
			}		
		}
		
		System.out.println(string);
	}
	
	public static Material getBlockMaterial(World par1World, BlockPos pos){
		return par1World.getBlockState(pos).getBlock().getMaterial();
	}
	
	public static void closePlayerScreen(){
		Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
		Minecraft.getMinecraft().setIngameFocus();
	}
	
	public static EnumFacing getSideFacingFromIndex(int index){
		return EnumFacing.values()[index];
	}
	
	public static void destroyBlock(World par1World, BlockPos pos, boolean par5){
		par1World.destroyBlock(pos, par5);
	}
	
	public static void destroyBlock(World par1World, int par2, int par3, int par4, boolean par5){
		par1World.destroyBlock(new BlockPos(par2, par3, par4), par5);
	}
	
	public static void setBlock(World par1World, BlockPos pos, Block block){
		par1World.setBlockState(pos, block.getDefaultState());
	}
	
	public static void setBlock(World par1World, int par2, int par3, int par4, Block block){
		setBlock(par1World, new BlockPos(par2, par3, par4), block);
	}
	
	public static Block getBlock(World par1World, BlockPos pos){
		return par1World.getBlockState(pos).getBlock();
	}
	
	public static Block getBlock(World par1World, int par2, int par3, int par4){
		return par1World.getBlockState(new BlockPos(par2, par3, par4)).getBlock();
	}
	
	public static void setBlockProperty(World par1World, BlockPos pos, PropertyBool property, boolean value) {
		setBlockProperty(par1World, pos, property, value, false);
	}

	public static void setBlockProperty(World par1World, BlockPos pos, PropertyBool property, boolean value, boolean retainOldTileEntity) {
		if(retainOldTileEntity){
			ItemStack[] modules = null;
			ItemStack[] inventory = null;
			int[] times = new int[4];
			String password = "";
			String ownerUUID = "";
			String ownerName = "";
			int cooldown = -1;

			if(par1World.getTileEntity(pos) instanceof CustomizableSCTE){
				modules = ((CustomizableSCTE) par1World.getTileEntity(pos)).itemStacks;
			}
			
			if(par1World.getTileEntity(pos) instanceof TileEntityKeypadFurnace){
				inventory = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).furnaceItemStacks;
				times[0] = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).furnaceBurnTime;
				times[1] = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).currentItemBurnTime;
				times[2] = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).cookTime;
				times[3] = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).totalCookTime;
			}
			
			if(par1World.getTileEntity(pos) instanceof TileEntityOwnable && ((TileEntityOwnable) par1World.getTileEntity(pos)).getOwnerUUID() != null){
				ownerUUID = ((TileEntityOwnable) par1World.getTileEntity(pos)).getOwnerUUID();
				ownerName = ((TileEntityOwnable) par1World.getTileEntity(pos)).getOwnerName();
			}
			
			if(par1World.getTileEntity(pos) instanceof TileEntityKeypad && ((TileEntityKeypad) par1World.getTileEntity(pos)).getKeypadCode() != null){
				password = ((TileEntityKeypad) par1World.getTileEntity(pos)).getKeypadCode();
			}
			
			if(par1World.getTileEntity(pos) instanceof TileEntityKeypadFurnace && ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).getKeypadCode() != null){
				password = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).getKeypadCode();
			}
			
			if(par1World.getTileEntity(pos) instanceof TileEntityKeypadChest && ((TileEntityKeypadChest) par1World.getTileEntity(pos)).getKeypadCode() != null){
				password = ((TileEntityKeypadChest) par1World.getTileEntity(pos)).getKeypadCode();
			}
			
			if(par1World.getTileEntity(pos) instanceof TileEntityPortableRadar && ((TileEntityPortableRadar) par1World.getTileEntity(pos)).cooldown != 0){
				cooldown = ((TileEntityPortableRadar) par1World.getTileEntity(pos)).cooldown;
			}
			
			TileEntity tileEntity = par1World.getTileEntity(pos);
			par1World.setBlockState(pos, par1World.getBlockState(pos).withProperty(property, value));
			par1World.setTileEntity(pos, tileEntity);
			
			if(modules != null){
				((CustomizableSCTE) par1World.getTileEntity(pos)).itemStacks = modules;
			}
			
			if(inventory != null && par1World.getTileEntity(pos) instanceof TileEntityKeypadFurnace){
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).furnaceItemStacks = inventory;
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).furnaceBurnTime = times[0];
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).currentItemBurnTime = times[1];
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).cookTime = times[2];
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).totalCookTime = times[3];
			}
			
			if(!ownerUUID.isEmpty() && !ownerName.isEmpty()){
				((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(ownerUUID, ownerName);
			}
			
			if(!password.isEmpty() && par1World.getTileEntity(pos) instanceof TileEntityKeypad){
				((TileEntityKeypad) par1World.getTileEntity(pos)).setKeypadCode(password);
			}
			
			if(!password.isEmpty() && par1World.getTileEntity(pos) instanceof TileEntityKeypadFurnace){
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).setKeypadCode(password);
			}
			
			if(!password.isEmpty() && par1World.getTileEntity(pos) instanceof TileEntityKeypadChest){
				((TileEntityKeypadChest) par1World.getTileEntity(pos)).setKeypadCode(password);
			}
			
			if(cooldown != -1 && par1World.getTileEntity(pos) instanceof TileEntityPortableRadar){
				((TileEntityPortableRadar) par1World.getTileEntity(pos)).cooldown = cooldown;
			}
		}else{
			par1World.setBlockState(pos, par1World.getBlockState(pos).withProperty(property, value));
		}
	}
	
	public static void setBlockProperty(World par1World, int par2, int par3, int par4, PropertyBool property, boolean value) {
		ItemStack[] modules = null;
		if(par1World.getTileEntity(new BlockPos(par2, par3, par4)) instanceof CustomizableSCTE){
			modules = ((CustomizableSCTE) par1World.getTileEntity(new BlockPos(par2, par3, par4))).itemStacks;
		}
		
		TileEntity tileEntity = par1World.getTileEntity(new BlockPos(par2, par3, par4));
		par1World.setBlockState(new BlockPos(par2, par3, par4), par1World.getBlockState(new BlockPos(par2, par3, par4)).withProperty(property, value));
		par1World.setTileEntity(new BlockPos(par2, par3, par4), tileEntity);
		
		if(modules != null){
			((CustomizableSCTE) par1World.getTileEntity(new BlockPos(par2, par3, par4))).itemStacks = modules;
		}
	}
	
	public static boolean hasBlockProperty(World par1World, BlockPos pos, IProperty property){
		try{
			Comparable comparable = par1World.getBlockState(pos).getValue(property);
			return true;
		}catch(IllegalArgumentException e){
			return false;
		}
	}
	
	public static boolean hasBlockProperty(World par1World, int par2, int par3, int par4, IProperty property){
		return hasBlockProperty(par1World, new BlockPos(par2, par3, par4), property);
	}
	
	public static Comparable getBlockProperty(World par1World, BlockPos pos, PropertyBool property) {
		return par1World.getBlockState(pos).getValue(property);
	}
	
	public static Comparable getBlockProperty(World par1World, int par2, int par3, int par4, PropertyBool property) {
		return par1World.getBlockState(new BlockPos(par2, par3, par4)).getValue(property);
	}
	
	public static boolean getBlockPropertyAsBoolean(World par1World, BlockPos pos, PropertyBool property){
		return ((Boolean) par1World.getBlockState(pos).getValue(property)).booleanValue();
	}
	
	public static boolean getBlockPropertyAsBoolean(World par1World, int par2, int par3, int par4, PropertyBool property){
		return ((Boolean) par1World.getBlockState(new BlockPos(par2, par3, par4)).getValue(property)).booleanValue();
	}
	
	public static EnumFacing getBlockProperty(World par1World, BlockPos pos, PropertyDirection property) {
		return (EnumFacing) par1World.getBlockState(pos).getValue(property);
	}
	
	public static EnumFacing getBlockProperty(World par1World, int par2, int par3, int par4, PropertyDirection property) {
		return (EnumFacing) par1World.getBlockState(new BlockPos(par2, par3, par4)).getValue(property);
	}

}
