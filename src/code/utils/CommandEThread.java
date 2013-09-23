package code.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandEThread extends BukkitRunnable{
	
	

	private CommandSender sender;
	private String cmd;
	
	public CommandEThread (CommandSender sender, String cmd){
		this.sender = sender;
		this.cmd = cmd;
	}
	
	@Override
	public void run() {
		sender.getServer().dispatchCommand(sender, cmd);
		
	}

}
