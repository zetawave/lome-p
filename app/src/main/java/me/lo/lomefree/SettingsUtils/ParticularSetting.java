package me.lo.lomefree.SettingsUtils;

import java.io.Serializable;

public class ParticularSetting implements Serializable
{
    private Object value;
    private String preference;
    static final long serialVersionUID = 987639234389L;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

// --Commented out by Inspection START (18/07/18 19.11):
//    public String getPreference() {
//        return preference;
//    }
// --Commented out by Inspection STOP (18/07/18 19.11)

    public void setPreference(String preference) {
        this.preference = preference;
    }
}
