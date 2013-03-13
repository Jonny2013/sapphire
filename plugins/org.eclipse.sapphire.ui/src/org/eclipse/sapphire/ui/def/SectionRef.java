/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.FoldingXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.ui.def.internal.SectionReferenceService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "section reference" )
@XmlBinding( path = "section-ref" )

public interface SectionRef extends FormComponentDef
{
    ModelElementType TYPE = new ModelElementType( SectionRef.class );
    
    // *** Section ***

    @Reference( target = SectionDef.class )
    @Service( impl = SectionReferenceService.class )
    @Label( standard = "section" )
    @PossibleValues( property = "/PartDefs[#type=SectionDef]/Id" )
    @Required
    @MustExist
    @CustomXmlValueBinding( impl = FoldingXmlValueBindingImpl.class, params = "section" )
    
    ValueProperty PROP_SECTION = new ValueProperty( TYPE, "Section" );
    
    ReferenceValue<String,SectionDef> getSection();
    void setSection( String value );
    
    // *** Params ***
    
    @Label( standard = "params" )
    @Type( base = ISapphireParam.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "param", type = ISapphireParam.class ) )
    
    ListProperty PROP_PARAMS = new ListProperty( TYPE, "Params" );
    
    ModelElementList<ISapphireParam> getParams();

}