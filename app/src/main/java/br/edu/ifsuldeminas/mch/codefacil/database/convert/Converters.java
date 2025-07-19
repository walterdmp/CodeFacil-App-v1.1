package br.edu.ifsuldeminas.mch.codefacil.database.convert;

import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        return value == null ? null : Arrays.asList(value.split(";"));
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : list) {
            stringBuilder.append(s).append(";");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}
