/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.shape.def;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */
@GenerateImpl

public interface RectangleDef extends ContainerShapeDef 
{
	ModelElementType TYPE = new ModelElementType( RectangleDef.class );
	
    // *** CornerRadius ***
    
    @Type( base = Integer.class )
    @Label( standard = "corner radius" )
    @XmlBinding( path = "corner-radius" )
    @DefaultValue( text = "0" )
    
    ValueProperty PROP_CORNER_RADIUS = new ValueProperty( TYPE, "CornerRadius" );
    
    Value<Integer> getCornerRadius();
    void setCornerRadius( String value );
    void setCornerRadius( Integer value );
	
    // *** Border ***
    
    @Type( base = BorderDef.class )
    @XmlBinding( path = "border" )

    ElementProperty PROP_BORDER = new ElementProperty( TYPE, "Border" );
    
    ModelElementHandle<BorderDef> getBorder();
    
    // *** TopBorder ***
    
    @Type( base = BorderDef.class )
    @XmlBinding( path = "top-border" )

    ElementProperty PROP_TOP_BORDER = new ElementProperty( TYPE, "TopBorder" );
    
    ModelElementHandle<BorderDef> getTopBorder();
    
    // *** BottomBorder ***
    
    @Type( base = BorderDef.class )
    @XmlBinding( path = "bottom-border" )

    ElementProperty PROP_BOTTOM_BORDER = new ElementProperty( TYPE, "BottomBorder" );
    
    ModelElementHandle<BorderDef> getBottomBorder();
    
    // *** LeftBorder ***
    
    @Type( base = BorderDef.class )
    @XmlBinding( path = "left-border" )

    ElementProperty PROP_LEFT_BORDER = new ElementProperty( TYPE, "LeftBorder" );
    
    ModelElementHandle<BorderDef> getLeftBorder();
    
    // *** RightBorder ***
    
    @Type( base = BorderDef.class )
    @XmlBinding( path = "right-border" )

    ElementProperty PROP_RIGHT_BORDER = new ElementProperty( TYPE, "RightBorder" );
    
    ModelElementHandle<BorderDef> getRightBorder();
    
    // *** Background ***
    
    @Type
    ( 
        base = BackgroundDef.class, 
        possible = 
        { 
            SolidBackgroundDef.class, 
            GradientBackgroundDef.class
        }
    )    
    @Label( standard = "background" )
    @XmlElementBinding
    ( 
    	path = "background",
        mappings = 
        {
            @XmlElementBinding.Mapping( element = "color", type = SolidBackgroundDef.class ),
            @XmlElementBinding.Mapping( element = "gradient", type = GradientBackgroundDef.class )
        }
    )
    
    ElementProperty PROP_BACKGROUND = new ElementProperty( TYPE, "Background" );
    
    ModelElementHandle<BackgroundDef> getBackground();
    
    // ** ScrollHorizontally ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "scroll-horizontally" )
    @DefaultValue( text = "false" )
    @Label( standard = "scroll horizontally")
    
    ValueProperty PROP_SCROLL_HORIZONTALLY = new ValueProperty(TYPE, "ScrollHorizontally");
    
    Value<Boolean> isScrollHorizontally();
    void setScrollHorizontally( String value );
    void setScrollHorizontally( Boolean value );    
    
    // ** ScrollVertically ***
        
    @Type( base = Boolean.class )
    @XmlBinding( path = "scroll-vertically" )
    @DefaultValue( text = "false" )
    @Label( standard = "scroll vertically")
    
    ValueProperty PROP_SCROLL_VERTICALLY = new ValueProperty(TYPE, "ScrollVertically");
    
    Value<Boolean> isScrollVertically();
    void setScrollVertically( String value );
    void setScrollVertically( Boolean value );    
    
}