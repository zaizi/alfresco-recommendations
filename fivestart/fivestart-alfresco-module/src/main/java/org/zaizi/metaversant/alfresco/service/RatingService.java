package org.zaizi.metaversant.alfresco.service;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author iarroyo
 *
 */
public interface RatingService
{
    /**
     * @param nodeRef
     * @param rating
     * @param user
     */
    public void rate(NodeRef nodeRef, int rating, String user);

    /**
     * @param nodeRef
     */
    public void deleteRatings(NodeRef nodeRef);

    /**
     * @param nodeRef
     * @return
     */
    public RatingData getRatingData(NodeRef nodeRef);

    /**
     * @param nodeRef
     * @param user
     * @return
     */
    public int getUserRating(NodeRef nodeRef, String user);

    /**
     * @param nodeRef
     * @return
     */
    public boolean hasRatings(NodeRef nodeRef);

    /**
     * @author iarroyo
     *
     */
    public interface RatingData
    {
        /**
         * @return
         */
        public int getCount();

        /**
         * @return
         */
        public double getRating();

        /**
         * @return
         */
        public int getTotal();
    }

}
