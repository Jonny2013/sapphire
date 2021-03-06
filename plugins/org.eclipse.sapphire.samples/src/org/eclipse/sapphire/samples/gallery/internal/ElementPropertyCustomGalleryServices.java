/******************************************************************************
 * Copyright (c) 2015 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.gallery.internal;

import java.util.Set;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleTypesService;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.samples.gallery.ElementPropertyCustomGallery;
import org.eclipse.sapphire.samples.gallery.IChildElement;
import org.eclipse.sapphire.samples.gallery.IChildElementWithEnum;
import org.eclipse.sapphire.samples.gallery.IChildElementWithInteger;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ElementPropertyCustomGalleryServices
{
    private ElementPropertyCustomGalleryServices() {}
    
    public static final class ElementPossibleTypesService extends PossibleTypesService
    {
        @Override
        protected void initPossibleTypesService()
        {
            final Listener listener = new FilteredListener<PropertyContentEvent>()
            {
                @Override
                protected void handleTypedEvent( final PropertyContentEvent event )
                {
                    refresh();
                }
            };
            
            final ElementPropertyCustomGallery gallery = context( ElementPropertyCustomGallery.class );
            gallery.property( ElementPropertyCustomGallery.PROP_ALLOW_CHILD_ELEMENT_WITH_INTEGER ).attach( listener );
            gallery.property( ElementPropertyCustomGallery.PROP_ALLOW_CHILD_ELEMENT_WITH_ENUM ).attach( listener );
        }
        
        @Override
        protected void compute( final Set<ElementType> types )
        {
            final ElementPropertyCustomGallery gallery = context( ElementPropertyCustomGallery.class );
            
            types.add( IChildElement.TYPE );
            
            if( gallery.getAllowChildElementWithInteger().content() )
            {
                types.add( IChildElementWithInteger.TYPE );
            }
            
            if( gallery.getAllowChildElementWithEnum().content() )
            {
                types.add( IChildElementWithEnum.TYPE );
            }
        }
    }
    
}
