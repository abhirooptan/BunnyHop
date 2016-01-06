/**
 * @file        GamePreferences.java
 * @author      Abhiroop Tandon (20061667)
 * @assignment  NumericalXandO     
 */

package wit.cgd.bunnyhop.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GamePreferences {
	
	public boolean	 level;
	public boolean 	 free;
	
	public boolean 	 music;
	public float 	 musicVolume;
	public boolean 	 sound;
	public float 	 soundVolume;

    public static final String          TAG         = GamePreferences.class.getName();

    public static final GamePreferences instance    = new GamePreferences();
    private Preferences                 prefs;
	
    

    private GamePreferences() {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
    }

    public void load() {
    	free = prefs.getBoolean("free");
    	level = prefs.getBoolean("level");
    }

    public void save() {
    	prefs.putBoolean("free", free);
    	prefs.putBoolean("level", level);
    }

}