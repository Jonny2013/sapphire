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

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.xml.annotations.GenerateXmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "page book" )
@GenerateXmlBinding

public interface ISapphirePageBookKeyMapping

    extends ISapphireCompositeDef
    
{
    ModelElementType TYPE = new ModelElementType( ISapphirePageBookKeyMapping.class );
    
    // *** Key ***
    
    @Label( standard = "key" )
    @NonNullValue
    @XmlBinding( path = "key" )
    
    ValueProperty PROP_KEY = new ValueProperty( TYPE, "Key" );
    
    Value<String> getKey();
    void setKey( String key );

}