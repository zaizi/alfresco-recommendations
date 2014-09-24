/**
 * Alfresco Content Recommendation. Copyright (C) 2014 Zaizi Limited.
 *
 * ——————-
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * ———————
 */
package org.zaizi.mahout.recommendation;

import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 12/09/2011
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */
public class RecommendationServiceImpl<T> implements RecommendationService<T> {

    private ItemService<T> itemService;

    private Recommender recommender;

    private static Logger logger = Logger.getLogger(RecommendationService.class);

    /**
     * Score obtained if scored
     */
    private double score = Double.NaN;

    public RecommendationServiceImpl(ItemService<T> itemService, Recommender recommender) {
        this.itemService = itemService;
        this.recommender = recommender;
    }

    public List<T> getRecommendations(Long userId, int howMany, IDRescorer idRescorer) throws TasteException {
        long startTime = 0, endTime = 0;
        boolean calculateTime = logger.isDebugEnabled();

        if (calculateTime) {
          startTime = System.currentTimeMillis();
        }

        List<RecommendedItem> items = recommender.recommend(userId, howMany, idRescorer);

        if (calculateTime) {
            endTime = System.currentTimeMillis();
        }

        List<T> results = new ArrayList<T>(items.size());
        for (RecommendedItem item : items) {
            results.add(itemService.getItemById(item));
        }

        if (calculateTime) {
            long recommendationsTime = endTime - startTime;
            logger.debug("Recommendations calculation time : " + recommendationsTime + " ms");
        }


        return results;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }
}
