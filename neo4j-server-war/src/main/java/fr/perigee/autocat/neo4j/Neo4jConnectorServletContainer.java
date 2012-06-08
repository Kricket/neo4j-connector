package fr.perigee.autocat.neo4j;

import java.util.Set;

import javax.annotation.Resource;
import javax.resource.ResourceException;

import org.apache.commons.configuration.Configuration;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.Config;
import org.neo4j.server.NeoServerProvider;
import org.neo4j.server.NeoServerWithEmbeddedWebServer;
import org.neo4j.server.configuration.ConfigurationProvider;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.neo4j.server.database.Database;
import org.neo4j.server.database.DatabaseProvider;
import org.neo4j.server.database.GraphDatabaseServiceProvider;
import org.neo4j.server.modules.RESTApiModule;
import org.neo4j.server.plugins.Injectable;
import org.neo4j.server.plugins.PluginInvocatorProvider;
import org.neo4j.server.plugins.PluginManager;
import org.neo4j.server.rest.paging.LeaseManagerProvider;
import org.neo4j.server.rest.repr.DefaultFormat;
import org.neo4j.server.rest.repr.InputFormatProvider;
import org.neo4j.server.rest.repr.OutputFormatProvider;
import org.neo4j.server.rest.repr.RepresentationFormatRepository;
import org.neo4j.server.rest.repr.formats.JsonFormat;
import org.neo4j.server.rrd.RrdDbProvider;
import org.neo4j.server.web.WebServerProvider;

import com.netoprise.neo4j.connection.Neo4JConnectionFactory;
import com.netoprise.neo4j.connection.Neo4jConnection;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

public class Neo4jConnectorServletContainer extends ServletContainer {

	private static final long serialVersionUID = 69696969L;
	
	/**
	 * Hooks up our shit wit da databazzz
	 */
	@Resource(mappedName = "jca/Neo4j/resource", type = Neo4JConnectionFactory.class)
	private Neo4JConnectionFactory neo4jConnectionFactory;

    @Override
    protected void configure( WebConfig wc, ResourceConfig rc, WebApplication wa )
    {
        super.configure( wc, rc, wa );

        // Get some stuff from neo4j
		Neo4jConnection connection;
		try {
			connection = neo4jConnectionFactory.getConnection();
		} catch (ResourceException e) {
			e.printStackTrace();
			return;
		}
		AbstractGraphDatabase db = (AbstractGraphDatabase) connection.getDatabase();
		
        Set<Object> singletons = rc.getSingletons();
        singletons.add( new LeaseManagerProvider() );
        singletons.add( new DatabaseProvider( new Database(db) ) );
        singletons.add( new GraphDatabaseServiceProvider( db ) );
        //singletons.add( new NeoServerProvider( server ) );
        
        Configurator conf = new EmbeddedServerConfigurator(db);
        singletons.add( new ConfigurationProvider(conf.configuration()) );
/*
        if ( server.getDatabase()
                .rrdDb() != null )
        {
            singletons.add( new RrdDbProvider( server.getDatabase()
                    .rrdDb() ) );
        }

        if ( server instanceof NeoServerWithEmbeddedWebServer )
        {
            singletons.add( new WebServerProvider( ( (NeoServerWithEmbeddedWebServer) server ).getWebServer() ) );
        }
*/
        singletons.add( new WebServerProvider(null) );
        
        PluginManager pMan = new RESTApiModule().getPlugins();
        RepresentationFormatRepository repository = new RepresentationFormatRepository(pMan);
        //RepresentationFormatRepository repository = new RepresentationFormatRepository( server.getExtensionManager() );

        singletons.add( new InputFormatProvider( repository ) );
        singletons.add( new OutputFormatProvider( repository ) );
        singletons.add( new PluginInvocatorProvider( pMan ) );
    }
}
