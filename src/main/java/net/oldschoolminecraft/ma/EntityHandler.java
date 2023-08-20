package net.oldschoolminecraft.ma;

import com.earth2me.essentials.User;
import org.bukkit.craftbukkit.entity.CraftMonster;
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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof CraftMonster)) return; // ignore event if it's not a monster

        System.out.println(event.getEntity().getClass().getSimpleName() + " damaged by " + event.getDamager().getClass().getSimpleName());
        System.out.println("Current health: " + ((CraftMonster) event.getEntity()).getHealth());
        System.out.println("Damage: " + event.getDamage());

        boolean dead = (((CraftMonster) event.getEntity()).getHealth() - event.getDamage()) <= 0;
        if (!dead) return; // ignore event if monster isn't dead

        if (!MobAssassin.instance.spawnerEntities.containsKey(event.getEntity().getEntityId()))
        {
            // if the damage cause reason isn't an entity attack, ignore it
            EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();
            System.out.println("mob death: " + cause);
            if (!(cause == EntityDamageEvent.DamageCause.CONTACT)) return;
            // if the damage cause entity isn't a player, check if it's an arrow
            if (!(event.getDamager() instanceof Player))
            {
                if (event.getDamager() instanceof Arrow)
                {
                    // get the shooter of the arrow
                    LivingEntity shooter = ((Arrow) event.getEntity().getLastDamageCause().getEntity()).getShooter();
                    // check if the shooter is a player, return if it's not.
                    if (!(shooter instanceof Player)) return;
                } else return; // not a player or an arrow
            }

            String mobType = event.getEntity().getClass().getSimpleName();

            System.out.println("mob death: " + mobType + " killed by " + event.getDamager().getClass().getSimpleName());

            // dish out the dough
            Player player = (Player) event.getEntity().getLastDamageCause().getEntity();
            User user = MobAssassin.instance.essentials.getOfflineUser(player.getName());

            double value = MobAssassin.instance.maConfig.getConfigDouble("incentives." + mobType);

            user.giveMoney(value);

            System.out.println(player.getName() + " has received $" + value + " for killing " + mobType);
        } else {
            // mob is dead, remove it from the hashmap
            MobAssassin.instance.spawnerEntities.remove(event.getEntity().getEntityId());
        }
    }
}
