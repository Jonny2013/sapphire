/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.presentation;

import org.eclipse.draw2d.IFigure;
import org.eclipse.sapphire.Color;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.diagram.shape.def.FontDef;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TextPresentation extends ShapePresentation 
{
	public TextPresentation(DiagramPresentation parent, TextPart textPart, DiagramResourceCache resourceCache)
	{
		super(parent, textPart, resourceCache);
	}
	
	public Color getTextColor()
	{
		return part().getTextColor();
	}
	
	public String getContent()
	{
		return part().getContent();
	}

	public FontDef getFontDef()
	{
		return part().getFontDef();
	}
	
	public boolean truncatable()
	{
		return part().truncatable();
	}
	
	@Override
	public void refreshVisuals()
	{
		super.refreshVisuals();
		if (this.getFigure() != null)
		{
			TextFigure textFigure = (TextFigure)getFigure();
			textFigure.setText(part().getContent());
		}
	}
	
	@Override
	public TextPart part()
	{
		return (TextPart) super.part();
	}
	
	@Override
    public void render()
    {
		IFigure figure = null;
		if (visible())
		{
			figure = new TextFigure(getResourceCache(), this);
		}
		setFigure(figure);
    }
	
}
