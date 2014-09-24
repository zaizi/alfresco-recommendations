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
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public class IRMahoutEvaluator implements MahoutEvaluator {
	
	private RecommenderIRStatsEvaluator evaluator;
	
	private int precisionAt = 10;
	
	private double evaluationPercentage = 1.0;

    public void setRecommenderEvaluator(RecommenderIRStatsEvaluator evaluator){
    	this.evaluator = evaluator;
    }
    
	public void setPrecisionAt(int precisionAt) {
		this.precisionAt = precisionAt;
	}

	public void setEvaluationPercentage(double evaluationPercentage) {
		this.evaluationPercentage = evaluationPercentage;
	}

	public double evaluate(RecommenderBuilder builder,
			DataModel datamodel) throws TasteException {

		DataModelBuilder modelBuilder = new DataModelBuilder() {
			
			public DataModel buildDataModel(FastByIDMap<PreferenceArray> trainingData) {
				return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
			}
		};
		
				
		return evaluator.evaluate(builder, modelBuilder, datamodel, null, precisionAt, GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, evaluationPercentage).getF1Measure();
		
	}

}
