package pers.ccdongyu.neo4jbackend.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pers.ccdongyu.neo4jbackend.domain.Dynamic;

import java.util.List;

public interface DynamicRepository extends Neo4jRepository<Dynamic, Long> {
    @Query("MATCH (a:Person), (b:Dynamic) WHERE a.name={0} AND ID(b)={1}"+
            "CREATE (a)-[:Release]->(b)")
    void createDynamic(String userid, Long dynamicid);

    List<Dynamic> getDynamicsByUserid(String userid);
}