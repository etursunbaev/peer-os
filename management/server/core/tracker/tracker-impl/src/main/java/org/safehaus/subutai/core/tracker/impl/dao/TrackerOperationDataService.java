package org.safehaus.subutai.core.tracker.impl.dao;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.safehaus.subutai.common.tracker.TrackerOperationView;
import org.safehaus.subutai.core.tracker.impl.TrackerOperationImpl;
import org.safehaus.subutai.core.tracker.impl.TrackerOperationViewImpl;
import org.safehaus.subutai.core.tracker.impl.model.TrackerOperationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by talas on 12/7/14.
 */
public class TrackerOperationDataService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( TrackerOperationDataService.class );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    EntityManagerFactory emf;


    public TrackerOperationDataService( final EntityManagerFactory emf )
    {
        this.emf = emf;
        EntityManager em = emf.createEntityManager();
        em.close();
    }


    public List<TrackerOperationEntity> getAll()
    {
        List<TrackerOperationEntity> result = new ArrayList<>();
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            result = em.createQuery( "SELECT to FROM TrackerOperationEntity to", TrackerOperationEntity.class )
                       .getResultList();

            em.getTransaction().commit();
        }
        catch ( PersistenceException e )
        {
            LOGGER.error( "Error getting all TrackerOperations.", e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
        }
        finally
        {
            em.close();
        }
        return result;
    }


    public TrackerOperationView getTrackerOperation( String source, final UUID operationTrackId )
    {
        TrackerOperationEntity result = null;
        source = source.toUpperCase();
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            TypedQuery<TrackerOperationEntity> query = em.createQuery(
                    "SELECT to FROM TrackerOperationEntity to WHERE to.source = :source AND to.operationTrackId = "
                            + ":operationTrackId", TrackerOperationEntity.class );
            query.setParameter( "source", source );
            query.setParameter( "operationTrackId", operationTrackId.toString() );
            result = query.getSingleResult();

            em.getTransaction().commit();
        }
        catch ( PersistenceException e )
        {
            LOGGER.error( "Error .", e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
        }
        finally
        {
            em.close();
        }
        if ( result != null )
        {
            return createTrackerOperation( result.getInfo() );
        }
        else
        {
            return null;
        }
    }


    private TrackerOperationViewImpl createTrackerOperation( String infoClob )
    {
        if ( infoClob != null && infoClob.length() > 0 )
        {
            TrackerOperationImpl po = GSON.fromJson( infoClob, TrackerOperationImpl.class );
            return new TrackerOperationViewImpl( po );
        }
        return null;
    }


    public void saveTrackerOperation( String source, final TrackerOperationImpl po ) throws SQLException
    {
        source = source.toUpperCase();
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            TrackerOperationEntity entity = extractFromTrackerOperationImpl( source, po );
            em.merge( entity );

            em.getTransaction().commit();
        }
        catch ( PersistenceException e )
        {
            LOGGER.error( "Error merging TrackerOperationEntity.", e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
            throw new SQLException( e );
        }
        finally
        {
            em.close();
        }
    }


    private TrackerOperationEntity extractFromTrackerOperationImpl( String source, TrackerOperationImpl po )
    {
        source = source.toUpperCase();
        return new TrackerOperationEntity( source, po.getId().toString(), po.createDate().getTime(),
                GSON.toJson( po ) );
    }


    public List<TrackerOperationView> getTrackerOperations( String source, final Date fromDate, final Date toDate,
                                                            final int limit ) throws SQLException
    {
        source = source.toUpperCase();
        List<TrackerOperationView> result = new ArrayList<>();
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            TypedQuery<String> query = em.createQuery(
                    "select to.info from TrackerOperationEntity to where to.source = :source and to.ts >= :fromDate "
                            + "and to.ts <= :toDate  order by to.ts desc", String.class );
            query.setParameter( "source", source );
            query.setParameter( "fromDate", fromDate.getTime() );
            query.setParameter( "toDate", toDate.getTime() );
            query.setMaxResults( limit );
            List<String> infoList = query.getResultList();
            for ( final String info : infoList )
            {
                result.add( createTrackerOperation( info ) );
            }

            em.getTransaction().commit();
        }
        catch ( Exception e )
        {
            LOGGER.error( "Error in getTrackerOperations.", e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
            throw new SQLException( e );
        }
        finally
        {
            em.close();
        }
        return result;
    }


    public List<String> getTrackerOperationSources() throws SQLException
    {
        List<String> result = new ArrayList<>();
        EntityManager em = emf.createEntityManager();
        try
        {
            em.getTransaction().begin();

            TypedQuery<String> query =
                    em.createQuery( "select distinct to.source from TrackerOperationEntity to", String.class );
            result = query.getResultList();

            em.getTransaction().commit();
        }
        catch ( PersistenceException e )
        {
            LOGGER.error( "Error getting trackerOperationSources.", e );
            if ( em.getTransaction().isActive() )
            {
                em.getTransaction().rollback();
            }
            throw new SQLException( e );
        }
        finally
        {
            em.close();
        }
        return result;
    }
}