package stark.stellasearch.service.constants;

import stark.dataworks.basic.beans.StaticConstantExtractor;

import java.util.HashSet;

public class PhoneNumberPrefixes
{
    private PhoneNumberPrefixes(){}

    public static final String PREFIX_CHINA = "86";
    public static final String PREFIX_87 = "87";

    private static final HashSet<String> prefixes;

    static
    {
        prefixes = StaticConstantExtractor.extractConstants(PhoneNumberPrefixes.class, String.class);
    }

    public static boolean inRange(String prefix)
    {
        return prefixes.contains(prefix);
    }

    public static String[] acceptableValues()
    {
        return prefixes.toArray(new String[0]);
    }
}
