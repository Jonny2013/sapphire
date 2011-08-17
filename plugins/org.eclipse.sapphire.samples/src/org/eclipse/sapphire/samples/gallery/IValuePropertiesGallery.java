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

package org.eclipse.sapphire.samples.gallery;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface IValuePropertiesGallery extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( IValuePropertiesGallery.class );
    
    // *** WhitespaceHandlingGallery ***
    
    @Type( base = IWhitespaceHandlingGallery.class )

    ImpliedElementProperty PROP_WHITESPACE_HANDLING_GALLERY = new ImpliedElementProperty( TYPE, "WhitespaceHandlingGallery" );
    
    IWhitespaceHandlingGallery getWhitespaceHandlingGallery();
    
    // *** DefaultValueGallery ***
    
    @Type( base = DefaultValueGallery.class )
    @XmlBinding( path = "default-value" )

    ImpliedElementProperty PROP_DEFAULT_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "DefaultValueGallery" );
    
    DefaultValueGallery getDefaultValueGallery();
    
    // *** InitialValueGallery ***
    
    @Type( base = InitialValueGallery.class )
    @XmlBinding( path = "initial-value" )

    ImpliedElementProperty PROP_INITIAL_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "InitialValueGallery" );
    
    InitialValueGallery getInitialValueGallery();
    
}
