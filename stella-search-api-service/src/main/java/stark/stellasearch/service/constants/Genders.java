package stark.stellasearch.service.constants;

import stark.dataworks.basic.beans.StaticConstantExtractor;

import java.util.HashSet;

public class Genders
{
    private Genders()
    {
    }

    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String SECRET = "secret";

    private static final HashSet<String> genders;

    static
    {
        genders = StaticConstantExtractor.extractConstants(Genders.class, String.class);
    }

    public static boolean inRange(String gender)
    {
        return genders.contains(gender);
    }

    public static String[] acceptableValues()
    {
        return genders.toArray(new String[0]);
    }
}
