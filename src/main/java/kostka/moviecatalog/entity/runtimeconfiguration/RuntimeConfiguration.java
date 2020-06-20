package kostka.moviecatalog.entity.runtimeconfiguration;

import kostka.moviecatalog.enumeration.RuntimeConfigurationEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import static javax.persistence.EnumType.ORDINAL;

@Entity
@Table(name = "runtime_configuration")
public class RuntimeConfiguration {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "config_type", unique = true)
    @Enumerated(ORDINAL)
    private RuntimeConfigurationEnum configType;

    @Lob
    @Column(name = "config_options", columnDefinition = "BLOB")
    private String options;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public RuntimeConfigurationEnum getConfigType() {
        return configType;
    }

    public void setConfigType(final RuntimeConfigurationEnum configType) {
        this.configType = configType;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(final String options) {
        this.options = options;
    }
}
