package io.subutai.common.util;


import java.util.Set;

import com.google.common.base.Preconditions;

import io.subutai.common.peer.InterfacePattern;


/**
 * N2N utils.
 */
public abstract class N2NUtil
{
    public static String N2N_SUBNET_MASK = "255.255.255.0";
    public static InterfacePattern N2N_SUBNET_INTERFACES_PATTERN = new InterfacePattern( "ip", "^10.*" );


    public static String findFreeSubnet( final Set<String> excludedSubnets )
    {
        String result = null;
        int i = 11;
        int j = 0;

        while ( result == null && i < 254 )
        {
            String s = String.format( "10.%d.%d.0", i, j );
            if ( !excludedSubnets.contains( s ) )
            {
                result = s;
            }

            j++;
            if ( j > 254 )
            {
                i++;
                j = 0;
            }
        }

        return result;
    }


    public static String findFreeAddress( Set<String> excludedAddresses )
    {
        Preconditions.checkNotNull( excludedAddresses, "Excepted address set could not be null." );
        Preconditions.checkArgument( !excludedAddresses.isEmpty(),
                "Excluded address set should contains at least one address." );

        String fmt = excludedAddresses.iterator().next().replaceAll( ".\\d$", ".%s" );

        String result = null;
        for ( int i = 1; i < 255; i++ )
        {
            String nextAddress = String.format( fmt, i );
            if ( !excludedAddresses.contains( nextAddress ) )
            {
                result = nextAddress;
                break;
            }
        }

        return result;
    }


    public static String generateCommunityName( final String ip )
    {
        return String.format( "com_%s", ip.replace( ".", "_" ) );
    }


    public static String generateInterfaceName( final String ip )
    {
        return String.format( "n2n_%s", ip.replace( ".", "_" ) );
    }
}