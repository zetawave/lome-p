package me.lo.lomefree.Model.Shredding;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import me.lo.lomefree.Interfaces.ShredAlgos;
import me.lo.lomefree.R;

public class ShredBox implements ShredAlgos
{
    private HashMap<String, String> algorithms;
    static final long serialVersionUID = 987639234389L;

    public ShredBox()
    {
        algorithms = new HashMap<>();
        for(String [] algorithm : AVAILABLE_ALGO)
        {
            algorithms.put(algorithm[0], algorithm[1]);
        }
    }

    public HashMap<String, String> getAlgorithms()
    {
        return algorithms;
    }

    public ArrayList<String> getStringInformations(Context context)
    {
        ArrayList <String> infos = new ArrayList<>();
        HashMap<String, String> algos = getAlgorithms();
        for(String key : algos.keySet())
            infos.add(key
                    .concat(" | ")
                    .concat(Objects.requireNonNull(algos.get(key)))
                    .concat(" ")
                    .concat(context.getString(R.string.passes)));
        return infos;
    }

    public String getAlgorithmSelected(int which)
    {
        return (String) algorithms.keySet().toArray()[which];
    }
}
