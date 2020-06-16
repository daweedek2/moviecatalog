package kostka.moviecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class JsonConvertService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonConvertService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public <T> String dataToJson(final T data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    public <T> T jsonToData(final String json, Class<T> dataType) throws JsonProcessingException {
        return objectMapper.readValue(json, dataType);
    }

    public void registerMapperModule(final SimpleModule module) {
        objectMapper.registerModule(module);
    }
}
