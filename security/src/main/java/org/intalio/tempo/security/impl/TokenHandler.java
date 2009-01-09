/**
 * Copyright (C) 2003, Intalio Inc.
 *
 * The program(s) herein may be used and/or copied only with the
 * written permission of Intalio Inc. or in accordance with the terms
 * and conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package org.intalio.tempo.security.impl;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import org.castor.util.Base64Decoder;
import org.castor.util.Base64Encoder;
import org.intalio.tempo.security.Property;
import org.intalio.tempo.security.authentication.AuthenticationConstants;
import org.intalio.tempo.security.authentication.AuthenticationException;
import org.intalio.tempo.security.util.Base64u;
import org.intalio.tempo.security.util.SHA1;
import org.intalio.tempo.security.util.TimeExpirationMap;

/**
 * Handler to manipulate cryptographic token.
 */
public class TokenHandler implements AuthenticationConstants {
    // final static Logger LOG = Logger.getLogger("tempo.security");

    /**
     * Token name/value separator
     */
    private static final String VALUE_SEPARATOR = "==";

    /**
     * Token property separator
     */
    private static final String PROP_SEPARATOR = "&&";

    /**
     * Token prefix
     */
    private static final String PREFIX = "TOKEN&&";

    /**
     * Token suffix.
     */
    private static final String SUFFIX = "&&TOKEN";

    /**
     * Property name for custom secret
     */
    private static final String PROPERTY_SECRET = "tempo.security.TokenHandler.secret";

    /**
     * Default value for secret shared between all token handlers
     */
    private static final String DEFAULT_SECRET = "secret";

    /**
     * Secret key shared by all token handlers. Used to "sign" the token and
     * prevent replay attacks.
     */
    private String _secret;
    
    /**
     * Should we use compression for the generated token
     */
    private Boolean _compressToken = false;

    /**
     * Cache of nonce (unique identifiers). Basically limits unauthorized replay
     * attacks by insuring that any given nonce is only used once in a specified
     * period of time (default: 5 minutes)
     */
    private static final TimeExpirationMap NONCE_CACHE = new TimeExpirationMap(5 * 60 * 1000, 30 * 1000);

    /**
     * Random number generator for nonce.
     */
    private static Random RANDOM = new Random();

    /**
     * Construct a token handler.
     */
    public TokenHandler() {
        Properties props;

        _secret = DEFAULT_SECRET;

        props = System.getProperties();
        if (props.get(PROPERTY_SECRET) != null) {
            _secret = (String) props.get(PROPERTY_SECRET);
        }
    }

    /**
     * Get secret shared between all token handlers
     */
    public String getSecret() {
        return _secret;
    }

    /**
     * Set secret shared between all token handlers
     */
    public void setSecret(String secret) {
        _secret = secret;
    }

    public final Boolean getCompressToken() {
        return _compressToken;
    }

    public final void setCompressToken(Boolean token) {
        _compressToken = token;
    }

    /**
     * Parse the properties contained in a token.
     * 
     * @param props
     *            Properties
     * @return cryptographic token
     */
    public String createToken(Property[] props) {
        StringBuffer buf;
        String result;
        long timestamp;
        Long nonce;

        timestamp = System.currentTimeMillis();

        while (true) {
            // use current time random and initial random to prevent guessing
            Random random = new Random(timestamp + RANDOM.nextLong());
            nonce = new Long(random.nextLong());
            if (NONCE_CACHE.put(nonce, nonce) == null) {
                break;
            }
            // collision, try a different random; also prevents guessing
            RANDOM = new Random(timestamp);
        }

        buf = new StringBuffer();
        for (int i = 0; i < props.length; i++) {
            buf.append(props[i].getName());
            buf.append(VALUE_SEPARATOR);
            buf.append((String) props[i].getValue());
            buf.append(PROP_SEPARATOR);
        }

        buf.append("nonce");
        buf.append(VALUE_SEPARATOR);
        buf.append(nonce.toString());
        buf.append(PROP_SEPARATOR);

        buf.append("timestamp");
        buf.append(VALUE_SEPARATOR);
        buf.append(Long.toString(timestamp));
        buf.append(PROP_SEPARATOR);

        String digest = digest(timestamp, nonce.longValue(), _secret, props);
        buf.append("digest");
        buf.append(VALUE_SEPARATOR);
        buf.append(digest);
        buf.append(PROP_SEPARATOR);
        result = encode(buf.toString());
        return result;
    }

    /**
     * Return a Based64 encoded hash of the given timestamp, nonce and password
     * (or password equivalent).
     * 
     * @param properties
     *            TODO
     */
    private String digest(long timestamp, long nonce, String password, Property[] properties) {
        Property[] orderedCopy = new Property[properties.length];
        System.arraycopy(properties, 0, orderedCopy, 0, properties.length);
        Arrays.sort(orderedCopy, new Comparator<Property>() {
            public int compare(Property p1, Property p2) {
                int order = p1.getName().compareTo(p2.getName());
                if (order == 0) {
                    String s1 = p1.getValue().toString();
                    String s2 = p2.getValue().toString();
                    order = s1.compareTo(s2);
                }
                return order;
            }
        });

        /*
         * LOG.debug( "timestamp: " + timestamp ); LOG.debug( "nonce: " + nonce
         * ); LOG.debug( "password: " + password ); LOG.debug( "props: " +
         * PropertyUtils.toMap(properties) );
         */

        StringBuffer buf = new StringBuffer();
        buf.append(Long.toString(timestamp));
        buf.append(Long.toString(nonce));
        buf.append(password);
        for (Property prop : orderedCopy) {
            buf.append(prop.getName());
            buf.append(prop.getValue());
        }
        /*
         * LOG.debug( "pre-encode: " + buf.toString() ); LOG.debug( "digest: " +
         * SHA1.encode( buf.toString() ) );
         */
        return SHA1.encode(buf.toString());
    }

    /**
     * Parse the cryptographic token and return its properties.
     * 
     * @param token
     *            Token
     * @return properties
     */
    public Property[] parseToken(String token) throws AuthenticationException, RemoteException {
        int pos1, pos2;
        HashMap<String, Object> props;
        String buf;
        Property prop;
        String name, value;

        if (token == null) {
            throw new IllegalArgumentException("Token is null");
        }

        buf = decode(token);

        props = new HashMap<String, Object>();
        while (buf.length() > 0) {
            pos1 = buf.indexOf(VALUE_SEPARATOR);
            if (pos1 == -1) {
                throw new IllegalArgumentException("Token is corrupted");
            }
            pos2 = buf.indexOf(PROP_SEPARATOR);
            if (pos2 == -1) {
                pos2 = buf.length();
            }

            // parse property
            name = buf.substring(0, pos1);
            value = buf.substring(pos1 + VALUE_SEPARATOR.length(), pos2);
            prop = new Property(name, value);
            props.put(name, prop);

            // remove property name,value
            if (pos2 < buf.length()) {
                buf = buf.substring(pos2 + PROP_SEPARATOR.length());
            } else {
                buf = "";
            }
        }

        prop = (Property) props.remove(PROPERTY_NONCE);
        if (prop == null) {
            throw new IllegalArgumentException("Missing '" + PROPERTY_NONCE + "' property");
        }
        long nonce = Long.parseLong((String) prop.getValue());

        prop = (Property) props.remove(PROPERTY_TIMESTAMP);
        if (prop == null) {
            throw new IllegalArgumentException("Missing '" + PROPERTY_TIMESTAMP + "' property");
        }
        long timestamp = Long.parseLong((String) prop.getValue());

        prop = (Property) props.remove(PROPERTY_DIGEST);
        if (prop == null) {
            throw new IllegalArgumentException("Missing '" + PROPERTY_DIGEST + "' property");
        }
        String digest = (String) prop.getValue();

        Property[] propsArray = props.values().toArray(new Property[props.size()]);

        String localDigest = digest(timestamp, nonce, _secret, propsArray);

        if (!localDigest.equals(digest)) {
            throw new AuthenticationException("Incorrect digest");
        }

        return propsArray;
    }

    /**
     * Encode token into non-human readable format
     */
    protected String encode(String token) {
        token = PREFIX + token + SUFFIX;
        try {
            if(this._compressToken) {
                return Base64u.encodeBytes(token.getBytes("UTF-8"), Base64u.GZIP|Base64u.DONT_BREAK_LINES );    
            } else {
                Base64Encoder encoder = new Base64Encoder();
                encoder.translate( token.getBytes( "UTF-8" ) );
                return new String( encoder.getCharArray() );
            }
        } catch (UnsupportedEncodingException except) {
            throw new RuntimeException(except.toString());
        }
    }

    /**
     * Decode token from non-human readable format
     */
    protected String decode(String token) {
        if(!this._compressToken) {
            Base64Decoder decoder = new Base64Decoder();
            decoder.translate( token );
            try {
                token = new String( decoder.getByteArray(), "UTF-8" );
            } catch ( UnsupportedEncodingException except ) {
                throw new RuntimeException( except.toString() );
            }    
        } else {
            try {
                token = new String(Base64u.decode(token), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Token is corrupted");
            }    
        }

        if (!token.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Token is corrupted");
        }
        token = token.substring(PREFIX.length());
        if (!token.endsWith(SUFFIX)) {
            throw new IllegalArgumentException("Token is corrupted");
        }
        token = token.substring(0, token.length() - SUFFIX.length());
        return token;
    }

}
