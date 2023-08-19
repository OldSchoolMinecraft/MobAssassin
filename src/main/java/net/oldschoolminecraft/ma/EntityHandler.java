package net.oldschoolminecraft.ma;

import com.earth2me.essentials.User;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class EntityHandler extends EntityListener
{
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

    public void onEntityDeath(EntityDeathEvent event)
    {
        if (!MobAssassin.instance.spawnerEntities.containsKey(event.getEntity().getEntityId()))
        {
            // if the damage cause reason isn't an entity attack, ignore it
            if (!(event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
            // if the damage cause entity isn't a player, check if it's an arrow
            if (!(event.getEntity().getLastDamageCause().getEntity() instanceof Player))
            {
                if (event.getEntity().getLastDamageCause().getEntity() instanceof Arrow)
                {
                    // get the shooter of the arrow
                    LivingEntity shooter = ((Arrow) event.getEntity().getLastDamageCause().getEntity()).getShooter();
                    // check if the shooter is a player, return if it's not.
                    if (!(shooter instanceof Player)) return;
                }
            }

            String mobType = event.getEntity().getClass().getSimpleName();

            // dish out the dough
            Player player = (Player) event.getEntity().getLastDamageCause().getEntity();
            User user = MobAssassin.instance.essentials.getOfflineUser(player.getName());

            double value = MobAssassin.instance.mobIncentiveMap.get(mobType);

            user.giveMoney(value);

            System.out.println(player.getName() + " has received $" + value + " for killing " + mobType);
        } else {
            // mob is dead, remove it from the hashmap
            MobAssassin.instance.spawnerEntities.remove(event.getEntity().getEntityId());
        }
    }
}
