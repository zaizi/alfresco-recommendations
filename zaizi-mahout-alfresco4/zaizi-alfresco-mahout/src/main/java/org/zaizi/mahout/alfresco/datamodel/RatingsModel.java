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
/**
 Copyright 2011, Jeff Potts

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.zaizi.mahout.alfresco.datamodel;

import org.alfresco.service.namespace.QName;

public class RatingsModel {
    public static final String NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL = "http://www.metaversant.com/model/ratings/1.0";

    public static final QName TYPE_MR_RATING = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "rating");

    // Aspects
    public static final QName ASPECT_MR_RATEABLE = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "rateable");

    public static final QName PROP_RATING = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "rating");
    public static final QName PROP_RATER = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "rater");
    public static final QName PROP_AVERAGE_RATING = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "averageRating");
    public static final QName PROP_TOTAL_RATING = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "totalRating");
    public static final QName PROP_RATING_COUNT = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "ratingCount");

    // Associations
    public static final QName ASSN_MR_RATINGS = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "ratings");

}
