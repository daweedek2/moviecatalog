package kostka.moviecatalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kostka.moviecatalog.annotation.UniqueRuntimeConfigName;

import java.util.Map;

public class RuntimeConfigDto {
    @UniqueRuntimeConfigName
    private String configName;

    @JsonProperty("options")
    private Map<String, String> options;

    public RuntimeConfigDto(
            final String configName,
            final Map<String, String> options) {
        this.configName = configName;
        this.options = options;
    }

    public RuntimeConfigDto() {
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(final String configName) {
        this.configName = configName;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(final Map<String, String> options) {
        this.options = options;
    }
}
