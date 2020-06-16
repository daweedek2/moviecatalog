package kostka.moviecatalog.service.integrationTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.dto.RuntimeConfigDto;
import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfiguration;
import kostka.moviecatalog.entity.runtimeconfiguration.VisibleMoviesOptionsRuntimeConfig;
import kostka.moviecatalog.exception.MissingRuntimeConfigurationException;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.RuntimeConfigRepository;
import kostka.moviecatalog.service.EsMovieService;
import kostka.moviecatalog.service.runtimeconfiguration.RuntimeConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static kostka.moviecatalog.enumeration.RuntimeConfigurationEnum.OTHER;
import static kostka.moviecatalog.enumeration.RuntimeConfigurationEnum.VISIBLE_MOVIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class RuntimeConfigServiceIntegrationTest {
    private static final String VALID_CONFIG_NAME = "VISIBLE_MOVIES";
    private static final String LIMIT_FIELD = "limit";
    private static final String LIMIT_VALUE = "1";
    private static final int EXISTING_LIMIT_VALUE = 5;

    @Autowired
    private RuntimeConfigurationService runtimeConfigurationService;

    @Autowired
    private RuntimeConfigRepository runtimeConfigRepository;

    @MockBean
    EsMovieService esMovieService;

    @MockBean
    MovieElasticSearchRepository movieElasticSearchRepository;

    @Test
    public void updateConfigConfigNotFoundDtoIntegrationTest() {
        RuntimeConfigDto dto = getValidDto();

        assertThatThrownBy(() -> runtimeConfigurationService.update(dto))
                .isInstanceOf(MissingRuntimeConfigurationException.class)
                .hasMessageContaining("Runtime Configuration is not found.");
    }

    @Test
    public void updateConfigSuccessfullyDtoIntegrationTest() throws JsonProcessingException {
        String updatedOptions = "{\"limit\":\"1\"}";
        var existingRuntimeConfig = runtimeConfigRepository.save(getExistingRuntimeConfig());
        RuntimeConfigDto dto = getValidDto();

        var result = runtimeConfigurationService.update(dto);

        assertThat(result)
                .isNotNull()
                .extracting(RuntimeConfiguration::getId, RuntimeConfiguration::getConfigType, RuntimeConfiguration::getOptions)
                .containsExactly(existingRuntimeConfig.getId(), existingRuntimeConfig.getConfigType(), updatedOptions);
    }

    @Test
    public void getVisibleMoviesOptionsIntegrationTest() {
        runtimeConfigRepository.save(getExistingRuntimeConfig());
        VisibleMoviesOptionsRuntimeConfig result = runtimeConfigurationService.getRuntimeConfigurationOptions(VISIBLE_MOVIES);

        assertThat(result)
                .isInstanceOf(VisibleMoviesOptionsRuntimeConfig.class)
                .extracting(VisibleMoviesOptionsRuntimeConfig::getLimit)
                .isEqualTo(EXISTING_LIMIT_VALUE);
    }

    @Test
    public void getRuntimeConfigOptionsMissingRuntimeConfigIntegrationTest() {
        runtimeConfigRepository.save(getExistingRuntimeConfig());
        assertThatThrownBy(() -> runtimeConfigurationService.getRuntimeConfigurationOptions(OTHER))
                .isInstanceOf(MissingRuntimeConfigurationException.class)
                .hasMessageContaining("Runtime Configuration is not found.");
    }

    private RuntimeConfigDto getValidDto() {
        var dto = new RuntimeConfigDto();
        dto.setConfigName(VALID_CONFIG_NAME);
        var options = new HashMap<String, String>();
        options.put(LIMIT_FIELD, LIMIT_VALUE);
        dto.setOptions(options);
        return dto;
    }

    private RuntimeConfiguration getExistingRuntimeConfig() {
        String existingOptions = "{\"limit\":\"5\"}";
        var existingRuntimeConfig = new RuntimeConfiguration();
        existingRuntimeConfig.setConfigType(VISIBLE_MOVIES);
        existingRuntimeConfig.setOptions(existingOptions);
        return existingRuntimeConfig;
    }
}
