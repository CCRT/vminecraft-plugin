package com.gmail.nossr50;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Axes;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Swords;
import com.gmail.nossr50.skills.Taming;
import com.gmail.nossr50.skills.Unarmed;

public class Combat {
	public static void combatChecks(EntityDamageEvent event, Plugin pluginx){
		if(event.isCancelled() || event.getDamage() == 0)
			return;
		/*
		 * OFFENSIVE CHECKS FOR PLAYERS VERSUS ENTITIES
		 */
		if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player)
		{
			//Declare Things
			EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent) event;
			Player attacker = (Player)((EntityDamageByEntityEvent) event).getDamager();
			PlayerProfile PPa = Users.getProfile(attacker);
			
			//Damage modifiers
			if(mcPermissions.getInstance().unarmed(attacker) && attacker.getItemInHand().getTypeId() == 0) //Unarmed
				Unarmed.unarmedBonus(attacker, eventb);
			if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker) && PPa.getSkill("axes") >= 500)
					event.setDamage(event.getDamage()+4);
			
			//If there are any abilities to activate
	    	combatAbilityChecks(attacker, PPa, pluginx);
	    	
	    	//Check for offensive procs
	    	if(!(event instanceof EntityDamageByProjectileEvent)){
		    	if(mcPermissions.getInstance().axes(attacker))
		    		Axes.axeCriticalCheck(attacker, eventb, pluginx); //Axe Criticals
		    	if(!Config.getInstance().isBleedTracked(event.getEntity())) //Swords Bleed
		   			Swords.bleedCheck(attacker, event.getEntity(), pluginx);
			   	if(event.getEntity() instanceof Player && mcPermissions.getInstance().unarmed(attacker)){
			   		Player defender = (Player)event.getEntity();
			   		Unarmed.disarmProcCheck(attacker, defender);
			    }
		    	
		    	
		    	
		    	//Modify the event damage if Attacker is Berserk
		    	if(PPa.getBerserkMode())
		    		event.setDamage(event.getDamage() + (event.getDamage() / 2));
	       	
		   		//Handle Ability Interactions
		   		if(PPa.getSkullSplitterMode() && m.isAxes(attacker.getItemInHand()))
	       			Axes.applyAoeDamage(attacker, eventb, pluginx);
	      		if(PPa.getSerratedStrikesMode() && m.isSwords(attacker.getItemInHand()))
	       			Swords.applySerratedStrikes(attacker, eventb, pluginx);
	      		
	      		//Experience
	      		if(event.getEntity() instanceof Player)
	      		{
	      			Player defender = (Player)event.getEntity();
	      			PlayerProfile PPd = Users.getProfile(defender);
		    		if(attacker != null && defender != null && LoadProperties.pvpxp)
		    		{
		    			if(System.currentTimeMillis() >= PPd.getRespawnATS() + 5000 
		    					&& ((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis()
		    					&& defender.getHealth() >= 1)
		    			{
			    			if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker))
			    				PPa.addAxesXP((event.getDamage() * 2) * LoadProperties.pvpxprewardmodifier);
			    			if(m.isSwords(attacker.getItemInHand()) && mcPermissions.getInstance().swords(attacker))
			    				PPa.addSwordsXP((event.getDamage() * 2) * LoadProperties.pvpxprewardmodifier);
			    			if(attacker.getItemInHand().getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker))
			    				PPa.addUnarmedXP((event.getDamage() * 2) * LoadProperties.pvpxprewardmodifier);
		    			}
		    		}
	      		}
	      		
	      		if(event.getEntity() instanceof Monster)
	      		{
	      			int xp = 0;
	      			if(event.getEntity() instanceof Creeper)
						xp = (event.getDamage() * 4) * LoadProperties.xpGainMultiplier;
					if(event.getEntity() instanceof Spider)
						xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
					if(event.getEntity() instanceof Skeleton)
						xp = (event.getDamage() * 2) * LoadProperties.xpGainMultiplier;
					if(event.getEntity() instanceof Zombie)
						xp = (event.getDamage() * 2) * LoadProperties.xpGainMultiplier;
					if(event.getEntity() instanceof PigZombie)
						xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
					if(event.getEntity() instanceof Slime)
						xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
					if(event.getEntity() instanceof Ghast)
						xp = (event.getDamage() * 3) * LoadProperties.xpGainMultiplier;
					
					if(m.isSwords(attacker.getItemInHand()) && mcPermissions.getInstance().swords(attacker))
						PPa.addSwordsXP(xp);
					if(m.isAxes(attacker.getItemInHand()) && mcPermissions.getInstance().axes(attacker))
						PPa.addAxesXP(xp);
					if(attacker.getItemInHand().getTypeId() == 0 && mcPermissions.getInstance().unarmed(attacker))
						PPa.addUnarmedXP(xp);
	      		}
	      		Skills.XpCheck(attacker);
	      		
	      		//Taming Debug Stuff
	      		if(event.getEntity() instanceof Wolf)
	      		{
	      			if(Party.getInstance().inSameParty(attacker, Taming.getOwner(event.getEntity(), pluginx)))
	      				event.setCancelled(true);
	      			if(attacker.getItemInHand().getTypeId() == 352 && mcPermissions.getInstance().taming(attacker))
	      			{
		      			attacker.sendMessage(Messages.getString("Combat.WolfExamine")); //$NON-NLS-1$
		      			if(Taming.getOwnerName(event.getEntity()) != null)
		      				attacker.sendMessage(Messages.getString("Combat.WolfShowMaster", new Object[] {Taming.getOwnerName(event.getEntity())})); //$NON-NLS-1$
		      			else
		      				attacker.sendMessage(Messages.getString("Combat.WolfNoMaster")); //$NON-NLS-1$
		      			attacker.sendMessage(Messages.getString("Combat.WolfHealth", new Object[] {((Wolf)event.getEntity()).getHealth()})); //$NON-NLS-1$ //$NON-NLS-2$
		      			event.setCancelled(true);
	      			}
	      		}
			}
		}
		
		/*
		 * OFFENSIVE CHECKS FOR WOLVES VERSUS ENTITIES
		 */
		if(event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Wolf)
		{
			EntityDamageByEntityEvent eventb = (EntityDamageByEntityEvent) event;
			if(Taming.hasOwner(eventb.getDamager(), pluginx))
			{
				Player master = Taming.getOwner(eventb.getDamager(), pluginx);
				PlayerProfile PPo = Users.getProfile(master);
				if(mcPermissions.getInstance().taming(master)){
					//Sharpened Claws
					if(PPo.getSkill("taming") >= 750)
					{
						event.setDamage(event.getDamage() + 2);
					}
					
					//Gore
					if(Math.random() * 1000 <= PPo.getSkill("taming"))
					{
						event.setDamage(event.getDamage() * 2);
						
						if(event.getEntity() instanceof Player)
						{
							Player target = (Player)event.getEntity();
							target.sendMessage(Messages.getString("Combat.StruckByGore")); //$NON-NLS-1$
							Users.getProfile(target).setBleedTicks(2);
						}
						else
							Config.getInstance().addToBleedQue(event.getEntity());
						
						master.sendMessage(Messages.getString("Combat.Gore")); //$NON-NLS-1$
					}
				}
			}
		}
		//Another offensive check for Archery
		if(event instanceof EntityDamageByProjectileEvent)
			archeryCheck((EntityDamageByProjectileEvent) event);
			
		/*
		 * DEFENSIVE CHECKS
		 */
		if(event instanceof EntityDamageByEntityEvent && event.getEntity() instanceof Player)
		{
			Player defender = (Player)event.getEntity();
			Swords.parryCheck((EntityDamageByEntityEvent) event, defender);
			Swords.counterAttackChecks(event);
			Acrobatics.dodgeChecks((EntityDamageByEntityEvent)event);
		}
		/*
		 * DEFENSIVE CHECKS FOR WOLVES
		 */
		if(event.getEntity() instanceof Wolf){
			if(Taming.hasOwner(event.getEntity(), pluginx))
			{
				Player master = Taming.getOwner(event.getEntity(), pluginx);
				PlayerProfile PPo = Users.getProfile(master);
				if(mcPermissions.getInstance().taming(master))
				{
					/*
					 * TEMPORARY FIX AS WOLVES AREN'T TRIGGERING DAMAGE EVENTS WHEN ATTACKING NON PLAYERS AT THE TIME OF WRITING
					 */
					if(!event.isCancelled() && event.getCause() != DamageCause.LIGHTNING){
						PPo.addTamingXP(event.getDamage() * 3);
						Skills.XpCheck(master);
					}
					
					//Shock-Proof
					if((event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) && PPo.getSkill("taming") >= 500)
					{
						event.setDamage(2);
					}
					
					//Thick Fur
					if(PPo.getSkill("taming") >= 250)
						event.setDamage(event.getDamage() / 2);
				}
			}
		}
	}
	
	public static void combatAbilityChecks(Player attacker, PlayerProfile PPa, Plugin pluginx)
	{
		//Check to see if any abilities need to be activated
		if(PPa.getAxePreparationMode())
			Axes.skullSplitterCheck(attacker, pluginx);
		if(PPa.getSwordsPreparationMode())
			Swords.serratedStrikesActivationCheck(attacker, pluginx);
		if(PPa.getFistsPreparationMode())
			Unarmed.berserkActivationCheck(attacker, pluginx);
	}
	public static void archeryCheck(EntityDamageByProjectileEvent event){
    	Entity y = event.getDamager();
    	Entity x = event.getEntity();
    	if(event.getProjectile().toString().equals("CraftArrow") && x instanceof Player){ //$NON-NLS-1$
    		Player defender = (Player)x;
    		PlayerProfile PPd = Users.getProfile(defender);
    		if(PPd == null)
    			Users.addUser(defender);
    		if(mcPermissions.getInstance().unarmed(defender) && defender.getItemInHand().getTypeId() == 0){
	    		if(defender != null && PPd.getSkill("unarmed") >= 1000){
	    			if(Math.random() * 1000 <= 500){
	    				event.setCancelled(true);
	    				defender.sendMessage(Messages.getString("Combat.ArrowDeflect")); //$NON-NLS-1$
	    				return;
	    			}
	    		} else if(defender != null && Math.random() * 1000 <= (PPd.getSkill("unarmed") / 2)){
	    			event.setCancelled(true);
	    			defender.sendMessage(Messages.getString("Combat.ArrowDeflect")); //$NON-NLS-1$
	    			return;
	    		}
    		}
    	}
    	/*
    	 * If attacker is player
    	 */
    	if(y instanceof Player){
    		Player attacker = (Player)y;
    		PlayerProfile PPa = Users.getProfile(attacker);
    		if(event.getProjectile().toString().equals("CraftArrow") && mcPermissions.getInstance().archery(attacker)){ //$NON-NLS-1$
    			if(!Config.getInstance().isTracked(x) && event.getDamage() > 0){
    				Config.getInstance().addArrowTrack(x, 0);
    				if(attacker != null){
    					if(Math.random() * 1000 <= PPa.getSkill("archery")){
    						Config.getInstance().addArrowCount(x, 1);
    					}
    				}
    			} else {
    				if(event.getDamage() > 0){
    					if(attacker != null){
        					if(Math.random() * 1000 <= PPa.getSkill("archery")){
        						Config.getInstance().addArrowCount(x, 1);
        					}
        				}
    				}
    			}
    			/*
    			 * DAMAGE MODIFIER
    			 */
    			if(PPa.getSkill("archery") >= 50 && PPa.getSkill("archery") < 250)
    				event.setDamage(event.getDamage()+1);
    			if(PPa.getSkill("archery") >= 250 && PPa.getSkill("archery") < 575)
    				event.setDamage(event.getDamage()+2);
    			if(PPa.getSkill("archery") >= 575 && PPa.getSkill("archery") < 725)
    				event.setDamage(event.getDamage()+3);
    			if(PPa.getSkill("archery") >= 725 && PPa.getSkill("archery") < 1000)
    				event.setDamage(event.getDamage()+4);
    			if(PPa.getSkill("archery") >= 1000)
    				event.setDamage(event.getDamage()+5);
    			
    			/*
    			 * IGNITION
    			 */
    			if(Math.random() * 100 >= 75){
    				
    				int ignition = 20;	
    				if(PPa.getSkill("archery") >= 200)
    					ignition+=20;
    				if(PPa.getSkill("archery") >= 400)
    					ignition+=20;
    				if(PPa.getSkill("archery") >= 600)
    					ignition+=20;
    				if(PPa.getSkill("archery") >= 800)
    					ignition+=20;
    				if(PPa.getSkill("archery") >= 1000)
    					ignition+=20;
    				
        			if(x instanceof Player){
        				Player Defender = (Player)x;
        				if(!Party.getInstance().inSameParty(attacker, Defender)){
        					event.getEntity().setFireTicks(ignition);
        					attacker.sendMessage(Messages.getString("Combat.Ignition")); //$NON-NLS-1$
        					Defender.sendMessage(Messages.getString("Combat.BurningArrowHit")); //$NON-NLS-1$
        				}
        			} else {
        			event.getEntity().setFireTicks(ignition);
        			attacker.sendMessage(Messages.getString("Combat.Ignition")); //$NON-NLS-1$
        			}
        		}
    		/*
    		 * Defender is Monster
    		 */
    		if(x instanceof Monster){
    			//XP
    			if(x instanceof Creeper)
    				PPa.addArcheryXP((event.getDamage() * 4) * LoadProperties.xpGainMultiplier);
				if(x instanceof Spider)
					PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
				if(x instanceof Skeleton)
					PPa.addArcheryXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
				if(x instanceof Zombie)
					PPa.addArcheryXP((event.getDamage() * 2) * LoadProperties.xpGainMultiplier);
				if(x instanceof PigZombie)
					PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
				if(x instanceof Slime)
					PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
				if(x instanceof Ghast)
					PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.xpGainMultiplier);
    		}
    		/*
    		 * Attacker is Player
    		 */
    		if(x instanceof Player){
    			Player defender = (Player)x;
    			PlayerProfile PPd = Users.getProfile(defender);
    			/*
    			 * Stuff for the daze proc
    			 */
    	    		if(PPa.inParty() && PPd.inParty()){
    					if(Party.getInstance().inSameParty(defender, attacker)){
    						event.setCancelled(true);
    						return;
    					}
    	    		}
    	    		/*
    	    		 * PVP XP
    	    		 */
    	    		if(LoadProperties.pvpxp && !Party.getInstance().inSameParty(attacker, defender) 
    	    				&& ((PPd.getLastLogin()+5)*1000) < System.currentTimeMillis()){
    	    			PPa.addArcheryXP((event.getDamage() * 3) * LoadProperties.pvpxprewardmodifier);
    	    		}
    				/*
    				 * DAZE PROC
    				 */
    	    		Location loc = defender.getLocation();
    				if(Math.random() * 10 > 5){
					loc.setPitch(90);
					} else {
						loc.setPitch(-90);
					}
    				if(PPa.getSkill("archery") >= 1000){
    	    			if(Math.random() * 1000 <= 500){
    	    				defender.teleport(loc);
    	    				defender.sendMessage(Messages.getString("Combat.TouchedFuzzy")); //$NON-NLS-1$
    	    				attacker.sendMessage(Messages.getString("Combat.TargetDazed")); //$NON-NLS-1$ //$NON-NLS-2$
    	    			}
    	    		} else if(Math.random() * 2000 <= PPa.getSkill("archery")){
    	    			defender.teleport(loc);
	    				defender.sendMessage(Messages.getString("Combat.TouchedFuzzy")); //$NON-NLS-1$
	    				attacker.sendMessage(Messages.getString("Combat.TargetDazed")); //$NON-NLS-1$ //$NON-NLS-2$
    	    		}
    			}
    		}
    		Skills.XpCheck(attacker);
    	}
    }
    public static void dealDamage(Entity target, int dmg){
    	if(target instanceof Player){
    		((Player) target).damage(dmg);
    	}
    	if(target instanceof Animals){
    		((Animals) target).damage(dmg);
    	}
    	if(target instanceof Monster){
    		((Monster) target).damage(dmg);
    	}
    }
    public static boolean pvpAllowed(EntityDamageByEntityEvent event, World world)
    {
    	if(!event.getEntity().getWorld().getPVP())
    		return false;
    	if(world.getPVP())
    		return false;
    	//If it made it this far, pvp is enabled
    	return true;
    }
}
