package org.zaizi.metaversant.alfresco.model;

import org.alfresco.service.namespace.QName;

/**
 * @author iarroyo
 *
 */
public class RatingsModel
{
    public static final String NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL = "http://www.metaversant.com/model/ratings/1.0";

    public static final QName TYPE_MR_RATING = QName.createQName(
            RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "rating");

    // Aspects
    public static final QName ASPECT_MR_RATEABLE = QName.createQName(
            RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "rateable");

    public static final QName PROP_RATING = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL,
            "rating");
    public static final QName PROP_RATER = QName.createQName(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL,
            "rater");
    public static final QName PROP_AVERAGE_RATING = QName.createQName(
            RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "averageRating");
    public static final QName PROP_TOTAL_RATING = QName.createQName(
            RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "totalRating");
    public static final QName PROP_RATING_COUNT = QName.createQName(
            RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "ratingCount");

    // Associations
    public static final QName ASSN_MR_RATINGS = QName.createQName(
            RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "ratings");

}
