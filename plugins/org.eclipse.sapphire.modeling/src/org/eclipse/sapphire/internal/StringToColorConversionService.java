/******************************************************************************
 * Copyright (c) 2015 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - adapted from ValueSerializationService
 ******************************************************************************/

package org.eclipse.sapphire.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sapphire.Color;
import org.eclipse.sapphire.ConversionService;

/**
 * ConversionService implementation for String to Color conversions.
 * 
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StringToColorConversionService extends ConversionService<String,Color>
{
    private static Map<String, Color> namedColors;
    
    static
    {
        namedColors = new HashMap<String, Color>();
        namedColors.put( "aqua", new Color( 0, 255, 255 ) );
        namedColors.put( "black", new Color( 0, 0, 0 ) );
        namedColors.put( "blue", new Color( 0, 0, 255 ) );
        namedColors.put( "fuchsia", new Color( 255, 0, 255 ) );
        namedColors.put( "gray", new Color( 128, 128, 128 ) );
        namedColors.put( "green", new Color( 0, 128, 0 ) );
        namedColors.put( "lime", new Color( 0, 255, 0 ) );
        namedColors.put( "maroon", new Color( 128, 0, 0 ) );
        namedColors.put( "navy", new Color( 0, 0, 128 ) );
        namedColors.put( "olive", new Color( 128, 128, 0 ) );
        namedColors.put( "orange", new Color( 255, 165, 0 ) );
        namedColors.put( "purple", new Color( 128, 0, 128 ) );
        namedColors.put( "red", new Color( 255, 0, 0 ) );
        namedColors.put( "silver", new Color( 192, 192, 192 ) );
        namedColors.put( "teal", new Color( 0, 128, 128 ) );
        namedColors.put( "white", Color.WHITE );
        namedColors.put( "yellow", new Color( 255, 255, 0 ) );
    }
    
    public StringToColorConversionService()
    {
        super( String.class, Color.class );
    }

    @Override
    public Color convert( final String string )
    {
        Color result = namedColors.get( string.toLowerCase() );
        
        if( result == null )
        {
            try
            {
                result = new Color( string );
            }
            catch( final IllegalArgumentException e )
            {
                // Intentionally ignored
            }
        }
        
        return result;
    }

}
