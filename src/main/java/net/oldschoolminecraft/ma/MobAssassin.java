package net.oldschoolminecraft.ma;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class MobAssassin extends JavaPlugin
{
    public static MobAssassin instance;

    public HashMap<Integer, Entity> spawnerEntities = new HashMap<>();
    public HashMap<String, Double> mobIncentiveMap = new HashMap<>();
    public Essentials essentials;

    public void onEnable()
    {
        instance = this;
        essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");

        mobIncentiveMap.put("zombie", 0.2);
        mobIncentiveMap.put("skeleton", 0.3);
        mobIncentiveMap.put("creeper", 0.4);
        mobIncentiveMap.put("spider", 0.25);
        mobIncentiveMap.put("slime:0", 1.0); // big slime
        mobIncentiveMap.put("slime:1", 0.35); // medium slime
        mobIncentiveMap.put("slime:2", 0.0); // small slime
        mobIncentiveMap.put("pigman", 0.4);
        mobIncentiveMap.put("ghast", 4.0);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () ->
        {
            // cleanup despawned entities from map
            //TODO: this is not great code, as it only is getting the first entry in the worlds list. it may need to be more specific, or just do all worlds.
            for (Entity entity : Bukkit.getServer().getWorlds().get(0).getEntities())
                if (entity.isDead()) spawnerEntities.remove(entity.getEntityId());
        }, 0L, 20 * 60);

        System.out.println("MobAssassin enabled");
    }

    public void onDisable()
    {
        System.out.println("MobAssassin disabled");
    }
}
