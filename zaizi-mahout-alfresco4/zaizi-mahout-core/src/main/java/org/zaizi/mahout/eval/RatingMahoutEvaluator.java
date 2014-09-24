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
package org.zaizi.mahout.eval;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.model.DataModel;

public class RatingMahoutEvaluator implements MahoutEvaluator {

    private RecommenderEvaluator evaluator;
    
    private double trainingPercentage = 0.7;
    
    private double evaluationPercentage = 1.0;
    
    public void setRecommenderEvaluator(RecommenderEvaluator evaluator){
    	this.evaluator = evaluator;
    }
	
    	
	public void setTrainingPercentage(double trainingPercentage) {
		this.trainingPercentage = trainingPercentage;
	}


	public void setEvaluationPercentage(double evaluationPercentage) {
		this.evaluationPercentage = evaluationPercentage;
	}



	public double evaluate(RecommenderBuilder builder,
			DataModel datamodel) throws TasteException {
		return evaluator.evaluate(builder, null, datamodel, trainingPercentage, evaluationPercentage);
	}

}
