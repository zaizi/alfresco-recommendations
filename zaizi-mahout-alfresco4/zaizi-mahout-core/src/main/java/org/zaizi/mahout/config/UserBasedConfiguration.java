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
package org.zaizi.mahout.config;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 12/09/2011
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */
public class UserBasedConfiguration implements RecommenderConfiguration{

    private SimilarityConfiguration userSimilarityConfiguration;

    private NeighborhoodConfiguration neighborhoodConfiguration;

    private RatingScheme ratingScheme;

    public SimilarityConfiguration getUserSimilarityConfiguration() {
        return userSimilarityConfiguration;
    }

    public void setUserSimilarityConfiguration(SimilarityConfiguration userSimilarityConfiguration) {
        this.userSimilarityConfiguration = userSimilarityConfiguration;
    }

    public NeighborhoodConfiguration getNeighborhoodConfiguration() {
        return neighborhoodConfiguration;
    }

    public void setNeighborhoodConfiguration(NeighborhoodConfiguration neighborhoodConfiguration) {
        this.neighborhoodConfiguration = neighborhoodConfiguration;
    }

    public RatingScheme getRatingScheme() {
        return ratingScheme;
    }

    public void setRatingScheme(RatingScheme ratingScheme) {
        this.ratingScheme = ratingScheme;
    }


    public Recommender configure(DataModel dataModel) throws TasteException {
        UserSimilarity userSimilarity = userSimilarityConfiguration.getUserSimilarity(dataModel);
        UserNeighborhood neighborhood = neighborhoodConfiguration.getNeighborhood(dataModel,userSimilarity);
       switch (ratingScheme) {
           case BOOLEAN_PREF:
               return new GenericBooleanPrefUserBasedRecommender(dataModel, neighborhood, userSimilarity);
           case SCORE_PREF:
               return new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
           default:
               throw new TasteException("Only Boolean or Score rating supported.");
       }

    }

    public Recommender buildRecommender(DataModel dataModel) throws TasteException {
        return configure(dataModel);
    }

    @Override
    public String toString() {
        return new StringBuilder("UserBasedConfiguration{").append(
                "userSimilarityConfiguration=").append( userSimilarityConfiguration).append(
                ", neighborhoodConfiguration=").append( neighborhoodConfiguration).append(
                ", ratingScheme=").append( ratingScheme).append(
                '}').toString();
    }
}
