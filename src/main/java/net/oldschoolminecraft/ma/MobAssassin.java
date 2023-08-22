package net.oldschoolminecraft.ma;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class MobAssassin extends JavaPlugin
{
    public static MobAssassin instance;

    public HashMap<Integer, Entity> spawnerEntities = new HashMap<>();
    public HashMap<Integer, String> slimeSizeMap = new HashMap<>();
    public Essentials essentials;
    public MAConfig maConfig;

    public void onEnable()
    {
        instance = this;
        essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        maConfig = new MAConfig(new File(getDataFolder(), "config.yml"));

        slimeSizeMap.put(1, "Small");
        slimeSizeMap.put(2, "Medium");
        slimeSizeMap.put(4, "Large");

        EntityHandler handler = new EntityHandler();
        getServer().getPluginManager().registerEvents(handler, this);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () ->
        {
            // cleanup despawned entities from map
            //TODO: this is not great code, as it only is getting the first entry in the worlds list. it may need to be more specific, or just do all worlds.
            for (Entity entity : Bukkit.getServer().getWorlds().get(0).getEntities())
                if (entity.isDead()) spawnerEntities.remove(entity.getEntityId());
        }, 0L, 20 * 60);

        System.out.println("MobAssassin enabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (label.equalsIgnoreCase("ma"))
        {
            if (args.length == 0)
            {
                sender.sendMessage(ChatColor.GRAY + "MobAssassin version " + getDescription().getVersion() + " by moderator_man");
                return true;
            }

            if (args[0].equalsIgnoreCase("stats"))
            {
                String statsTarget = sender.getName();

                if (args.length >= 2) statsTarget = args[1]; // target username to load

                if (!(sender instanceof Player) && sender.getName().equals(statsTarget))
                {
                    sender.sendMessage(ChatColor.RED + "You must be a player to load your own stats! Please specify a username if loading a player's stats from console.");
                    return true;
                }

                //TODO: display stats
            }
        }

        return false;
    }

    public void onDisable()
    {
        System.out.println("MobAssassin disabled");
    }
}
