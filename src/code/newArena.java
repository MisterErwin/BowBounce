package code; 

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import code.utils.LocUtils;
import code.utils.Utils;


public class newArena implements Listener{
	
	private String name;
	private String title;
	private String[] lore;
	private boolean enable = false;
	private HashMap<String, Boolean> players = new HashMap<String, Boolean>(); //player = hasarrow
	private boolean running = false;
	private BowBounce plugin;
	private World w;
	private boolean loaded = false;
	private int player2start = 4;

	private Location start = null;
	
	public int laterstarter = 0;
	public int afkkiller = 0;
	private int afktime;
	
	
	private int afktimeleft = 100;



	public newArena(String name, BowBounce pl) {
		this.plugin = pl;
		this.name = name;
		this.title = name;
		this.lore = new String[]{""};
		this.load();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public newArena(String name, BowBounce pl, World w) {
		this.plugin = pl;
		this.name = name;
		this.title = name;
		this.lore = new String[]{""};
		this.w = w;
		this.load();
	}
	

	
	public void disable(){
		if (this.running){
			for (String pn : this.players.keySet()){
				Player p = Bukkit.getPlayer(pn);
				if (p == null)
					continue;
				p.sendMessage(ChatColor.RED + "The game had been stoped!");
				this.leave(p, true);
			}
			this.players.clear();
			this.running = false;
		}
        HandlerList.unregisterAll(this);
        this.enable = false;
	}
	
	public void load() {
		File f = new File(plugin.folder.getAbsolutePath() + "/" + this.getName() + "/");
		if (!f.isDirectory()){
			f.mkdirs();
		}
		
		File conf = new File(f, "arena.yml");
		if (!conf.exists()){
			if (w == null){
				plugin.log.info("Can't write the config file for arena " + this.name + " => no world");
				return;
			}
			try{
				PrintWriter pw = new PrintWriter(conf);
				pw.write("world=" + this.w.getName() + "\r\n");
				pw.write("enabled=true\r\n");
				pw.write("player2start=4\r\n");
				pw.write("title=§3§lCustom title with spaces\r\n");
				pw.write("lore=Line1<br>Line2<br>Line3\r\n");
				pw.write("afkkillertime=20\r\n");

				pw.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		Boolean toenable = false;
		try{
			Scanner snr = new Scanner(conf);
			while (snr.hasNext()){
				String line = snr.nextLine();
				if (line.startsWith("#"))
					continue;
				String[] c = line.split("=");
				if (c.length != 2)
					continue;
				//WORLD
				if (c[0].trim().equalsIgnoreCase("world"))
					this.w = plugin.getServer().getWorld(c[1].trim());
				if (c[0].trim().equalsIgnoreCase("enabled"))
					toenable = Boolean.valueOf(c[1].trim());
				if (c[0].trim().equalsIgnoreCase("player2start"))
					this.player2start = (Utils.isInteger(c[1].trim()))? Integer.valueOf(c[1].trim()) : 4;
				if (c[0].trim().equalsIgnoreCase("title"))
					this.title = c[1].trim();
				if (c[0].trim().equalsIgnoreCase("lore"))
					this.lore = c[1].trim().split("<br>");
				if (c[0].trim().equalsIgnoreCase("afkkillertime"))
					this.afktime = (Utils.isInteger(c[1].trim()))? Integer.valueOf(c[1].trim()) : 20;
			}
			snr.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if (this.w == null){
			plugin.log.info("Can't load arena " + this.name);
			return;
		}
		
		File startF = new File(f, "start.loc");

		if (startF.exists())
			this.start = LocUtils.loadLocationfromFile(startF, this.w.getName(), plugin);
		if (this.start == null)
			this.start = w.getSpawnLocation();

		
	
		this.loaded = true;

		
		if (toenable)
			this.enable();
		
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public String[] getLore()
	{
		return this.lore;
	}
	
	public boolean isLoaded(){
		return this.loaded;
	}
	
	public boolean isEnabled(){
		return this.enable ;
	}

	
	public void setSpawn(Location l){
		LocUtils.WriteLocFile(l, this.name + "/start" , plugin);
		this.start = l;
	}
	


	
	public String enable() {
		if (!this.loaded)
			this.load();
		
		if (this.start == null){
			return "start";
		}
		
		this.enable = true;
		return "OK";
	}
	
	public void reallystart(CommandSender sender) {
        //PluginMessager.update(this);
		if (!this.enable){
			sender.sendMessage(ChatColor.RED + "The arena is not enabled!");
			return;
		}	
		
		for (String pn: this.players.keySet()){
			Player p = Bukkit.getPlayerExact(pn);
			if (p == null)
				continue;
			p.sendMessage("");
			p.sendMessage("");
			p.sendMessage("");
			p.sendMessage("");
			p.sendMessage("");
			p.sendMessage("");
			p.sendMessage(ChatColor.AQUA + "Bow Bounce starts now on map: " + ChatColor.YELLOW + this.title);
			p.sendMessage(ChatColor.RED + "You have " + (this.afktime ) + " seconds time to shoot your arrow!");
			p.sendMessage("");
			p.sendMessage("");
			p.sendMessage("");
			p.sendMessage("");
			
			ItemStack b = new ItemStack(Material.BOW, 1);
			b.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
			p.getInventory().setItem(0, b);
			
			p.getInventory().setItem(1, new ItemStack(Material.ARROW));
			
			
			
		}
		
		this.running = true;
		this.laterstarter = 0;
		
		
		restartafk();
	}
	
	public void start(final CommandSender sender) {
		if (!this.enable){
			sender.sendMessage(ChatColor.RED + "The arena is not enabled!");
			return;
		}
		
		if (this.laterstarter != 0)
			return;
		for (String pn : this.players.keySet()){
			Player p = Bukkit.getPlayerExact(pn);
			if (p != null)
				p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "The game starts in 20 Seconds!!!");		
		}
		this.laterstarter = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run(){
				reallystart(sender);
			}
			}, 10 * 20L);

	}

	public int isWaiting() {
		return this.player2start - this.players.size();
	}
	
	public boolean isRunning() {
		return this.running;
	}

	public void join(final Player sender) {
		if (this.players.containsKey(sender.getName())){
			sender.sendMessage(ChatColor.RED + "You're already in that game!");
			return;
		}

		if (this.running){
			sender.sendMessage(ChatColor.RED + " This match is already running!");
			return;
		}
		
		this.players.put(sender.getName(), true);
	
		sender.sendMessage(ChatColor.AQUA + "You've joined arena " + this.title);
		
		plugin.log.info("Player " + sender.getName() + " has joined!");
		
		Player p;
		for (String pn : this.players.keySet())
		{
			p = Bukkit.getPlayer(pn);
			if(p != null)
				p.sendMessage(ChatColor.YELLOW + sender.getName() + " joined the match!");
		}
		
		
		
		try{
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				public void run(){
				sender.teleport(start);
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			sender.sendMessage(e.getMessage());
		}
		
		if (this.players.size() >= this.player2start)
			this.start(sender);
		else
		{
			if (this.laterstarter == 0)
				for (String pn: this.players.keySet()){
					p = Bukkit.getPlayer(pn);
					if(p != null)
						p.sendMessage(ChatColor.GREEN + "Waiting for " + (this.player2start - this.players.size()) + " more players!");
				}
			
		}
		
	

		
	}
	
	
	public String leave(final Player p, boolean end) {

		if (!this.players.containsKey(p.getName())){
			return "NIG";
		}
		this.players.remove(p.getName());
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run(){		
				p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
				p.getInventory().clear();
				p.setLevel(0);
			}
		});
		
		
		if (this.players.size() < 1)
		{
			this.running = false;
		}
		
		if (!end){
			Player p2;
			for (String pn : this.players.keySet()){
				p2 = Bukkit.getPlayerExact(pn);
				if (p2 == null)
					continue;
				p2.sendMessage(ChatColor.YELLOW + p.getDisplayName() + " left the match!");
			}
		}
		
		updateBow();
		
		return "OK";
		
		
		

	}

	
	public ArrayList<String> getListbyString (String s) {		
//		ArrayList<?> ret = new ArrayList<Object>();
		Pattern p = Pattern.compile(".*\\{(.*)\\}.*");
	    Matcher m = p.matcher(s);
	    String sc = "";
	    if(m.find()){
	    	sc = m.group(1);
	    }else{
	    	plugin.log.info("List was empty-->" + s);
	    	return null;
	    }

	    return new ArrayList<String>(Arrays.asList(sc.split(",")));
	}
	
	
	@EventHandler
	public void onPlayerLogout (PlayerQuitEvent event){
		if (this.players.containsKey(event.getPlayer().getName()))
			this.leave(event.getPlayer(), false);
	}
	
	@EventHandler
	public void onPlayerFallDMG(EntityDamageEvent event){
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (!this.players.containsKey(((Player) event.getEntity()).getName()))
			return;
		
		if (event.getCause() == DamageCause.FALL){
			event.setCancelled(true);
			return;
		}
		
		
		if (event.getCause() == DamageCause.VOID){
			Player p2;
			for (String pn : this.players.keySet()){
				p2 = Bukkit.getPlayerExact(pn);
				if (p2 == null)
					continue;
				p2.sendMessage(ChatColor.YELLOW + ((Player) event.getEntity()).getDisplayName() + " went into the void");
			}		
			
			this.leave((Player) event.getEntity(), false);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDmg(EntityDamageByEntityEvent event){
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (!this.players.containsKey(((Player) event.getEntity()).getName()))
			return;

		
		if (event.getCause() == DamageCause.PROJECTILE && event.getDamager() instanceof Arrow){
			Arrow a = (Arrow) event.getDamager();
			if ( !( a.getShooter() instanceof Player) )
				return;
			
			Player s = (Player) a.getShooter();
			
			if (!(this.isInGame(s.getName())))
				return;
			
			event.setDamage(40D);
			s.playSound(s.getEyeLocation(), Sound.LEVEL_UP, 20, 10);
			
			Player p2;
			for (String pn : this.players.keySet()){
				p2 = Bukkit.getPlayerExact(pn);
				if (p2 == null)
					continue;
				p2.sendMessage(ChatColor.YELLOW + ((Player) event.getEntity()).getDisplayName() + " was killed by " + s.getDisplayName());
			}
			
			this.leave((Player) event.getEntity(), true);
			
			
			if (this.players.size() < 2){
				Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "[BowBounce]" + ChatColor.YELLOW + s.getDisplayName() + ChatColor.RESET + "" + ChatColor.AQUA + " won the match on " + this.title);
				this.leave(s, true);
			}
		}
		
	}
	
	@EventHandler
	public void onPlayerFoodChange(FoodLevelChangeEvent event){
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (!this.players.containsKey(((Player) event.getEntity()).getName()))
			return;
		
		event.setCancelled(true);
		((Player)event.getEntity()).setFoodLevel(20);		
	}
	
	@EventHandler
	public void LiftMeUp(PlayerMoveEvent event){

		if(event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
			return;
		
		if (!this.players.containsKey(event.getPlayer().getName()))
			return;
		
		Vector d = event.getPlayer().getVelocity();
		
		d.setY(1);
		
		event.getPlayer().setVelocity(d);
		
	}

	@EventHandler
	public void onPlayerShootBow(EntityShootBowEvent event){
		if (! (event.getEntity() instanceof Player))
			return;
		
		if (!this.players.containsKey(((Player) event.getEntity()).getName()))
			return;
		
		this.players.put(((Player) event.getEntity()).getName(), false);
		this.updateBow();
	}
	
	public boolean isInGame(String pn){
		return this.players.containsKey(pn);
	}
	
	private void updateBow(){
		if (this.players.size() < 1)
			return;
		if (this.players.size() <= 1){
			Player p = Bukkit.getPlayer(this.players.keySet().iterator().next());
			if (p == null)
				return;
			
//			Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "[BowBounce]" + ChatColor.YELLOW + p.getDisplayName() + ChatColor.RESET + "" + ChatColor.AQUA + " won the match on " + this.title);
			this.leave(p, true);
			
			return;
		}
		int i = 0;
		for (Boolean b : this.players.values()){
			if (b)
				i++;
		}
		
		if (i < 1){
			
			restartafk();
			
			
			Player p2;
			for (String pn : this.players.keySet()){
				p2 = Bukkit.getPlayerExact(pn);
				if (p2 == null)
					continue;
				p2.sendMessage(ChatColor.AQUA + "You got a new arrow!");
				p2.getInventory().setItem(1, new ItemStack(Material.ARROW, 1));
			}
		}
	}

	
	private void restartafk(){
		this.afktimeleft = this.afktime;
		if (this.afkkiller != 0)
			Bukkit.getScheduler().cancelTask(this.afkkiller);
		
		this.afkkiller = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				afktimeleft--;
				
				Player p2;
				for (String pn : players.keySet()){
					p2 = Bukkit.getPlayerExact(pn);
					if (p2 == null)
						continue;
					p2.setLevel(afktimeleft);
				}
				
				if (afktimeleft > 0)
					return;
				ArrayList<String> toleave = new ArrayList<String>();
				for (Entry<String, Boolean> e : players.entrySet()){
					if (e.getValue()){
						toleave.add(e.getKey());
						
					}
				}
				
				for (String pn : toleave){
					Player p = Bukkit.getPlayerExact(pn);
					if (p == null || !p.isOnline())
						continue;
					p.sendMessage(ChatColor.RED + "Don't afk!");
					
					leave(p, false);
				}
				
				afktimeleft = afktime;
				
			}
		}, 20, 20);
		
	}
}
