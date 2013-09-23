package code;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import code.utils.ndIconMenuManager;

public class BowBounce extends JavaPlugin{
	
	public newArenaManager am;
	public newArenaCommands cc;
	public File folder;
	public Logger log;	
	public ndIconMenuManager imm;
		
	public void onEnable(){
		this.log = this.getLogger();
		
		File f = new File(this.getDataFolder().getAbsolutePath() + "/arenen/");
		if (!f.isDirectory()){
			f.mkdirs();
		}
		this.folder = f;
		
		this.am = new newArenaManager(this);
		this.cc = new newArenaCommands(this);
		
		this.imm = new ndIconMenuManager(this);

		getCommand("BB").setExecutor(new newCommandManager(this));
		
		
	}
	
	public void onDisable(){
		this.imm.closeall();
		this.am.disable();
		HandlerList.unregisterAll(this);
	}
}
