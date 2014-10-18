package org.freeforums.geforce.securitycraft.main;

import java.util.Iterator;
import java.util.Scanner;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityPortableRadar;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class HelpfulMethods {
	
	
	public static void checkBlocksMetadata(World par1World, int par2, int par3, int par4){
		if(par1World.getBlockMetadata(par2, par3, par4) == 0){
			System.out.println("down (MD: 0)");
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 1){
			System.out.println("up (MD: 1)");
	    }else if(par1World.getBlockMetadata(par2, par3, par4) == 2){
        	System.out.println("north (MD: 2)");
        }else if(par1World.getBlockMetadata(par2, par3, par4) == 3){
        	System.out.println("south (MD: 3)");
        }else if(par1World.getBlockMetadata(par2, par3, par4) == 4){
        	System.out.println("west (MD: 4)");
        }else if(par1World.getBlockMetadata(par2, par3, par4) == 5){
        	System.out.println("east (MD: 5)");
	}
	
	
	}
	
	public static ForgeDirection getDirection(World par1World, int par2, int par3, int par4){
		if(par1World.getBlockMetadata(par2, par3, par4) == 2){
			return ForgeDirection.NORTH;
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 3){
			return ForgeDirection.SOUTH;
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 4){
			return ForgeDirection.WEST;
		}else if(par1World.getBlockMetadata(par2, par3, par4) == 4){
			return ForgeDirection.EAST;
		}else{
			return ForgeDirection.UNKNOWN;
		}
	}

	public static boolean isTruelySingleplayer(){
		Side side = FMLCommonHandler.instance().getSide();
		if(side == Side.CLIENT){
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.isSingleplayer() && !mc.getIntegratedServer().getPublic()){
	    		System.out.println("We are in offline singleplayer mode!");
	    		return true;
			}else{
				System.out.println("We are in online multiplayer mode! (LAN) ");
				return false;
			}
		}else if(side == Side.SERVER){
			System.out.println("We are in online multiplayer mode! (SERVER)");
    		return false;
	    }else{
			System.out.println("Unknown mode!");
	    	return false;
	    }
	}
	
	public static String getFormattedCoordinates(int par1, int par2, int par3){
		return " X:" + par1 + " Y:" + par2 + " Z:" + par3;
	}
	
	public static void setBlockInBox(World par1World, int par2, int par3, int par4, Block par5){
		par1World.setBlock(par2 + 1, par3 + 1, par4, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 1, par4, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 2, par4, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 2, par4, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 3, par4, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 3, par4, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 1, par4 + 1, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 1, par4 + 1, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 2, par4 + 1, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 2, par4 + 1, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 3, par4 + 1, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 3, par4 + 1, par5, 1200);

		par1World.setBlock(par2 - 1, par3 + 1, par4, par5);
    	par1World.scheduleBlockUpdate(par2 - 1, par3 + 1, par4, par5, 1200);

		par1World.setBlock(par2 - 1, par3 + 2, par4, par5);
    	par1World.scheduleBlockUpdate(par2 - 1, par3 + 2, par4, par5, 1200);

		par1World.setBlock(par2 - 1, par3 + 3, par4, par5);
    	par1World.scheduleBlockUpdate(par2 - 1, par3 + 3, par4, par5, 1200);

		par1World.setBlock(par2 - 1, par3 + 1, par4 + 1, par5);
    	par1World.scheduleBlockUpdate(par2 - 1, par3 + 1, par4 + 1, par5, 1200);

		par1World.setBlock(par2 - 1, par3 + 2, par4 + 1, par5);
    	par1World.scheduleBlockUpdate(par2 - 1, par3 + 2, par4 + 1, par5, 1200);

		par1World.setBlock(par2 - 1, par3 + 3, par4 + 1, par5);
    	par1World.scheduleBlockUpdate(par2 - 1, par3 + 3, par4 + 1, par5, 1200);

		par1World.setBlock(par2, par3 + 1, par4 + 1, par5);
    	par1World.scheduleBlockUpdate(par2, par3 + 1, par4 + 1, par5, 1200);

		par1World.setBlock(par2, par3 + 2, par4 + 1, par5);
    	par1World.scheduleBlockUpdate(par2, par3 + 2, par4 + 1, par5, 1200);

		par1World.setBlock(par2, par3 + 3, par4 + 1, par5);
    	par1World.scheduleBlockUpdate(par2, par3 + 3, par4 + 1, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 1, par4, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 1, par4, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 2, par4, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 2, par4, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 3, par4, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 3, par4, par5, 1200);

		
		par1World.setBlock(par2, par3 + 1, par4 - 1, par5);
    	par1World.scheduleBlockUpdate(par2, par3 + 1, par4 - 1, par5, 1200);

		par1World.setBlock(par2, par3 + 2, par4 - 1, par5);
    	par1World.scheduleBlockUpdate(par2, par3 + 2, par4 - 1, par5, 1200);

		par1World.setBlock(par2, par3 + 3, par4 - 1, par5);
    	par1World.scheduleBlockUpdate(par2, par3 + 3, par4 - 1, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 1, par4 - 1, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 1, par4 - 1, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 2, par4 - 1, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 2, par4 - 1, par5, 1200);

		par1World.setBlock(par2 + 1, par3 + 3, par4 - 1, par5);
    	par1World.scheduleBlockUpdate(par2 + 1, par3 + 3, par4 - 1, par5, 1200);

		
		par1World.setBlock(par2 - 1, par3 + 1, par4 - 1, par5);
    	par1World.scheduleBlockUpdate(par2 - 1, par3 + 1, par4 - 1, par5, 1200);

		par1World.setBlock(par2 - 1, par3 + 2, par4 - 1, par5);
    	par1World.scheduleBlockUpdate(par2 - 1, par3 + 2, par4 - 1, par5, 1200);

		par1World.setBlock(par2 - 1, par3 + 3, par4 - 1, par5);
    	par1World.scheduleBlockUpdate(par2 - 1, par3 + 3, par4 - 1, par5, 1200);

	}
	
	public static void disableEMPField(World par1World, double par2, double par3, double par4, Block block, int radius, int height, boolean isClient){	
		disableEMPField(par1World, (int) par2, (int) par3, (int) par4, block, radius, height, isClient);
	}
	
	public static void disableEMPField(World par1World, int par2, int par3, int par4, Block par5, int radius, int height, boolean isClient){
		int heightDivided = (height / 2);
		
		for(int i = 0; i < heightDivided; i++){
			checkBlocksAndDisable(par1World, par2, par3 - i, par4, par5, radius, isClient);

		}
		
		for(int i = heightDivided; i > 0; i--){
			checkBlocksAndDisable(par1World, par2, par3 + i, par4, par5, radius, isClient);

		}
		
		

	}
	
	private static void checkBlocksAndDisable(World par1World, int par2, int par3, int par4, Block par5, int radius, boolean isClient) {	
		if(!isClient){
			for(int i = 0; i < radius; i++){
				for(int j = 0; j < radius; j++){
					
					if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.empedWire){
						par1World.setBlock(par2 + i, par3, par4 + j, Blocks.redstone_wire, par1World.getBlockMetadata(par2 + i, par3, par4 + j), 3);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.MineCut){
						par1World.setBlock(par2 + i, par3, par4 + j, mod_SecurityCraft.Mine);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.portableRadar){
						TileEntityPortableRadar TEPR = (TileEntityPortableRadar) par1World.getTileEntity(par2 + i, par3, par4 + j);
						TEPR.setEmped(false);
						par1World.setTileEntity(par2 + i, par3, par4 + j, TEPR);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.Keypad){
//						int metadata = par1World.getBlockMetadata(par2 + i, par3, par4 + j);
//						TileEntityKeypad TEK = (TileEntityKeypad) par1World.getBlockTileEntity(par2 + i, par3, par4 + j);
//						par1World.setBlock(par2 + i, par3, par4 + j, mod_SecurityCraft.Keypad.blockID, metadata, 3);
//						par1World.setBlockTileEntity(par2 + i, par3, par4 + j, TEK);
						par1World.setBlockMetadataWithNotify(par2 + i, par3, par4 + j, par1World.getBlockMetadata(par2 + i, par3, par4 + j) - 5, 3);
						par1World.notifyBlocksOfNeighborChange(par2 + i, par3, par4 + j, par1World.getBlock(par2 + i, par3, par4 + j));
					}
					
					updateAndNotify(par1World, par2 + i, par3, par4 + j, par1World.getBlock(par2 + i, par3, par4 + j), 1, false);

				}
			}
		}else{
			for(int i = 0; i < radius; i++){
				for(int j = 0; j < radius; j++){
					
					if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.empedWire){
			            par1World.spawnParticle("largeexplode", par2 + i, par3 + 0.5D, par4 + j, 1.0D, 0.0D, 0.0D);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.MineCut){
			            par1World.spawnParticle("largeexplode", par2 + i, par3 + 0.5D, par4 + j, 1.0D, 0.0D, 0.0D);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.portableRadar){
			            par1World.spawnParticle("largeexplode", par2 + i, par3 + 0.5D, par4 + j, 1.0D, 0.0D, 0.0D);
					}
				}
			}
		}
	}
	
	public static void createEMPField(World par1World, double par2, double par3, double par4, Block block, int radius, int height, boolean isClient){	
		createEMPField(par1World, (int) par2, (int) par3, (int) par4, block, radius, height, isClient);
	}
	
	public static void createEMPField(World par1World, int par2, int par3, int par4, Block par5, int radius, int height, boolean isClient){
		int heightDivided = (height / 2);
		
		for(int i = 0; i < heightDivided; i++){
			checkBlocksAndReplace(par1World, par2, par3 - i, par4, par5, radius, isClient);

		}
		
		for(int i = heightDivided; i > 0; i--){
			checkBlocksAndReplace(par1World, par2, par3 + i, par4, par5, radius, isClient);

		}
		
		

	}
	
	private static void checkBlocksAndReplace(World par1World, int par2, int par3, int par4, Block par5, int radius, boolean isClient) {	
		if(!isClient){
			for(int i = 0; i < radius; i++){
				for(int j = 0; j < radius; j++){
					
					if(par1World.getBlock(par2 + i, par3, par4 + j) == Blocks.redstone_wire){
						par1World.setBlock(par2 + i, par3, par4 + j, mod_SecurityCraft.empedWire, par1World.getBlockMetadata(par2 + i, par3, par4 + j), 3);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.Mine){
						par1World.setBlock(par2 + i, par3, par4 + j, mod_SecurityCraft.MineCut);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.portableRadar){
						TileEntityPortableRadar TEPR = (TileEntityPortableRadar) par1World.getTileEntity(par2 + i, par3, par4 + j);
						TEPR.setEmped(true);
						par1World.setTileEntity(par2 + i, par3, par4 + j, TEPR);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.Keypad){
//						int metadata = par1World.getBlockMetadata(par2 + i, par3, par4 + j);
//						TileEntityKeypad TEK = (TileEntityKeypad) par1World.getBlockTileEntity(par2 + i, par3, par4 + j);
//						par1World.setBlock(par2 + i, par3, par4 + j, mod_SecurityCraft.KeypadActive.blockID, metadata, 3);
//						par1World.setBlockTileEntity(par2 + i, par3, par4 + j, TEK);
						par1World.setBlockMetadataWithNotify(par2 + i, par3, par4 + j, par1World.getBlockMetadata(par2 + i, par3, par4 + j) + 5, 3);
					}
					
					updateAndNotify(par1World, par2 + i, par3, par4 + j, par1World.getBlock(par2 + i, par3, par4 + j), 1, false);

				}
			}
		}else{
			for(int i = 0; i < radius; i++){
				for(int j = 0; j < radius; j++){
					
					if(par1World.getBlock(par2 + i, par3, par4 + j) == Blocks.redstone_wire){
			            par1World.spawnParticle("largeexplode", par2 + i, par3 + 0.5D, par4 + j, 1.0D, 0.0D, 0.0D);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.Mine){
			            par1World.spawnParticle("largeexplode", par2 + i, par3 + 0.5D, par4 + j, 1.0D, 0.0D, 0.0D);
					}else if(par1World.getBlock(par2 + i, par3, par4 + j) == mod_SecurityCraft.portableRadar){
			            par1World.spawnParticle("largeexplode", par2 + i, par3 + 0.5D, par4 + j, 1.0D, 0.0D, 0.0D);
					}
				}
			}
		}	
	}
	
	/**
	 * Updates a block and notify's neighboring blocks of a change.
	 * 
	 * Args: worldObj, x, y, z, blockID, tickRate, shouldUpdate
	 * 
	 * 
	 */
	public static void updateAndNotify(World par1World, int par2, int par3, int par4, Block par5, int par6, boolean par7){
		if(par7){
			par1World.scheduleBlockUpdate(par2, par3, par4, par5, par6);
		}
		par1World.notifyBlockOfNeighborChange(par2 + 1, par3, par4, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2 - 1, par3, par4, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3, par4 + 1, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3, par4 - 1, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3 + 1, par4, par1World.getBlock(par2, par3, par4));
		par1World.notifyBlockOfNeighborChange(par2, par3 - 1, par4, par1World.getBlock(par2, par3, par4));
	}

	public static int getNumberOfUsernames(String usernames) {
		Scanner scanner = new Scanner(usernames);
		scanner.useDelimiter(",");
		
		int i = 0;
		while(scanner.hasNext()){
			scanner.next();
			i++;
		}
		
		return i;
	}
	
//	public static World getWorld(){
//		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
//			return MinecraftServer.getServer().getEntityWorld();
//		}else{
//			return Minecraft.getMinecraft().theWorld;
//		}
//	}
	
	public static Item getItemFromBlock(Block par1){
		return Item.getItemFromBlock(par1);
	}
	
	public static void sendMessage(ICommandSender par1ICommandSender, String par2, EnumChatFormatting par3){
        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(par2, new Object[0]);
        chatcomponenttranslation.getChatStyle().setColor(par3);
        par1ICommandSender.addChatMessage(chatcomponenttranslation);
	}
	
	public static void sendMessageToPlayer(EntityPlayer par1EntityPlayer, String par2, EnumChatFormatting par3){
		ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(par2, new Object[0]);
    	
		if(par3 != null){
    		chatcomponenttranslation.getChatStyle().setColor(par3);
    	}
    	
		par1EntityPlayer.addChatComponentMessage(chatcomponenttranslation);
	}
	
	public static void closePlayerScreen(EntityPlayer par1){
		Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
		Minecraft.getMinecraft().setIngameFocus();
	}
	
	public static void destroyBlock(World par1World, int par2, int par3, int par4, boolean par5){
		par1World.func_147480_a(par2, par3, par4, par5);
	}
	
	public static boolean isActiveBeacon(World par1World, int beaconX, int beaconY, int beaconZ){
		if(par1World.getBlock(beaconX, beaconY, beaconZ) == Blocks.beacon){
			float f = ((TileEntityBeacon) par1World.getTileEntity(beaconX, beaconY, beaconZ)).func_146002_i();
			
			return f > 0.0F ? true : false;
		}else{
			return false;
		}
	}
	
	/**
	 * Checks if the block at x, y, z is touching the specified block on any side.
	 */

	public static boolean blockSurroundedBy(World world, int x, int y, int z, Block blockToCheckFor, boolean checkYAxis) {
		if(!checkYAxis && (world.getBlock(x + 1, y, z) == blockToCheckFor || world.getBlock(x - 1, y, z) == blockToCheckFor || world.getBlock(x, y, z + 1) == blockToCheckFor || world.getBlock(x, y, z - 1) == blockToCheckFor)){
			return true;
		}else if(checkYAxis && (world.getBlock(x + 1, y, z) == blockToCheckFor || world.getBlock(x - 1, y, z) == blockToCheckFor || world.getBlock(x, y, z + 1) == blockToCheckFor || world.getBlock(x, y, z - 1) == blockToCheckFor || world.getBlock(x, y + 1, z) == blockToCheckFor || world.getBlock(x, y - 1, z) == blockToCheckFor)){
			return true;
		}else{
			return false;
		}
	}
	
	public static void setISinTEAppropriately(World par1World, int par2, int par3, int par4, ItemStack[] contents, String type) {
		//System.out.println("Our meta: " + (par1World.getBlockMetadata(par2, par3, par4)) + " | Alts meta: ( X-: " + (par1World.getBlockMetadata(par2 - 2, par3, par4)) + ", X+: " + (par1World.getBlockMetadata(par2 + 2, par3, par4)) + ", Z-: " + (par1World.getBlockMetadata(par2, par3, par4 - 2)) + ", Z+: " + (par1World.getBlockMetadata(par2, par3, par4 + 2)) +")");
		if(par1World.getBlockMetadata(par2, par3, par4) == 4 && par1World.getBlock(par2 - 2, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2 - 2, par3, par4) == 5){
			//System.out.println("Running X-");
			//par1World.setTileEntity(par2 - 2, par3, par4, par5TileEntityIS);
			//((TileEntityOwnable)par1World.getTileEntity(par2 - 2, par3, par4)).setOwner(par5TileEntityIS.getOwner());
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 2, par3, par4)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 - 2, par3, par4)).setType(type);
			//mod_SecurityCraft.network.sendToAll(new PacketCUpdateOwner(par2 - 2, par3, par4, par5TileEntityIS.getOwner(), false));
		}
		else if(par1World.getBlockMetadata(par2, par3, par4) == 5 && par1World.getBlock(par2 + 2, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2 + 2, par3, par4) == 4){
			//System.out.println("Running X+");
			//par1World.setTileEntity(par2 + 2, par3, par4, par5TileEntityIS);
			//((TileEntityOwnable)par1World.getTileEntity(par2 + 2, par3, par4)).setOwner(par5TileEntityIS.getOwner());
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 2, par3, par4)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2 + 2, par3, par4)).setType(type);
			//mod_SecurityCraft.network.sendToAll(new PacketCUpdateOwner(par2 + 2, par3, par4, par5TileEntityIS.getOwner(), false));
		}
		else if(par1World.getBlockMetadata(par2, par3, par4) == 2 && par1World.getBlock(par2, par3, par4 - 2) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2, par3, par4 - 2) == 3){
			//System.out.println("Running Z-");
			//par1World.setTileEntity(par2, par3, par4 - 2, par5TileEntityIS);
			//((TileEntityOwnable)par1World.getTileEntity(par2, par3, par4 - 2)).setOwner(par5TileEntityIS.getOwner());
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 2)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 - 2)).setType(type);
			//mod_SecurityCraft.network.sendToAll(new PacketCUpdateOwner(par2, par3, par4 - 2, par5TileEntityIS.getOwner(), false));
		}
		else if(par1World.getBlockMetadata(par2, par3, par4) == 3 && par1World.getBlock(par2, par3, par4 + 2) == mod_SecurityCraft.inventoryScanner && par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.inventoryScannerField && par1World.getBlockMetadata(par2, par3, par4 + 2) == 2){
			//System.out.println("Running Z+");
			//par1World.setTileEntity(par2, par3, par4 + 2, par5TileEntityIS);
			//((TileEntityOwnable)par1World.getTileEntity(par2, par3, par4 + 2)).setOwner(par5TileEntityIS.getOwner());
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 2)).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(par2, par3, par4 + 2)).setType(type);
			//mod_SecurityCraft.network.sendToAll(new PacketCUpdateOwner(par2, par3, par4 + 2, par5TileEntityIS.getOwner(), false));
		}
	}

	public static boolean hasInventoryScannerFacingBlock(World par1World, int par2, int par3, int par4) {
		if(par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 + 1, par3, par4) == 4 && par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 - 1, par3, par4) == 5){
			return true;
		}
		else if(par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 - 1, par3, par4) == 5 && par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2 + 1, par3, par4) == 4){
			return true;
		}
		else if(par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 + 1) == 2 && par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 - 1) == 3){
			return true;
		}
		else if(par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 - 1) == 3 && par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.inventoryScanner && par1World.getBlockMetadata(par2, par3, par4 + 1) == 2){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean doesMobHavePotionEffect(EntityLivingBase mob, Potion potion){
		Iterator iterator = mob.getActivePotionEffects().iterator();

		while(iterator.hasNext()){
			PotionEffect effect = (PotionEffect) iterator.next();
			String eName = effect.getEffectName();
			
			if(eName.matches(potion.getName())){
				return true;
			}else{
				continue;
			}
		}
		
		return false;
	}
	
//	private static void bookCode(){
//		ItemStack book = new ItemStack(Items.written_book);
//		
//		NBTTagCompound tag = new NBTTagCompound();
//		NBTTagList bookPages = new NBTTagList();
//		bookPages.appendTag(new NBTTagString("SecurityCraft " + mod_SecurityCraft.getVersion() + " info book."));
//		bookPages.appendTag(new NBTTagString("Keypad: \n \nThe keypad is used by placing the keypad, right-clicking it, and setting a numerical passcode. Once the keycode is set, right-clicking the keypad will allow you to enter the code. If it's correct, the keypad will emit redstone power for three seconds."));
//		bookPages.appendTag(new NBTTagString("Laser block: The laser block is used by putting two of them within five blocks of each other. When the blocks are placed correctly, a laser should form between them. Whenever a player walks through the laser, both the laser blocks will emit a 15-block redstone signal."));
//		
//		book.setTagInfo("pages", bookPages);
//		book.setTagInfo("author", new NBTTagString("Geforce"));
//		book.setTagInfo("title", new NBTTagString("SecurityCraft"));
//		
//		player.inventory.addItemStackToInventory(book);
//	}
	
	
}
