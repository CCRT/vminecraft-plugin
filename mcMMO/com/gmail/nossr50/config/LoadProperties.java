package com.gmail.nossr50.config;

import java.io.File;
import java.util.List;
import org.bukkit.util.config.Configuration;

import com.gmail.nossr50.mcMMO;

public class LoadProperties {
	public static Boolean enableMotd, enableMySpawn, enableRegen, enableCobbleToMossy, useMySQL, cocoabeans, archeryFireRateLimit, mushrooms, toolsLoseDurabilityFromAbilities, pvpxp, miningrequirespickaxe, woodcuttingrequiresaxe, eggs, apples, cake, music, diamond, glowstone, slowsand, sulphur, netherrack, bones, coal, clay, anvilmessages;
	public static String MySQLtablePrefix, MySQLuserName, MySQLserverName, MySQLdbName, MySQLdbPass, mctop, addxp, mcability, mcmmo, mcc, mcrefresh, mcgod, stats, mmoedit, ptp, party, myspawn, setmyspawn, whois, invite, accept, clearmyspawn, nWood, nStone, nIron, nGold, nDiamond, locale;
	public static int mbones, msulphur, mslowsand, mmushroom2, mglowstone2, mmusic, mdiamond2, mbase, mapple, meggs, mcake, mpine, mbirch, mspruce, mcactus, mmushroom, mflower, msugar, mpumpkin, mwheat, mgold, mdiamond, miron, mredstone, mlapus, mobsidian, mnetherrack, mglowstone, mcoal, mstone, MySQLport, xpGainMultiplier, superBreakerCooldown, greenTerraCooldown, gigaDrillBreakerCooldown, treeFellerCooldown, berserkCooldown, serratedStrikeCooldown, skullSplitterCooldown, abilityDurabilityLoss, feathersConsumedByChimaeraWing, pvpxprewardmodifier, repairdiamondlevel, globalxpmodifier, tamingxpmodifier, miningxpmodifier, repairxpmodifier, woodcuttingxpmodifier, unarmedxpmodifier, herbalismxpmodifier, excavationxpmodifier, archeryxpmodifier, swordsxpmodifier, axesxpmodifier, acrobaticsxpmodifier, rWood, rStone, rIron, rGold, rDiamond;
	private static mcMMO plugin;
	
	public String directory = "plugins/mcMMO/"; 
	File file = new File(directory + File.separator + "config.yml");
	
	public LoadProperties() 
	{
	        
	}
		public void configCheck(){
	        new File(directory).mkdir();


	        if(!file.exists()){
	            try {
	                file.createNewFile();
	                addDefaults();

	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        } else {
	            loadkeys();
	        }
	    }
	    private void write(String root, Object x){
	        Configuration config = load();
	        config.setProperty(root, x);
	        config.save();
	    }
	    private Boolean readBoolean(String root){
	        Configuration config = load();
	        return config.getBoolean(root, true);
	    }
	    private Integer readInteger(String root){
	    	Configuration config = load();
	    	return config.getInt(root, 0);
	    }

	    private Double readDouble(String root){
	        Configuration config = load();
	        return config.getDouble(root, 0);
	    }
	    private List<String> readStringList(String root){
	        Configuration config = load();
	        return config.getKeys(root);
	    }
	    private String readString(String root){
	        Configuration config = load();
	        return config.getString(root);
	    }
	    private Configuration load(){

	        try {
	            Configuration config = new Configuration(file);
	            config.load();
	            return config;

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	    private void addDefaults(){
	        plugin.log.info("Generating Config File...");  	
	    	
	        //Put in defaults
	        write("MySQL.Enabled", false);
	        write("MySQL.Server.Address", "localhost");
	        write("MySQL.Server.Port", 3306);
        	write("MySQL.Database.Name", "DataBaseName");
        	write("MySQL.Database.User.Name", "UserName");
        	write("MySQL.Database.TablePrefix", "_mcmmo");
        	write("MySQL.Database.User.Password", "UserPassword");
        	
        	write("General.Locale", "en_us");
	    	write("General.MOTD.Enabled", true);
	    	write("General.MySpawn.Enabled", true);
	    	write("General.HP_Regeneration.Enabled", true);
	    	
	    	write("Items.Chimaera_Wing.Feather_Cost", 10);
	    	
	    	write("XP.PVP.Rewards", true);
	    	write("XP.Gains.Multiplier.PVP", 1);
	    	write("XP.Gains.Multiplier.Global", 1);
	    	write("XP.Formula.Multiplier.Global", 1);
	    	write("XP.Formula.Multiplier.Taming", 2);
	    	write("XP.Formula.Multiplier.Mining", 2);
	    	write("XP.Formula.Multiplier.Repair", 2);
	    	write("XP.Formula.Multiplier.Woodcutting", 2);
	    	write("XP.Formula.Multiplier.Unarmed", 2);
	    	write("XP.Formula.Multiplier.Herbalism", 2);
	    	write("XP.Formula.Multiplier.Excavation", 2);
	    	write("XP.Formula.Multiplier.Swords", 2);
	    	write("XP.Formula.Multiplier.Archery", 2);
	    	write("XP.Formula.Multiplier.Axes", 2);
	    	write("XP.Formula.Multiplier.Acrobatics", 2);
	    	write("XP.Mining.Gold", 35);
	    	write("XP.Mining.Diamond", 75);
	    	write("XP.Mining.Iron", 25);
	    	write("XP.Mining.Redstone", 15);
	    	write("XP.Mining.Lapus", 40);
	    	write("XP.Mining.Obsidian", 15);
	    	write("XP.Mining.Netherrack", 3);
	    	write("XP.Mining.Glowstone", 3);
	    	write("XP.Mining.Coal", 10);
	    	write("XP.Mining.Stone", 3);
	    	write("XP.Herbalism.Sugar_Cane", 3);
	    	write("XP.Herbalism.Cactus", 3);
	    	write("XP.Herbalism.Pumpkin", 55);
	    	write("XP.Herbalism.Flowers", 10);
	    	write("XP.Herbalism.Mushrooms", 15);
	    	write("XP.Woodcutting.Pine", 9);
	    	write("XP.Woodcutting.Birch", 7);
	    	write("XP.Woodcutting.Spruce", 8);
	    	write("XP.Excavation.Base", 4);
	    	write("XP.Excavation.Mushroom", 8);
	    	write("XP.Excavation.Sulphur", 3);
	    	write("XP.Excavation.Slowsand", 8);
	    	write("XP.Excavation.Glowstone", 8);
	    	write("XP.Excavation.Music", 300);
	    	write("XP.Excavation.Bones", 3);
	    	write("XP.Excavation.Diamond", 100);
	    	write("XP.Excavation.Apple", 10);
	    	write("XP.Excavation.Eggs", 10);
	    	write("XP.Excavation.Cake", 300);
	    	
	    	write("Excavation.Drops.Cocoa_Beans", true);
	    	write("Excavation.Drops.Mushrooms", true);
	    	write("Excavation.Drops.Glowstone", true);
	    	write("Excavation.Drops.Eggs", true);
	    	write("Excavation.Drops.Apples", true);
	    	write("Excavation.Drops.Cake", true);
	    	write("Excavation.Drops.Music", true);
	    	write("Excavation.Drops.Diamond", true);
	    	write("Excavation.Drops.Slowsand", true);
	    	write("Excavation.Drops.Sulphur", true);
	    	write("Excavation.Drops.Netherrack", true);
	    	write("Excavation.Drops.Bones", true);
	    	
	    	write("Commands.mctop", "mctop");
	    	write("Commands.addxp", "addxp");
	    	write("Commands.mcability", "mcability");
	    	write("Commands.mcrefresh", "mcrefresh");
	    	write("Commands.mcmmo", "mcmmo");
	    	write("Commands.mcc", "mcc");
	    	write("Commands.mcgod", "mcgod");
	    	write("Commands.stats", "stats");
	    	write("Commands.mmoedit", "mmoedit");
	    	write("Commands.ptp", "ptp");
	    	write("Commands.party", "party");
	    	write("Commands.myspawn", "myspawn");
	    	write("Commands.setmyspawn", "setmyspawn");
	    	write("Commands.whois", "whois");
	    	write("Commands.invite", "invite");
	    	write("Commands.accept", "accept");
	    	write("Commands.clearmyspawn", "clearmyspawn");
	    	
	    	write("Abilities.Tools.Durability_Loss_Enabled", true);
	    	write("Abilities.Tools.Durability_Loss", 2);
	    	write("Abilities.Cooldowns.Green_Terra", 240);
	    	write("Abilities.Cooldowns.Super_Breaker", 240);
	    	write("Abilities.Cooldowns.Giga_Drill_Breaker", 240);
	    	write("Abilities.Cooldowns.Tree_Feller", 240);
	    	write("Abilities.Cooldowns.Berserk", 240);
	    	write("Abilities.Cooldowns.Serrated_Strikes", 240);
	    	write("Abilities.Cooldowns.Skull_Splitter", 240);
	    	
	    	write("Skills.Repair.Anvil_Messages", true);
	    	write("Skills.Repair.Gold.ID", 266);
	    	write("Skills.Repair.Gold.Name", "Gold Bars");
	    	write("Skills.Repair.Stone.ID", 4);
	    	write("Skills.Repair.Stone.Name", "Cobblestone");
	    	write("Skills.Repair.Wood.ID", 5);
	    	write("Skills.Repair.Wood.Name", "Wood Planks");
	    	write("Skills.Repair.Diamond.ID", 264);
	    	write("Skills.Repair.Diamond.Name", "Diamond Ore");
	    	write("Skills.Repair.Diamond.Level_Required", 50);
	    	write("Skills.Repair.Iron.ID", 265);
	    	write("Skills.Repair.Iron.Name", "Iron Bars");
	    	write("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true);
	    	write("Skills.Archery.Fire_Rate_Limiter", true);
	    	write("Skills.Mining.Requires_Pickaxe", true);
	    	write("Skills.Woodcutting.Requires_Axe", true);
	    	
	     loadkeys();
	    }
	    private void loadkeys(){
	        plugin.log.info("Loading Config File...");
	        
	        msulphur = readInteger("XP.Excavation.Sulphur");
	        mbones = readInteger("XP.Excavation.Bones");
	        mbase = readInteger("XP.Excavation.Base");
	        mmushroom2 = readInteger("XP.Excavation.Mushroom");
	    	mslowsand = readInteger("XP.Excavation.Slowsand");
	    	mglowstone2 = readInteger("XP.Excavation.Glowstone");
	    	mmusic = readInteger("XP.Excavation.Music");
	    	mdiamond2 = readInteger("XP.Excavation.Diamond");
	    	mapple = readInteger("XP.Excavation.Apple");
	    	meggs = readInteger("XP.Excavation.Eggs");
	    	mcake = readInteger("XP.Excavation.Cake");
	        
	        msugar = readInteger("XP.Herbalism.Sugar_Cane");
	    	mcactus = readInteger("XP.Herbalism.Cactus");
	    	mpumpkin = readInteger("XP.Herbalism.Pumpkin");
	    	mflower = readInteger("XP.Herbalism.Flowers");
	    	mmushroom = readInteger("XP.Herbalism.Mushrooms");
	    	
	    	mpine = readInteger("XP.Woodcutting.Pine");
	    	mbirch = readInteger("XP.Woodcutting.Birch");
	    	mspruce = readInteger("XP.Woodcutting.Spruce");
	        
	        mgold = readInteger("XP.Mining.Gold");
	        mdiamond = readInteger("XP.Mining.Diamond");
	        miron = readInteger("XP.Mining.Iron");
	        mredstone = readInteger("XP.Mining.Redstone");
	        mlapus = readInteger("XP.Mining.Lapus");
	        mobsidian = readInteger("XP.Mining.Obsidian");
	        mnetherrack = readInteger("XP.Mining.Netherrack");
	        mglowstone = readInteger("XP.Mining.Glowstone");
	        mcoal = readInteger("XP.Mining.Coal");
	        mstone = readInteger("XP.Mining.Sand");
	        
	        enableMotd = readBoolean("enableMOTD");
	        
	        greenTerraCooldown = readInteger("Abilities.Cooldowns.Green_Terra");
	    	superBreakerCooldown = readInteger("Abilities.Cooldowns.Super_Breaker");
	    	gigaDrillBreakerCooldown = readInteger("Abilities.Cooldowns.Giga_Drill_Breaker");
	    	treeFellerCooldown = readInteger("Abilities.Cooldowns.Tree_Feller");
	    	berserkCooldown = readInteger("Abilities.Cooldowns.Berserk");
	    	serratedStrikeCooldown = readInteger("Abilities.Cooldowns.Serrated_Strikes");
	    	skullSplitterCooldown = readInteger("Abilities.Cooldowns.Skull_Splitter");
	    	
	    	MySQLserverName = readString("MySQL.Server.Address");
	    	MySQLdbPass = readString("MySQL.Database.User.Password");
	    	MySQLdbName = readString("MySQL.Database.Name");
	    	MySQLuserName = readString("MySQL.Database.User.Name");
	    	MySQLtablePrefix = readString("MySQL.Database.TablePrefix");
	    	MySQLport = readInteger("MySQL.Server.Port");
	    	useMySQL = readBoolean("MySQL.Enabled");
	    	
	    	locale = readString("General.Locale");
	    	enableMotd = readBoolean("General.MOTD.Enabled");
	    	enableMySpawn = readBoolean("General.MySpawn.Enabled");
	    	enableRegen = readBoolean("General.HP_Regeneration.Enabled");
	    	
	    	enableCobbleToMossy = readBoolean("MySQL.Enabled");
	    	archeryFireRateLimit = readBoolean("MySQL.Enabled");
	    	
	    	xpGainMultiplier = readInteger("XP.Gains.Multiplier.Global");
	    	toolsLoseDurabilityFromAbilities = readBoolean("Abilities.Tools.Durability_Loss_Enabled");
	    	abilityDurabilityLoss = readInteger("Abilities.Tools.Durability_Loss");
	    	
	    	feathersConsumedByChimaeraWing = readInteger("Items.Chimaera_Wing.Feather_Cost");
	    	pvpxp = readBoolean("XP.PVP.Rewards");
	    	pvpxprewardmodifier = readInteger("XP.Gains.Multiplier.PVP");
	    	miningrequirespickaxe = readBoolean("Skills.Mining.Requires_Pickaxe");
	    	woodcuttingrequiresaxe = readBoolean("Skills.Woodcutting.Requires_Axe");
	    	repairdiamondlevel = readInteger("Skills.Repair.Diamond.Level_Required");

	    	globalxpmodifier = readInteger("XP.Formula.Multiplier.Global");
	    	tamingxpmodifier = readInteger("XP.Formula.Multiplier.Taming");
	    	miningxpmodifier = readInteger("XP.Formula.Multiplier.Mining");
	    	repairxpmodifier = readInteger("XP.Formula.Multiplier.Repair");
	    	woodcuttingxpmodifier = readInteger("XP.Formula.Multiplier.Woodcutting");
	    	unarmedxpmodifier = readInteger("XP.Formula.Multiplier.Unarmed");
	    	herbalismxpmodifier = readInteger("XP.Formula.Multiplier.Herbalism");
	    	excavationxpmodifier = readInteger("XP.Formula.Multiplier.Excavation");
	    	archeryxpmodifier = readInteger("XP.Formula.Multiplier.Archery");
	    	swordsxpmodifier = readInteger("XP.Formula.Multiplier.Swords");
	    	axesxpmodifier = readInteger("XP.Formula.Multiplier.Axes");
	    	acrobaticsxpmodifier = readInteger("XP.Formula.Multiplier.Acrobatics");

	    	anvilmessages = readBoolean("Skills.Repair.Anvil_Messages");
	    	
	        rGold =  readInteger("Skills.Repair.Gold.ID");
	        nGold =  readString("Skills.Repair.Gold.Name");      
	        rStone =  readInteger("Skills.Repair.Stone.ID");
	        nStone =  readString("Skills.Repair.Stone.Name");     
	        rWood =  readInteger("Skills.Repair.Wood.ID");
	        nWood =  readString("Skills.Repair.Wood.Name");        
	        rDiamond =   readInteger("Skills.Repair.Diamond.ID");
	        nDiamond =  readString("Skills.Repair.Diamond.Name");          
	        rIron =   readInteger("Skills.Repair.Iron.ID");
	        nIron =  readString("Skills.Repair.Iron.Name");  

	    	cocoabeans = readBoolean("Excavation.Drops.Cocoa_Beans");
	    	mushrooms = readBoolean("Excavation.Drops.Mushrooms");
	    	glowstone = readBoolean("Excavation.Drops.Glowstone");
	    	eggs = readBoolean("Excavation.Drops.Eggs");
	    	apples = readBoolean("Excavation.Drops.Apples");
	    	cake = readBoolean("Excavation.Drops.Cake");
	    	music = readBoolean("Excavation.Drops.Music");
	    	diamond = readBoolean("Excavation.Drops.Diamond");
	    	slowsand = readBoolean("Excavation.Drops.Slowsand");
	    	sulphur = readBoolean("Excavation.Drops.Sulphur");
	    	netherrack = readBoolean("Excavation.Drops.Netherrack");
	    	bones = readBoolean("Excavation.Drops.Bones");
	    	
	    	mctop = readString("Commands.mctop");
	    	addxp = readString("Commands.addxp");
	    	mcability = readString("Commands.mcability");
	    	mcrefresh = readString("Commands.mcrefresh");
	    	mcmmo = readString("Commands.mcmmo");
	    	mcc = readString("Commands.mcc");
	    	mcgod = readString("Commands.mcgod");
	    	stats = readString("Commands.stats");
	    	mmoedit = readString("Commands.mmoedit");
	    	ptp = readString("Commands.ptp");
	    	party = readString("Commands.party");
	    	myspawn = readString("Commands.myspawn");
	    	setmyspawn = readString("Commands.setmyspawn");
	    	whois = readString("Commands.whois");
	    	invite = readString("Commands.invite");
	    	accept = readString("Commands.accept");
	    	clearmyspawn = readString("Commands.clearmyspawn");
	        }
	}

	/*
	public static void loadMain(){
    	String propertiesFile = mcMMO.maindirectory + "mcmmo.properties";
    	mcProperties properties = new mcProperties(propertiesFile);
    	properties.load();

    	greenTerraCooldown = properties.getInteger("greenTerraCooldown", 240);
    	superBreakerCooldown = properties.getInteger("superBreakerCooldown", 240);
    	gigaDrillBreakerCooldown = properties.getInteger("gigaDrillBreakerCooldown", 240);
    	treeFellerCooldown = properties.getInteger("treeFellerCooldown", 240);
    	berserkCooldown = properties.getInteger("berserkCooldown", 240);
    	serratedStrikeCooldown = properties.getInteger("serratedStrikeCooldown", 240);
    	skullSplitterCooldown = properties.getInteger("skullSplitterCooldown", 240);
    	
    	MySQLserverName = properties.getString("MySQLServer", "ipofserver");
    	MySQLdbPass = properties.getString("MySQLdbPass", "defaultdbpass");
    	MySQLdbName = properties.getString("MySQLdbName", "defaultdbname");
    	MySQLuserName = properties.getString("MySQLuserName", "defaultusername");
    	MySQLtablePrefix = properties.getString("MySQLTablePrefix", "mcmmo_");
    	MySQLport = properties.getInteger("MySQLport", 3306);
    	useMySQL = properties.getBoolean("UseMySQL", false);
    	
    	enableMotd = properties.getBoolean("enableMOTD", true);
    	enableMySpawn = properties.getBoolean("enableMySpawn", true);
    	enableRegen = properties.getBoolean("enableHpRegeneration", true);
    	enableCobbleToMossy = properties.getBoolean("enableGreenThumbCobbleToMossy", true);
    	archeryFireRateLimit = properties.getBoolean("archeryFireRateLimit", true);
    	xpGainMultiplier = properties.getInteger("xpGainMultiplier", 1);
    	toolsLoseDurabilityFromAbilities = properties.getBoolean("toolsLoseDurabilityFromAbilities", true);
    	abilityDurabilityLoss = properties.getInteger("abilityDurabilityLoss", 2);
    	feathersConsumedByChimaeraWing = properties.getInteger("feathersConsumedByChimaeraWing", 10);
    	pvpxp = properties.getBoolean("pvpGivesXP", true);
    	pvpxprewardmodifier = properties.getInteger("pvpXpRewardModifier", 1);
    	miningrequirespickaxe = properties.getBoolean("miningRequiresPickaxe", true);
    	woodcuttingrequiresaxe = properties.getBoolean("woodcuttingRequiresAxe", true);
    	repairdiamondlevel = properties.getInteger("repairDiamondLevel", 50);
    	locale = properties.getString("locale", "en_us");

    	globalxpmodifier = properties.getInteger("globalXpModifier", 1);
    	tamingxpmodifier = properties.getInteger("tamingXpModifier", 2);
    	miningxpmodifier = properties.getInteger("miningXpModifier", 2);
    	repairxpmodifier = properties.getInteger("repairXpModifier", 2);
    	woodcuttingxpmodifier = properties.getInteger("woodcuttingXpModifier", 2);
    	unarmedxpmodifier = properties.getInteger("unarmedXpModifier", 2);
    	herbalismxpmodifier = properties.getInteger("herbalismXpModifier", 2);
    	excavationxpmodifier = properties.getInteger("excavationXpModifier", 2);
    	archeryxpmodifier = properties.getInteger("archeryXpModifier", 2);
    	swordsxpmodifier = properties.getInteger("swordsXpModifier", 2);
    	axesxpmodifier = properties.getInteger("axesXpModifier", 2);
    	acrobaticsxpmodifier = properties.getInteger("acrobaticsXpModifier", 2);

    	anvilmessages = properties.getBoolean("anvilMessages", true);
    	
        rGold =  properties.getInteger("GoldRepairItemNumber", 266);
        nGold =  properties.getString("GoldItemRepairName", "Gold Bars");        
        rStone =  properties.getInteger("CobblestoneRepairItemNumber", 4);
        nStone =  properties.getString("CobblestoneItemRepairName", "Cobblestone");        
        rWood =  properties.getInteger("WoodRepairItemNumber", 5);
        nWood =  properties.getString("WoodItemRepairName", "Wood Planks");        
        rDiamond =   properties.getInteger("DiamondRepairItemNumber", 264);
        nDiamond =  properties.getString("DiamondItemRepairName", "Diamond Ore");        
        rIron =   properties.getInteger("IronRepairItemNumber", 265);
        nIron =  properties.getString("IronItemRepairName", "Iron Bars");

    	cocoabeans = properties.getBoolean("canExcavateCocoaBeans", true);
    	mushrooms = properties.getBoolean("canExcavateMushrooms", true);
    	glowstone = properties.getBoolean("canExcavateGlowstone", true);
    	eggs = properties.getBoolean("canExcavateEggs", true);
    	apples = properties.getBoolean("canExcavateApples", true);
    	cake = properties.getBoolean("canExcavateCake", true);
    	music = properties.getBoolean("canExcavateMusic", true);
    	diamond = properties.getBoolean("canExcavateDiamond", true);
    	slowsand = properties.getBoolean("canExcavateSlowSand", true);
    	sulphur = properties.getBoolean("canExcavateSulphur", true);
    	netherrack = properties.getBoolean("canExcavateNetherrack", true);
    	bones = properties.getBoolean("canExcavateBones", true);
    	
    	mctop = properties.getString("/mctop", "mctop");
    	addxp = properties.getString("/addxp", "addxp");
    	mcability = properties.getString("/mcability", "mcability");
    	mcrefresh = properties.getString("/mcrefresh", "mcrefresh");
    	mcitem = properties.getString("/mcitem", "mcitem");
    	mcmmo = properties.getString("/mcmmo", "mcmmo");
    	mcc = properties.getString("/mcc", "mcc");
    	mcgod = properties.getString("/mcgod", "mcgod");
    	stats = properties.getString("/stats", "stats");
    	mmoedit = properties.getString("/mmoedit", "mmoedit");
    	ptp = properties.getString("/ptp", "ptp");
    	party = properties.getString("/party", "party");
    	myspawn = properties.getString("/myspawn", "myspawn");
    	setmyspawn = properties.getString("/setmyspawn", "setmyspawn");
    	whois = properties.getString("/whois", "whois");
    	invite = properties.getString("/invite", "invite");
    	accept = properties.getString("/accept", "accept");
    	clearmyspawn = properties.getString("/clearmyspawn", "clearmyspawn");
    	properties.save("==McMMO Configuration==\r\nYou can turn off excavation loot tables by turning the option to false\r\nYou can customize mcMMOs command names by modifying them here as well\r\nThis is an early version of the configuration file, eventually you'll be able to customize messages from mcMMO and XP gains");
    	//herp derp
    	 * 
    	 */