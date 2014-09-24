package org.zaizi.metaversant.alfresco.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.TransactionListenerAdapter;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.apache.log4j.Logger;
import org.zaizi.metaversant.alfresco.model.RatingsModel;

/**
 * @author iarroyo
 *
 */
public class RatingServiceImpl extends TransactionListenerAdapter implements RatingService
{

    private NodeService nodeService;
//    private SearchService searchService;
    private Set<String> waitSet = new HashSet<String>();

    private Logger logger = Logger.getLogger(RatingServiceImpl.class);

    private static final String KEY_USERNAME = RatingServiceImpl.class.getName() + ".username";
    private static final String KEY_NODE = RatingServiceImpl.class.getName() + ".node";

    public RatingData getRatingData(NodeRef nodeRef)
    {

        Integer total = (Integer) nodeService.getProperty(nodeRef, RatingsModel.PROP_TOTAL_RATING);
        if (total == null)
            total = 0;
        Integer count = (Integer) nodeService.getProperty(nodeRef, RatingsModel.PROP_RATING_COUNT);
        if (count == null)
            count = 0;
        Double rating = (Double) nodeService.getProperty(nodeRef, RatingsModel.PROP_AVERAGE_RATING);
        if (rating == null)
            rating = 0.0d;

        RatingData ratingData = new RatingDataImpl(total, count, rating);

        return ratingData;
    }

    protected NodeRef getRatingNodeRef(NodeRef nodeRef, String user)
    {
        
        // searchService (Solr synchronization too slow), we need to retrieve the node immediately after the transaction commit.
        // Get nodes from database by nodeService.

        NodeRef rateRef = null;
        List<ChildAssociationRef> assocs= nodeService.getChildAssocsByPropertyValue(nodeRef, RatingsModel.PROP_RATER, user);

        if(!assocs.isEmpty()){
            rateRef= assocs.get(0).getChildRef();
        }

        return rateRef;
        
        // String queryString = "PARENT:\"" + nodeRef.toString() + "\" AND @mr\\:rater:\"" + user + "\"";
        //
        // ResultSet results = searchService.query(nodeRef.getStoreRef(), SearchService.LANGUAGE_LUCENE, queryString);
        //
        // List<NodeRef> resultList = results.getNodeRefs();
        //
        // if (resultList == null || resultList.isEmpty())
        // {
        // logger.debug("No ratings found for this node for user: " + user);
        // return null;
        // }
        // else
        // {
        // return resultList.get(resultList.size() - 1);
        // }
    }

    public int getUserRating(NodeRef nodeRef, String user)
    {
        int rating = 0;

        if (user == null || user.equals(""))
        {
            logger.debug("User name was not passed in");
            return rating;
        }

        NodeRef ratingNodeRef = getRatingNodeRef(nodeRef, user);
        if (ratingNodeRef != null)
        {
            Integer ratingProp = (Integer) nodeService.getProperty(ratingNodeRef, RatingsModel.PROP_RATING);

            if (ratingProp != null)
            {
                rating = ratingProp;
            }
        }

        return rating;
    }

    public boolean hasRatings(NodeRef nodeRef)
    {
        List<NodeRef> ratingList = getRatings(nodeRef);
        return !ratingList.isEmpty();
    }

    /**
     * @param nodeRef
     * @return
     */
    public List<NodeRef> getRatings(NodeRef nodeRef)
    {
        ArrayList<NodeRef> returnList = new ArrayList<NodeRef>();
        List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef,
                (QNamePattern) RatingsModel.ASSN_MR_RATINGS, new RegexQNamePattern(
                        RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL, "rating.*"));
        for (ChildAssociationRef child : children)
        {
            returnList.add(child.getChildRef());
        }
        return returnList;
    }

    public void rate(NodeRef nodeRef, int rating, String user)
    {
        String key = nodeRef.toString() + "_" + user;
        if (isOtherThreadRating(key))
        {
            return;
        }

        NodeRef ratingNodeRef = getRatingNodeRef(nodeRef, user);
        if (ratingNodeRef == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating rate with nodeRef " + nodeRef + " and user " + user);
            }
            createRatingNode(nodeRef, rating, user);
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Updating rate with nodeRef " + nodeRef + " and user " + user);
            }
            // do an update instead of a create--we aren't going to allow more than
            // one vote per user
            // maybe we should make this configurable in the future
            updateRatingNode(ratingNodeRef, rating);
        }

        AlfrescoTransactionSupport.bindListener(this);
        AlfrescoTransactionSupport.bindResource(KEY_NODE, nodeRef);
        AlfrescoTransactionSupport.bindResource(KEY_USERNAME, user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.transaction.TransactionListenerAdapter#afterCommit()
     */
    @Override
    public void afterCommit()
    {
        NodeRef nodeRef = AlfrescoTransactionSupport.getResource(RatingServiceImpl.KEY_NODE);
        String user = AlfrescoTransactionSupport.getResource(RatingServiceImpl.KEY_USERNAME);

        String key = nodeRef.toString() + "_" + user;
        if (waitSet.contains(key))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Remove key by thread " + Thread.currentThread().getName());
            }
            waitSet.remove(key);
            if (logger.isDebugEnabled())
            {
                logger.debug("Removed key by thread " + Thread.currentThread().getName());
            }
        }
    }

    /**
     * @param nodeRef
     * @param user
     */
    private boolean isOtherThreadRating(String key)
    {
        boolean res = true;

        if (logger.isDebugEnabled())
        {
            logger.debug("Start isOtherThreadRating " + Thread.currentThread().getName());
        }

        synchronized (waitSet)
        {

            if (logger.isDebugEnabled())
            {
                logger.debug("synchronized block to thread name " + Thread.currentThread().getName());
            }
            if (!waitSet.contains(key))
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Key " + key + " not exist");
                    logger.debug("This rate will be processed...");
                }
                waitSet.add(key);
                res = false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("End isOtherThreadRating " + Thread.currentThread().getName());
        }

        return res;
    }

    protected void updateRatingNode(final NodeRef ratingNodeRef, final int rating)
    {

        AuthenticationUtil.runAs(new RunAsWork<String>()
        {
            @SuppressWarnings("synthetic-access")
            public String doWork() throws Exception
            {

                nodeService.setProperty(ratingNodeRef, RatingsModel.PROP_RATING, rating);

                logger.debug("Updated rating node");
                return "";
            }
        }, AuthenticationUtil.SYSTEM_USER_NAME);
    }

    protected void createRatingNode(final NodeRef nodeRef, final int rating, final String user)
    {

        AuthenticationUtil.runAs(new RunAsWork<String>()
        {
            @SuppressWarnings("synthetic-access")
            public String doWork() throws Exception
            {
                // add the aspect to this document if it needs it
                if (nodeService.hasAspect(nodeRef, RatingsModel.ASPECT_MR_RATEABLE))
                {
                    logger.debug("Document already has aspect");
                }
                else
                {
                    logger.debug("Adding rateable aspect");
                    nodeService.addAspect(nodeRef, RatingsModel.ASPECT_MR_RATEABLE, null);
                }

                Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                props.put(RatingsModel.PROP_RATING, rating);
                props.put(RatingsModel.PROP_RATER, user);

                nodeService.createNode(nodeRef, RatingsModel.ASSN_MR_RATINGS, QName.createQName(
                        RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL,
                        RatingsModel.PROP_RATING.getLocalName() + new Date().getTime()), RatingsModel.TYPE_MR_RATING,
                        props);

                logger.debug("Created rating node");
                return "";
            }
        }, AuthenticationUtil.SYSTEM_USER_NAME);
    }

    public void deleteRatings(NodeRef nodeRef)
    {
        // check the parent to make sure it has the right aspect
        if (nodeService.hasAspect(nodeRef, RatingsModel.ASPECT_MR_RATEABLE))
        {
            // continue, this is what we want
        }
        else
        {
            logger.debug("Node did not have rateable aspect.");
            return;
        }

        // get the node's children
        List<NodeRef> ratingList = getRatings(nodeRef);

        if (ratingList.size() == 0)
        {
            // No children so no work to do
            if (logger.isDebugEnabled())
                logger.debug("No children found");
        }
        else
        {
            // iterate through the children and remove each one
            for (NodeRef ratingNodeRef : ratingList)
            {
                nodeService.removeChild(nodeRef, ratingNodeRef);
            }
        }
    }

    /**
     * @author iarroyo
     *
     */
    public class RatingDataImpl implements RatingService.RatingData
    {
        protected int total;
        protected int count;
        protected double rating;

        /**
         * @param total
         * @param count
         * @param rating
         */
        public RatingDataImpl(int total, int count, double rating)
        {
            this.total = total;
            this.count = count;
            this.rating = rating;
        }

        public int getCount()
        {
            return count;
        }

        public double getRating()
        {
            return rating;
        }

        public int getTotal()
        {
            return total;
        }
    }

    /**
     * @param nodeService
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    // /**
    // * @param searchService
    // */
    // public void setSearchService(SearchService searchService)
    // {
    // this.searchService = searchService;
    // }
}
