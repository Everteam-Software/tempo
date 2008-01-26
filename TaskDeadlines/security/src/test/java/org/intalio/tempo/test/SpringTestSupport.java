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

package org.intalio.tempo.test;

import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractXmlApplicationContext;

/**
 * @version $Revision: 603 $
 */
public abstract class SpringTestSupport
    extends TestCase
{

    protected transient Logger _log = Logger.getLogger( getClass() );

    protected AbstractXmlApplicationContext _context;

    protected void setUp() throws Exception {

        _context = createBeanFactory();

        // TODO: check where this method is
        //_context.setXmlValidating(false);
    }

    protected void tearDown() throws Exception {
        if (_context != null) {
            _log.debug("Closing down the spring _context");
            _context.destroy();
        }
    }

    protected Object getBean(String name) {
        Object answer = null;
        if (_context != null) {
            answer = _context.getBean(name);
        }
        assertNotNull("Could not find object in Spring for key: " + name, answer);
        return answer;
    }

    protected abstract AbstractXmlApplicationContext createBeanFactory();

    protected Source getSourceFromClassPath(String fileOnClassPath) {
        InputStream stream = getClass().getResourceAsStream(fileOnClassPath);
        assertNotNull("Could not find file: " + fileOnClassPath + " on the classpath", stream);

        Source content = new StreamSource(stream);
        return content;
    }

}
