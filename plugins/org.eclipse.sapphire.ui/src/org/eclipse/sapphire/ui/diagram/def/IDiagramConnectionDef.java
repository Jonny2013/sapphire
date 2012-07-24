/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.LineStyle;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;
import org.eclipse.sapphire.ui.diagram.def.internal.ToolPaletteCompartmentPossibleValuesService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@Label( standard = "diagram connection" )
@GenerateImpl

public interface IDiagramConnectionDef 
    
    extends ISapphirePartDef
    
{
    ModelElementType TYPE = new ModelElementType( IDiagramConnectionDef.class );
    
    // *** Id ***
    
    @Required
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, ISapphirePartDef.PROP_ID );
    
    // *** ImplicitConnection ***
    
    @Type( base = Boolean.class )
    @Label( standard = "Implicit Connection" )
    @XmlBinding( path = "implicit-connection" )
    @DefaultValue( text = "false" )

    ValueProperty PROP_IMPLICIT_CONNECTION = new ValueProperty( TYPE, "ImplicitConnection" );
    
    Value<Boolean> isImplicitConnection();
    void setImplicitConnection( String value );
    void setImplicitConnection( Boolean value );

    // *** ToolPaletteLabel ***
    
    @Label( standard = "tool palette item label" )
    @Localizable
    @XmlBinding( path = "tool-palette-label" )
    @Enablement( expr = "${!ImplicitConnection}" )
    
    ValueProperty PROP_TOOL_PALETTE_LABEL = new ValueProperty( TYPE, "ToolPaletteLabel" );
    
    Value<String> getToolPaletteLabel();
    void setToolPaletteLabel( String paletteLabel );
    
    // *** ToolPaletteDescription ***
    
    @Label( standard = "tool palette item description" )
    @Localizable
    @LongString
    @XmlBinding( path = "tool-palette-desc" )
    @Enablement( expr = "${!ImplicitConnection}" )
    
    ValueProperty PROP_TOOL_PALETTE_DESCRIPTION = new ValueProperty( TYPE, "ToolPaletteDescription" );
    
    Value<String> getToolPaletteDescription();
    void setToolPaletteDescription( String paletteDesc );    
        
    // *** ToolPaletteImage ***

    @Type( base = IDiagramImageChoice.class )
    @Label( standard = "tool palette item image" )
    @XmlBinding( path = "tool-palette-image" )
    @Enablement( expr = "${!ImplicitConnection}" )

    ElementProperty PROP_TOOL_PALETTE_IMAGE = new ElementProperty( TYPE, "ToolPaletteImage" );
    
    ModelElementHandle<IDiagramImageChoice> getToolPaletteImage();
    
    // *** ToolPaletteCompartment ***

    @Label( standard = "tool palette compartment" )
    @XmlBinding( path = "tool-palette-compartment" )    
    @DefaultValue( text = "Sapphire.Diagram.Palette.Connections" )
    @Service( impl = ToolPaletteCompartmentPossibleValuesService.class )
    @DependsOn("../PaletteCompartments/Id")
    @Enablement( expr = "${!ImplicitConnection}" )
    
    ValueProperty PROP_TOOL_PALETTE_COMPARTMENT = new ValueProperty( TYPE, "ToolPaletteCompartment" );
    
    Value<String> getToolPaletteCompartment();
    void setToolPaletteCompartment( String value );
    
    // *** Endpoint1 ***
    
    @Type( base = IDiagramConnectionEndpointDef.class )
    @XmlBinding( path = "endpoint1" )

    ImpliedElementProperty PROP_ENDPOINT_1 = new ImpliedElementProperty( TYPE, "Endpoint1" );
    
    IDiagramConnectionEndpointDef getEndpoint1();

    // *** Endpoint2 ***
    
    @Type( base = IDiagramConnectionEndpointDef.class )
    @XmlBinding( path = "endpoint2" )

    ImpliedElementProperty PROP_ENDPOINT_2 = new ImpliedElementProperty( TYPE, "Endpoint2" );
    
    IDiagramConnectionEndpointDef getEndpoint2();
        
    // *** LineWidth ***
    
    @Type( base = Integer.class )
    @Label( standard = "line width" )
    @XmlBinding( path = "line-width" )
    @DefaultValue( text = "1" )
    
    ValueProperty PROP_LINE_WIDTH = new ValueProperty( TYPE, "LineWidth" );
    
    Value<Integer> getLineWidth();
    void setLineWidth( String width );
    void setLineWidth( Integer width );
    
    // *** LineStyle ***
    
    @Type( base = LineStyle.class )
    @Label( standard = "line style")
    @XmlBinding( path = "line-style" )
    @DefaultValue( text = "solid" )
    
    ValueProperty PROP_LINE_STYLE = new ValueProperty( TYPE, "LineStyle" );
    
    Value<LineStyle> getLineStyle();
    void setLineStyle( String value );
    void setLineStyle( LineStyle value ) ;
    
    // *** LineColor ***
    
    @Type( base = Color.class )
    @Label( standard = "line color")
    @XmlBinding( path = "line-color")
    @DefaultValue( text = "#333399" )
    
    ValueProperty PROP_LINE_COLOR = new ValueProperty( TYPE, "LineColor" );
    
    Value<Color> getLineColor();
    void setLineColor( String value );
    void setLineColor( Color value );
}