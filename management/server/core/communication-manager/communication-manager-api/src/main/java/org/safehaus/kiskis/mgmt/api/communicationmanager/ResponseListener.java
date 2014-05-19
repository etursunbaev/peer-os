package org.safehaus.kiskis.mgmt.api.communicationmanager;


import org.safehaus.kiskis.mgmt.shared.protocol.Response;


/**
 * This interface must be implemented to receive responses from agents.
 */
public interface ResponseListener {

    /**
     * Response arrival event
     *
     * @param response - received response
     */
    public void onResponse( Response response );
}
