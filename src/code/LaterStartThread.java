package code;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class LaterStartThread extends BukkitRunnable{
	private newArena a ; //Arena
	private CommandSender sender;
	
	public LaterStartThread(newArena a, CommandSender sender){
		this.a = a;
		this.sender = sender;
	}

	@Override
	public void run() {
		a.reallystart(sender);
		
	}
	
}
