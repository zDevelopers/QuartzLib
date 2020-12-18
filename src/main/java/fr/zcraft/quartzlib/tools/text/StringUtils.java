package fr.zcraft.quartzlib.tools.text;

import org.jetbrains.annotations.Nullable;

/**
 * Various string-related utilities.
 */
public final class StringUtils {
    private StringUtils() {
    }

    /**
     * Find the nearest from a given string among the given list of candidates,
     *   computed based on a Levenshtein Distance.
     * <p>The string must be from at most a given maximum distance of all candidates, else null is returned.</p>
     * @param toTest The string to test.
     * @param candidates The list of candidates.
     * @param maxDistance The maximum distance.
     * @param <T> The type of CharSequence to test (usually String)
     * @return The nearest candidate to the string to test, if found within maxDistance.
     */
    @Nullable
    public static <T extends CharSequence> T levenshteinNearest(T toTest, Iterable<T> candidates, int maxDistance) {
        T nearest = null;
        int nearestDistance = maxDistance;

        for (T subCommand : candidates) {
            int distance = StringUtils.levenshteinDistance(toTest, subCommand);

            if (distance < nearestDistance) {
                nearest = subCommand;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    /**
     * Compute the distance of Levenshtein Distance between two strings.
     *
     * <p>Implementation is from:
     * https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java</p>
     * @param lhs The first string
     * @param rhs The second string
     * @return The distance between the two strings
     */
    public static int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) {
            cost[i] = i;
        }

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for (int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int costReplace = cost[i - 1] + match;
                int costInsert = cost[i] + 1;
                int costDelete = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
            }

            // swap cost/newcost arrays
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }
}
