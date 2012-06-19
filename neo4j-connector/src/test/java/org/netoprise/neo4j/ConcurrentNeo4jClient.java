package org.netoprise.neo4j;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.resource.ResourceException;

import org.neo4j.graphdb.Node;

import com.netoprise.neo4j.connection.Neo4JConnectionFactory;
import com.netoprise.neo4j.connection.Neo4jConnection;


@Stateless
@LocalBean
@Resource(mappedName=ConcurrentNeo4jClient.NEO4J_NAME,name="Neo4j",type=Neo4JConnectionFactory.class)
public class ConcurrentNeo4jClient {
	
	public static final String NEO4J_NAME = "java:/eis/Neo4j";
	
	@Resource(mappedName=NEO4J_NAME)
	private Neo4JConnectionFactory connectionFactory;

	
	public Neo4JConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}


	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String sayHello(String who) throws ResourceException{
		Neo4jConnection connection = connectionFactory.getConnection();
		Node referenceNode = connection.getReferenceNode();
		referenceNode.setProperty("text", who);
		connection.close();
		return "Hello "+who;
	}

	public String sayGoodbye() throws ResourceException {
		Neo4jConnection connection = connectionFactory.getConnection();
		String value = connection.getReferenceNode().getProperty("text").toString();
		connection.close();
		return "goodbye "+value;
	}
}
