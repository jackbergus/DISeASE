/*
 * Copyright (C) 2011 Gian Luca Farina Perseu (http://21-style.com/blog/2011/09/apache-lucene-e-la-gestione-degli-apostrofi/)
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
package disease.Phase.cleaner.blog;

import java.util.StringTokenizer;

/**
 *
 * @author Gian Luca Farina Perseu (http://21-style.com/blog/2011/09/apache-lucene-e-la-gestione-degli-apostrofi/)
 */
public class ItalianStopWords {
 
        static String[] stopWords = null;
 
        public static String[] getStopWords() {
            if (stopWords != null) {
                return stopWords;
            } else {
                return elaborateStopWords();
            }
        }
 
        // By glfp.
        static String[] elaborateStopWords() {
 
            StringTokenizer st = new StringTokenizer(
                    "a b c d e f g h i j k l m n o p q r s t u v w x y z il lo la gli " +
                     "le un uno una di da in su per con tra fra al allo alla ai agli alle dal " +
                     "dallo dalla dai dagli dalle del dello della dei degli delle nel nello nella " +
                     "nei negli nelle sul sullo sulla sui sugli sulle avanti dietro stante durante " +
                     "sopra sotto salvo accanto avanti verso presso contro circa intorno fuori " +
                     "malgrado vicino lontano dentro indietro insieme assieme oltre senza attraverso " +
                     "nondimeno mio mia miei mie tuo tua tuoi tue suo sua suoi sue nostro nostra nostri " +
                     "nostre vostro vostra vostri vostre loro questo codesto cotesto quello ciò questa " +
                     "codesta cotesta quella io tu egli esso ella essa noi voi essi esse me mi te ti lui " +
                     "lei ce ci ve vi se si ne che colui colei cui chi sono sei è siamo siete sarebbe sarà " +
                     "essendo ho hai ha abbiamo avete hanno avrebbe avrà avendo avuto l' un' all' dall' dell' " +
                     "sull' nell' quell' c' v' po' può potrà potrebbe potuto deve dovrà dovrebbe dovuto " +
                     "ma però anzi tuttavia pure invece perciò quindi dunque pertanto ebbene orbene né nè neppure " +
                     "neanche nemmeno sia oppure ossia altrimenti cioè infatti invero difatti perchè perché poichè poiché " +
                     "giacchè giacché quando mentre finchè finché affinchè affinché acciocchè acciocché qualora purchè purché sebbene quantunque benchè benché " +
                     "nonostante come quasi fuorchè fuorché tranne eccetto laddove ah oh eh orsù urrà ahimè suvvia basta " +
                     "insomma così qui qua lì là già allora prima dopo ora poi sempre mai presto tardi intanto " +
                     "frattanto talvolta spesso molto troppo poco più meno assai niente nulla alquanto altrettanto " +
                     "anche perfino persino altresì finanche abbastanza almeno ancora appunto attualmente certamente " +
                     "comunque altrove dove dovunque effettivamente forse generalmente inoltre insufficientemente " +
                     "inutilmente naturalmente no non nuovamente ovunque ovviamente piuttosto precedentemente " +
                     "probabilmente realmente realmente semplicemente sì solitamente soprattutto specificamente " +
                     "successivamente sufficientemente veramente alcune alcuni alcuno altri altro certo chiunque ciascuno molti nessun nessuno ogni ognuno " +
                     "parecchi parecchio pochi qualche qualcosa qualcuno qualunque tanto tutti tutto qual quale quali " +
                     "quanto bene cosa cose data esempio male scelta " +
                     "differente difficile dissimile diverso entrambe entrambi facile inusuale " +
                     "inutile impossibile improbabile insolito insufficiente "+
                     "possibile probabile pronto semplice siffatto simile sufficiente usuale utile vuoto interno " +
                     "mediante modo ovvio precedente propri proprio prossimo scelto soli solito solo soltanto " +
                     "specifico stessi stesso subito successivo super tale totale totali uguale uguali ulteriore " +
                     "vari vario verso fu ed dell dall");
            String temp[] = new String[st.countTokens()];
            int i=0;
            while(st.hasMoreTokens()) {
                temp[i]=st.nextToken();
                i++;
            }
 
            return temp;
        }
    }