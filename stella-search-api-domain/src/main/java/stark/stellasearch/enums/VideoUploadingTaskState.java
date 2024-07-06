package stark.stellasearch.enums;

import lombok.Getter;

@Getter
public enum VideoUploadingTaskState
{
    CREATED(0),
    COMPLETED(1),
    ABORTED(2),
    TO_BE_DELETED(3),
    DELETED(4)
    ;

    private final int value;

    VideoUploadingTaskState(final int value)
    {
        this.value = value;
    }
}
