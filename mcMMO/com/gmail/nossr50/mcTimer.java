package com.gmail.nossr50;
import java.util.TimerTask;

import org.bukkit.entity.*;

import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Swords;


public class mcTimer extends TimerTask{
	private final mcMMO plugin;
	Player[] playerlist = null;
	int thecount = 1;

    public mcTimer(final mcMMO plugin) {
        this.plugin = plugin;
    }
    
	public void run() {
		playerlist = plugin.getServer().getOnlinePlayers();
		for(Player player : playerlist)
		{
			if(player == null)
				continue;
			PlayerProfile PP = Users.getProfile(player);
			if(PP == null)
	    		Users.addUser(player);
			/*
			 * MONITOR SKILLS
			 */
			Skills.monitorSkills(player);
			/*
			 * COOLDOWN MONITORING
			 */
			Skills.watchCooldowns(player);
			
			/*
			 * PLAYER BLEED MONITORING
			 */
			if(thecount % 2 == 0 && PP != null && PP.getBleedTicks() >= 1){
        		player.damage(2);
        		PP.decreaseBleedTicks();
        	}
			
			if(LoadProperties.enableRegen && mcPermissions.getInstance().regeneration(player) && PP != null && System.currentTimeMillis() >= PP.getRecentlyHurt() + 60000)
			{
				if(thecount == 10 || thecount == 20 || thecount == 30 || thecount == 40){
				    if(player != null &&
				    	player.getHealth() > 0 && player.getHealth() < 20 
				    	&& m.getPowerLevel(player) >= 1000){
				    	player.setHealth(m.calculateHealth(player.getHealth(), 1));
				    }
				}
				if(thecount == 20 || thecount == 40){
			   		if(player != null &&
			   			player.getHealth() > 0 && player.getHealth() < 20 
			    		&& m.getPowerLevel(player) >= 500 
			    		&& m.getPowerLevel(player) < 1000){
			    		player.setHealth(m.calculateHealth(player.getHealth(), 1));
			    	}
				}
				if(thecount == 40){
			    	if(player != null &&
			    		player.getHealth() > 0 && player.getHealth() < 20  
			    		&& m.getPowerLevel(player) < 500){
			    		player.setHealth(m.calculateHealth(player.getHealth(), 1));
			    	}
				}
			}
		}
		
		
		/*
		 * NON-PLAYER BLEED MONITORING
		 */
		if(thecount % 2 == 0)
			Swords.bleedSimulate();
		
		if(thecount < 40)
		{
			thecount++;
		} else 
		{
			thecount = 1;
		}
	}
}
