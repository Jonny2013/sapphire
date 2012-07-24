/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.xml;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlNamespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlNamespaces;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class StandardXmlNamespaceResolver extends XmlNamespaceResolver
{
    private final ModelElementType type;
    
    public StandardXmlNamespaceResolver( final ModelElementType type )
    {
        this.type = type;
    }
    
    @Override
    public String resolve( final String prefix )
    {
        final XmlNamespaces xmlNamespacesAnnotation = this.type.getAnnotation( XmlNamespaces.class );

        if( xmlNamespacesAnnotation != null )
        {
            for( XmlNamespace xmlNamespaceAnnotation : xmlNamespacesAnnotation.value() )
            {
                if( xmlNamespaceAnnotation.prefix().equals( prefix ) )
                {
                    return xmlNamespaceAnnotation.uri();
                }
            }
        }
        
        final XmlNamespace xmlNamespaceAnnotation = this.type.getAnnotation( XmlNamespace.class );
        
        if( xmlNamespaceAnnotation != null )
        {
            if( xmlNamespaceAnnotation.prefix().equals( prefix ) )
            {
                return xmlNamespaceAnnotation.uri();
            }
        }

        final XmlRootBinding xmlRootBindingAnnotation = this.type.getAnnotation( XmlRootBinding.class );
        
        if( xmlRootBindingAnnotation != null )
        {
            if( xmlRootBindingAnnotation.defaultPrefix().equals( prefix ) )
            {
                return xmlRootBindingAnnotation.namespace();
            }
        }
        
        return null;
    }
    
}