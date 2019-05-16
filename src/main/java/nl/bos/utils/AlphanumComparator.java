package nl.bos.utils;

import java.util.Comparator;

/**
 * A string comparator that uses the <a href="http://www.davekoelle.com/alphanum.html">alphanum algorithm</a> to compare
 * strings that contain numbers.
 *
 * <p>This is an updated version with enhancements made by Daniel Migowski,
 * Andre Bogus, and David Koelle. Updated by David Koelle in 2017.
 *
 * <p><a href="http://www.davekoelle.com/files/AlphanumComparator.java">Original source</a></p>
 */
public final class AlphanumComparator implements Comparator<String> {

    /**
     * A public default instance to use, since every instance of this class is identical.
     */
    public static final AlphanumComparator INSTANCE = new AlphanumComparator();

    private boolean isDigit(char ch) {
        return (ch >= 48) && (ch <= 57);
    }

    private String getChunk(String string, int startMarker) {
        final int numChars = string.length();
        int marker = startMarker;
        StringBuilder chunk = new StringBuilder();
        char curChar = string.charAt(marker);
        chunk.append(curChar);
        marker++;
        if (isDigit(curChar)) {
            while (marker < numChars) {
                curChar = string.charAt(marker);
                if (!isDigit(curChar)) {
                    break;
                }
                chunk.append(curChar);
                marker++;
            }
        } else {
            while (marker < numChars) {
                curChar = string.charAt(marker);
                if (isDigit(curChar)) {
                    break;
                }
                chunk.append(curChar);
                marker++;
            }
        }
        return chunk.toString();
    }

    @Override
    public int compare(String s1, String s2) {
        if ((s1 == null) || (s2 == null)) {
            return 0;
        }

        int thisMarker = 0;
        int thatMarker = 0;
        int s1Length = s1.length();
        int s2Length = s2.length();

        while (thisMarker < s1Length && thatMarker < s2Length) {
            String thisChunk = getChunk(s1, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2, thatMarker);
            thatMarker += thatChunk.length();

            // If both chunks contain numeric characters, sort them numerically
            int result = 0;
            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                // Simple chunk comparison by length.
                int thisChunkLength = thisChunk.length();
                result = thisChunkLength - thatChunk.length();
                // If equal, the first different number counts
                if (result == 0) {
                    for (int i = 0; i < thisChunkLength; i++) {
                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
                        if (result != 0) {
                            return result;
                        }
                    }
                }
            } else {
                result = thisChunk.compareTo(thatChunk);
            }

            if (result != 0) {
                return result;
            }
        }

        return s1Length - s2Length;
    }

}