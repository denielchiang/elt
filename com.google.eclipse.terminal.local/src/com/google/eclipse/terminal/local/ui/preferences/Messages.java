/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.terminal.local.ui.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Messages extends NLS {
  public static String backgroundAndForegroundCannotBeTheSame;
  public static String backgroundPrompt;
  public static String bufferLineCount;
  public static String change;
  public static String closeViewOnExit;
  public static String colorsAndFontsTitle;
  public static String foregroundPrompt;
  public static String generalPreferencesTitle;
  public static String invalidBufferLineCount;
  public static String previewPrompt;
  public static String textFontLink;
  public static String unableToLoadPreviewContent;
  public static String useCustomFont;
  public static String useTextFont;

  static {
    Class<Messages> type = Messages.class;
    NLS.initializeMessages(type.getName(), type);
  }

  private Messages() {}
}
