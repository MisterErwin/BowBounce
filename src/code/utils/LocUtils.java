package code.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.bukkit.Location;
import org.bukkit.World;

import code.BowBounce;



public class LocUtils {

	public static Location loadLocationfromFile(File f, String worldname,
			BowBounce plugin) {
		if (!f.exists()) {
			try { 
				f.createNewFile();
				return null;
			} catch (IOException e) {
			}
		}
		try { 
			Scanner snr = new Scanner(f);
			World w = plugin.getServer().getWorld(worldname);
			if (w == null) {
				plugin.getLogger().warning("Error while loading Location : null");
				return null;
			}
			Location loc = new Location(w, 0, 0, 0);

			loc.setX(Double.parseDouble(snr.nextLine().trim().split("=")[1]));
			loc.setY(Double.parseDouble(snr.nextLine().trim().split("=")[1]));
			loc.setZ(Double.parseDouble(snr.nextLine().trim().split("=")[1]));
			
			loc.setYaw(Float.parseFloat(snr.nextLine().trim().split("=")[1]));
			loc.setPitch(Float.parseFloat(snr.nextLine().trim().split("=")[1]));

			snr.close();
			return loc;
		} catch (Exception e) {
			plugin.getLogger().warning("[BowBounce] Error while loading Location");
		}
		return null;
	}

	public static void WriteLocFile(Location l, String name, BowBounce plugin) {
		File f = new File(plugin.folder + "/" + name + ".loc");

		WriteLocFile(f, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), plugin);
	}

	public static void WriteLocFile(File f, double X, double Y, double Z, float yaw, float pitch,
			BowBounce plugin) {
		plugin.getLogger().info(f.getAbsolutePath());
		if (!f.exists()) {
			try {
				File oldFile = f;
				if (!oldFile.getParentFile().isDirectory()) {
					oldFile.mkdirs();
				}
				oldFile.delete();

				f.createNewFile();

				PrintWriter pw = new PrintWriter(f);
				pw.write("#X=" + X + "\r\n");
				pw.write("#Y=" + Y + "\r\n");
				pw.write("#Z=" + Z + "\r\n");
				
				pw.write("#Y=" + yaw + "\r\n");
				pw.write("#P=" + pitch + "\r\n");

				pw.close();
			} catch (Exception e) {
				plugin.getLogger().warning("[BowBounce] Error while writing location file:"
						+ f.getAbsolutePath());
			}
			return;
		}// Ende Create
			// File existiert => Überschreiben
		else {
			PrintWriter pw;
			try {
				pw = new PrintWriter(f);

				pw.write("#X=" + X + "\r\n");
				pw.write("#Y=" + Y + "\r\n");
				pw.write("#Z=" + Z + "\r\n");

				pw.write("#Y=" + yaw + "\r\n");
				pw.write("#P=" + pitch + "\r\n");
				
				pw.close();
			} catch (FileNotFoundException e) {
				plugin.getLogger().warning("[BowBounce] Error while writing loc");
			}
		}
	} // END FUNC
}
