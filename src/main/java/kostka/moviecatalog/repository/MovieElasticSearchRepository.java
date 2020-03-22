package kostka.moviecatalog.repository;

import kostka.moviecatalog.entity.EsMovie;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieElasticSearchRepository extends ElasticsearchRepository<EsMovie, Long> {
    @Query("{\"multi_match\" : { \"query\" : \"?0\", \"fields\" : [\"name\", \"director\", \"description\"], \"operator\" : \"or\"}}")
    Iterable<EsMovie> fullTextSearch(String term);
}
