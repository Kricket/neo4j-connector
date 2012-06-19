package org.netoprise.neo4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.resource.ResourceException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import com.netoprise.neo4j.connection.Neo4JConnectionFactory;
import com.netoprise.neo4j.connection.Neo4jConnection;


@Stateless
@LocalBean
@Resource(mappedName=Neo4jClient.NEO4J_NAME,name="Neo4j",type=Neo4JConnectionFactory.class)
public class Neo4jClient {
	
	public static final String NEO4J_NAME = "java:/eis/Neo4j";
	
	@Resource(mappedName=NEO4J_NAME)
	private Neo4JConnectionFactory connectionFactory;

	private Neo4jConnection connection;
	
	private @EJB ConcurrentNeo4jClient concurrent;
	
	@PostConstruct
	public void createConnection() throws ResourceException {
		connection  = connectionFactory.getConnection();
	}

	
	public Neo4JConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}


	public String sayHello(String who) throws ResourceException{
		Node referenceNode = connection.getReferenceNode();
		referenceNode.setProperty("text", who);
		return "Hello "+who;
	}

	public String sayGoodbye() throws ResourceException {
		String value = connection.getReferenceNode().getProperty("text").toString();
		return "goodbye "+value;
	}


	public String sayHelloToTeam(String string, String string2) throws ResourceException {
		Node referenceNode = connection.getReferenceNode();
		referenceNode.setProperty("text", string);
		return concurrent.sayHello(string2)+referenceNode.getProperty("text");
	}
}
