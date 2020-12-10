package it.rob.test.neotest.util;

import it.rob.test.neotest.constant.SearchType;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class ElasticUtils {

    private ElasticUtils() {

    }

    public static String getIndexForLabel(String label) {
        String index = null;
        switch (label) {
            case "Pf":
                index = "persone_fisiche";
                break;
            case "UfficioTerritoriale":
                index = "uffici_territoriali";
                break;
            default:
                index = "";
        }
        return index;
    }

    public static String getIdNameForLabel(String label) {
        String idName = null;
        switch (label) {
            case "Pf":
                idName = "zzCode";
                break;
            case "UfficioTerritoriale":
                idName = "zzCode";
                break;
            default:
                idName = "";
        }
        return idName;
    }

    public static QueryBuilder getQueryForSearchType(SearchType searchType, String key, String value) {
        //QueryBuilders.matchQuery(key, value).maxExpansions(10)
        QueryBuilder queryBuilder;
        switch (searchType) {
            case EXACT:
                queryBuilder = QueryBuilders.matchQuery(key, value).maxExpansions(10);
                break;
            case STARTS_WITH:
                queryBuilder = QueryBuilders.prefixQuery(key, value.toLowerCase());
                break;
            case CONTAINS:
                queryBuilder = QueryBuilders.wildcardQuery(key, value.toLowerCase());
                break;
            default:
                queryBuilder = QueryBuilders.matchQuery(key, value);
                break;
        }
        return queryBuilder;
    }
}
