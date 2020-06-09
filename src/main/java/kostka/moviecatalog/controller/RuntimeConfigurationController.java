package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.RuntimeConfigDto;
import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfiguration;
import kostka.moviecatalog.service.runtimeconfiguration.RuntimeConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/runtimeConfig")
public class RuntimeConfigurationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeConfigurationController.class);
    private final RuntimeConfigurationService runtimeConfigurationService;

    @Autowired
    public RuntimeConfigurationController(
            final RuntimeConfigurationService runtimeConfigurationService) {
        this.runtimeConfigurationService = runtimeConfigurationService;
    }

    @PostMapping("/update")
    public RuntimeConfiguration updateConfiguration(
            @RequestBody final RuntimeConfigDto dto) {
        LOGGER.info("update runtime config '{}' request", dto.getConfigName());
        return runtimeConfigurationService.updateRuntimeConfiguration(dto);
    }

    @ExceptionHandler(value = Exception.class)
    public String runtimeConfigExceptionHandler(final Exception e) {
        return e.getMessage();
    }
}
