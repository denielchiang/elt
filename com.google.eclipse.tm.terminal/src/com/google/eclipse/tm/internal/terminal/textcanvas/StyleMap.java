/*******************************************************************************
 * Copyright (c) 2007, 2011 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.google.eclipse.tm.internal.terminal.textcanvas;

import java.util.*;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import com.google.eclipse.tm.terminal.model.*;

public class StyleMap {
  private static final String BLACK = "black";
  private static final String WHITE = "white";
  private static final String WHITE_FOREGROUND = "white_foreground";
  private static final String GRAY = "gray";
  private static final String MAGENTA = "magenta";
  private static final String CYAN = "cyan";
  private static final String YELLOW = "yellow";
  private static final String BLUE = "blue";
  private static final String GREEN = "green";
  private static final String RED = "red";

  private static final String PREFIX = "org.eclipse.tm.internal.";

  // TODO propagate the name of the font in the FontRegistry
  private static final String DEFAULT_FONT_NAME = "terminal.views.view.font.definition";

  private String fontName = DEFAULT_FONT_NAME;

  private final Map<StyleColor, Color> colorMapForeground = new HashMap<StyleColor, Color>();
  private final Map<StyleColor, Color> colorMapBackground = new HashMap<StyleColor, Color>();
  private final Map<StyleColor, Color> colorMapIntense = new HashMap<StyleColor, Color>();

  private Point charSize;

  private final Style defaultStyle;

  private boolean invertColors;
  private boolean proportional;

  private final int[] offsets = new int[256];

  private RGB background = new RGB(0, 0, 0);
  private RGB foreground = new RGB(229, 229, 229);

  private Font font = JFaceResources.getFontRegistry().get(fontName);

  StyleMap() {
    initColors();
    defaultStyle = Style.getStyle(StyleColor.getStyleColor(BLACK), StyleColor.getStyleColor(WHITE));
    updateFont();
  }

  private void initColors() {
    initForegroundColors();
    initBackgroundColors();
    initIntenseColors();
  }

  private void initForegroundColors() {
    if (invertColors) {
      setColor(colorMapForeground, WHITE, 0, 0, 0);
      setColor(colorMapForeground, WHITE_FOREGROUND, 50, 50, 50);
      setColor(colorMapForeground, BLACK, foreground.red, foreground.green, foreground.blue); // set foreground
    } else {
      setColor(colorMapForeground, WHITE, 255, 255, 255);
      setColor(colorMapForeground, WHITE_FOREGROUND, 229, 229, 229);
      setColor(colorMapForeground, BLACK, 50, 50, 50);
    }
    setColor(colorMapForeground, RED, 205, 0, 0);
    setColor(colorMapForeground, GREEN, 0, 205, 0);
    setColor(colorMapForeground, BLUE, 0, 0, 238);
    setColor(colorMapForeground, YELLOW, 205, 205, 0);
    setColor(colorMapForeground, CYAN, 0, 205, 205);
    setColor(colorMapForeground, MAGENTA, 205, 0, 205);
    setColor(colorMapForeground, GRAY, 229, 229, 229);
  }

  private void initBackgroundColors() {
    if (invertColors) {
      setColor(colorMapBackground, WHITE, background.red, background.green, background.blue); // set background
      setColor(colorMapBackground, WHITE_FOREGROUND, 50, 50, 50); // only used when colors are inverse
      setColor(colorMapBackground, BLACK, foreground.red, foreground.green, foreground.blue); // set cursor color
    } else {
      setColor(colorMapBackground, WHITE, 255, 255, 255);
      setColor(colorMapBackground, WHITE_FOREGROUND, 229, 229, 229);
      setColor(colorMapBackground, BLACK, 0, 0, 0);
    }
    setColor(colorMapBackground, RED, 205, 0, 0);
    setColor(colorMapBackground, GREEN, 0, 205, 0);
    setColor(colorMapBackground, BLUE, 0, 0, 238);
    setColor(colorMapBackground, YELLOW, 205, 205, 0);
    setColor(colorMapBackground, CYAN, 0, 205, 205);
    setColor(colorMapBackground, MAGENTA, 205, 0, 205);
    setColor(colorMapBackground, GRAY, 229, 229, 229);
  }

  private void initIntenseColors() {
    if (invertColors) {
      setColor(colorMapIntense, WHITE, 127, 127, 127);
      setColor(colorMapIntense, WHITE_FOREGROUND, 0, 0, 0); // only used when colors are inverse
      setColor(colorMapIntense, BLACK, 255, 255, 255);
    } else {
      setColor(colorMapIntense, WHITE, 255, 255, 255);
      setColor(colorMapIntense, WHITE_FOREGROUND, 255, 255, 255);
      setColor(colorMapIntense, BLACK, 0, 0, 0);
    }
    setColor(colorMapIntense, RED, 255, 0, 0);
    setColor(colorMapIntense, GREEN, 0, 255, 0);
    setColor(colorMapIntense, BLUE, 92, 92, 255);
    setColor(colorMapIntense, YELLOW, 255, 255, 0);
    setColor(colorMapIntense, CYAN, 0, 255, 255);
    setColor(colorMapIntense, MAGENTA, 255, 0, 255);
    setColor(colorMapIntense, GRAY, 255, 255, 255);
  }

  private void setColor(Map<StyleColor, Color> colorMap, String name, int r, int g, int b) {
    String colorName = PREFIX + r + "-" + g + "-" + b;
    Color color = JFaceResources.getColorRegistry().get(colorName);
    if (color == null) {
      JFaceResources.getColorRegistry().put(colorName, new RGB(r, g, b));
      color = JFaceResources.getColorRegistry().get(colorName);
    }
    colorMap.put(StyleColor.getStyleColor(name), color);
    colorMap.put(StyleColor.getStyleColor(name.toUpperCase()), color);
  }

  public Color getForegrondColor(Style style) {
    style = defaultIfNull(style);
    Map<StyleColor, Color> map = style.isBold() ? colorMapIntense : colorMapForeground;
    if (style.isReverse()) {
      return getColor(map, style.getBackground());
    }
    return getColor(map, style.getForground());
  }

  public Color getBackgroundColor(Style style) {
    style = defaultIfNull(style);
    if (style.isReverse()) {
      return getColor(colorMapBackground, style.getForground());
    }
    return getColor(colorMapBackground, style.getBackground());
  }

  Color getColor(Map<StyleColor, Color> map, StyleColor color) {
    Color c = map.get(color);
    if (c == null) {
      c = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
    }
    return c;
  }

  private Style defaultIfNull(Style style) {
    if (style == null) {
      style = defaultStyle;
    }
    return style;
  }

  public void setInvertedColors(boolean invert) {
    if (invert == invertColors) {
      return;
    }
    invertColors = invert;
    initColors();
  }

  public Font getFont(Style style) {
    style = defaultIfNull(style);
    FontData fontDatas[] = font.getFontData();
    FontData data = fontDatas[0];
    if (style.isBold()) {
      return new Font(font.getDevice(), data.getName(), data.getHeight(), data.getStyle() | SWT.BOLD);
    }
    if (style.isUnderline()) {
      return new Font(font.getDevice(), data.getName(), data.getHeight(), data.getStyle() | SWT.ITALIC);
    }
    return font;
  }

  public Font getFont() {
    return font;
  }

  public int getFontWidth() {
    return charSize.x;
  }

  public int getFontHeight() {
    return charSize.y;
  }

  public void updateFont() {
    Display display = Display.getCurrent();
    GC gc = new GC(display);
    if (JFaceResources.getFontRegistry().hasValueFor(DEFAULT_FONT_NAME)) {
      fontName = DEFAULT_FONT_NAME;
    } else if (JFaceResources.getFontRegistry().hasValueFor("REMOTE_COMMANDS_VIEW_FONT")) {
      // try RSE Shell View Font
      fontName = "REMOTE_COMMANDS_VIEW_FONT";
    } else {
      // fall back to "basic jface text font"
      fontName = "org.eclipse.jface.textfont";
    }
    gc.setFont(getFont());
    charSize = gc.textExtent("W");
    proportional = false;
    for (char c = ' '; c <= '~'; c++) {
      // Consider only the first 128 chars for deciding if a font is proportional.
      if (measureChar(gc, c, true)) {
        proportional = true;
      }
    }
    // TODO should we also consider the upper 128 chars?
    for (char c = ' ' + 128; c <= '~' + 128; c++) {
      measureChar(gc, c, false);
    }
    if (proportional) {
      charSize.x -= 2; // Works better on small fonts.
    }
    for (int i = 0; i < offsets.length; i++) {
      offsets[i] = (charSize.x - offsets[i]) / 2;
    }
    if (!proportional) {
      // Measure font in boldface, too, and if wider then treat like proportional.
      gc.setFont(getFont(defaultStyle.setBold(true)));
      Point charSizeBold = gc.textExtent("W");
      if (charSize.x != charSizeBold.x) {
        proportional = true;
      }
    }
    gc.dispose();
  }

  private boolean measureChar(GC gc, char c, boolean updateMax) {
    boolean proportional = false;
    Point extent = gc.textExtent(String.valueOf(c));
    if (extent.x > 0 && extent.y > 0 && (charSize.x != extent.x || charSize.y != extent.y)) {
      proportional = true;
      if (updateMax) {
        charSize.x = Math.max(charSize.x, extent.x);
        charSize.y = Math.max(charSize.y, extent.y);
      }
    }
    offsets[c] = extent.x;
    return proportional;
  }

  public boolean isFontProportional() {
    return proportional;
  }

  /**
   * Return the offset in pixels required to center a given character.
   *
   * @param c the character to measure.
   * @return the offset in x direction to center this character.
   */
  public int getCharOffset(char c) {
    if (c >= offsets.length) {
      return 0;
    }
    return offsets[c];
  }

  public void setColors(RGB background, RGB foreground) {
    this.background = background;
    this.foreground = foreground;
    initColors();
  }

  public void setFont(Font font) {
    this.font = font;
  }
}