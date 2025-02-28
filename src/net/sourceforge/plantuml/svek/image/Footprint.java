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
package net.sourceforge.plantuml.svek.image;

import static net.sourceforge.plantuml.utils.ObjectUtils.instanceOfAny;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UBackground;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UEmpty;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicNo;
import net.sourceforge.plantuml.ugraphic.UHorizontalLine;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class Footprint {

	private final StringBounder stringBounder;

	public Footprint(StringBounder stringBounder) {
		this.stringBounder = stringBounder;

	}

	class MyUGraphic extends UGraphicNo {

		private final List<Point2D.Double> all;

		public MyUGraphic() {
			super(stringBounder, new UTranslate());
			this.all = new ArrayList<>();
		}

		private MyUGraphic(MyUGraphic other, UChange change) {
			// super(other, change);
			super(other.getStringBounder(),
					change instanceof UTranslate ? other.getTranslate().compose((UTranslate) change)
							: other.getTranslate());
			if (!instanceOfAny(change, UBackground.class, HColor.class, UStroke.class, UTranslate.class))
				throw new UnsupportedOperationException(change.getClass().toString());

			this.all = other.all;
		}

		public UGraphic apply(UChange change) {
			return new MyUGraphic(this, change);
		}

		public void draw(UShape shape) {
			final double x = getTranslate().getDx();
			final double y = getTranslate().getDy();
			if (shape instanceof UText) {
				drawText(x, y, (UText) shape);
			} else if (shape instanceof UHorizontalLine) {
				// Definitively a Horizontal line
//				line.drawTitleInternalForFootprint(this, x, y);
			} else if (shape instanceof ULine) {
				// Probably a Horizontal line
			} else if (shape instanceof UImage) {
				drawImage(x, y, (UImage) shape);
			} else if (shape instanceof UPath) {
				drawPath(x, y, (UPath) shape);
			} else if (shape instanceof URectangle) {
				drawRectangle(x, y, (URectangle) shape);
			} else if (shape instanceof UEllipse) {
				drawEllipse(x, y, (UEllipse) shape);
			} else if (shape instanceof UEmpty) {
				drawEmpty(x, y, (UEmpty) shape);
			} else {
				throw new UnsupportedOperationException(shape.getClass().toString());
			}
		}

		public ColorMapper getColorMapper() {
			return new ColorMapperIdentity();
		}

		private void addPoint(double x, double y) {
			all.add(new Point2D.Double(x, y));
		}

		private void drawText(double x, double y, UText text) {
			final Dimension2D dim = getStringBounder().calculateDimension(text.getFontConfiguration().getFont(),
					text.getText());
			y -= dim.getHeight() - 1.5;
			addPoint(x, y);
			addPoint(x, y + dim.getHeight());
			addPoint(x + dim.getWidth(), y);
			addPoint(x + dim.getWidth(), y + dim.getHeight());
		}

		private void drawImage(double x, double y, UImage image) {
			addPoint(x, y);
			addPoint(x, y + image.getHeight());
			addPoint(x + image.getWidth(), y);
			addPoint(x + image.getWidth(), y + image.getHeight());
		}

		private void drawPath(double x, double y, UPath path) {
			addPoint(x + path.getMinX(), y + path.getMinY());
			addPoint(x + path.getMaxX(), y + path.getMaxY());
		}

		private void drawRectangle(double x, double y, URectangle rect) {
			addPoint(x, y);
			addPoint(x + rect.getWidth(), y + rect.getHeight());
		}

		private void drawEllipse(double x, double y, UEllipse rect) {
			addPoint(x, y);
			addPoint(x + rect.getWidth(), y + rect.getHeight());
		}

		private void drawEmpty(double x, double y, UEmpty rect) {
			addPoint(x, y);
			addPoint(x + rect.getWidth(), y + rect.getHeight());
		}
	}

	public ContainingEllipse getEllipse(UDrawable drawable, double alpha) {
		final MyUGraphic ug = new MyUGraphic();
		drawable.drawU(ug);
		final List<Point2D.Double> all = ug.all;
		final ContainingEllipse circle = new ContainingEllipse(alpha);
		for (Point2D pt : all) {
			circle.append(pt);
		}
		return circle;
	}

	// public void drawDebug(UGraphic ug, double dx, double dy, TextBlock text) {
	// final MyUGraphic mug = new MyUGraphic();
	// text.drawU(mug, dx, dy);
	// for (Point2D pt : mug.all) {
	// ug.draw(pt.getX(), pt.getY(), new URectangle(1, 1));
	// }
	//
	// }

}
