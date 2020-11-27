package it.rob.test.neotest.ogm.queryresults;

import it.rob.test.neotest.ogm.entity.node.*;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.neo4j.annotation.QueryResult;

@Data
@QueryResult
public class QRUfficiPf {
    @Field("pf")
    Pf pf;

    @Field("up")
    UfficioProvinciale up;

    @Field("ut")
    UfficioTerritoriale ut;

    @Field("to_up")
    Object to_up;

    @Field("to_ut")
    Object to_ut;
}
