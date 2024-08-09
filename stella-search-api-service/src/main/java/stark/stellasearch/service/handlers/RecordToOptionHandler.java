package stark.stellasearch.service.handlers;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import stark.dataworks.basic.beans.FieldExtractor;
import stark.stellasearch.dto.results.DropDownOption;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RecordToOptionHandler
{
    public static <TRecord> List<DropDownOption<Long>> convertRecordsToDropDownOption(List<TRecord> records, String titleProperty, String valueProperty) throws IllegalAccessException
    {
        if (CollectionUtils.isEmpty(records))
            throw new IllegalArgumentException("\"records\" is null or empty.");

        if (!StringUtils.hasText(titleProperty))
            throw new IllegalArgumentException("\"titleProperty\" is null or empty.");

        if (!StringUtils.hasText(valueProperty))
            throw new IllegalArgumentException("\"valueProperty\" is null or empty.");

        TRecord firstRecord = records.get(0);

        Class<?> recordClass = firstRecord.getClass();
        List<Field> fields = FieldExtractor.getAllFields(recordClass);

        Field titleField = null;
        Field valueField = null;

        for (Field field : fields)
        {
            if (field.getName().equals(titleProperty))
            {
                Class<?> titleType = field.getType();
                if (!titleType.equals(String.class))
                    throw new IllegalArgumentException("\"" + titleProperty + "\" must be a string.");

                titleField = field;
            }

            if (field.getName().equals(valueProperty))
            {
                valueField = field;
            }
        }

        if (titleField == null)
            throw new IllegalArgumentException("There is no field with name \"" + titleProperty + "\".");

        if (valueField == null)
            throw new IllegalArgumentException("There is no field with name \"" + valueProperty + "\".");

        List<DropDownOption<Long>> options = new ArrayList<DropDownOption<Long>>();

        for (TRecord record : records)
        {
            boolean canAccessTitle = titleField.canAccess(record);
            boolean canAccessValue = valueField.canAccess(record);

            titleField.setAccessible(true);
            valueField.setAccessible(true);

            String title = (String) titleField.get(record);
            Long value = (Long) valueField.get(record);

            DropDownOption<Long> option = new DropDownOption<>();
            option.setTitle(title);
            option.setValue(value);
            options.add(option);

            titleField.setAccessible(canAccessTitle);
            valueField.setAccessible(canAccessValue);
        }

        return options;
    }
}
