package io.subutai.core.peer.ui.forms;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.subutai.common.peer.PeerInfo;
import io.subutai.common.peer.PeerPolicy;
import io.subutai.core.peer.api.LocalPeer;
import io.subutai.core.peer.api.PeerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


/**
 * The Integer fields of PeerPolicy class is autogenerated in this window TODO If types other than Integer need to be
 * added, then this window should be extended to support them
 */
public class PolicyManagerWindow extends Window
{

    private PeerInfo localPeerInfo;
    private PeerInfo remotePeerInfo;
    private static String WINDOW_CAPTION;
    private PeerPolicy peerPolicy;
    private PeerManager peerManager;
    private List<HorizontalLayout> policyRules;
    private static final Logger LOG = LoggerFactory.getLogger( PolicyManagerWindow.class );


    public PolicyManagerWindow( final PeerManager peerManager, final PeerInfo remotePeerInfo )
    {
        this.peerManager = peerManager;
        this.remotePeerInfo = remotePeerInfo;
        LocalPeer localPeer = peerManager.getLocalPeer();
        localPeerInfo = localPeer.getPeerInfo();
        setWindowCaption( "Manage Peer Policy for \"" + this.remotePeerInfo.getName() + "\"" );
        setCaption( getWindowCaption() );
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing( true );
        verticalLayout.setMargin( true );
        HorizontalLayout saveCancelButtonLayout = new HorizontalLayout();
        saveCancelButtonLayout.setSpacing( true );
        Button cancelButton = createCancelButton();
        Button saveButton = createSaveButton();

        peerPolicy = localPeerInfo.getPeerPolicy( remotePeerInfo.getId() );
        if ( peerPolicy == null )
        {
            peerPolicy = new PeerPolicy( remotePeerInfo.getId() );
        }

        policyRules = generateRulesByPolicy( peerPolicy );

        for ( HorizontalLayout rules : policyRules )
        {
            verticalLayout.addComponent( rules );
        }
        saveCancelButtonLayout.addComponent( cancelButton );
        saveCancelButtonLayout.addComponent( saveButton );
        verticalLayout.addComponent( saveCancelButtonLayout );
        this.setContent( verticalLayout );
        setWidthUndefined();
        this.center();
    }


    private List<HorizontalLayout> generateRulesByPolicy( final PeerPolicy peerPolicy )
    {
        List<HorizontalLayout> rules = new ArrayList<>();
        Class<?> peerPolicyClass;
        try
        {
            peerPolicyClass = Class.forName( PeerPolicy.class.getCanonicalName() );
        }
        catch ( ClassNotFoundException e )
        {
            LOG.error( "Error retrieving class for PeerPolicy", e );
            return rules;
        }
        Field[] fields = peerPolicyClass.getDeclaredFields();

        for ( Field field : fields )
        {
            if ( !field.getType().equals( int.class ) )
            {
                continue;
            }
            HorizontalLayout rule = new HorizontalLayout();
            rule.setSpacing( true );
            TextField textField = new TextField( getCaptionNameByVariableName( field.getName() ) );
            textField.setId( field.getName() );
            if ( peerPolicy != null )
            {
                Object value = runGetter( field, peerPolicy );
                if ( value == null )
                {
                    value = "";
                }
                textField.setValue( value.toString() );
            }
            rule.addComponent( textField );
            rules.add( rule );
        }
        return rules;
    }


    private String getCaptionNameByVariableName( final String name )
    {
        StringBuilder captionName = new StringBuilder( "" );
        String[] r = name.split( "(?=\\p{Upper})" );
        for ( String word : r )
        {
            captionName.append( word ).append( " " );
        }
        return Character.toUpperCase( captionName.charAt( 0 ) ) + captionName.substring( 1, captionName.length() - 1 )
                + ":";
    }


    public static Object runGetter( Field field, Object object )
    {
        Class clazz = object.getClass();
        for ( Method method : clazz.getMethods() )
        {
            if ( ( method.getName().startsWith( "get" ) ) && ( method.getName().length() == ( field.getName().length()
                    + 3 ) ) && method.getName().toLowerCase().endsWith( field.getName().toLowerCase() ) )
            {
                try
                {
                    return method.invoke( object );
                }
                catch ( IllegalAccessException e )
                {
                    LOG.error( "Could not determine method IllegalAccessException: " + method.getName() );
                }
                catch ( InvocationTargetException e )
                {
                    LOG.error( "Could not determine method InvocationTargetException: " + method.getName() );
                }
            }
        }
        return null;
    }


    public static void runSetter( Field field, Object object, int value )
    {
        Class clazz = object.getClass();
        for ( Method method : clazz.getMethods() )
        {
            if ( ( method.getName().startsWith( "set" ) ) && ( method.getName().length() == ( field.getName().length()
                    + 3 ) ) && method.getName().toLowerCase().endsWith( field.getName().toLowerCase() ) )
            {
                try
                {
                    method.invoke( object, value );
                }
                catch ( IllegalAccessException e )
                {
                    LOG.error( "Could not determine method IllegalAccessException: " + method.getName() );
                }
                catch ( InvocationTargetException e )
                {
                    LOG.error( "Could not determine method InvocationTargetException: " + method.getName() );
                }
            }
        }
    }


    private Button createCancelButton()
    {
        Button cancelButton = new Button( "Cancel" );
        cancelButton.addClickListener( new Button.ClickListener()
        {
            @Override
            public void buttonClick( final Button.ClickEvent clickEvent )
            {
                close();
            }
        } );
        return cancelButton;
    }


    private Button createSaveButton()
    {
        Button saveButton = new Button( "Save" );
        saveButton.addClickListener( new Button.ClickListener()
        {
            @Override
            public void buttonClick( final Button.ClickEvent clickEvent )
            {
                savePolicyAction();
            }
        } );
        return saveButton;
    }


    private void savePolicyAction()
    {
        Class<?> peerPolicyClass = null;
        try
        {
            peerPolicyClass = Class.forName( PeerPolicy.class.getCanonicalName() );
        }
        catch ( ClassNotFoundException e )
        {
            LOG.error( "Error getting PeerPolicy class object", e );
            return;
        }
        Field[] fields = peerPolicyClass.getDeclaredFields();

        for ( HorizontalLayout ruleLayout : policyRules )
        {
            TextField inputField = ( TextField ) ruleLayout.getComponent( 0 );
            if ( inputField.getValue().isEmpty() )
            {
                continue;
            }
            String value = inputField.getValue();
            for ( Field field : fields )
            {
                if ( field.getName().equals( inputField.getId() ) )
                {
                    try
                    {
                        runSetter( field, peerPolicy, Integer.valueOf( value ) );
                    }
                    catch ( Exception e )
                    {
                        Notification.show( value + " is not an appropriate value for " + field.getName()
                                + "! It should be \"" + field.getType().getSimpleName() + "\" type." );
                        return;
                    }
                    break;
                }
            }
        }

        localPeerInfo.setPeerPolicy( peerPolicy );
        boolean success = peerManager.update( localPeerInfo );
        if ( success )
        {
            close();
        }
        else
        {
            Notification.show( "Failed to update policy!" );
        }
    }


    private static synchronized void setWindowCaption( String caption )
    {
        WINDOW_CAPTION = caption;
    }


    private static synchronized String getWindowCaption()
    {
        return WINDOW_CAPTION;
    }
}
