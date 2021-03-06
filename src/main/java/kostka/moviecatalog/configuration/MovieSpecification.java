package kostka.moviecatalog.configuration;

import kostka.moviecatalog.dto.SearchCriteriaDto;
import kostka.moviecatalog.entity.Movie;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Configuration of jpa specification for searching movie with some pre-defined criteria.
 */
public class MovieSpecification implements Specification<Movie> {
    private SearchCriteriaDto criteria;

    public MovieSpecification(final SearchCriteriaDto criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(final Root<Movie> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
        final String field = criteria.getField();
        final String operation = criteria.getOperation();
        final Object value = criteria.getValue();
        final Object type = root.get(field).getJavaType();

        if (operation.equalsIgnoreCase(">")) {
            return builder.greaterThan(root.<String>get(field), value.toString());
        }

        if (operation.equalsIgnoreCase("<")) {
            return builder.lessThan(root.<String>get(field), value.toString());
        }

        if (operation.equalsIgnoreCase(">=")) {
            return builder.greaterThanOrEqualTo(root.<String>get(field), value.toString());
        }

        if (operation.equalsIgnoreCase("<=")) {
            return builder.lessThanOrEqualTo(root.<String>get(field), value.toString());
        }

        if (operation.equalsIgnoreCase("==")) {
            if (type == String.class) {
                return builder.like(
                        root.<String>get(field), "%" + value + "%");
            } else if (type == boolean.class) {
                return builder.equal(root.get(field), Boolean.valueOf(value.toString()));
            } else {
                return builder.equal(root.get(field), value);
            }
        }

        if (operation.equalsIgnoreCase("!=")) {
            if (type == String.class) {
                return builder.notLike(
                        root.<String>get(field), "%" + value + "%");
            } else if (type == boolean.class) {
                return builder.notEqual(root.get(field), Boolean.getBoolean(value.toString()));
            } else {
                return builder.notEqual(root.get(field), value);
            }
        }

        return null;
    }
}
