package io.subutai.core.peer.impl;


import java.util.Date;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.jaxrs.client.WebClient;

import com.google.common.base.Preconditions;

import io.subutai.common.environment.Containers;
import io.subutai.common.host.HostInterfaces;
import io.subutai.common.metric.ResourceHostMetrics;
import io.subutai.common.network.NetworkResourceImpl;
import io.subutai.common.network.UsedNetworkResources;
import io.subutai.common.peer.AlertEvent;
import io.subutai.common.peer.EnvironmentId;
import io.subutai.common.peer.PeerException;
import io.subutai.common.peer.PeerId;
import io.subutai.common.peer.PeerInfo;
import io.subutai.common.protocol.P2PConfig;
import io.subutai.common.protocol.P2PCredentials;
import io.subutai.common.protocol.P2pIps;
import io.subutai.common.protocol.TemplateKurjun;
import io.subutai.common.resource.PeerResources;
import io.subutai.common.security.PublicKeyContainer;
import io.subutai.common.security.WebClientBuilder;
import io.subutai.common.security.relation.RelationLinkDto;
import io.subutai.common.security.relation.RelationVerificationException;
import io.subutai.common.util.DateTimeParam;


/**
 * Peer REST client
 */
public class PeerWebClient
{
    private static final Logger LOG = LoggerFactory.getLogger( PeerWebClient.class );

    private final Object provider;
    private final PeerInfo peerInfo;
    private RemotePeerImpl remotePeer;


    public PeerWebClient( final Object provider, final PeerInfo peerInfo, final RemotePeerImpl remotePeer )
    {
        Preconditions.checkNotNull( peerInfo );
        Preconditions.checkNotNull( provider );
        Preconditions.checkNotNull( remotePeer );

        this.provider = provider;
        this.peerInfo = peerInfo;
        this.remotePeer = remotePeer;
    }


    public boolean ping()
    {
        Response response;
        try
        {
            remotePeer.checkRelation();

            String path = "/ping";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider, 3000, 5000, 1 );

            response = client.get();

            WebClientBuilder.checkResponse( response, Response.Status.OK );

            return true;
        }
        catch ( RelationVerificationException e )
        {
            LOG.error( e.getMessage(), e );

            return false;
        }
        catch ( Exception e )
        {
            return false;
        }
    }


    public PeerInfo getInfo() throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();

            String path = "/info";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider, 3000, 15000, 1 );

            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );

            response = client.get();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error getting peer info: %s", e.getMessage() ) );
        }

        return WebClientBuilder.checkResponse( response, PeerInfo.class );
    }


    public PublicKeyContainer createEnvironmentKeyPair( final RelationLinkDto envLink ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = "/pek";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );

            response = client.post( envLink );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error creating peer environment key: %s", e.getMessage() ) );
        }

        return WebClientBuilder.checkResponse( response, PublicKeyContainer.class );
    }


    public void updateEnvironmentPubKey( PublicKeyContainer publicKeyContainer ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = "/pek";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );
            response = client.put( publicKeyContainer );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error updating peer environment key: %s", e.getMessage() ) );
        }

        WebClientBuilder.checkResponse( response );
    }


    public HostInterfaces getInterfaces() throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = "/interfaces";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );
            response = client.get();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error getting interfaces: %s", e.getMessage() ) );
        }

        return WebClientBuilder.checkResponse( response, HostInterfaces.class );
    }


    public void resetP2PSecretKey( final P2PCredentials p2PCredentials ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = "/p2presetkey";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );
            response = client.post( p2PCredentials );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error resetting P2P secret key: %s", e.getMessage() ) );
        }

        WebClientBuilder.checkResponse( response );
    }


    public void joinP2PSwarm( final P2PConfig config ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = "/p2ptunnel";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.type( MediaType.APPLICATION_JSON );
            response = client.post( config );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error joining P2P swarm: %s", e.getMessage() ) );
        }

        WebClientBuilder.checkResponse( response );
    }


    public void joinOrUpdateP2PSwarm( final P2PConfig config ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = "/p2ptunnel";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.type( MediaType.APPLICATION_JSON );
            response = client.put( config );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error joining/updating P2P swarm: %s", e.getMessage() ) );
        }

        WebClientBuilder.checkResponse( response );
    }


    public void cleanupEnvironment( final EnvironmentId environmentId ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = String.format( "/cleanup/%s", environmentId.getId() );

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );
            response = client.delete();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error cleaning up environment: %s", e.getMessage() ) );
        }

        WebClientBuilder.checkResponse( response );
    }


    public ResourceHostMetrics getResourceHostMetrics() throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = "/resources";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );
            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );
            response = client.get();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error getting rh metrics: %s", e.getMessage() ) );
        }

        return WebClientBuilder.checkResponse( response, ResourceHostMetrics.class );
    }


    public void alert( final AlertEvent alert ) throws PeerException
    {
        try
        {
            remotePeer.checkRelation();
            String path = "/alert";

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );
            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );
            Response response = client.post( alert );

            WebClientBuilder.checkResponse( response, Response.Status.ACCEPTED );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error on alert: %s", e.getMessage() ) );
        }
    }


    public String getHistoricalMetrics( final String hostName, final Date startTime, final Date endTime )
            throws PeerException
    {
        Response response;

        try
        {
            remotePeer.checkRelation();
            final DateTimeParam startParam = new DateTimeParam( startTime );
            final DateTimeParam endParam = new DateTimeParam( endTime );
            String path = String.format( "/hmetrics/%s/%s/%s", hostName, startParam, endParam );

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.accept( MediaType.APPLICATION_JSON );

            response = client.get();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException(
                    String.format( "Error on retrieving historical metrics from remote peer: %s", e.getMessage() ) );
        }

        return WebClientBuilder.checkResponse( response, String.class );
    }


    public PeerResources getResourceLimits( final PeerId peerId ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = String.format( "/limits/%s", peerId.getId() );

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider, 3000, 15000, 1 );
            client.accept( MediaType.APPLICATION_JSON );

            response = client.get();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error on retrieving peer limits: %s", e.getMessage() ) );
        }

        return WebClientBuilder.checkResponse( response, PeerResources.class );
    }


    public void setupTunnels( final P2pIps p2pIps, final EnvironmentId environmentId ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = String.format( "/tunnels/%s", environmentId.getId() );

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider, 3000, 60000, 1 );
            client.type( MediaType.APPLICATION_JSON );

            response = client.post( p2pIps );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error setting up tunnels: %s", e.getMessage() ) );
        }

        WebClientBuilder.checkResponse( response );
    }


    public void addPeerEnvironmentPubKey( final String keyId, final String pubKeyRing ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = String.format( "/pek/add/%s", keyId );

            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider, 3000, 15000, 1 );
            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );
            response = client.post( pubKeyRing );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error adding PEK: %s", e.getMessage() ) );
        }

        WebClientBuilder.checkResponse( response );
    }


    public Containers getEnvironmentContainers( final EnvironmentId environmentId ) throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = String.format( "/containers/%s", environmentId.getId() );
            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.type( MediaType.APPLICATION_JSON );
            client.accept( MediaType.APPLICATION_JSON );
            response = client.get();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error on obtaining environment containers: %s", e.getMessage() ) );
        }

        return WebClientBuilder.checkResponse( response, Containers.class );
    }


    public UsedNetworkResources getUsedNetResources() throws PeerException
    {
        Response response;
        try
        {
            remotePeer.checkRelation();
            String path = "/netresources";
            WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

            client.accept( MediaType.APPLICATION_JSON );

            response = client.get();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException(
                    String.format( "Error obtaining reserved network resources: %s", e.getMessage() ) );
        }

        return WebClientBuilder.checkResponse( response, UsedNetworkResources.class );
    }


    public void reserveNetworkResource( final NetworkResourceImpl networkResource ) throws PeerException
    {
        String path = "/netresources";

        WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

        client.type( MediaType.APPLICATION_JSON );

        Response response;

        try
        {
            response = client.post( networkResource );
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error reserving network resources: %s", e.getMessage() ) );
        }

        WebClientBuilder.checkResponse( response );
    }


    public TemplateKurjun getTemplate( final String templateName ) throws PeerException
    {
        String path = String.format( "/template/%s/get", templateName );

        WebClient client = WebClientBuilder.buildPeerWebClient( peerInfo, path, provider );

        client.accept( MediaType.APPLICATION_JSON );

        Response response;

        try
        {
            response = client.get();
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new PeerException( String.format( "Error obtaining template : %s", e.getMessage() ) );
        }

        return WebClientBuilder.checkResponse( response, TemplateKurjun.class );
    }
}
