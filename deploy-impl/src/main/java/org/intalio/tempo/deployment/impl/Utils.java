/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.deployment.impl;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility methods
 */
public class Utils {

    /**
     * Copies the contents of the <code>InputStream</code> into the <code>OutputStream</code>.
     */
    public static void copyStream(InputStream input, OutputStream output) { 
        try {
            byte[] bytes = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = input.read(bytes)) >= 0) {
                output.write(bytes, 0, bytesRead);
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Unzip the given inputstream into a directory.
     */
    public static void unzip(InputStream input, File dest) throws IOException {
        ZipInputStream zis = new ZipInputStream(input);
        ZipEntry entry;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File dir = new File(dest, entry.getName());
                    dir.mkdir();
                    if (!dir.exists() || !dir.isDirectory()) {
                        throw new IOException("Error creating directory: " + dir);
                    }
                    continue;
                }
                File destFile = new File(dest, entry.getName());
                File parent = destFile.getParentFile();
                if (!parent.exists())
                    parent.mkdirs();
                if (!parent.exists() || !parent.isDirectory()) {
                    throw new IOException("Error creating directory: " + parent);
                }
                OutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
                try {
                    copyStream(zis, out);
                } finally {
                    out.close();
                }
            }
        } finally {
            zis.close();
        }
    }
    
    /**
     * Delete a file/directory, recursively.
     */
    public static void deleteRecursively(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; ++i) {
                    deleteRecursively(files[i]);
                }
            }
            if (file.exists() && !file.delete()) { 
                throw new IllegalStateException("Unable to delete: "+ file);
            }
        }
    }

    /**
     * Copy a file/directory, recursively, to a target file/directory.
     */
    public static void copyRecursively(File source, File destination) 
        throws IOException
     {
        if (!source.exists()) throw new IOException("Source doesn't exist: "+source.getAbsolutePath());
        
        if (source.isFile()) {
            if (destination.exists() && destination.isDirectory()) {
                copyFile(source, new File(destination, source.getName()));
            } else if (destination.exists() && destination.isFile()) {
                copyFile(source, destination);
            } else if (!destination.exists()) {
                copyFile(source, destination);
            } else {
                throw new IOException("Unknown target node type: "+destination.getAbsolutePath());
            }                
        } else if (source.isDirectory()) {
            if (!destination.exists()) {
                if (!destination.mkdir()) {
                    throw new IOException("Cannot create target directory: "+destination.getAbsolutePath());
                }
            }
            if (destination.isFile()) {
                throw new IOException("Cannot copy directory "+source.getAbsolutePath()+" into file "+destination.getAbsolutePath());
            }
            
            File[] files = source.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isFile()) {
                    copyRecursively(files[i], destination);
                } else if (files[i].isDirectory()) {
                    copyRecursively(files[i], new File(destination, files[i].getName()));
                } else {
                    throw new IOException("Unknown source node type: "+destination.getAbsolutePath());
                }                
            }
        }
    }

    private static void copyFile(File source, File destination)
            throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(source);
        if (destination.isDirectory()) {
            destination = new File(destination, source.getName());
        }
        FileOutputStream fos = new FileOutputStream(destination);
        try {
            copyStream(fis, fos);
        } finally {
            fis.close();
            fos.close();
        }
    }

    /**
     * Close stream, ignoring any possible IOException.
     */
    public static void close(Closeable c) {
        try {
            if (c != null) c.close();
        } catch (IOException except) {
            // ignore
        }
    }

    /**
     * Create a file.
     */
    public static void createFile(File f) {
        try {
            if (!f.exists()) {
                if (!f.createNewFile()) {
                    throw new IOException("Unable to create file: "
                            + f.getAbsolutePath());
                }
            }
        } catch (IOException except) {
            throw new RuntimeException("Unable to create file: "
                    + f.getAbsolutePath(), except);
        }
    }

    /**
     * Delete a file
     */
    public static void deleteFile(File f) {
        if (f.exists() && !f.delete()) {
            throw new RuntimeException(new IOException("Unable to delete file: "
                    + f.getAbsolutePath()));
        }
    }

    /**
     * List files matching a given glob pattern
     */
    public static File[] listFiles(File dir, String glob) {
        if (!dir.exists()) throw new RuntimeException(new FileNotFoundException("Directory does not exist: "+dir));
        if (!dir.isDirectory()) throw new RuntimeException(new IOException("Not a directory: "+dir));
        ArrayList<File> list = new ArrayList<File>();
        Pattern p = globToRegexPattern(glob);
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (p.matcher(files[i].getName()).matches()) {
                list.add(files[i]);
            }
        }
        return (File[]) list.toArray(new File[list.size()]);
    }

    /**
     * Return the compiled Java regex Pattern equivalent to the specified GLOB expression.
     * 
     * @param glob GLOB expression to be compiled
     * @return Equivalent compiled Java regex Pattern
     */
    public static Pattern globToRegexPattern(final String glob) throws PatternSyntaxException {
        /* Stack to keep track of the parser mode: */
        /* "--" : Base mode (first on the stack) */
        /* "[]" : Square brackets mode "[...]" */
        /* "{}" : Curly braces mode "{...}" */
        final LinkedList<String> parserMode = new LinkedList<String>();
        parserMode.addFirst("--"); // base mode

        final int globLength = glob.length();
        int index = 0; // index in glob

        /* equivalent REGEX expression to be compiled */
        final StringBuilder t = new StringBuilder();

        while (index < globLength) {
            char c = glob.charAt(index++);

            if (c == '\\') {
                // (1) ESCAPE SEQUENCE
                if (index == globLength) {
                    /* no characters left, so treat '\' as literal char */
                    t.append(Pattern.quote("\\"));
                } else {
                    /* read next character */
                    c = glob.charAt(index);
                    final String s = c + "";

                    if (("--".equals(parserMode.peek()) && "\\[]{}?*".contains(s))
                            || ("[]".equals(parserMode.peek()) && "\\[]{}?*!-".contains(s))
                            || ("{}".equals(parserMode.peek()) && "\\[]{}?*,".contains(s))) {
                        /* escape the construct char */
                        index++;
                        t.append(Pattern.quote(s));
                    } else {
                        /* treat '\' as literal char */
                        t.append(Pattern.quote("\\"));
                    }
                }
            } else if (c == '*') {
                // (2) GLOB PATTERN

                /* create non-capturing group to match zero or more characters */
                t.append(".*");
            } else if (c == '?') {
                // (3) GLOB PATTERN '?'

                /* create non-capturing group to match exactly one character */
                t.append('.');
            } else if (c == '[') {
                // (4) GLOB PATTERN "[...]"

                /* opening square bracket '[' */
                /* create non-capturing group to match exactly one character */
                /* inside the sequence */
                t.append('[');
                parserMode.addFirst("[]");

                /* check for negation character '!' immediately after */
                /* the opening bracket '[' */
                if ((index < globLength) && (glob.charAt(index) == '!')) {
                    index++;
                    t.append('^');
                }
            } else if ((c == ']') && "[]".equals(parserMode.peek())) {
                /* closing square bracket ']' */
                t.append(']');
                parserMode.removeFirst();
            } else if ((c == '-') && "[]".equals(parserMode.peek())) {
                /* character range '-' in "[...]" */
                t.append('-');
            } else if (c == '{') {
                // (5) GLOB PATTERN "{...}" 

                /* opening curly brace '{' */
                /* create non-capturing group to match one of the */
                /* strings inside the sequence */
                t.append("(?:(?:");
                parserMode.addFirst("{}");
            } else if ((c == '}') && "{}".equals(parserMode.peek())) {
                /* closing curly brace '}' */
                t.append("))");
                parserMode.removeFirst();
            } else if ((c == ',') && "{}".equals(parserMode.peek())) {
                /* comma between strings in "{...}" */
                t.append(")|(?:");
            } else {
                // (6) LITERAL CHARACTER 

                /* convert literal character to a regex string */
                t.append(Pattern.quote(c + ""));
            }
        }
        /* done parsing all chars of the source pattern string */

        /* check for mismatched [...] or {...} */
        if ("[]".equals(parserMode.peek()))
            throw new PatternSyntaxException("Cannot find matching closing square bracket ] in GLOB expression", glob,
                    -1);

        if ("{}".equals(parserMode.peek()))
            throw new PatternSyntaxException("Cannot find matching closing curly brace } in GLOB expression", glob, -1);

        return Pattern.compile(t.toString());
    }
    
}
