package stark.stellasearch.enums;

import lombok.Getter;

@Getter
public enum VideoCreationType
{
    ORIGINAL(0),
    REPRINT(1),
    ;

    private final int value;

    VideoCreationType(final int value)
    {
        this.value = value;
    }
}
