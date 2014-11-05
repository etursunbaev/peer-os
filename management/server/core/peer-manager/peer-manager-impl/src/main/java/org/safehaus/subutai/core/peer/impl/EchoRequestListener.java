package org.safehaus.subutai.core.peer.impl;


import org.safehaus.subutai.core.peer.api.Payload;
import org.safehaus.subutai.core.peer.api.RequestListener;
import org.safehaus.subutai.core.peer.impl.RecipientType;


/**
 * Simple echo request listener for testing purposes
 */
public class EchoRequestListener extends RequestListener
{
    protected EchoRequestListener()
    {
        super( RecipientType.ECHO_LISTENER.name() );
    }


    @Override
    public Object onRequest( final Payload payload ) throws Exception
    {
        return payload.getMessage( String.class );
    }
}