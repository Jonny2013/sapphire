/******************************************************************************
 * Copyright (c) 2015 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.sapphire.ui.swt.gef.figures;

import org.eclipse.draw2d.geometry.Point;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */
public class BezierPoint {

	private int x;
	private int y;
	private int bezierDistanceBefore;
	private int bezierDistanceAfter;

	public BezierPoint() {
	}

	public BezierPoint(int x, int y) {
		setX(x);
		setY(y);
	}

	public BezierPoint(int x, int y, int bezierDistanceBefore, int bezierDistanceAfter) {
		setX(x);
		setY(y);
		setBezierDistanceBefore(bezierDistanceBefore);
		setBezierDistanceAfter(bezierDistanceAfter);
	}

	public final int getX() {
		return x;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final int getY() {
		return y;
	}

	public final void setY(int y) {
		this.y = y;
	}

	public final int getBezierDistanceBefore() {
		return bezierDistanceBefore;
	}

	public final void setBezierDistanceBefore(int bezierDistanceBefore) {
		this.bezierDistanceBefore = bezierDistanceBefore;
	}

	public final int getBezierDistanceAfter() {
		return bezierDistanceAfter;
	}

	public final void setBezierDistanceAfter(int bezierDistanceAfter) {
		this.bezierDistanceAfter = bezierDistanceAfter;
	}

	public final Point createDraw2dPoint() {
		return new Point(getX(), getY());
	}

	public final void copyToDraw2dPoint(Point target) {
		target.x = getX();
		target.y = getY();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof BezierPoint) {
			BezierPoint other = (BezierPoint) o;
			return getX() == other.getX() && getY() == other.getY() && getBezierDistanceBefore() == other.getBezierDistanceBefore()
					&& getBezierDistanceAfter() == other.getBezierDistanceAfter();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (getX() * getY()) ^ (getX() + getY());
	}
}
