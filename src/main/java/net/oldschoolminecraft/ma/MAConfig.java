package net.oldschoolminecraft.ma;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class MAConfig extends Configuration
{
    public MAConfig(File file)
    {
        super(file);
        reload();
    }

    public void reload()
    {
        load();
        write();
        save();
    }

    private void write()
    {
        generateConfigOption("incentives.Zombie", 0.2D);
        generateConfigOption("incentives.Skeleton", 0.3D);
        generateConfigOption("incentives.Creeper", 0.4D);
        generateConfigOption("incentives.Spider", 0.25D);
        generateConfigOption("incentives.LargeSlime", 1.0D);
        generateConfigOption("incentives.MediumSlime", 0.35D);
        generateConfigOption("incentives.Pigman", 0.4D);
        generateConfigOption("incentives.Ghast", 4.0D);
    }

    private void generateConfigOption(String key, Object defaultValue)
    {
        if (this.getProperty(key) == null)
            this.setProperty(key, defaultValue);
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    //Getters Start
    public Object getConfigOption(String key)
    {
        return this.getProperty(key);
    }

    public Object getConfigOption(String key, Object defaultValue)
    {
        Object value = getConfigOption(key);
        if (value == null)
            value = defaultValue;
        return value;

    }

    public String getConfigString(String key)
    {
        return String.valueOf(getConfigOption(key));
    }

    public Integer getConfigInteger(String key)
    {
        return Integer.valueOf(getConfigString(key));
    }

    public Long getConfigLong(String key)
    {
        return Long.valueOf(getConfigString(key));
    }

    public Double getConfigDouble(String key)
    {
        return Double.valueOf(getConfigString(key));
    }

    public Boolean getConfigBoolean(String key)
    {
        return Boolean.valueOf(getConfigString(key));
    }

    //Getters End

    private boolean convertToNewAddress(String newKey, String oldKey)
    {
        if (this.getString(newKey) != null)
            return false;
        if (this.getString(oldKey) == null)
            return false;
        System.out.println("Converting Config: " + oldKey + " to " + newKey);
        Object value = this.getProperty(oldKey);
        this.setProperty(newKey, value);
        this.removeProperty(oldKey);
        return true;
    }
}
