package org.intalio.tempo.persistence;

import java.io.*;

public class ByteArrayReader extends Reader
{

    /** The byte buffer. */

    protected byte buf[];

    /** The current buffer position. */

    protected int pos;

    /** The position of mark in buffer. */

    protected int markedPos = 0;

    /**
     * The index of the end of this buffer. There is not valid data at or beyond
     * this index.
     */

    protected int count;

    /***************************************************************************
     * 
     * Create an ByteArrayReader from the specified array of bytes.
     * 
     * @param buf
     *            Input buffer (not copied)
     * 
     */

    public ByteArrayReader(byte buf[])

    {

        this.buf = buf;

        this.pos = 0;

        this.count = buf.length;

    }

    /***************************************************************************
     * 
     * Create an ByteArrayReader from the specified array of bytes.
     * 
     * @param buf
     *            Input buffer (not copied)
     * 
     * @param offset
     *            Offset of the first byte to read
     * 
     * @param length
     *            Number of bytes to read
     * 
     */

    public ByteArrayReader(byte buf[], int offset, int length)

    {

        if ((offset < 0) || (offset > buf.length) || (length < 0) || ((offset + length) < 0))

            throw new IllegalArgumentException();

        this.buf = buf;

        this.pos = offset;

        this.count = Math.min(offset + length, buf.length);

        this.markedPos = offset;

    }

    /***************************************************************************
     * 
     * Check to make sure that the stream has not been closed
     * 
     */

    private void ensureOpen()

    throws IOException

    {

        if (buf == null)
            throw new IOException("Stream closed");

    }

    /***************************************************************************
     * 
     * Read a single byteacter.
     * 
     * @exception IOException
     *                If an I/O error occurs
     * 
     */

    public int read()

    throws IOException

    {

        synchronized (lock)

        {

            ensureOpen();

            if (pos >= count)
                return -1;

            else
                return buf[pos++];

        }

    }

    /***************************************************************************
     * 
     * Reads all bytes until the next \n
     * 
     * @exception IOException
     *                If an I/O error occurs
     * 
     */

    public String readLine()

    throws IOException

    {

        synchronized (lock)

        {

            int spos = pos;

            ensureOpen();

            if (pos >= count)
                return null;

            else

            {

                while ((pos < count) && (buf[pos] != '\n'))
                    pos++;

                if (buf[pos] == '\n')
                    pos++;

                return new String(buf, spos, pos - spos);

            }

        }

    }

    public int read(char b[], int off, int len)

    {

        new NoSuchMethodException("Not implemented!");

        return -1;

    }

    /***************************************************************************
     * 
     * Read byteacters into a portion of an array.
     * 
     * @param b
     *            Destination buffer
     * 
     * @param off
     *            Offset at which to start storing byteacters
     * 
     * @param len
     *            Maximum number of byteacters to read
     * 
     * @return The actual number of byteacters read, or -1 if the end
     * 
     * of the stream has been reached
     * 
     * @exception IOException
     *                If an I/O error occurs
     * 
     */

    public int read(byte b[], int off, int len)

    throws IOException

    {

        synchronized (lock)

        {

            ensureOpen();

            if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0))

                throw new IndexOutOfBoundsException();

            else if (len == 0)
                return 0;

            if (pos >= count)
                return -1;

            if (pos + len > count)
                len = count - pos;

            if (len <= 0)
                return 0;

            System.arraycopy(buf, pos, b, off, len);

            pos += len;

            return len;

        }

    }

    /***************************************************************************
     * 
     * Skip bytes.
     * 
     * @param n
     *            The number of byteacters to skip
     * 
     * @return The number of byteacters actually skipped
     * 
     * @exception IOException
     *                If an I/O error occurs
     * 
     */

    public long skip(long n)

    throws IOException

    {

        synchronized (lock)

        {

            ensureOpen();

            if (pos + n > count)
                n = count - pos;

            if (n < 0)
                return 0;

            pos += n;

            return n;

        }

    }

    /***************************************************************************
     * 
     * Tell whether this stream is ready to be read. Bytearray readers
     * 
     * are always ready to be read.
     * 
     * @exception IOException
     *                If an I/O error occurs
     * 
     */

    public boolean ready()

    throws IOException

    {

        synchronized (lock)

        {

            ensureOpen();

            return (count - pos) > 0;

        }

    }

    /***************************************************************************
     * 
     * Tell whether this stream supports the mark() operation, which it does.
     * 
     */

    public boolean markSupported()

    {

        return true;

    }

    /***************************************************************************
     * 
     * Mark the present position in the stream. Subsequent calls to
     * 
     * reset() will reposition the stream to this point.
     * 
     * @param readAheadLimit
     *            Limit on the number of byteacters that
     * 
     * may be read while still preserving the mark. Because the
     * 
     * stream's input comes from a bytearray, there is no actual
     * 
     * limit and this argument is ignored.
     * 
     * @exception IOException
     *                If an I/O error occurs
     * 
     */

    public void mark(int readAheadLimit)

    throws IOException

    {

        synchronized (lock)

        {

            ensureOpen();

            markedPos = pos;

        }

    }

    /***************************************************************************
     * 
     * Reset the stream to the most recent mark, or to the beginning
     * 
     * if it has never been marked.
     * 
     * @exception IOException
     *                If an I/O error occurs
     * 
     */

    public void reset()

    throws IOException

    {

        synchronized (lock)

        {

            ensureOpen();

            pos = markedPos;

        }

    }

    /***************************************************************************
     * 
     * Close the stream.
     * 
     */

    public void close()

    {

        buf = null;

    }

}
