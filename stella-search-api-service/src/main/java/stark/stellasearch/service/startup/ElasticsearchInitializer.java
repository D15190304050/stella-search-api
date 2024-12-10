package stark.stellasearch.service.startup;

import org.springframework.data.elasticsearch.annotations.FieldType;
import stark.stellasearch.domain.entities.es.Boost;
import stark.stellasearch.domain.entities.es.VideoSummaryInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class ElasticsearchInitializer
{
    private ElasticsearchInitializer()
    {}

    public static List<String> getQueryFieldsOfVideoSummaryInfo()
    {
        Field[] fields = VideoSummaryInfo.class.getDeclaredFields();
        List<String> queryFields = new ArrayList<>();

        for (Field field : fields)
        {
            org.springframework.data.elasticsearch.annotations.Field fieldAnnotation = field.getAnnotation(org.springframework.data.elasticsearch.annotations.Field.class);
            if (fieldAnnotation != null)
            {
                if (fieldAnnotation.type() == FieldType.Text ||
                    fieldAnnotation.type() == FieldType.Keyword)
                {
                    Boost boost = field.getAnnotation(Boost.class);
                    if (boost == null)
                        queryFields.add(field.getName());
                    else
                        queryFields.add(field.getName() + "^" + boost.value());
                }
            }
        }

        return queryFields;
    }
}
