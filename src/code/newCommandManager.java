package code;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import code.utils.CommandEThread;
import code.utils.IconMenuClass;
import code.utils.IconMenuClass.OptionClickEvent;

//I HATE ECLIPSE!

public class newCommandManager implements CommandExecutor{

	private BowBounce plugin;

	
	public newCommandManager(BowBounce pl){
		this.plugin = pl;
	}

	
	@Override
	public boolean onCommand(CommandSender sender,
			Command cmd, String paramString,
			String[] args) {
		
		if (args.length == 0){
			sender.sendMessage(ChatColor.RED + "~~~BowBounce~~~");
			sender.sendMessage(ChatColor.AQUA + "By: MisterErwin @ " + ChatColor.YELLOW + "http://minecraftiangalaxy.com/");


			if (sender.isOp()){
				sender.sendMessage(ChatColor.RED + "~Arena Commands~");
				sender.sendMessage(ChatColor.YELLOW + "/BB arena");

			}
			sender.sendMessage(ChatColor.BLUE + "~User Commands~");
			sender.sendMessage(ChatColor.YELLOW + "/BB list");
			if (sender.isOp()){
				sender.sendMessage(ChatColor.RED + "~Admin Commands~");
				sender.sendMessage(ChatColor.YELLOW + "/BB start <arena_name>");
				sender.sendMessage(ChatColor.AQUA + "/BB join <arena_name>");
			}
		}else if(args[0].equalsIgnoreCase("arena") && sender.isOp()){
			plugin.cc.onCommand(sender, args);
		}else if(args[0].equalsIgnoreCase("start") && sender.isOp()){
			if (args.length < 2){
				newArena a = plugin.am.getArenaByPlayer(sender.getName());
				if (a == null){
					sender.sendMessage(ChatColor.RED + "You are in no arena, use: /BB start <arena_name>");
					return true;
				}
				
				if (!a.isEnabled()){
					sender.sendMessage(ChatColor.RED + a.getTitle() + " is not enabled!");
					return true;
				}
				if (a.isRunning()){
					sender.sendMessage(ChatColor.RED + a.getTitle() + " is already starting!");
					return true;
				}
				if (a.laterstarter != 0){
					sender.sendMessage(ChatColor.RED + a.getTitle() + " is already starting!");
					return true;
				}
				a.start(sender);
				
				return true;
			}
			newArena a = plugin.am.getArena(args[1]);
			if (a == null){
				sender.sendMessage(ChatColor.RED + args[1] + " is not an arena!");
				return true;
			}
			if (!a.isEnabled()){
				sender.sendMessage(ChatColor.RED + args[1] + " is not enabled!");
				return true;
			}
			if (a.isRunning()){
				sender.sendMessage(ChatColor.RED + args[1] + " is already starting!");
				return true;
			}
			if (a.laterstarter != 0){
				sender.sendMessage(ChatColor.RED + args[1] + " is already starting!");
				return true;
			}
			a.start(sender);
		}else if(args[0].equalsIgnoreCase("list")){
			if (!(sender instanceof Player)){
				sender.sendMessage("List of all Arenas (for the console!)");
				String r = "";
				for (String n : plugin.am.getNames()){
					if (r != "")
						r += "-" + n;
					else
						r += n;
				}
				return true;
			}else{
				Player p = (Player) sender;
				plugin.imm.create(p, "Select an arena!!", 9, new IconMenuClass.OptionClickEventHandler() {
					
					@Override
					public void onOptionClick(OptionClickEvent event) {
						
						new CommandEThread(event.getPlayer(), "BB join " + event.getName()).runTaskLaterAsynchronously(plugin, 10L);
						event.setWillClose(true);
						event.setWillDestroy(true);
					}
				});
												
				Integer i = 0;
				for (newArena a : plugin.am.getArenas()){
					if (a == null)
						continue;
					String s = "";
					DyeColor dc = DyeColor.GRAY;
					if (a.isRunning()){
						s = ChatColor.RED + a.getTitle() + ChatColor.RESET + "" + ChatColor.RED + " [Running] ";
						dc = DyeColor.RED;
					}else if (!a.isLoaded() || !a.isEnabled()){
						if (!p.isOp())
							continue;
						s =  ChatColor.GRAY + a.getTitle() + ChatColor.RESET + "" + ChatColor.GRAY +" [NOT_ENABLED] ";
						dc = DyeColor.GRAY;
					}else {
						s = ChatColor.GREEN + a.getTitle() + "" + ChatColor.RESET + "" + ChatColor.GREEN + " [Waiting for "+ ChatColor.AQUA + a.isWaiting() + ChatColor.GREEN +"] ";
						dc = DyeColor.GREEN;
					}
					ItemStack is =  new ItemStack(Material.STAINED_CLAY, 1, dc.getWoolData());
					
					
					
					plugin.imm.setOption(p, i, is , s , a.getName(), a.getLore());
					i++;
				}
				
				
				plugin.imm.show(p);
			}
		}else if(args[0].equalsIgnoreCase("join")){
			if (!(sender instanceof Player)){
				sender.sendMessage("You have to be a player!");
				return true;
			}
			
			if (args.length != 2){
				sender.sendMessage(ChatColor.RED + "Usage: /BB join <arena_name>");
				return true;
			}else{
				
				newArena a = plugin.am.getArenaByPlayer(sender.getName());
				
				if (a != null){
					
					a.leave((Player) sender, false);
					sender.sendMessage(ChatColor.YELLOW + "You left arena " + a.getName());
					return true;
				}
				
				a = plugin.am.getArena(args[1]);
				if (a == null){
					sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.RED + " is not an arena!");  
					return true;
				}
				a.join((Player) sender);
				
			}
		}else if(args[0].equalsIgnoreCase("leave")){
			if (args.length != 2){
				sender.sendMessage(ChatColor.RED + "Usage: /BB leave <arena_name>");
				return true;
			}else{
				if (!(sender instanceof Player)){
					sender.sendMessage("You have to be a player!");
					return true;
				}
				newArena a = plugin.am.getArena(args[1]);
				if (a == null){
					sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.RED + " is not an arena!");  
					return true;
				}
				String ret = a.leave((Player) sender, false);
				if (ret == "OK")
					sender.sendMessage(ChatColor.GOLD + "You left the match!");
			}
		}
		
		return true;
	}

}