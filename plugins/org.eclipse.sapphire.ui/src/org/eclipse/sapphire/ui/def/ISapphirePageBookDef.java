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

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "page book" )

public interface ISapphirePageBookDef

    extends IFormPartDef
    
{
    ModelElementType TYPE = new ModelElementType( ISapphirePageBookDef.class );
    
    // *** Pages ***
    
    @Label( standard = "pages" )
    @Type( base = ISapphirePageBookKeyMapping.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "panel", type = ISapphirePageBookKeyMapping.class ) )
    
    ListProperty PROP_PAGES = new ListProperty( TYPE, "Pages" );
    
    ModelElementList<ISapphirePageBookKeyMapping> getPages();

    // *** DefaultPage ***
    
    @Type( base = ISapphireCompositeDef.class )
    @Label( standard = "default page" )
    @XmlBinding( path = "default-panel" )
    
    ImpliedElementProperty PROP_DEFAULT_PAGE = new ImpliedElementProperty( TYPE, "DefaultPage" );
    
    ISapphireCompositeDef getDefaultPage();

}