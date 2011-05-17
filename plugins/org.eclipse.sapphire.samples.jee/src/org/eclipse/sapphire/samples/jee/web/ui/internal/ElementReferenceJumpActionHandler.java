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

package org.eclipse.sapphire.samples.jee.web.ui.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.ui.SapphireJumpActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPagePart;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ElementReferenceJumpActionHandler extends SapphireJumpActionHandler
{
    @Override
    protected boolean computeEnablementState()
    {
        if( super.computeEnablementState() == true )
        {
            return ( reference().resolve() != null );
        }
        
        return false;
    }

    @Override
    protected Object run( final SapphireRenderingContext context )
    {
        final ReferenceValue<String,IModelElement> ref = reference();
        final IModelElement element = ref.resolve();
        
        if( element != null )
        {
            final MasterDetailsEditorPagePart page = getPart().nearest( MasterDetailsEditorPagePart.class );
            final MasterDetailsContentNode root = page.getContentOutline().getRoot();
            final MasterDetailsContentNode node = root.findNodeByModelElement( element );
            
            if( node != null )
            {
                node.select();
            }
        }
        
        return null;
    }
    
    @SuppressWarnings( "unchecked" )
    private ReferenceValue<String,IModelElement> reference()
    {
        return (ReferenceValue<String,IModelElement>) getModelElement().<String>read( getProperty() );
    }
    
}
