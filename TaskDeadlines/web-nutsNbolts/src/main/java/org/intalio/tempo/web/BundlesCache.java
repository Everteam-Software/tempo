/**
 * Copyright (C) 2006, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with
 * the written permission of Intalio Inc. or in accordance with
 * the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * $Id$
 * $Log$
 */
package org.intalio.tempo.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class BundlesCache {

	private final static Map<Key, ResourceBundle> BUNDLES = new HashMap<Key, ResourceBundle>();

	public static ResourceBundle getBundle(String bundleName, Locale locale) {
		Key key = new Key(bundleName, locale);
		ResourceBundle bundle = BUNDLES.get(key);
		if (bundle == null) {
			bundle = PropertyResourceBundle.getBundle(bundleName, locale);
			if (bundle != null) {
				BUNDLES.put(key, bundle);
			}
		}
		return bundle;
	}
	
	static class Key {
		private final String _bundleName;
		private final Locale _locale;

		public Key(String bundleName, Locale locale) {
			super();
			this._bundleName = bundleName;
			this._locale = locale;
		}

		@Override
		public boolean equals(Object obj) {
			if (super.equals(obj)) {
				return true;
			}
			Key key = (Key) obj;
			return key._bundleName.equals(_bundleName)
					&& key._locale.equals(_locale);
		}

		@Override
		public int hashCode() {
			return _bundleName.hashCode() << 8 | (byte) _locale.hashCode();
		}
	}
	
}
