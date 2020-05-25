package kostka.moviecatalog.entity.runtimeconfiguration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "runtime_configuration")
public class RuntimeConfig {
    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column(name = "config_name")
    private String name;

    @Lob
    @Column(name = "config_value", columnDefinition = "BLOB")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
