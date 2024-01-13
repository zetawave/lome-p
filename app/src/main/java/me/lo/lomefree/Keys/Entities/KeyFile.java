package me.lo.lomefree.Keys.Entities;


import java.io.Serializable;

import me.lo.lomefree.Interfaces.Extensions;

public class KeyFile implements Serializable, Extensions
{
    private String name;
    private String description;
    private String path;
    private String value;
    private String algorithm;
    static final long serialVersionUID = 987639234389L;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setPath(String path) {
        this.path = path;
    }



    public static boolean isKeyFile(String name)
    {
        return name.endsWith(KEY_FILE_EXT);
    }
}
