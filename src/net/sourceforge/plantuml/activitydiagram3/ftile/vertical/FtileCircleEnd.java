/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 * 
 *
 */
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class FtileCircleEnd extends AbstractFtile {

	private static final int SIZE = 20;

	private HColor borderColor;
	private HColor backColor;
	private final Swimlane swimlane;
	private double shadowing;

	@Override
	public Collection<Ftile> getMyChildren() {
		return Collections.emptyList();
	}

	public FtileCircleEnd(ISkinParam skinParam, HColor backColor, HColor borderColor, Swimlane swimlane, Style style) {
		super(skinParam);
		this.borderColor = borderColor;
		this.backColor = backColor;
		this.swimlane = swimlane;
		this.shadowing = style.value(PName.Shadowing).asDouble();
		this.backColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(), getIHtmlColorSet());
		this.borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(), getIHtmlColorSet());

	}

	public Set<Swimlane> getSwimlanes() {
		if (swimlane == null)
			return Collections.emptySet();

		return Collections.singleton(swimlane);
	}

	public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	public Swimlane getSwimlaneOut() {
		return swimlane;
	}

	public void drawU(UGraphic ug) {
		double xTheoricalPosition = 0;
		double yTheoricalPosition = 0;
		xTheoricalPosition = Math.round(xTheoricalPosition);
		yTheoricalPosition = Math.round(yTheoricalPosition);

		final UEllipse circle = new UEllipse(SIZE, SIZE);
		circle.setDeltaShadow(shadowing);
		ug = ug.apply(borderColor);
		final double thickness = 2.5;
		ug.apply(backColor.bg()).apply(new UStroke(1.5)).apply(new UTranslate(xTheoricalPosition, yTheoricalPosition))
				.draw(circle);

		final double size2 = (SIZE - thickness) / Math.sqrt(2);
		final double delta = (SIZE - size2) / 2;
		ug = ug.apply(new UStroke(thickness));
		ug.apply(new UTranslate(delta, delta)).draw(new ULine(size2, size2));
		ug.apply(new UTranslate(delta, SIZE - delta)).draw(new ULine(size2, -size2));

	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		return new FtileGeometry(SIZE, SIZE, SIZE / 2, 0);
	}

}
