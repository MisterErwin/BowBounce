package code.utils;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Enderman;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import net.minecraft.server.v1_6_R2.MathHelper;

public class Utils {

	public static boolean isInteger(String s) {
		try {
			Integer i = Integer.valueOf(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isBoolean(String s) {
		try {
			Boolean b = Boolean.valueOf(s);
			return true;
		} catch (Exception e) {
			return false;
			
		}
	}

	public static void setBlock(Enderman e) {
		if (e.getCarriedMaterial() == null)
			return;
		Random random = new Random();
		int x = MathHelper.floor(e.getLocation().getDirection().getX() - 1.0D
				+ random.nextDouble() * 2.0D);
		int y = MathHelper.floor(e.getLocation().getDirection().getY()
				+ random.nextDouble() * 2.0D);
		int z = MathHelper.floor(e.getLocation().getDirection().getZ() - 1.0D
				+ random.nextDouble() * 2.0D);
		
		Location l = new Location(e.getWorld(), x, y, z);

		EntityChangeBlockEvent event = new EntityChangeBlockEvent(e, l.getBlock(), e.getCarriedMaterial()
				.getItemType(), e.getCarriedMaterial().getData());

		e.getServer().getPluginManager().callEvent(event);

	}
}
