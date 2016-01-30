/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.sourceforge.net
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * Original Author:  Arnaud Roques
 * 
 * Revision $Revision: 3837 $
 *
 */
package net.sourceforge.plantuml.ugraphic.sprite;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sourceforge.plantuml.version.Version;

public class RessourcesUtils {

	public static Set<String> getJarFile(String path, boolean folder) throws IOException {
		if (path.startsWith("/") || path.endsWith("/")) {
			throw new IllegalArgumentException();
		}
		final URL resource = Version.class.getClassLoader().getResource("net/sourceforge/plantuml/version/logo.png");
		final String protocol = resource.getProtocol();
		if ("file".equals(protocol)) {
			final URL local = Version.class.getClassLoader().getResource(path);
			try {
				return listEntry(new File(local.toURI()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return null;
			}
		}
		if ("jar".equals(protocol)) {
			final String classFile = Version.class.getName().replace(".", "/") + ".class";
			final URL versionURL = Version.class.getClassLoader().getResource(classFile);
			final String jarPath = versionURL.getPath().substring(5, versionURL.getPath().indexOf("!"));
			if (folder) {
				return listFolders(new JarFile(URLDecoder.decode(jarPath, "UTF-8")), path + "/");
			} else {
				return listFiles(new JarFile(URLDecoder.decode(jarPath, "UTF-8")), path + "/");

			}
		}
		return Collections.<String> emptySet();
	}

	private static Set<String> listFiles(JarFile jarFile, String path) {
		final Enumeration<JarEntry> entries = jarFile.entries();
		final Set<String> result = new TreeSet<String>();
		while (entries.hasMoreElements()) {
			final String name = entries.nextElement().getName();
			if (name.startsWith(path)) {
				result.add(name.substring(path.length()));
			}
		}
		return result;
	}

	private static Set<String> listFolders(JarFile jarFile, String path) {
		final Enumeration<JarEntry> entries = jarFile.entries();
		final Set<String> result = new TreeSet<String>();
		while (entries.hasMoreElements()) {
			final String name = entries.nextElement().getName();
			if (name.startsWith(path)) {
				final String folder = name.substring(path.length());
				final int x = folder.indexOf('/');
				if (x != -1) {
					result.add(folder.substring(0, x));
				}
			}
		}
		return result;
	}

	private static Set<String> listEntry(File dir) {
		final Set<String> result = new TreeSet<String>();
		for (String n : dir.list()) {
			result.add(n);
		}
		return result;
	}

}
