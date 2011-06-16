package com.gmail.nossr50.skills;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.Messages;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.config.*;


public class WoodCutting {
	static int w = 0;
	private static boolean isdone = false;
	
    public static void woodCuttingProcCheck(Player player, Block block){
    	PlayerProfile PP = Users.getProfile(player);
    	byte type = block.getData();
    	Material mat = Material.getMaterial(block.getTypeId());
    	if(player != null){
    		if(Math.random() * 1000 <= PP.getSkill("woodcutting")){
    			ItemStack item = new ItemStack(mat, 1, (short) 0, type);
    			block.getWorld().dropItemNaturally(block.getLocation(), item);
    		}
    	}
    }
    public static void treeFellerCheck(Player player, Block block, Plugin pluginx){
    	PlayerProfile PP = Users.getProfile(player);
    	if(m.isAxes(player.getItemInHand())){
    		if(block != null){
        		if(!m.abilityBlockCheck(block))
        			return;
        	}
    		/*
    		 * CHECK FOR AXE PREP MODE
    		 */
    		if(PP.getAxePreparationMode()){
    			PP.setAxePreparationMode(false);
    		}
    		int ticks = 2;
    		int x = PP.getSkill("woodcutting");
    		while(x >= 50){
    			x-=50;
    			ticks++;
    		}

    		if(!PP.getTreeFellerMode() && Skills.cooldownOver(player, PP.getTreeFellerDeactivatedTimeStamp(), LoadProperties.treeFellerCooldown)){
    			player.sendMessage(Messages.getString("Skills.TreeFellerOn"));
    			for(Player y : pluginx.getServer().getOnlinePlayers()){
	    			if(y != null && y != player && m.getDistance(player.getLocation(), y.getLocation()) < 10)
	    				y.sendMessage(Messages.getString("Skills.TreeFellerPlayer", new Object[] {player.getName()}));
	    		}
    			PP.setTreeFellerActivatedTimeStamp(System.currentTimeMillis());
    			PP.setTreeFellerDeactivatedTimeStamp(System.currentTimeMillis() + (ticks * 1000));
    			PP.setTreeFellerMode(true);
    		}
    		if(!PP.getTreeFellerMode() && !Skills.cooldownOver(player, PP.getTreeFellerDeactivatedTimeStamp(), LoadProperties.treeFellerCooldown)){
    			player.sendMessage(ChatColor.RED+"You are too tired to use that ability again."
    					+ChatColor.YELLOW+" ("+Skills.calculateTimeLeft(player, PP.getTreeFellerDeactivatedTimeStamp(), LoadProperties.treeFellerCooldown)+"s)");
    		}
    	}
    }
    public static void treeFeller(Block block, Player player){
    	PlayerProfile PP = Users.getProfile(player);
    	int radius = 1;
    	if(PP.getSkill("woodcutting") >= 500)
    		radius++;
    	if(PP.getSkill("woodcutting") >= 950)
    		radius++;
        ArrayList<Block> blocklist = new ArrayList<Block>();
        ArrayList<Block> toAdd = new ArrayList<Block>();
        if(block != null)
        	blocklist.add(block);
        while(isdone == false){
        	addBlocksToTreeFelling(blocklist, toAdd, radius);
        }
        //This needs to be a hashmap too!
        isdone = false;
        /*
         * Add blocks from the temporary 'toAdd' array list into the 'treeFeller' array list
         * We use this temporary list to prevent concurrent modification exceptions
         */
        for(Block x : toAdd){
        	if(!Config.getInstance().isTreeFellerWatched(x))
        		Config.getInstance().addTreeFeller(x);
        }
        toAdd.clear();
    }
    public static void addBlocksToTreeFelling(ArrayList<Block> blocklist, ArrayList<Block> toAdd, Integer radius){
    	int u = 0;
    	for (Block x : blocklist){
    		u++;
    		if(toAdd.contains(x))
    			continue;
    		w = 0;
    		Location loc = x.getLocation();
    		int vx = x.getX();
            int vy = x.getY();
            int vz = x.getZ();
            
            /*
             * Run through the blocks around the broken block to see if they qualify to be 'felled'
             */
    		for (int cx = -radius; cx <= radius; cx++) {
	            for (int cy = -radius; cy <= radius; cy++) {
	                for (int cz = -radius; cz <= radius; cz++) {
	                    Block blocktarget = loc.getWorld().getBlockAt(vx + cx, vy + cy, vz + cz);
	                    if (!blocklist.contains(blocktarget) && !toAdd.contains(blocktarget) && (blocktarget.getTypeId() == 17 || blocktarget.getTypeId() == 18)) { 
	                        toAdd.add(blocktarget);
	                        w++;
	                    }
	                }
	            }
	        }
    	}
    	/*
		 * Add more blocks to blocklist so they can be 'felled'
		 */
		for(Block xx : toAdd){
    		if(!blocklist.contains(xx))
        	blocklist.add(xx);
        }
    	if(u >= blocklist.size()){
    		isdone = true;
    	} else {
    		isdone = false;
    	}
    }
}
