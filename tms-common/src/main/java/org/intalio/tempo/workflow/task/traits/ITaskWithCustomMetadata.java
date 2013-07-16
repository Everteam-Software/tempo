package org.intalio.tempo.workflow.task.traits;

import java.util.Map;

public interface ITaskWithCustomMetadata {
    void setCustomMetadata(Map<String, String> customMetadata);
    Map<String, String> getCustomMetadata();

    void setCustomTaskMetadata(String ctm);
    String getCustomTaskMetadata();
}
