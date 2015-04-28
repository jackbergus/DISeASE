/*
 * Copyright (C) 2015 vasistas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package disease.similarities;

/**
 *
 * @author vasistas
 */
public class Levenshtein extends Similarity {

    private static Levenshtein self = null;
    public Levenshtein() {
        
    }
    public static Levenshtein getInstance() {
        if (self==null)
            self = new Levenshtein();
        return self;
    }
    
    public static int levenshteinDistance(String word1, String word2) {
        int n = word1.length();
        int m = word2.length();
        int distance[][] = new int[n+1][m+1];
        if (n == 0) return m;
        if (m == 0) return n;
        for (int i=0; i<=n; distance[i][0]=i++);
        for (int i=0; i<=0; distance[0][i]=i++);
        for (int i=1; i<=n; i++)
            for (int j=1; j<=m; j++) {
                char left = word1.charAt(i-1);
                char right = word2.charAt(j-1);
                int toset = distance[i-1][j-1] + (left==right ? 1 : 0);
                if (toset > distance[i][j-1]+1)
                    toset = distance[i][j-1]+1;
                if (toset > distance[i-1][j]+1)
                    toset = distance[i-1][j]+1;
                distance[i][j] = toset;
            }
        return distance[n][m];
    } 
    
    @Override
    public double sim(String word1, String word2) {
        double dis = levenshteinDistance(word1,word2);
        double maxLen = word1.length();
        if (maxLen < word2.length())
            maxLen = word2.length();
        if (maxLen==0)
            return 1.0;
        else
            return (1.0-(dis/maxLen));
    }

}
