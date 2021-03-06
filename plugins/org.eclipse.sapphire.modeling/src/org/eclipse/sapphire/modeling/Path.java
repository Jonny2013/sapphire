/*******************************************************************************
 * Copyright (c) 2015 IBM, Oracle and Other Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *    Konstantin Komissarchik - Adaptation for Sapphire and Java 5
 *******************************************************************************/

package org.eclipse.sapphire.modeling;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import java.io.File;
import java.util.Arrays;

import org.eclipse.sapphire.FileName;

/** 
 * The standard implementation of the <code>Path</code> interface.
 * Paths are always maintained in canonicalized form.  That is, parent
 * references (i.e., <code>../../</code>) and duplicate separators are 
 * resolved.  For example,
 * <pre>     new Path("/a/b").append("../foo/bar")</pre>
 * will yield the path
 * <pre>     /a/foo/bar</pre>
 * 
 * @author IBM
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class Path
{
    /**
     * Path separator character constant "/" used in paths.
     */
    public static final char SEPARATOR = '/';

    /** 
     * Device separator character constant ":" used in paths.
     */
    public static final char DEVICE_SEPARATOR = ':';

    /** masks for separator values */
    private static final int HAS_LEADING = 1;
    private static final int IS_UNC = 2;
    private static final int HAS_TRAILING = 4;

    private static final int ALL_SEPARATORS = HAS_LEADING | IS_UNC | HAS_TRAILING;

    /** Constant empty string value. */
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    /** Constant value indicating no segments */
    private static final String[] NO_SEGMENTS = new String[0];

    /** Mask for all bits that are involved in the hash code */
    private static final int HASH_MASK = ~HAS_TRAILING;

    /** Constant root path string (<code>"/"</code>). */
    private static final String ROOT_STRING = "/"; //$NON-NLS-1$

    /** Constant value indicating if the current platform is Windows */
    private static final boolean WINDOWS = java.io.File.separatorChar == '\\';

    /** Constant value containing the empty path with no device. */
    public static final Path EMPTY = new Path(EMPTY_STRING);

    /** Constant value containing the root path with no device. */
    public static final Path ROOT = new Path(ROOT_STRING);

    /** The device id string. May be null if there is no device. */
    private String device = null;

    //Private implementation note: the segments and separators 
    //arrays are never modified, so that they can be shared between 
    //path instances

    /** The path segments */
    private String[] segments;

    /** flags indicating separators (has leading, is UNC, has trailing) */
    private int separators;

    /** 
     * Constructs a new path from the given string path.
     * The string path must represent a valid file system path
     * on the local file system. 
     * The path is canonicalized and double slashes are removed
     * except at the beginning. (to handle UNC paths). All forward
     * slashes ('/') are treated as segment delimiters, and any
     * segment and device delimiters for the local file system are
     * also respected.
     *
     * @param pathString the portable string path
     * @see Path#toPortableString()
     */
    public static Path fromOSString(String pathString) {
        return new Path(pathString);
    }

    /** 
     * Constructs a new path from the given path string.
     * The path string must have been produced by a previous
     * call to <code>Path.toPortableString</code>.
     *
     * @param pathString the portable path string
     * @see Path#toPortableString()
     */
    public static Path fromPortableString(String pathString) {
        int firstMatch = pathString.indexOf(DEVICE_SEPARATOR) + 1;
        //no extra work required if no device characters
        if (firstMatch <= 0)
            return new Path().initialize(null, pathString);
        //if we find a single colon, then the path has a device
        String devicePart = null;
        int pathLength = pathString.length();
        if (firstMatch == pathLength || pathString.charAt(firstMatch) != DEVICE_SEPARATOR) {
            devicePart = pathString.substring(0, firstMatch);
            pathString = pathString.substring(firstMatch, pathLength);
        }
        //optimize for no colon literals
        if (pathString.indexOf(DEVICE_SEPARATOR) == -1)
            return new Path().initialize(devicePart, pathString);
        //contract colon literals
        char[] chars = pathString.toCharArray();
        int readOffset = 0, writeOffset = 0, length = chars.length;
        while (readOffset < length) {
            if (chars[readOffset] == DEVICE_SEPARATOR)
                if (++readOffset >= length)
                    break;
            chars[writeOffset++] = chars[readOffset++];
        }
        return new Path().initialize(devicePart, new String(chars, 0, writeOffset));
    }

    /* (Intentionally not included in javadoc)
     * Private constructor.
     */
    private Path() {
        // not allowed
    }

    /** 
     * Constructs a new path from the given string path.
     * The string path must represent a valid file system path
     * on the local file system. 
     * The path is canonicalized and double slashes are removed
     * except at the beginning. (to handle UNC paths). All forward
     * slashes ('/') are treated as segment delimiters, and any
     * segment and device delimiters for the local file system are
     * also respected (such as colon (':') and backslash ('\') on some file systems).
     *
     * @param fullPath the string path
     * @see #isValidPath(String)
     */
    public Path(String fullPath) {
        String devicePart = null;
        if (WINDOWS) {
            //convert backslash to forward slash
            fullPath = fullPath.indexOf('\\') == -1 ? fullPath : fullPath.replace('\\', SEPARATOR);
            //extract device
            int i = fullPath.indexOf(DEVICE_SEPARATOR);
            if (i != -1) {
                //remove leading slash from device part to handle output of URL.getFile()
                int start = fullPath.charAt(0) == SEPARATOR ? 1 : 0;
                devicePart = fullPath.substring(start, i + 1);
                fullPath = fullPath.substring(i + 1, fullPath.length());
            }
        }
        initialize(devicePart, fullPath);
    }

    /** 
     * Constructs a new path from the given device id and string path.
     * The given string path must be valid.
     * The path is canonicalized and double slashes are removed except
     * at the beginning (to handle UNC paths). All forward
     * slashes ('/') are treated as segment delimiters, and any
     * segment delimiters for the local file system are
     * also respected (such as backslash ('\') on some file systems).
     *
     * @param device the device id
     * @param path the string path
     * @see #isValidPath(String)
     * @see #setDevice(String)
     */
    public Path(String device, String path) {
        if (WINDOWS) {
            //convert backslash to forward slash
            path = path.indexOf('\\') == -1 ? path : path.replace('\\', SEPARATOR);
        }
        initialize(device, path);
    }

    /* (Intentionally not included in javadoc)
     * Private constructor.
     */
    private Path(String device, String[] segments, int _separators) {
        // no segment validations are done for performance reasons  
        this.segments = segments;
        this.device = device;
        //hash code is cached in all but the bottom three bits of the separators field
        this.separators = (computeHashCode() << 3) | (_separators & ALL_SEPARATORS);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#addFileExtension
     */
    public Path addFileExtension(String extension) {
        if (isRoot() || isEmpty() || hasTrailingSeparator())
            return this;
        int len = this.segments.length;
        String[] newSegments = new String[len];
        System.arraycopy(this.segments, 0, newSegments, 0, len - 1);
        newSegments[len - 1] = this.segments[len - 1] + '.' + extension;
        return new Path(this.device, newSegments, this.separators);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#addTrailingSeparator
     */
    public Path addTrailingSeparator() {
        if (hasTrailingSeparator() || isRoot()) {
            return this;
        }
        //XXX workaround, see 1GIGQ9V
        if (isEmpty()) {
            return new Path(this.device, this.segments, HAS_LEADING);
        }
        return new Path(this.device, this.segments, this.separators | HAS_TRAILING);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#append(Path)
     */
    public Path append(Path tail) {
        //optimize some easy cases
        if (tail == null || tail.segmentCount() == 0)
            return this;
        //these call chains look expensive, but in most cases they are no-ops
        if (this.isEmpty())
            return tail.setDevice(this.device).makeRelative().makeUNC(isUNC());
        if (this.isRoot())
            return tail.setDevice(this.device).makeAbsolute().makeUNC(isUNC());

        //concatenate the two segment arrays
        int myLen = this.segments.length;
        int tailLen = tail.segmentCount();
        String[] newSegments = new String[myLen + tailLen];
        System.arraycopy(this.segments, 0, newSegments, 0, myLen);
        for (int i = 0; i < tailLen; i++) {
            newSegments[myLen + i] = tail.segment(i);
        }
        //use my leading separators and the tail's trailing separator
        Path result = new Path(this.device, newSegments, (this.separators & (HAS_LEADING | IS_UNC)) | (tail.hasTrailingSeparator() ? HAS_TRAILING : 0));
        String tailFirstSegment = newSegments[myLen];
        if (tailFirstSegment.equals("..") || tailFirstSegment.equals(".")) { //$NON-NLS-1$ //$NON-NLS-2$
            result.canonicalize();
        }
        return result;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#append(java.lang.String)
     */
    public Path append(String tail) {
        //optimize addition of a single segment
        if (tail.indexOf(SEPARATOR) == -1 && tail.indexOf("\\") == -1 && tail.indexOf(DEVICE_SEPARATOR) == -1) { //$NON-NLS-1$
            int tailLength = tail.length();
            if (tailLength < 3) {
                //some special cases
                if (tailLength == 0 || ".".equals(tail)) { //$NON-NLS-1$
                    return this;
                }
                if ("..".equals(tail)) //$NON-NLS-1$
                    return removeLastSegments(1);
            }
            //just add the segment
            int myLen = this.segments.length;
            String[] newSegments = new String[myLen + 1];
            System.arraycopy(this.segments, 0, newSegments, 0, myLen);
            newSegments[myLen] = tail;
            return new Path(this.device, newSegments, this.separators & ~HAS_TRAILING);
        }
        //go with easy implementation
        return append(new Path(tail));
    }

    /**
     * Destructively converts this path to its canonical form.
     * <p>
     * In its canonical form, a path does not have any
     * "." segments, and parent references ("..") are collapsed
     * where possible.
     * </p>
     * @return true if the path was modified, and false otherwise.
     */
    private boolean canonicalize() {
        //look for segments that need canonicalizing
        for (int i = 0, max = this.segments.length; i < max; i++) {
            String segment = this.segments[i];
            if (segment.charAt(0) == '.' && (segment.equals("..") || segment.equals("."))) { //$NON-NLS-1$ //$NON-NLS-2$
                //path needs to be canonicalized
                collapseParentReferences();
                //paths of length 0 have no trailing separator
                if (this.segments.length == 0)
                    this.separators &= (HAS_LEADING | IS_UNC);
                //recompute hash because canonicalize affects hash
                this.separators = (this.separators & ALL_SEPARATORS) | (computeHashCode() << 3);
                return true;
            }
        }
        return false;
    }

    /**
     * Destructively removes all occurrences of ".." segments from this path.
     */
    private void collapseParentReferences() {
        int segmentCount = this.segments.length;
        String[] stack = new String[segmentCount];
        int stackPointer = 0;
        for (int i = 0; i < segmentCount; i++) {
            String segment = this.segments[i];
            if (segment.equals("..")) { //$NON-NLS-1$
                if (stackPointer == 0) {
                    // if the stack is empty we are going out of our scope 
                    // so we need to accumulate segments.  But only if the original
                    // path is relative.  If it is absolute then we can't go any higher than
                    // root so simply toss the .. references.
                    if (!isAbsolute())
                        stack[stackPointer++] = segment; //stack push
                } else {
                    // if the top is '..' then we are accumulating segments so don't pop
                    if ("..".equals(stack[stackPointer - 1])) //$NON-NLS-1$
                        stack[stackPointer++] = ".."; //$NON-NLS-1$
                    else
                        stackPointer--;
                    //stack pop
                }
                //collapse current references
            } else if (!segment.equals(".") || segmentCount == 1) //$NON-NLS-1$
                stack[stackPointer++] = segment; //stack push
        }
        //if the number of segments hasn't changed, then no modification needed
        if (stackPointer == segmentCount)
            return;
        //build the new segment array backwards by popping the stack
        String[] newSegments = new String[stackPointer];
        System.arraycopy(stack, 0, newSegments, 0, stackPointer);
        this.segments = newSegments;
    }

    /**
     * Removes duplicate slashes from the given path, with the exception
     * of leading double slash which represents a UNC path.
     */
    private String collapseSlashes(String path) {
        int length = path.length();
        // if the path is only 0, 1 or 2 chars long then it could not possibly have illegal
        // duplicate slashes.
        if (length < 3)
            return path;
        // check for an occurrence of // in the path.  Start at index 1 to ensure we skip leading UNC //
        // If there are no // then there is nothing to collapse so just return.
        if (path.indexOf("//", 1) == -1) //$NON-NLS-1$
            return path;
        // We found an occurrence of // in the path so do the slow collapse.
        char[] result = new char[path.length()];
        int count = 0;
        boolean hasPrevious = false;
        char[] characters = path.toCharArray();
        for (int index = 0; index < characters.length; index++) {
            char c = characters[index];
            if (c == SEPARATOR) {
                if (hasPrevious) {
                    // skip double slashes, except for beginning of UNC.
                    // note that a UNC path can't have a device.
                    if (this.device == null && index == 1) {
                        result[count] = c;
                        count++;
                    }
                } else {
                    hasPrevious = true;
                    result[count] = c;
                    count++;
                }
            } else {
                hasPrevious = false;
                result[count] = c;
                count++;
            }
        }
        return new String(result, 0, count);
    }

    /* (Intentionally not included in javadoc)
     * Computes the hash code for this object.
     */
    private int computeHashCode() {
        int hash = this.device == null ? 17 : this.device.hashCode();
        int segmentCount = this.segments.length;
        for (int i = 0; i < segmentCount; i++) {
            //this function tends to given a fairly even distribution
            hash = hash * 37 + this.segments[i].hashCode();
        }
        return hash;
    }

    /* (Intentionally not included in javadoc)
     * Returns the size of the string that will be created by toString or toOSString.
     */
    private int computeLength() {
        int length = 0;
        if (this.device != null)
            length += this.device.length();
        if ((this.separators & HAS_LEADING) != 0)
            length++;
        if ((this.separators & IS_UNC) != 0)
            length++;
        //add the segment lengths
        int max = this.segments.length;
        if (max > 0) {
            for (int i = 0; i < max; i++) {
                length += this.segments[i].length();
            }
            //add the separator lengths
            length += max - 1;
        }
        if ((this.separators & HAS_TRAILING) != 0)
            length++;
        return length;
    }

    /* (Intentionally not included in javadoc)
     * Returns the number of segments in the given path
     */
    private int computeSegmentCount(String path) {
        int len = path.length();
        if (len == 0 || (len == 1 && path.charAt(0) == SEPARATOR)) {
            return 0;
        }
        int count = 1;
        int prev = -1;
        int i;
        while ((i = path.indexOf(SEPARATOR, prev + 1)) != -1) {
            if (i != prev + 1 && i != len) {
                ++count;
            }
            prev = i;
        }
        if (path.charAt(len - 1) == SEPARATOR) {
            --count;
        }
        return count;
    }

    /**
     * Computes the segment array for the given canonicalized path.
     */
    private String[] computeSegments(String path) {
        // performance sensitive --- avoid creating garbage
        int segmentCount = computeSegmentCount(path);
        if (segmentCount == 0)
            return NO_SEGMENTS;
        String[] newSegments = new String[segmentCount];
        int len = path.length();
        // check for initial slash
        int firstPosition = (path.charAt(0) == SEPARATOR) ? 1 : 0;
        // check for UNC
        if (firstPosition == 1 && len > 1 && (path.charAt(1) == SEPARATOR))
            firstPosition = 2;
        int lastPosition = (path.charAt(len - 1) != SEPARATOR) ? len - 1 : len - 2;
        // for non-empty paths, the number of segments is 
        // the number of slashes plus 1, ignoring any leading
        // and trailing slashes
        int next = firstPosition;
        for (int i = 0; i < segmentCount; i++) {
            int start = next;
            int end = path.indexOf(SEPARATOR, next);
            
            final String segment = path.substring( start, ( end == -1 ? lastPosition + 1 : end ) );

            if( ! segment.equals( "." ) && ! segment.equals( ".." ) )
            {
                new FileName( segment ); // Validation for illegal characters, etc.
            }

            newSegments[ i ] = segment;
            next = end + 1;
        }
        return newSegments;
    }

    /**
     * Returns the platform-neutral encoding of the given segment onto
     * the given string buffer. This escapes literal colon characters with double colons.
     */
    private void encodeSegment(String string, StringBuffer buf) {
        int len = string.length();
        for (int i = 0; i < len; i++) {
            char c = string.charAt(i);
            buf.append(c);
            if (c == DEVICE_SEPARATOR)
                buf.append(DEVICE_SEPARATOR);
        }
    }

    /* (Intentionally not included in javadoc)
     * Compares objects for equality.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Path))
            return false;
        Path target = (Path) obj;
        //check leading separators and hash code
        if ((this.separators & HASH_MASK) != (target.separators & HASH_MASK))
            return false;
        String[] targetSegments = target.segments;
        int i = this.segments.length;
        //check segment count
        if (i != targetSegments.length)
            return false;
        //check segments in reverse order - later segments more likely to differ
        while (--i >= 0)
            if (!this.segments[i].equals(targetSegments[i]))
                return false;
        //check device last (least likely to differ)
        return equal( this.device, target.device );
    }

    /* (Intentionally not included in javadoc)
     * @see Path#getDevice
     */
    public String getDevice() {
        return this.device;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#getFileExtension
     */
    public String getFileExtension() {
        if (hasTrailingSeparator()) {
            return null;
        }
        String lastSegment = lastSegment();
        if (lastSegment == null) {
            return null;
        }
        int index = lastSegment.lastIndexOf('.');
        if (index == -1) {
            return null;
        }
        return lastSegment.substring(index + 1);
    }

    /* (Intentionally not included in javadoc)
     * Computes the hash code for this object.
     */
    @Override
    public int hashCode() {
        return this.separators & HASH_MASK;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#hasTrailingSeparator2
     */
    public boolean hasTrailingSeparator() {
        return (this.separators & HAS_TRAILING) != 0;
    }

    /*
     * Initialize the current path with the given string.
     */
    private Path initialize(String deviceString, String path) {
        
        if( path == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.device = deviceString;

        path = collapseSlashes(path);
        int len = path.length();

        //compute the separators array
        if (len < 2) {
            if (len == 1 && path.charAt(0) == SEPARATOR) {
                this.separators = HAS_LEADING;
            } else {
                this.separators = 0;
            }
        } else {
            boolean hasLeading = path.charAt(0) == SEPARATOR;
            boolean isUNC = hasLeading && path.charAt(1) == SEPARATOR;
            //UNC path of length two has no trailing separator
            boolean hasTrailing = !(isUNC && len == 2) && path.charAt(len - 1) == SEPARATOR;
            this.separators = hasLeading ? HAS_LEADING : 0;
            if (isUNC)
                this.separators |= IS_UNC;
            if (hasTrailing)
                this.separators |= HAS_TRAILING;
        }
        //compute segments and ensure canonical form
        this.segments = computeSegments(path);
        if (!canonicalize()) {
            //compute hash now because canonicalize didn't need to do it
            this.separators = (this.separators & ALL_SEPARATORS) | (computeHashCode() << 3);
        }
        return this;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#isAbsolute
     */
    public boolean isAbsolute() {
        //it's absolute if it has a leading separator
        return (this.separators & HAS_LEADING) != 0;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#isEmpty
     */
    public boolean isEmpty() {
        //true if no segments and no leading prefix
        return this.segments.length == 0 && ((this.separators & ALL_SEPARATORS) != HAS_LEADING);

    }

    /* (Intentionally not included in javadoc)
     * @see Path#isPrefixOf
     */
    public boolean isPrefixOf(Path anotherPath) {
        if (this.device == null) {
            if (anotherPath.getDevice() != null) {
                return false;
            }
        } else {
            if (!this.device.equalsIgnoreCase(anotherPath.getDevice())) {
                return false;
            }
        }
        if (isEmpty() || (isRoot() && anotherPath.isAbsolute())) {
            return true;
        }
        int len = this.segments.length;
        if (len > anotherPath.segmentCount()) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (!this.segments[i].equals(anotherPath.segment(i)))
                return false;
        }
        return true;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#isRoot
     */
    public boolean isRoot() {
        //must have no segments, a leading separator, and not be a UNC path.
        return this == ROOT || (this.segments.length == 0 && ((this.separators & ALL_SEPARATORS) == HAS_LEADING));
    }

    /* (Intentionally not included in javadoc)
     * @see Path#isUNC
     */
    public boolean isUNC() {
        if (this.device != null)
            return false;
        return (this.separators & IS_UNC) != 0;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#isValidPath(String)
     */
    public boolean isValidPath(String path) {
        Path test = new Path(path);
        for (int i = 0, max = test.segmentCount(); i < max; i++)
            if (!isValidSegment(test.segment(i)))
                return false;
        return true;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#isValidSegment(String)
     */
    public boolean isValidSegment(String segment) {
        int size = segment.length();
        if (size == 0)
            return false;
        for (int i = 0; i < size; i++) {
            char c = segment.charAt(i);
            if (c == '/')
                return false;
            if (WINDOWS && (c == '\\' || c == ':'))
                return false;
        }
        return true;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#lastSegment()
     */
    public String lastSegment() {
        int len = this.segments.length;
        return len == 0 ? null : this.segments[len - 1];
    }

    /* (Intentionally not included in javadoc)
     * @see Path#makeAbsolute()
     */
    public Path makeAbsolute() {
        if (isAbsolute()) {
            return this;
        }
        Path result = new Path(this.device, this.segments, this.separators | HAS_LEADING);
        //may need canonicalizing if it has leading ".." or "." segments
        if (result.segmentCount() > 0) {
            String first = result.segment(0);
            if (first.equals("..") || first.equals(".")) { //$NON-NLS-1$ //$NON-NLS-2$
                result.canonicalize();
            }
        }
        return result;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#makeRelative()
     */
    public Path makeRelative() {
        if (!isAbsolute()) {
            return this;
        }
        return new Path(this.device, this.segments, this.separators & HAS_TRAILING);
    }

    public Path makeRelativeTo( final Path base )
    {
        // Cannot make a relative path if devices are not equal. Note that paths may not have a device on
        // some operating systems.
        
        final String baseDevice = base.getDevice();
        
        if( ( this.device != null || baseDevice != null ) &&
            ( this.device == null || ! this.device.equalsIgnoreCase( baseDevice ) ) )
        {
            return this;
        }
        
        int commonLength = matchingFirstSegments(base);
        final int differenceLength = base.segmentCount() - commonLength;
        final int newSegmentLength = differenceLength + segmentCount() - commonLength;
        if (newSegmentLength == 0)
            return Path.EMPTY;
        String[] newSegments = new String[newSegmentLength];
        //add parent references for each segment different from the base
        Arrays.fill(newSegments, 0, differenceLength, ".."); //$NON-NLS-1$
        //append the segments of this path not in common with the base
        System.arraycopy(this.segments, commonLength, newSegments, differenceLength, newSegmentLength - differenceLength);
        return new Path(null, newSegments, this.separators & HAS_TRAILING);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#makeUNC(boolean)
     */
    public Path makeUNC(boolean toUNC) {
        // if we are already in the right form then just return
        if (!(toUNC ^ isUNC()))
            return this;

        int newSeparators = this.separators;
        if (toUNC) {
            newSeparators |= HAS_LEADING | IS_UNC;
        } else {
            //mask out the UNC bit
            newSeparators &= HAS_LEADING | HAS_TRAILING;
        }
        return new Path(toUNC ? null : this.device, this.segments, newSeparators);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#matchingFirstSegments(Path)
     */
    public int matchingFirstSegments(Path anotherPath) {
        if( anotherPath == null )
        {
            throw new IllegalArgumentException();
        }
        int anotherPathLen = anotherPath.segmentCount();
        int max = Math.min(this.segments.length, anotherPathLen);
        int count = 0;
        for (int i = 0; i < max; i++) {
            if (!this.segments[i].equals(anotherPath.segment(i))) {
                return count;
            }
            count++;
        }
        return count;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#removeFileExtension()
     */
    public Path removeFileExtension() {
        String extension = getFileExtension();
        if (extension == null || extension.equals("")) { //$NON-NLS-1$
            return this;
        }
        String lastSegment = lastSegment();
        int index = lastSegment.lastIndexOf(extension) - 1;
        return removeLastSegments(1).append(lastSegment.substring(0, index));
    }

    /* (Intentionally not included in javadoc)
     * @see Path#removeFirstSegments(int)
     */
    public Path removeFirstSegments(int count) {
        if (count == 0)
            return this;
        if (count >= this.segments.length) {
            return new Path(this.device, NO_SEGMENTS, 0);
        }
        if(count < 0)
        {
            throw new IllegalArgumentException();
        }
        int newSize = this.segments.length - count;
        String[] newSegments = new String[newSize];
        System.arraycopy(this.segments, count, newSegments, 0, newSize);

        //result is always a relative path
        return new Path(this.device, newSegments, this.separators & HAS_TRAILING);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#removeLastSegments(int)
     */
    public Path removeLastSegments(int count) {
        if (count == 0)
            return this;
        if (count >= this.segments.length) {
            //result will have no trailing separator
            return new Path(this.device, NO_SEGMENTS, this.separators & (HAS_LEADING | IS_UNC));
        }
        if(count < 0)
        {
            throw new IllegalArgumentException();
        }
        int newSize = this.segments.length - count;
        String[] newSegments = new String[newSize];
        System.arraycopy(this.segments, 0, newSegments, 0, newSize);
        return new Path(this.device, newSegments, this.separators);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#removeTrailingSeparator()
     */
    public Path removeTrailingSeparator() {
        if (!hasTrailingSeparator()) {
            return this;
        }
        return new Path(this.device, this.segments, this.separators & (HAS_LEADING | IS_UNC));
    }

    /* (Intentionally not included in javadoc)
     * @see Path#segment(int)
     */
    public String segment(int index) {
        if (index >= this.segments.length)
            return null;
        return this.segments[index];
    }

    /* (Intentionally not included in javadoc)
     * @see Path#segmentCount()
     */
    public int segmentCount() {
        return this.segments.length;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#segments()
     */
    public String[] segments() {
        String[] segmentCopy = new String[this.segments.length];
        System.arraycopy(this.segments, 0, segmentCopy, 0, this.segments.length);
        return segmentCopy;
    }

    /* (Intentionally not included in javadoc)
     * @see Path#setDevice(String)
     */
    public Path setDevice(String value) {
        if (value != null) {
            if( value.indexOf(Path.DEVICE_SEPARATOR) != (value.length() - 1) )
            {
                throw new IllegalArgumentException("Last character should be the device separator");
            }
        }
        //return the receiver if the device is the same
        if (value == this.device || (value != null && value.equals(this.device)))
            return this;

        return new Path(value, this.segments, this.separators);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#toFile()
     */
    public File toFile() {
        return new File(toOSString());
    }

    /* (Intentionally not included in javadoc)
     * @see Path#toOSString()
     */
    public String toOSString() {
        //Note that this method is identical to toString except
        //it uses the OS file separator instead of the path separator
        int resultSize = computeLength();
        if (resultSize <= 0)
            return EMPTY_STRING;
        char FILE_SEPARATOR = File.separatorChar;
        char[] result = new char[resultSize];
        int offset = 0;
        if (this.device != null) {
            int size = this.device.length();
            this.device.getChars(0, size, result, offset);
            offset += size;
        }
        if ((this.separators & HAS_LEADING) != 0)
            result[offset++] = FILE_SEPARATOR;
        if ((this.separators & IS_UNC) != 0)
            result[offset++] = FILE_SEPARATOR;
        int len = this.segments.length - 1;
        if (len >= 0) {
            //append all but the last segment, with separators
            for (int i = 0; i < len; i++) {
                int size = this.segments[i].length();
                this.segments[i].getChars(0, size, result, offset);
                offset += size;
                result[offset++] = FILE_SEPARATOR;
            }
            //append the last segment
            int size = this.segments[len].length();
            this.segments[len].getChars(0, size, result, offset);
            offset += size;
        }
        if ((this.separators & HAS_TRAILING) != 0)
            result[offset++] = FILE_SEPARATOR;
        return new String(result);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#toPortableString()
     */
    public String toPortableString() {
        int resultSize = computeLength();
        if (resultSize <= 0)
            return EMPTY_STRING;
        StringBuffer result = new StringBuffer(resultSize);
        if (this.device != null)
            result.append(this.device);
        if ((this.separators & HAS_LEADING) != 0)
            result.append(SEPARATOR);
        if ((this.separators & IS_UNC) != 0)
            result.append(SEPARATOR);
        int len = this.segments.length;
        //append all segments with separators
        for (int i = 0; i < len; i++) {
            if (this.segments[i].indexOf(DEVICE_SEPARATOR) >= 0)
                encodeSegment(this.segments[i], result);
            else
                result.append(this.segments[i]);
            if (i < len - 1 || (this.separators & HAS_TRAILING) != 0)
                result.append(SEPARATOR);
        }
        return result.toString();
    }

    /* (Intentionally not included in javadoc)
     * @see Path#toString()
     */
    @Override
    public String toString() {
        int resultSize = computeLength();
        if (resultSize <= 0)
            return EMPTY_STRING;
        char[] result = new char[resultSize];
        int offset = 0;
        if (this.device != null) {
            int size = this.device.length();
            this.device.getChars(0, size, result, offset);
            offset += size;
        }
        if ((this.separators & HAS_LEADING) != 0)
            result[offset++] = SEPARATOR;
        if ((this.separators & IS_UNC) != 0)
            result[offset++] = SEPARATOR;
        int len = this.segments.length - 1;
        if (len >= 0) {
            //append all but the last segment, with separators
            for (int i = 0; i < len; i++) {
                int size = this.segments[i].length();
                this.segments[i].getChars(0, size, result, offset);
                offset += size;
                result[offset++] = SEPARATOR;
            }
            //append the last segment
            int size = this.segments[len].length();
            this.segments[len].getChars(0, size, result, offset);
            offset += size;
        }
        if ((this.separators & HAS_TRAILING) != 0)
            result[offset++] = SEPARATOR;
        return new String(result);
    }

    /* (Intentionally not included in javadoc)
     * @see Path#uptoSegment(int)
     */
    public Path uptoSegment(int count) {
        if (count == 0)
            return new Path(this.device, NO_SEGMENTS, this.separators & (HAS_LEADING | IS_UNC));
        if (count >= this.segments.length)
            return this;
        if(count < 0 )
        {
            throw new IllegalArgumentException( "Invalid parameter to Path.uptoSegment" );
        }
        String[] newSegments = new String[count];
        System.arraycopy(this.segments, 0, newSegments, 0, count);
        return new Path(this.device, newSegments, this.separators);
    }
}