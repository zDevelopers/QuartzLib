package otg.zLib.Additions;

import org.bukkit.Material;

public class VersionUtils {
	
	
	static public enum GameServerVersion{
		VERSION_1_12_2_OR_OLDER,
		VERSION_1_13_X,
		VERSION_1_14_X_OR_NEWER,
		VERSION_ERROR
		
		
	}
	
	
	
	
	public static GameServerVersion getServerAPIVersion() {
		// Stupidly simple
		try {
			Material[] registered = Material.values();
			for(int i = 0; i<registered.length;i++) {
				Material reg = registered[i];
				// I am looking at you Cauldron
				if(reg == null || reg.toString() == null || reg.toString().isEmpty())
					continue;
				if(reg.toString().equalsIgnoreCase("SIGN_POST"))
					return GameServerVersion.VERSION_1_12_2_OR_OLDER;
				if(reg.toString().equalsIgnoreCase("SIGN"))
					return GameServerVersion.VERSION_1_13_X;
				if(reg.toString().equalsIgnoreCase("JUNGLE_SIGN"))
					return GameServerVersion.VERSION_1_14_X_OR_NEWER;
				
							
			}
		} catch(Exception e) {
			// Stupid
		}
		return GameServerVersion.VERSION_ERROR;
		
  		
 	}

}
