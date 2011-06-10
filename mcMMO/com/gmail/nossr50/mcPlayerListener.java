package com.gmail.nossr50;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.skills.Herbalism;
import com.gmail.nossr50.skills.Repair;
import com.gmail.nossr50.skills.Skills;


public class mcPlayerListener extends PlayerListener {
	protected static final Logger log = Logger.getLogger("Minecraft"); //$NON-NLS-1$
	public Location spawn = null;
    private mcMMO plugin;

    public mcPlayerListener(mcMMO instance) {
    	plugin = instance;
    }

   
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	if(LoadProperties.enableMySpawn){
	    	Player player = event.getPlayer();
	    	PlayerProfile PP = Users.getProfile(player);
	    	if(PP == null)
	    	{
	    		Users.addUser(player);
	    		PP = Users.getProfile(player);
	    	}
	    	if(player != null && PP != null){
	    		PP.setRespawnATS(System.currentTimeMillis());
				Location mySpawn = PP.getMySpawn(player);
				if(mySpawn != null && plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)) != null)
					mySpawn.setWorld(plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)));
				if(mcPermissions.getInstance().mySpawn(player) && mySpawn != null){
			    	event.setRespawnLocation(mySpawn);
				}
	    	}
    	}
    }
    public Player[] getPlayersOnline() {
    		return plugin.getServer().getOnlinePlayers();
    }
	public boolean isPlayer(String playerName){
    	for(Player herp :  getPlayersOnline()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return true;
    		}
    	}
    		return false;
    }
	public Player getPlayer(String playerName){
    	for(Player herp : getPlayersOnline()){
    		if(herp.getName().toLowerCase().equals(playerName.toLowerCase())){
    			return herp;
    		}
    	}
    	return null;
    }
    public void onPlayerLogin(PlayerLoginEvent event) 
    {
    	Users.addUser(event.getPlayer());
    	if(Users.getProfile(event.getPlayer()) != null)
    	{
    		Users.getProfile(event.getPlayer()).setOnline(true);
    	}
    }
    public void onPlayerQuit(PlayerQuitEvent event) 
    {
    	Users.getProfile(event.getPlayer()).setOnline(false);
    	if(Config.getInstance().isAdminToggled(event.getPlayer().getName()))
    		Config.getInstance().removeAdminToggled(event.getPlayer().getName());
    	if(Config.getInstance().isGodModeToggled(event.getPlayer().getName()))
    		Config.getInstance().removeGodModeToggled(event.getPlayer().getName());
    	if(Config.getInstance().isPartyToggled(event.getPlayer().getName()))
    		Config.getInstance().removePartyToggled(event.getPlayer().getName());
    }        
    public void onPlayerJoin(PlayerJoinEvent event) 
    {
    	Player player = event.getPlayer();
    	if(mcPermissions.getInstance().motd(player) && LoadProperties.enableMotd)
    	{
    		//player.sendMessage(ChatColor.BLUE +"This server is running mcMMO "+plugin.getDescription().getVersion()+" type /"+ChatColor.YELLOW+LoadProperties.mcmmo+ChatColor.BLUE+ " for help.");
    		player.sendMessage(Messages.getString("mcPlayerListener.MOTD", new Object[] {plugin.getDescription().getVersion(), LoadProperties.mcmmo}));
    		//player.sendMessage(ChatColor.GREEN+"http://mcmmo.wikia.com"+ChatColor.BLUE+" - mcMMO Wiki");
    		player.sendMessage(Messages.getString("mcPlayerListener.WIKI"));
    	}
    }
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	Action action = event.getAction();
    	Block block = event.getClickedBlock();
    	//Archery Nerf
    	if(player.getItemInHand().getTypeId() == 261 && LoadProperties.archeryFireRateLimit){
    		if(System.currentTimeMillis() < PP.getArcheryShotATS() + 1000){
    			/*
    			if(m.hasArrows(player))
    				m.addArrows(player);
    			*/
    			player.updateInventory();
    			event.setCancelled(true);
    		} else {
    			PP.setArcheryShotATS(System.currentTimeMillis());
    		}
    	}
    	/*
    	 * Ability checks
    	 */
    	if(action == Action.RIGHT_CLICK_BLOCK){
    		ItemStack is = player.getItemInHand();
    		if(LoadProperties.enableMySpawn && block != null && player != null){
    			if(block.getTypeId() == 26 && mcPermissions.getInstance().setMySpawn(player)){
    		    	Location loc = player.getLocation();
    		    	if(mcPermissions.getInstance().setMySpawn(player)){
    		    		PP.setMySpawn(loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName());
    		    	}
    		    	player.sendMessage(Messages.getString("mcPlayerListener.MyspawnSet"));
    			}
    		}
        	if(block != null && player != null && mcPermissions.getInstance().repair(player) && event.getClickedBlock().getTypeId() == 42){
            	Repair.repairCheck(player, is, event.getClickedBlock());
            }
        	
        	if(m.abilityBlockCheck(block))
	    	{
        		if(block != null && m.isHoe(player.getItemInHand()) && block.getTypeId() != 3 && block.getTypeId() != 2 && block.getTypeId() != 60){
        			Skills.hoeReadinessCheck(player);
        		}
	    		Skills.abilityActivationCheck(player);
	    	}
        	
        	//GREEN THUMB
        	if(block != null && (block.getType() == Material.COBBLESTONE || block.getType() == Material.DIRT) && player.getItemInHand().getType() == Material.SEEDS){
        		boolean pass = false;
        		if(Herbalism.hasSeeds(player) && mcPermissions.getInstance().herbalism(player)){
        			Herbalism.removeSeeds(player);
	        		if(LoadProperties.enableCobbleToMossy && m.blockBreakSimulate(block, player, plugin) && block.getType() == Material.COBBLESTONE && Math.random() * 1500 <= PP.getHerbalismInt()){
	        			player.sendMessage(Messages.getString("mcPlayerListener.GreenThumb"));
	        			block.setType(Material.MOSSY_COBBLESTONE);
	        			pass = true;
	        		}
	        		if(block.getType() == Material.DIRT && m.blockBreakSimulate(block, player, plugin) && Math.random() * 1500 <= PP.getHerbalismInt()){
	        			player.sendMessage(Messages.getString("mcPlayerListener.GreenThumb"));
	        			block.setType(Material.GRASS);
	        			pass = true;
	        		}
	        		if(pass == false)
	        			player.sendMessage(Messages.getString("mcPlayerListener.GreenThumbFail"));
	        		}
        		return;
        	}
    	}
    	if(action == Action.RIGHT_CLICK_AIR){
    		Skills.hoeReadinessCheck(player);
		    Skills.abilityActivationCheck(player);
		    
		    /*
        	 * HERBALISM MODIFIERS
        	 */
        	if(mcPermissions.getInstance().herbalism(player)){
        		Herbalism.breadCheck(player, player.getItemInHand());
        		Herbalism.stewCheck(player, player.getItemInHand());
        	}
    	}
    	/*
    	 * ITEM CHECKS
    	 */
    	if(action == Action.RIGHT_CLICK_AIR)
        	Item.itehecks(player, plugin);
    	if(action == Action.RIGHT_CLICK_BLOCK){
    		if(m.abilityBlockCheck(event.getClickedBlock()))
    			Item.itehecks(player, plugin);
    	}
    }
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	PlayerProfile PP = Users.getProfile(player);
    	String[] split = event.getMessage().split(" ");
    	String playerName = player.getName();
    	//Check if the command is an MMO related help command
    	m.mmoHelpCheck(split, player, event);
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+LoadProperties.mcability)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(PP.getAbilityUse()){
    			player.sendMessage(Messages.getString("mcPlayerListener.AbilitiesOff")); //$NON-NLS-1$
    			PP.toggleAbilityUse();
    		} else {
    			player.sendMessage(Messages.getString("mcPlayerListener.AbilitiesOn")); //$NON-NLS-1$
    			PP.toggleAbilityUse();
    		}
    	}
    	
    	/*
    	 * FFS -> MySQL
    	 */
    	if(split[0].equalsIgnoreCase("/mmoupdate")) //$NON-NLS-1$
    	{
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().admin(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		player.sendMessage(ChatColor.GRAY+"Starting conversion..."); //$NON-NLS-1$
    		Users.clearUsers();
    		m.convertToMySQL(plugin);
    		for(Player x : plugin.getServer().getOnlinePlayers())
    		{
    			Users.addUser(x);
    		}
    		player.sendMessage(ChatColor.GREEN+"Conversion finished!"); //$NON-NLS-1$
    	}
    	
    	/*
    	 * LEADER BOARD COMMAND
    	 */
    	if(split[0].equalsIgnoreCase("/"+LoadProperties.mctop)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(LoadProperties.useMySQL == false){
	    		/*
	    		 * POWER LEVEL INFO RETRIEVAL
	    		 */
	    		if(split.length == 1){
	    			int p = 1;
	    			String[] info = Leaderboard.retrieveInfo("powerlevel", p); //$NON-NLS-1$
	    			player.sendMessage(Messages.getString("mcPlayerListener.PowerLevelLeaderboard"));
	    			int n = 1 * p; //Position
	    			for(String x : info){
	    				if(x != null){
	    					String digit = String.valueOf(n);
	    					if(n < 10)
	    						digit ="0"+String.valueOf(n); //$NON-NLS-1$
		    				String[] splitx = x.split(":"); //$NON-NLS-1$
		    				//Format: 1. Playername - skill value
		    				player.sendMessage(digit+". "+ChatColor.GREEN+splitx[1]+" - "+ChatColor.WHITE+splitx[0]); //$NON-NLS-1$ //$NON-NLS-2$
		    				n++;
	    				}
	    			}
	    		}
	    		if(split.length >= 2 && Leaderboard.isInt(split[1])){
	    			int p = 1;
	    			//Grab page value if specified
	    			if(split.length >= 2){
	    				if(Leaderboard.isInt(split[1])){
	    					p = Integer.valueOf(split[1]);
	    				}
	    			}
	    			int pt = p;
	    			if(p > 1){
	    				pt -= 1;
	    				pt += (pt * 10);
	    				pt = 10;
	    			}
	    			String[] info = Leaderboard.retrieveInfo("powerlevel", p); //$NON-NLS-1$
	    			player.sendMessage(Messages.getString("mcPlayerListener.PowerLevelLeaderboard")); //$NON-NLS-1$
	    			int n = 1 * pt; //Position
	    			for(String x : info){
	    				if(x != null){
	    					String digit = String.valueOf(n);
	    					if(n < 10)
	    						digit ="0"+String.valueOf(n); //$NON-NLS-1$
		    				String[] splitx = x.split(":"); //$NON-NLS-1$
		    				//Format: 1. Playername - skill value
		    				player.sendMessage(digit+". "+ChatColor.GREEN+splitx[1]+" - "+ChatColor.WHITE+splitx[0]); //$NON-NLS-1$ //$NON-NLS-2$
		    				n++;
	    				}
	    			}
	    		}
	    		/*
	    		 * SKILL SPECIFIED INFO RETRIEVAL
	    		 */
	    		if(split.length >= 2 && Skills.isSkill(split[1])){
	    			int p = 1;
	    			//Grab page value if specified
	    			if(split.length >= 3){
	    				if(Leaderboard.isInt(split[2])){
	    					p = Integer.valueOf(split[2]);
	    				}
	    			}
	    			int pt = p;
	    			if(p > 1){
	    				pt -= 1;
	    				pt += (pt * 10);
	    				pt = 10;
	    			}
	    			String firstLetter = split[1].substring(0,1);  // Get first letter
	    	        String remainder   = split[1].substring(1);    // Get remainder of word.
	    	        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();
	    	        
	    			String[] info = Leaderboard.retrieveInfo(split[1].toLowerCase(), p);
	    			player.sendMessage(Messages.getString("mcPlayerListener.SkillLeaderboard", new Object[] {capitalized})); //$NON-NLS-1$ //$NON-NLS-2$
	    			int n = 1 * pt; //Position
	    			for(String x : info){
	    				if(x != null){
	    					String digit = String.valueOf(n);
	    					if(n < 10)
	    						digit ="0"+String.valueOf(n); //$NON-NLS-1$
		    				String[] splitx = x.split(":"); //$NON-NLS-1$
		    				//Format: 1. Playername - skill value
		    				player.sendMessage(digit+". "+ChatColor.GREEN+splitx[1]+" - "+ChatColor.WHITE+splitx[0]); //$NON-NLS-1$ //$NON-NLS-2$
		    				n++;
	    				}
	    			}
	    		}
    		} else
    		/*
    		* MYSQL LEADERBOARDS
    		*/
    		{
    			String powerlevel = "taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics"; //$NON-NLS-1$
    			if(split.length >= 2 && Skills.isSkill(split[1]))
    			{
    				/*
    				 * Create a nice consistent capitalized leaderboard name
    				 */
    				String lowercase = split[1].toLowerCase(); //For the query
    				String firstLetter = split[1].substring(0,1); //Get first letter
	    	        String remainder   = split[1].substring(1); //Get remainder of word.
	    	        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();
	    	        
	    	        player.sendMessage(Messages.getString("mcPlayerListener.SkillLeaderboard", new Object[] {capitalized})); //$NON-NLS-1$ //$NON-NLS-2$
	    	        if(split.length >= 3 && m.isInt(split[2]))
	    	        {
	    	        	int n = 1; //For the page number
	    	        	int n2 = Integer.valueOf(split[2]);
	    	        	if(n2 > 1)
	    	        	{
	    	        		//Figure out the 'page' here
	    	        		n = 10;
	    	        		n = n * (n2-1);
	    	        	}
	    	        	//If a page number is specified
	    	        	HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT "+lowercase+", user_id FROM " //$NON-NLS-1$ //$NON-NLS-2$
    	    					+LoadProperties.MySQLtablePrefix+"skills WHERE "+lowercase+" > 0 ORDER BY `"+LoadProperties.MySQLtablePrefix+"skills`.`"+lowercase+"` DESC "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	    	        	
	    	        	for(int i=n;i<=n+10;i++)
    	    			{
	    	        		if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    					break;
	    	        		HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    	    				player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0)); //$NON-NLS-1$ //$NON-NLS-2$
    	    			}
    	        		return;
	    	        }
	    	        //If no page number is specified
	    	        HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT "+lowercase+", user_id FROM " //$NON-NLS-1$ //$NON-NLS-2$
	    					+LoadProperties.MySQLtablePrefix+"skills WHERE "+lowercase+" > 0 ORDER BY `"+LoadProperties.MySQLtablePrefix+"skills`.`"+lowercase+"` DESC "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	    	        for(int i=1;i<=10;i++) //i<=userslist.size()
	    			{
	    	        	if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    					break;
	    				HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    				player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0)); //$NON-NLS-1$ //$NON-NLS-2$
	    			}
	    	        return;
    			}
    			if(split.length >= 1)
    			{
    				player.sendMessage(Messages.getString("mcPlayerListener.PowerLevelLeaderboard")); //$NON-NLS-1$
	    			if(split.length >= 2 && m.isInt(split[1]))
	    	        {
	    	        	int n = 1; //For the page number
	    	        	int n2 = Integer.valueOf(split[1]);
	    	        	if(n2 > 1)
	    	        	{
	    	        		//Figure out the 'page' here
	    	        		n = 10;
	    	        		n = n * (n2-1);
	    	        	}
	    	        	//If a page number is specified
	    	        	HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT "+powerlevel+", user_id FROM " //$NON-NLS-1$ //$NON-NLS-2$
		    					+LoadProperties.MySQLtablePrefix+"skills WHERE "+powerlevel+" > 0 ORDER BY taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics DESC "); //$NON-NLS-1$ //$NON-NLS-2$
	    	        	for(int i=n;i<=n+10;i++)
    	    			{
	    	        		if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    					break;
	    	        		HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    	    				player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0)); //$NON-NLS-1$ //$NON-NLS-2$
    	    			}
    	        		return;
	    	        }
	    			HashMap<Integer, ArrayList<String>> userslist = mcMMO.database.Read("SELECT taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics, user_id FROM " //$NON-NLS-1$
	    					+LoadProperties.MySQLtablePrefix+"skills WHERE "+powerlevel+" > 0 ORDER BY taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics DESC "); //$NON-NLS-1$ //$NON-NLS-2$
	    			for(int i=1;i<=10;i++)
	    			{
	    				if (i > userslist.size() || mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'") == null) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    					break;
	    				HashMap<Integer, ArrayList<String>> username =  mcMMO.database.Read("SELECT user FROM "+LoadProperties.MySQLtablePrefix+"users WHERE id = '" + Integer.valueOf(userslist.get(i).get(1)) + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    				player.sendMessage(String.valueOf(i)+". "+ChatColor.GREEN+userslist.get(i).get(0)+" - "+ChatColor.WHITE+username.get(1).get(0)); //$NON-NLS-1$ //$NON-NLS-2$
	    				//System.out.println(username.get(1).get(0));
	    				//System.out.println("Mining : " + userslist.get(i).get(0) + ", User id : " + userslist.get(i).get(1));
	    			}
    			}
    		}
    	}
    	
		if(split[0].equalsIgnoreCase("/"+LoadProperties.mcrefresh)){ //$NON-NLS-1$
			event.setCancelled(true);
    		if(!mcPermissions.getInstance().mcrefresh(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(split.length >= 2 && isPlayer(split[1])){
    			player.sendMessage("You have refreshed "+split[1]+"'s cooldowns!"); //$NON-NLS-1$ //$NON-NLS-2$
    			player = getPlayer(split[1]);
    		}
			/*
			 * PREP MODES
			 */
    		PP = Users.getProfile(player);
    		PP.setRecentlyHurt((long) 0);
    		PP.setHoePreparationMode(false);
    		PP.setAxePreparationMode(false);
    		PP.setFistsPreparationMode(false);
    		PP.setSwordsPreparationMode(false);
    		PP.setPickaxePreparationMode(false);
    		/*
    		 * GREEN TERRA
    		 */
    		PP.setGreenTerraMode(false);
    		PP.setGreenTerraDeactivatedTimeStamp((long) 0);
    		
    		/*
    		 * GIGA DRILL BREAKER
    		 */
    		PP.setGigaDrillBreakerMode(false);
    		PP.setGigaDrillBreakerDeactivatedTimeStamp((long) 0);
    		/*
    		 * SERRATED STRIKE
    		 */
    		PP.setSerratedStrikesMode(false);
    		PP.setSerratedStrikesDeactivatedTimeStamp((long) 0);
    		/*
    		 * SUPER BREAKER
    		 */
    		PP.setSuperBreakerMode(false);
    		PP.setSuperBreakerDeactivatedTimeStamp((long) 0);
    		/*
    		 * TREE FELLER
    		 */
    		PP.setTreeFellerMode(false);
    		PP.setTreeFellerDeactivatedTimeStamp((long) 0);
    		/*
    		 * BERSERK
    		 */
    		PP.setBerserkMode(false);
    		PP.setBerserkDeactivatedTimeStamp((long)0);
    		
    		player.sendMessage(Messages.getString("mcPlayerListener.AbilitiesRefreshed")); //$NON-NLS-1$
    	}
    	/*
    	 * GODMODE COMMAND
    	 */
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+LoadProperties.mcgod)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mcgod(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(Config.getInstance().isGodModeToggled(playerName)){
    			player.sendMessage(Messages.getString("mcPlayerListener.GodModeDisabled")); //$NON-NLS-1$
    			Config.getInstance().toggleGodMode(playerName);
    		} else {
    			player.sendMessage(Messages.getString("mcPlayerListener.GodModeEnabled")); //$NON-NLS-1$
    			Config.getInstance().toggleGodMode(playerName);
    		}
    	}
    	if(LoadProperties.enableMySpawn && mcPermissions.getInstance().mySpawn(player) && split[0].equalsIgnoreCase("/"+LoadProperties.clearmyspawn)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		double x = plugin.getServer().getWorlds().get(0).getSpawnLocation().getX();
    		double y = plugin.getServer().getWorlds().get(0).getSpawnLocation().getY();
    		double z = plugin.getServer().getWorlds().get(0).getSpawnLocation().getZ();
    		String worldname = plugin.getServer().getWorlds().get(0).getName();
    		PP.setMySpawn(x, y, z, worldname);
    		player.sendMessage(Messages.getString("mcPlayerListener.MyspawnCleared")); //$NON-NLS-1$
    	}
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+LoadProperties.mmoedit)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mmoedit(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(split.length < 3){
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.mmoedit+" playername skillname newvalue"); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(split.length == 4){
    			if(isPlayer(split[1]) && m.isInt(split[3]) && Skills.isSkill(split[2])){
    				int newvalue = Integer.valueOf(split[3]);
    				Users.getProfile(getPlayer(split[1])).modifyskill(newvalue, split[2]);
    				player.sendMessage(ChatColor.RED+split[2]+" has been modified."); //$NON-NLS-1$
    			}
    		}
    		else if(split.length == 3){
    			if(m.isInt(split[2]) && Skills.isSkill(split[1])){
    				int newvalue = Integer.valueOf(split[2]);
    				PP.modifyskill(newvalue, split[1]);
    				player.sendMessage(ChatColor.RED+split[1]+" has been modified."); //$NON-NLS-1$
    			}
    		} else {
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.mmoedit+" playername skillname newvalue"); //$NON-NLS-1$ //$NON-NLS-2$
    		}
    	}
    	/*
    	 * ADD EXPERIENCE COMMAND
    	 */
    	if(mcPermissions.permissionsEnabled && split[0].equalsIgnoreCase("/"+LoadProperties.addxp)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mmoedit(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(split.length < 3){
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.addxp+" playername skillname xp"); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(split.length == 4){
    			if(isPlayer(split[1]) && m.isInt(split[3]) && Skills.isSkill(split[2])){
    				int newvalue = Integer.valueOf(split[3]);
    				Users.getProfile(getPlayer(split[1])).addXP(split[2], newvalue);
    				getPlayer(split[1]).sendMessage(ChatColor.GREEN+"Experience granted!"); //$NON-NLS-1$
    				player.sendMessage(ChatColor.RED+split[2]+" has been modified."); //$NON-NLS-1$
    				Skills.XpCheck(getPlayer(split[1]));
    			}
    		}
    		else if(split.length == 3 && m.isInt(split[2]) && Skills.isSkill(split[1])){
    				int newvalue = Integer.valueOf(split[2]);
    				Users.getProfile(player).addXP(split[1], newvalue);
    				player.sendMessage(ChatColor.RED+split[1]+" has been modified."); //$NON-NLS-1$
    		} else {
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.addxp+" playername skillname xp"); //$NON-NLS-1$ //$NON-NLS-2$
    		}
    	}
    	
    	if(PP != null && PP.inParty() && split[0].equalsIgnoreCase("/"+LoadProperties.ptp)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().partyTeleport(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.ptp+" <playername>"); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(!isPlayer(split[1])){
    			player.sendMessage("That is not a valid player"); //$NON-NLS-1$
    		}
    		if(isPlayer(split[1])){
    			Player target = getPlayer(split[1]);
    			PlayerProfile PPt = Users.getProfile(target);
	        	if(PP.getParty().equals(PPt.getParty())){
	        		player.teleport(target);
	        		player.sendMessage(ChatColor.GREEN+"You have teleported to "+target.getName()); //$NON-NLS-1$
	        		target.sendMessage(ChatColor.GREEN+player.getName() + " has teleported to you."); //$NON-NLS-1$
	        	}
    		}
    	}
    	/*
    	 * WHOIS COMMAND
    	 */
    	if((player.isOp() || mcPermissions.getInstance().whois(player)) && split[0].equalsIgnoreCase("/"+LoadProperties.whois)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED + "Proper usage is /"+LoadProperties.whois+" <playername>"); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		//if split[1] is a player
    		if(isPlayer(split[1])){
    		Player target = getPlayer(split[1]);
    		PlayerProfile PPt = Users.getProfile(target);
    		double x,y,z;
    		x = target.getLocation().getX();
    		y = target.getLocation().getY();
    		z = target.getLocation().getZ();
    		player.sendMessage(ChatColor.GREEN + "~~WHOIS RESULTS~~"); //$NON-NLS-1$
    		player.sendMessage(target.getName());
    		if(PPt.inParty())
    		player.sendMessage("Party: "+PPt.getParty()); //$NON-NLS-1$
    		player.sendMessage("Health: "+target.getHealth()+ChatColor.GRAY+" (20 is full health)"); //$NON-NLS-1$ //$NON-NLS-2$
    		player.sendMessage("OP: " + target.isOp()); //$NON-NLS-1$
    		player.sendMessage(ChatColor.GREEN+"MMO Stats for "+ChatColor.YELLOW+target.getName()); //$NON-NLS-1$
    		if(mcPermissions.getInstance().taming(target))
        		player.sendMessage(Messages.getString("mcPlayerListener.TamingSkill") + ChatColor.GREEN + PPt.getTaming()+ChatColor.DARK_AQUA  //$NON-NLS-1$
        				+ " XP("+PPt.getTamingXP() //$NON-NLS-1$
        				+"/"+PPt.getXpToLevel("taming")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().mining(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.MiningSkill") + PPt.getMining()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getMiningXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("mining")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().repair(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.RepairSkill") + ChatColor.GREEN + PPt.getRepair()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getRepairXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("repair")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().woodcutting(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.WoodcuttingSkill") + ChatColor.GREEN + PPt.getWoodCutting()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getWoodCuttingXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("woodcutting")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().unarmed(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.UnarmedSkill") + ChatColor.GREEN + PPt.getUnarmed()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getUnarmedXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("unarmed")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().herbalism(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.HerbalismSkill") + ChatColor.GREEN +  PPt.getHerbalism()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getHerbalismXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("herbalism")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().excavation(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.ExcavationSkill") + ChatColor.GREEN +  PPt.getExcavation()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getExcavationXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("excavation")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().archery(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.ArcherySkill") + ChatColor.GREEN + PPt.getArchery()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getArcheryXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("archery")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().swords(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.SwordsSkill") + ChatColor.GREEN + PPt.getSwords()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getSwordsXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("swords")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().axes(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.AxesSkill") + ChatColor.GREEN + PPt.getAxes()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getAxesXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("axes")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().acrobatics(target))
    		player.sendMessage(Messages.getString("mcPlayerListener.AcrobaticsSkill") + ChatColor.GREEN + PPt.getAcrobatics()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PPt.getAcrobaticsXP() //$NON-NLS-1$
    				+"/"+PPt.getXpToLevel("acrobatics")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		player.sendMessage(Messages.getString("mcPlayerListener.PowerLevel") +ChatColor.GREEN+(m.getPowerLevel(target))); //$NON-NLS-1$
    		player.sendMessage(ChatColor.GREEN+"~~COORDINATES~~"); //$NON-NLS-1$
    		player.sendMessage("X: "+x); //$NON-NLS-1$
    		player.sendMessage("Y: "+y); //$NON-NLS-1$
    		player.sendMessage("Z: "+z); //$NON-NLS-1$
    		}
    	}
    	/*
    	 * STATS COMMAND
    	 */
    	if(split[0].equalsIgnoreCase("/"+LoadProperties.stats)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		player.sendMessage(Messages.getString("mcPlayerListener.YourStats")); //$NON-NLS-1$
    		if(mcPermissions.getInstance().permissionsEnabled)
    			player.sendMessage(Messages.getString("mcPlayerListener.NoSkillNote")); //$NON-NLS-1$
    		
    		if(mcPermissions.getInstance().taming(player))
        		player.sendMessage(Messages.getString("mcPlayerListener.TamingSkill") + ChatColor.GREEN + PP.getTaming()+ChatColor.DARK_AQUA  //$NON-NLS-1$
        				+ " XP("+PP.getTamingXP() //$NON-NLS-1$
        				+"/"+PP.getXpToLevel("taming")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().mining(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.MiningSkill") + ChatColor.GREEN + PP.getMining()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getMiningXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("mining")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().repair(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.RepairSkill")+ ChatColor.GREEN + PP.getRepair()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getRepairXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("repair")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().woodcutting(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.WoodcuttingSkill")+ ChatColor.GREEN + PP.getWoodCutting()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getWoodCuttingXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("woodcutting")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().unarmed(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.UnarmedSkill") + ChatColor.GREEN + PP.getUnarmed()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getUnarmedXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("unarmed")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().herbalism(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.HerbalismSkill")+ ChatColor.GREEN +  PP.getHerbalism()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getHerbalismXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("herbalism")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().excavation(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.ExcavationSkill")+ ChatColor.GREEN +  PP.getExcavation()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getExcavationXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("excavation")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().archery(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.ArcherySkill") + ChatColor.GREEN + PP.getArchery()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getArcheryXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("archery")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().swords(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.SwordsSkill") + ChatColor.GREEN + PP.getSwords()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getSwordsXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("swords")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().axes(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.AxesSkill") + ChatColor.GREEN + PP.getAxes()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getAxesXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("axes")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		if(mcPermissions.getInstance().acrobatics(player))
    		player.sendMessage(Messages.getString("mcPlayerListener.AcrobaticsSkill") + ChatColor.GREEN + PP.getAcrobatics()+ChatColor.DARK_AQUA  //$NON-NLS-1$
    				+ " XP("+PP.getAcrobaticsXP() //$NON-NLS-1$
    				+"/"+PP.getXpToLevel("acrobatics")+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		player.sendMessage(Messages.getString("mcPlayerListener.PowerLevel")+ChatColor.GREEN+(m.getPowerLevel(player))); //$NON-NLS-1$
    	}
    	//Invite Command
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/"+LoadProperties.invite)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(!PP.inParty()){
    			player.sendMessage(Messages.getString("mcPlayerListener.NotInParty")); //$NON-NLS-1$
    			return;
    		}
    		if(split.length < 2){
    			player.sendMessage(ChatColor.RED+"Usage is /"+LoadProperties.invite+" <playername>"); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(PP.inParty() && split.length >= 2 && isPlayer(split[1])){
    			Player target = getPlayer(split[1]);
    			PlayerProfile PPt = Users.getProfile(target);
    			PPt.modifyInvite(PP.getParty());
    			player.sendMessage(Messages.getString("mcPlayerListener.InviteSuccess")); //$NON-NLS-1$
    			//target.sendMessage(ChatColor.RED+"ALERT: "+ChatColor.GREEN+"You have received a party invite for "+PPt.getInvite()+" from "+player.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    			target.sendMessage(Messages.getString("mcPlayerListener.ReceivedInvite1", new Object[] {PPt.getInvite(), player.getName()}));
    			//target.sendMessage(ChatColor.YELLOW+"Type "+ChatColor.GREEN+"/"+LoadProperties.accept+ChatColor.YELLOW+" to accept the invite"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    			target.sendMessage(Messages.getString("mcPlayerListener.ReceivedInvite2", new Object[] {LoadProperties.accept}));
    		}
    	}
    	//Accept invite
    	if(mcPermissions.getInstance().party(player) && split[0].equalsIgnoreCase("/"+LoadProperties.accept)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(PP.hasPartyInvite()){
    			if(PP.inParty()){
    				Party.getInstance().informPartyMembersQuit(player, getPlayersOnline());
    			}
    			PP.acceptInvite();
    			Party.getInstance().informPartyMembers(player, getPlayersOnline());
    			player.sendMessage(Messages.getString("mcPlayerListener.InviteAccepted", new Object[]{PP.getParty()})); //$NON-NLS-1$ //$NON-NLS-2$
    		} else {
    			player.sendMessage(Messages.getString("mcPlayerListener.NoInvites")); //$NON-NLS-1$
    		}
    	}
    	//Party command
    	if(split[0].equalsIgnoreCase("/"+LoadProperties.party)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().party(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(split.length == 1 && !PP.inParty()){
    			player.sendMessage("Proper usage is "+"/"+LoadProperties.party+" <name> or 'q' to quit"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    			return;
    		}
    		if(split.length == 1 && PP.inParty()){
            	String tempList = ""; //$NON-NLS-1$
            	int x = 0;
                for(Player p : plugin.getServer().getOnlinePlayers())
                {
                	if(PP.getParty().equals(Users.getProfile(p).getParty())){
	                	if(p != null && x+1 >= Party.getInstance().partyCount(player, getPlayersOnline())){
	                		tempList+= p.getName();
	                		x++;
	                	}
	                	if(p != null && x < Party.getInstance().partyCount(player, getPlayersOnline())){
	                		tempList+= p.getName() +", "; //$NON-NLS-1$
	                		x++;
	                	}
                	}
                }
                player.sendMessage(Messages.getString("mcPlayerListener.YouAreInParty", new Object[] {PP.getParty()}));
                player.sendMessage(Messages.getString("mcPlayerListener.PartyMembers")+" ("+ChatColor.WHITE+tempList+ChatColor.GREEN+")"); //$NON-NLS-1$ //$NON-NLS-2$
    		}
    		if(split.length > 1 && split[1].equals("q") && PP.inParty()){ //$NON-NLS-1$
    			Party.getInstance().informPartyMembersQuit(player, getPlayersOnline());
    			PP.removeParty();
    			player.sendMessage(Messages.getString("mcPlayerListener.LeftParty")); //$NON-NLS-1$
    			return;
    		}
    		if(split.length >= 2)
    		{
	    		if(PP.inParty())
	    			Party.getInstance().informPartyMembersQuit(player, getPlayersOnline());
		    	PP.setParty(split[1]);
		    	player.sendMessage(Messages.getString("mcPlayerListener.JoinedParty", new Object[] {split[1]}));
		    	Party.getInstance().informPartyMembers(player, getPlayersOnline());
	    	}
    	}
    	if(split[0].equalsIgnoreCase("/p")){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().party(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(Config.getInstance().isAdminToggled(player.getName()))
    		Config.getInstance().toggleAdminChat(playerName);
    		Config.getInstance().togglePartyChat(playerName);
    		if(Config.getInstance().isPartyToggled(playerName)){
    			//player.sendMessage(ChatColor.GREEN + "Party Chat Toggled On"); //$NON-NLS-1$
    			player.sendMessage(Messages.getString("mcPlayerListener.PartyChatOn"));
    		} else {
    			//player.sendMessage(ChatColor.GREEN + "Party Chat Toggled " + ChatColor.RED + "Off"); //$NON-NLS-1$ //$NON-NLS-2$
    			player.sendMessage(Messages.getString("mcPlayerListener.PartyChatOff"));
    		}
    	}
    	if(split[0].equalsIgnoreCase("/a") && (player.isOp() || mcPermissions.getInstance().adminChat(player))){ //$NON-NLS-1$
    		if(!mcPermissions.getInstance().adminChat(player) && !player.isOp()){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		event.setCancelled(true);
    		if(Config.getInstance().isPartyToggled(player.getName()))
    			Config.getInstance().togglePartyChat(playerName);
    		Config.getInstance().toggleAdminChat(playerName);
    		if(Config.getInstance().isAdminToggled(playerName)){
    			player.sendMessage(Messages.getString("mcPlayerListener.AdminChatOn"));
    			//player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.GREEN + "On"); //$NON-NLS-1$ //$NON-NLS-2$
    		} else {
    			player.sendMessage(Messages.getString("mcPlayerListener.AdminChatOff"));
    			//player.sendMessage(ChatColor.AQUA + "Admin chat toggled " + ChatColor.RED + "Off"); //$NON-NLS-1$ //$NON-NLS-2$
    		}
    	}
    	/*
    	 * MYSPAWN
    	 */
    	if(LoadProperties.enableMySpawn && split[0].equalsIgnoreCase("/"+LoadProperties.myspawn)){ //$NON-NLS-1$
    		event.setCancelled(true);
    		if(!mcPermissions.getInstance().mySpawn(player)){
    			player.sendMessage(ChatColor.YELLOW+"[mcMMO]"+ChatColor.DARK_RED +Messages.getString("mcPlayerListener.NoPermission")); //$NON-NLS-1$ //$NON-NLS-2$
    			return;
    		}
    		if(System.currentTimeMillis() < PP.getMySpawnATS() + 3600000){
    			long x = System.currentTimeMillis();
    			int seconds = 0;
    			int minutes = 0;
    			while(x < PP.getMySpawnATS() + 3600000){
    				x+=1000;
    				seconds++;
    			}
    			while(seconds >= 60){
    				seconds-=60;
    				minutes++;
    			}
    			player.sendMessage(Messages.getString("mcPlayerListener.MyspawnTimeNotice", new Object[] {minutes, seconds})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    			return;
    		}
    		PP.setMySpawnATS(System.currentTimeMillis());
    		if(PP.getMySpawn(player) != null){
	    		player.setHealth(20);
	    		Location mySpawn = PP.getMySpawn(player);
	    		//player.sendMessage("MMO DEBUG CODE 1");
	    		if(PP.getMySpawnWorld(plugin) != null && !PP.getMySpawnWorld(plugin).equals("")){ //$NON-NLS-1$
	    			mySpawn.setWorld(plugin.getServer().getWorld(PP.getMySpawnWorld(plugin)));
	    			//player.sendMessage("MMO DEBUG CODE 2");
	    			} else {
	    				//player.sendMessage("MMO DEBUG CODE 5");
	    				mySpawn.setWorld(plugin.getServer().getWorlds().get(0));
	    		}
	    		player.teleport(mySpawn); //It's done twice because teleporting from one world to another is weird
	    		player.teleport(mySpawn);
    		} else {
    			player.sendMessage(Messages.getString("mcPlayerListener.MyspawnNotExist")); //$NON-NLS-1$
    		}
    	}
    }
 
    
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		PlayerProfile PP = Users.getProfile(player);
    	String x = ChatColor.GREEN + "(" + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ") "; //$NON-NLS-1$ //$NON-NLS-2$
    	String y = ChatColor.AQUA + "{" + ChatColor.WHITE + player.getName() + ChatColor.AQUA + "} "; //$NON-NLS-1$ //$NON-NLS-2$
    	if(Config.getInstance().isPartyToggled(player.getName())){
    		event.setCancelled(true);
    		log.log(Level.INFO, "[P]("+PP.getParty()+")"+"<"+player.getName()+"> "+event.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if(Users.getProfile(herp).inParty()){
    			if(Party.getInstance().inSameParty(herp, player)){
    				herp.sendMessage(x+event.getMessage());
    			}
    			}
    		}
    		return;
    	}
    	if((player.isOp() || mcPermissions.getInstance().adminChat(player)) && Config.getInstance().isAdminToggled(player.getName())){
    		log.log(Level.INFO, "[A]"+"<"+player.getName()+"> "+event.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		event.setCancelled(true);
    		for(Player herp : plugin.getServer().getOnlinePlayers()){
    			if((herp.isOp() || mcPermissions.getInstance().adminChat(herp))){
    				herp.sendMessage(y+event.getMessage());
    			}
    		}
    		return;
    	}    	
    	/*
    	 * Remove from normal chat if toggled 
    	for(Player z : event.getRecipients()){
    		if(Users.getProfile(z.getName()).getPartyChatOnlyToggle() == true)
    			event.getRecipients().remove(z);
    	}
    	*/
    	}
}