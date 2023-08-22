package net.oldschoolminecraft.ma;

import com.earth2me.essentials.User;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftMonster;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class EntityHandler extends EntityListener
{
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        // check if the spawned entity is from a spawner
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER))
        {
            // if it is, register the entity ID into a hashmap along with the entity object itself
            MobAssassin.instance.spawnerEntities.put(event.getEntity().getEntityId(), event.getEntity());
            // this way, we can ensure money is not given to players for killing spawner creatures.
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (!(event instanceof EntityDamageByEntityEvent)) return;

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        if (!(event.getEntity() instanceof CraftMonster || event.getEntity() instanceof CraftLivingEntity)) return; // ignore event if it's not a monster
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return; // if the damage cause reason isn't an entity attack, ignore it
        if (event.getEntity().isDead()) return;
        if (((CraftLivingEntity) event.getEntity()).getHealth() > 0) return; // health must be at least 0

        if (!MobAssassin.instance.spawnerEntities.containsKey(event.getEntity().getEntityId()))
        {
            // if the damage cause entity isn't a player, check if it's an arrow
            if (!(e.getDamager() instanceof Player))
            {
                if (e.getDamager() instanceof Arrow)
                {
                    // get the shooter of the arrow
                    LivingEntity shooter = ((Arrow) event.getEntity().getLastDamageCause().getEntity()).getShooter();
                    // check if the shooter is a player, return if it's not.
                    if (!(shooter instanceof Player)) return;
                } else return; // not a player or an arrow
            }

            String mobType = event.getEntity().getClass().getSimpleName().split("Craft")[1];
            String incentive = mobType;

            if (mobType.startsWith("Slime"))
            {
                int slimeSize = ((CraftSlime) event.getEntity()).getSize();
                String slimeSizeStr = MobAssassin.instance.slimeSizeMap.getOrDefault(slimeSize, "");
                incentive = slimeSizeStr + mobType;
                mobType = slimeSizeStr + " " + mobType;
                System.out.println("Slime died, size: " + slimeSize);
                System.out.println("Incentive: " + incentive);
                System.out.println("Mob type: " + mobType);
            }

            // dish out the dough
            Player player = (Player) e.getDamager();
            User user = MobAssassin.instance.essentials.getOfflineUser(player.getName());

            boolean hasProperty = MobAssassin.instance.maConfig.getProperty("incentives." + incentive) != null;
            double value = hasProperty ? MobAssassin.instance.maConfig.getConfigDouble("incentives." + incentive) : 0;
            if (value == 0) return;

            user.setMoney(user.getMoney() + value);

            player.sendMessage(ChatColor.GREEN + "You received $" + value + " for killing " + mobType);
            System.out.println(player.getName() + " has received $" + value + " for killing " + mobType);
        } else {
            // mob is dead, remove it from the hashmap
            MobAssassin.instance.spawnerEntities.remove(event.getEntity().getEntityId());
        }
    }
}
