package pers.ccdongyu.neo4jbackend.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import pers.ccdongyu.neo4jbackend.domain.Dynamic;

import java.util.Date;
import java.util.List;

public interface DynamicRepository extends Neo4jRepository<Dynamic, Long> {
    @Query("MATCH (a:Person), (b:Dynamic) WHERE a.name={0} AND ID(b)={1}"+
            "CREATE (a)-[:Release{create_Date:{2}}]->(b)")
    void createDynamic(String userId, Long dynamicId, Date createDate);

    List<Dynamic> getDynamicsByUserid(String userId);

    @Query("match ()-[relateTo]->(d:Dynamic) where ID(d)={0} return relateTo.create_Date")
    Long getCreateTime(Long dynamicId);

    @Query("MATCH (n:Dynamic) WHERE ID(n)={0} RETURN n")
    Dynamic findDynamicById(Long dynamicId);

    @Query("MATCH (n:Dynamic) WHERE ID(n)={0} DETACH DELETE n")
    void deleteDynamicById(Long dynamicId);
}
