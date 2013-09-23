package code;

import java.io.File;
import java.util.ArrayList;

//WTF

public class newArenaManager {
	
	private BowBounce plugin;
	
	private ArrayList<newArena> arenen = new ArrayList<newArena>();
	
	
	public newArenaManager(BowBounce bowBounce) {
		this.plugin = bowBounce;
		this.load();
	}

	public void disable(){
		for (newArena a : this.arenen)
			a.disable();
		
		this.arenen.clear();
	}
	
	private void load() {
		this.arenen.clear();
		for (File folder : plugin.folder.listFiles()){
			if (!folder.isDirectory())
				continue;
			
			plugin.log.info("FOUND ARENA" + folder.getName());
			newArena a = new newArena(folder.getName(), plugin);
			if (!a.isLoaded()){
				plugin.log.info("Error!");
				continue;
			}else{
				this.addArena(a);
			}
		}
	}
	
	public void addArena(newArena a){
		String name = a.getName();
		for (newArena at : this.arenen){
			if (at.getName().equalsIgnoreCase(name))
				return;
		}
		this.arenen.add(a);
		plugin.log.info("ADDED ARENA " + a.getName());
	}

	public newArena getArena(String name) {
		for (newArena at : this.arenen){
			if (at.getName().equalsIgnoreCase(name))
				return at;
		}
		return null;
	}
	
	public newArena getArenaByPlayer(String pname) {
		for (newArena at : this.arenen){
			if (at.isInGame(pname))
				return at;
		}
		return null;
	}
	
	public String[] getNames(){
		String[] ret = new String[this.arenen.size()];
		Integer i = 0;
		for (newArena a : this.arenen){
			ret[i] = a.getName();
			i++;
		}
		return ret;
	}
	
	public ArrayList<newArena> getArenas(){
		return this.arenen;
	}

	
}
