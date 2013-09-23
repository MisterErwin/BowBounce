package code;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class newArenaCommands {

	private BowBounce plugin;
	
	public newArenaCommands (BowBounce pl){
		this.plugin = pl;
	}
	
	public void onCommand(CommandSender sender, String[] args){
		if (args.length == 1){
			showHelp(sender);
		}else if (args[1].equalsIgnoreCase("create")){
			if (!(sender instanceof Player)){
				sender.sendMessage("You must be a player!");
				return;
			}
			if (args.length < 3){
				sender.sendMessage(ChatColor.RED + "/BB arena create <name>");
				return;
			}else{
				newArena a = new newArena(args[2], plugin, ((Player) sender).getWorld());
				plugin.am.addArena(a);
				sender.sendMessage(ChatColor.GREEN + "Created arena " + args[2]);
				sender.sendMessage(ChatColor.GREEN + "You can now set the spawn with " + ChatColor.AQUA + "/BB arena setStart " + args[2] );
				sender.sendMessage(ChatColor.GREEN + "You can enable the arena with " + ChatColor.AQUA + "/BB arena enable " + args[2] );

				return;
			}
		
		}else if (args[1].equalsIgnoreCase("setStart")){
			if (args.length < 3){
				sender.sendMessage("/BB arena setStart <arena-name>");
				return;
			}else{
				if (!(sender instanceof Player)){
					sender.sendMessage("You must be a player");
					return;
				}
				newArena a = plugin.am.getArena(args[2]);
				if (a == null){
					sender.sendMessage(ChatColor.AQUA + args[2] + ChatColor.RED + " is not an arena!");
					return;
				}			
				a.setSpawn(((Player) sender).getLocation());
				sender.sendMessage(ChatColor.GREEN + "Set start to arena " + args[2]);
				return;
			}
	
		}else if (args[1].equalsIgnoreCase("enable")){
			if (args.length < 3){
				sender.sendMessage("/BB arena enable <arena-name>");
				return;
			}else{
				newArena a = plugin.am.getArena(args[2]);
				if (a == null){
					sender.sendMessage(ChatColor.AQUA + args[2] + ChatColor.RED + " is not an arena!");
					return;
				}
				String ret = a.enable();
				
				if (ret.equalsIgnoreCase("OK")){
					sender.sendMessage(ChatColor.GREEN + "Enabled arena " + args[2]);
				}else if (ret.equalsIgnoreCase("start")){
					sender.sendMessage(ChatColor.RED + "Please set the spawn first! - Use this command: " + ChatColor.AQUA + "/BB arena " + args[2] + " setStart");
				}else {
					sender.sendMessage(ChatColor.RED + "Sorry, but I can't enable arena " + args[2]);
					sender.sendMessage(ret);
				}
				return;
			}
		}else if (args[1].equalsIgnoreCase("list")){
			if (args.length < 1){
				sender.sendMessage("/BB arena list");
				return;
			}else{
				String ar = "";
				for (String an : plugin.am.getNames())
					ar += an + ", ";
				sender.sendMessage(ChatColor.AQUA + "All arenen (" + plugin.am.getNames().length + ")");
				sender.sendMessage(ChatColor.YELLOW + ar);
				return;
			}
		}
	}
	
	public void showHelp(CommandSender sender){
		sender.sendMessage(ChatColor.GOLD + "~BowBounce~");
		sender.sendMessage(ChatColor.AQUA + "By: MisterErwin @ " + ChatColor.YELLOW + "http://minecraftiangalaxy.com/");

		if (sender.isOp()){
			sender.sendMessage(ChatColor.RED + "~Arena Commands~");
			sender.sendMessage(ChatColor.YELLOW + "/BB arena create <name>");
			sender.sendMessage(ChatColor.AQUA + "/BB arena setStart <arena-name>");
			sender.sendMessage(ChatColor.YELLOW + "/BB arena enable <arena-name>");

		}
	}
}
