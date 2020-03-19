package kostka.moviecatalog.repository;

import kostka.moviecatalog.entity.EsMovie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieElasticSearchRepository extends ElasticsearchRepository<EsMovie, Long> {
}
