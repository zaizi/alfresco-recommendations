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

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.zaizi.mahout.config.RecommenderConfiguration;
import org.zaizi.mahout.eval.MahoutEvaluator;
import org.zaizi.mahout.eval.MahoutRecommenderEvaluator;
import org.zaizi.mahout.eval.RecommenderScore;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 12/09/2011
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public class RecommendationFactory<T> {

	 public RecommendationService<T> getRecommender(ItemService<T> itemService,
                                                   RecommenderConfiguration configuration,
                                                   DataModel dataModel,
                                                   MahoutEvaluator evaluator,
                                                   boolean evaluate) throws TasteException {

        Recommender recommender = configuration.configure(dataModel);


        RecommendationServiceImpl<T> service =
                new RecommendationServiceImpl<T>(itemService,configuration.configure(dataModel));

        if (evaluate) {
            service.setScore(
                    MahoutRecommenderEvaluator.evaluate(recommender, evaluator).getScore()
            );
        }

        return service;
    }
	 
	 public RecommendationService<T> getRecommender(ItemService<T> itemService,
			 RecommenderConfiguration configuration,
			 DataModel dataModel) throws TasteException {

		  RecommendationServiceImpl<T> service =
				 new RecommendationServiceImpl<T>(itemService,configuration.configure(dataModel));

		 return service;
	 }

	 public static RecommenderScore evaluate(RecommenderConfiguration configuration, DataModel dataModel, MahoutEvaluator evaluator) throws TasteException {
        Recommender recommender = configuration.configure(dataModel);
        return MahoutRecommenderEvaluator.evaluate(recommender, evaluator);
    }
}
