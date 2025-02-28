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

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.SkinParamColors;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.creole.SheetBlock2;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;

public class FtileBoxOld extends AbstractFtile {

	// private final ClockwiseTopRightBottomLeft paddingUnused;

	private final TextBlock tb;
	private double roundCorner = 25;
	private final double shadowing;
	private final HorizontalAlignment horizontalAlignment;
	private double minimumWidth = 0;

	private final LinkRendering inRendering;
	private final Swimlane swimlane;
	private final BoxStyle boxStyle;

	private final HColor borderColor;
	private final HColor backColor;
	private final Style style;

	static public StyleSignatureBasic getDefaultStyleDefinitionActivity() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.activity);
	}

	static public StyleSignatureBasic getDefaultStyleDefinitionArrow() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.arrow);
	}

	final public LinkRendering getInLinkRendering() {
		return inRendering;
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

	class MyStencil implements Stencil {

		public double getStartingX(StringBounder stringBounder, double y) {
			return 0;
			// return -padding.getLeft();
		}

		public double getEndingX(StringBounder stringBounder, double y) {
			final Dimension2D dim = calculateDimension(stringBounder);
			return dim.getWidth();
			// return dim.getWidth() - padding.getRight();
		}

	}

	public static TextBlock createWbs(Style style, ISkinParam skinParam, Display label) {
		final Style styleArrow = style;
		return new FtileBoxOld(skinParam, label, null, BoxStyle.PLAIN, style, styleArrow);
	}

	public static TextBlock createMindMap(Style style, ISkinParam skinParam, Display label) {
		return new FtileBoxOld(skinParam, label, null, BoxStyle.PLAIN, style, style);
	}

	private FtileBoxOld(ISkinParam skinParam, Display label, Swimlane swimlane, BoxStyle boxStyle, Style style,
			Style styleArrow) {
		super(skinParam);
		Colors specBack = null;
		if (skinParam instanceof SkinParamColors)
			specBack = ((SkinParamColors) skinParam).getColors();

		style = style.eventuallyOverride(specBack);
		this.style = style;
		this.boxStyle = boxStyle;
		this.swimlane = swimlane;

		this.inRendering = LinkRendering
				.create(Rainbow.build(styleArrow, getIHtmlColorSet(), skinParam.getThemeStyle()));
		this.borderColor = style.value(PName.LineColor).asColor(skinParam.getThemeStyle(), getIHtmlColorSet());
		this.backColor = style.value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(), getIHtmlColorSet());
		final FontConfiguration fc = style.getFontConfiguration(skinParam.getThemeStyle(), getIHtmlColorSet());
		this.horizontalAlignment = style.getHorizontalAlignment();
		// this.padding = style.getPadding();
		this.roundCorner = style.value(PName.RoundCorner).asDouble();
		this.shadowing = style.value(PName.Shadowing).asDouble();
		final LineBreakStrategy wrapWidth = style.wrapWidth();
		this.minimumWidth = style.value(PName.MinimumWidth).asDouble();

		// final HorizontalAlignment alignment =
		// skinParam.getDefaultTextAlignment(horizontalAlignment);
		final Sheet sheet = Parser.build(fc, horizontalAlignment, skinParam, CreoleMode.FULL).createSheet(label);
//		this.tb = new SheetBlock1(sheet, wrapWidth, 0, this.padding.getLeft(), this.padding.getRight());
//		this.tb = new SheetBlock2(new SheetBlock1(sheet, wrapWidth, 0, this.padding.getLeft(), this.padding.getRight()),
//				new MyStencil(), new UStroke(1));
		this.tb = new SheetBlock2(new SheetBlock1(sheet, wrapWidth, style.getPadding()), new MyStencil(),
				new UStroke(1));
		this.print = label.toString();

	}

	final private String print;

	@Override
	public String toString() {
		return print;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final UDrawable shape = boxStyle.getUDrawable(widthTotal, heightTotal, shadowing, roundCorner);

		final UStroke thickness = style.getStroke();

		if (borderColor == null)
			ug = ug.apply(new HColorNone());
		else
			ug = ug.apply(borderColor);

		if (backColor == null)
			ug = ug.apply(new HColorNone().bg());
		else
			ug = ug.apply(backColor.bg());

		ug = ug.apply(thickness);
		shape.drawU(ug);

		if (horizontalAlignment == HorizontalAlignment.LEFT)
			tb.drawU(ug);
		else if (horizontalAlignment == HorizontalAlignment.RIGHT)
			tb.drawU(ug.apply(new UTranslate(dimTotal.getWidth() - tbWidth(stringBounder), 0)));
		else if (horizontalAlignment == HorizontalAlignment.CENTER)
			tb.drawU(ug.apply(new UTranslate((dimTotal.getWidth() - tbWidth(stringBounder)) / 2, 0)));

//		if (horizontalAlignment == HorizontalAlignment.LEFT)
//			tb.drawU(ug.apply(new UTranslate(padding.getLeft(), padding.getTop())));
//		else if (horizontalAlignment == HorizontalAlignment.RIGHT)
//			tb.drawU(ug.apply(new UTranslate(dimTotal.getWidth() - tbWidth(stringBounder) - padding.getRight(),
//					padding.getBottom())));
//		else if (horizontalAlignment == HorizontalAlignment.CENTER)
//			tb.drawU(ug.apply(new UTranslate((dimTotal.getWidth() - tbWidth(stringBounder)) / 2, padding.getBottom())));
//
	}

	private double tbWidth(final StringBounder stringBounder) {
		return Math.max(minimumWidth, tb.calculateDimension(stringBounder).getWidth());
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		Dimension2D dimRaw = tb.calculateDimension(stringBounder);
//		dimRaw = Dimension2DDouble.delta(dimRaw, padding.getLeft() + padding.getRight(),
//				padding.getBottom() + padding.getTop());
		dimRaw = Dimension2DDouble.atLeast(dimRaw, minimumWidth, 0);
		return new FtileGeometry(dimRaw.getWidth() + boxStyle.getShield(), dimRaw.getHeight(), dimRaw.getWidth() / 2, 0,
				dimRaw.getHeight());
	}

	public Collection<Ftile> getMyChildren() {
		return Collections.emptyList();
	}

}
