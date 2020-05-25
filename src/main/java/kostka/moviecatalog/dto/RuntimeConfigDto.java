package kostka.moviecatalog.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
//TODO: instead of config name user config enums, limit could be string as it comes from FE
public final class RuntimeConfigDto {
    @NotNull
    @Range(min = 1, max = 10)
    private Integer limit;

    private String configName;

    public RuntimeConfigDto(final Integer limit, final String configName) {
        this.limit = limit;
        this.configName = configName;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(final Integer limit) {
        this.limit = limit;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(final String configName) {
        this.configName = configName;
    }
}
