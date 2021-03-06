package io.subutai.core.environment.impl.workflow.modification.steps;


import java.util.Set;
import java.util.concurrent.Callable;

import io.subutai.common.environment.Environment;
import io.subutai.common.peer.Peer;
import io.subutai.common.peer.PeerException;
import io.subutai.common.tracker.TrackerOperation;
import io.subutai.common.util.PeerUtil;
import io.subutai.core.environment.impl.entity.LocalEnvironment;


public class UpdateEtcHostsStep
{
    private final LocalEnvironment environment;
    private final String oldHostname;
    private final String newHostname;
    private final TrackerOperation trackerOperation;
    protected PeerUtil<Object> peerUtil = new PeerUtil<>();


    public UpdateEtcHostsStep( final LocalEnvironment environment, final String oldHostname, final String newHostname,
                               TrackerOperation trackerOperation )
    {
        this.environment = environment;
        this.oldHostname = oldHostname;
        this.newHostname = newHostname;
        this.trackerOperation = trackerOperation;
    }


    public Environment execute() throws PeerException
    {
        Set<Peer> peers = environment.getPeers();


        for ( final Peer peer : peers )
        {
            peerUtil.addPeerTask( new PeerUtil.PeerTask<>( peer, new Callable<Object>()
            {
                @Override
                public Object call() throws Exception
                {
                    peer.updateEtcHostsWithNewContainerHostname( environment.getEnvironmentId(), oldHostname,
                            newHostname );

                    return null;
                }
            } ) );
        }

        PeerUtil.PeerTaskResults<Object> peerResults = peerUtil.executeParallel();


        for ( PeerUtil.PeerTaskResult peerResult : peerResults.getResults() )
        {
            if ( peerResult.hasSucceeded() )
            {
                trackerOperation.addLog( String.format( "Updated hosts on peer %s", peerResult.getPeer().getName() ) );
            }
            else
            {
                trackerOperation.addLog(
                        String.format( "Failed to update hosts on peer %s. Reason: %s", peerResult.getPeer().getName(),
                                peerResult.getFailureReason() ) );
            }
        }


        return environment;
    }
}
