package me.lo.lomefree.Keys.Entities;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import me.lo.lomefree.Interfaces.Extensions;

public class RSAKeyFile implements Serializable, Extensions
{
    private String path;
    private String name;
    private String description;
    private int type;
    static final long serialVersionUID = 987639234389L;


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

    private HashMap <Integer, Object>  keys = new HashMap<>();
    public static final int TYPE_PUBLIC = 0;
    public static final int TYPE_PRIVATE = 1;
    public static final int TYPE_ALL = 2;

// --Commented out by Inspection START (18/07/18 19.08):
//    public HashMap<Integer, Object> getKeys() {
//        return keys;
//    }
// --Commented out by Inspection STOP (18/07/18 19.08)

    public void setKeys(HashMap<Integer, Object> keys) {
        this.keys = keys;
    }

    public PrivateKey getPrivateKey()
    {
        return (PrivateKey) keys.get(TYPE_PRIVATE);
    }

    public PublicKey getPublicKey()
    {
        return (PublicKey) keys.get(TYPE_PUBLIC);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    private void setType(int type) {
        this.type = type;
    }

    public void setTypes() {
        boolean Private = (getPrivateKey() != null);
        boolean Public = (getPublicKey() != null);
        if (Private && Public)
            setType(TYPE_ALL);
        else if (Private)
            setType(TYPE_PRIVATE);
        else if (Public)
            setType(TYPE_PUBLIC);
        else
            setType(-1);
    }


}
