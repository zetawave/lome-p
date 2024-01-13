package me.lo.lomefree.Misc.Auxiliary;

public class TextEditing
{
    public static String getHtmlSpan(String title, String value, String color1, String color2)
    {
        return "<font color='"+color1+"'><b>"+title+"</b> </font> <font color='"+color2+"'>"+value+"</font>";
    }
}
