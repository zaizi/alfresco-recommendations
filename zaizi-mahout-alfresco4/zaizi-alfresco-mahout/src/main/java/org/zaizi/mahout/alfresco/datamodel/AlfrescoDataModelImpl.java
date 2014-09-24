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
package org.zaizi.mahout.alfresco.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.rating.Rating;
import org.alfresco.service.cmr.rating.RatingScheme;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveArrayIterator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.BooleanItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.BooleanPreference;
import org.apache.mahout.cf.taste.impl.model.BooleanUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.zaizi.mahout.alfresco.ZaiziAlfrescoServiceUtil;
import org.zaizi.mahout.util.LongListIterator;

/**
 * Created by IntelliJ IDEA. User: jcarrey Date: 31/08/2011 Time: 08:55 To change this template use File | Settings |
 * File Templates.
 */
public class AlfrescoDataModelImpl implements DataModel
{
    private static Logger logger = Logger.getLogger(AlfrescoDataModelImpl.class);

    private static final float MAX_SCORE = 5;

    private static final String preferencesFromUserQuery = "ASPECT:cm\\:rateable ";

    private static final String allItemIdsQuery = "ASPECT:cm\\:rateable AND TYPE:cm\\:content";

    private static final String LIKES_RATING_SCHEME = "likesRatingScheme";

    private org.zaizi.mahout.config.RatingScheme mahoutRatingScheme;

    public void setMahoutRatingScheme(org.zaizi.mahout.config.RatingScheme ratingScheme)
    {
        this.mahoutRatingScheme = ratingScheme;
    }

    // traceing
    private ResultSet executeQuery(SearchParameters searchParameters)
    {
        logger.trace("Executing query : " + searchParameters.getQuery());
        return ZaiziAlfrescoServiceUtil.getSearchService().query(searchParameters);
    }

    // traceing
    private String toList(long[] resultKeys)
    {
        StringBuilder buffer = new StringBuilder("[");
        for (long l : resultKeys)
        {
            buffer.append(String.valueOf(l)).append(",");
        }
        buffer.append("]");
        return buffer.toString();
    }

    private Rating convertNodeRefToRating(String user, NodeRef ratingNode)
    {
        logger.trace("convertNodeRefToRating {" + user + "}");

        Map<QName, Serializable> properties = ZaiziAlfrescoServiceUtil.getNodeService().getProperties(ratingNode);

        String existingRatingScheme = (String) properties.get(ContentModel.PROP_RATING_SCHEME);

        Float existingRatingScore = (Float) properties.get(ContentModel.PROP_RATING_SCORE);
        Date existingRatingDate = (Date) properties.get(ContentModel.PROP_RATED_AT);

        Rating result = new Rating(ZaiziAlfrescoServiceUtil.getServiceRegistry().getRatingService().getRatingScheme(
                existingRatingScheme), existingRatingScore, user, existingRatingDate);
        return result;
    }

    public LongPrimitiveIterator getUserIDs() throws TasteException
    {
        logger.trace("getUserIds");

        Set<NodeRef> persons = ZaiziAlfrescoServiceUtil.getPersonService().getAllPeople();
        long[] userIds = new long[persons.size()];
        int i = 0;
        for (NodeRef user : persons)
        {
            userIds[i] = ZaiziAlfrescoServiceUtil.getNodeId(user);
            i++;
        }

        if (logger.isTraceEnabled())
        {
            logger.trace(toList(userIds));
        }

        return new LongPrimitiveArrayIterator(userIds);
    }

    public PreferenceArray getPreferencesFromUser(long userID) throws TasteException
    {
        logger.trace("getPreferencesFromUser {" + userID + "}");

        String username = ZaiziAlfrescoServiceUtil.getUsername(userID);

        SearchParameters searchParams = new SearchParameters();
        searchParams.addStore(getStoreRef());
        searchParams.setQuery(preferencesFromUserQuery);
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);

        ResultSet results = null;
        List<Preference> resultlist;

        try
        {
            NodeRef documentRef;
            Rating ratingRef;
            float rating;
            long itemId;

            results = executeQuery(searchParams);

            resultlist = new ArrayList<Preference>(results.length());
            for (ResultSetRow row : results)
            {
                documentRef = row.getNodeRef(); // Document rated
                ratingRef = getRatingValue(documentRef, username);

                if (ratingRef != null)
                {
                    itemId = ZaiziAlfrescoServiceUtil.getNodeId(documentRef);
                    if (mahoutRatingScheme.equals(org.zaizi.mahout.config.RatingScheme.BOOLEAN_PREF))
                    {
                        resultlist.add(new BooleanPreference(userID, itemId));
                    }
                    else
                    {
                        rating = ratingRef.getScore();// (Integer)
                                                      // ZaiziAlfrescoServiceUtil.getNodeService().getProperty(ratingRef,
                                                      // RatingsModel.PROP_RATING);
                        resultlist.add(new GenericPreference(userID, itemId, rating));
                    }

                }
            }
        }
        finally
        {
            if (results != null)
            {
                results.close();
            }
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("Returning results { " + resultlist + "}");
        }

        if (mahoutRatingScheme.equals(org.zaizi.mahout.config.RatingScheme.BOOLEAN_PREF))
            return new BooleanUserPreferenceArray(resultlist);
        else
            return new GenericUserPreferenceArray(resultlist);
    }

    public FastIDSet getItemIDsFromUser(long userID) throws TasteException
    {

        logger.trace("getItemIDsFromUser {" + userID + "}");

        SearchParameters searchParams = new SearchParameters();

        String username = ZaiziAlfrescoServiceUtil.getUsername(userID);

        searchParams.setQuery(preferencesFromUserQuery);
        searchParams.addStore(getStoreRef());
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);

        ResultSet results = null;
        List<Long> resultKeys;
        try
        {
            NodeRef documentRef;
            Rating ratingRef;

            long itemId;

            results = executeQuery(searchParams);
            resultKeys = new ArrayList<Long>(results.length());
            for (ResultSetRow row : results)
            {
                documentRef = row.getNodeRef();
                ratingRef = getRatingValue(documentRef, username);
                if (ratingRef != null)
                {
                    itemId = ZaiziAlfrescoServiceUtil.getNodeId(documentRef);
                    resultKeys.add(itemId);
                }
            }
        }
        finally
        {
            if (results != null)
            {
                results.close();
            }
        }

        int i = 0;
        long[] keySet = new long[resultKeys.size()];
        for (Long l : resultKeys)
        {
            keySet[i] = l;
            i++;
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("Returining :" + toList(keySet));
        }

        return new FastIDSet(keySet);
    }

    public LongPrimitiveIterator getItemIDs() throws TasteException
    {

        logger.trace("getItemIDs");

        SearchParameters searchParams = new SearchParameters();

        searchParams.setQuery(allItemIdsQuery);
        searchParams.addStore(getStoreRef());
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);

        ResultSet results = null;
        List<Long> resultKeys;
        try
        {
            NodeRef currentNodeRef;
            long itemId;

            results = executeQuery(searchParams);
            resultKeys = new ArrayList<Long>(results.length());
            for (ResultSetRow row : results)
            {
                currentNodeRef = row.getNodeRef();
                itemId = ZaiziAlfrescoServiceUtil.getNodeId(currentNodeRef);
                resultKeys.add(itemId);
            }
        }
        finally
        {
            if (results != null)
            {
                results.close();
            }
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("Returining :" + resultKeys);
        }

        return new LongListIterator(resultKeys);
    }

    public PreferenceArray getPreferencesForItem(long itemID) throws TasteException
    {

        logger.trace("getPreferencesForItem {" + itemID + "}");

        List<GenericPreference> resultlist;

        NodeRef currentNodeRef;
        float rating;
        long userId;

        currentNodeRef = ZaiziAlfrescoServiceUtil.getNodeRefByEntryId(itemID);

        List<Rating> ratings = getRatings(currentNodeRef);

        resultlist = new ArrayList<GenericPreference>(ratings.size());

        for (Rating ratingRef : ratings)
        {
            userId = ZaiziAlfrescoServiceUtil.getNodeId(ZaiziAlfrescoServiceUtil.getPersonService().getPerson(
                    ratingRef.getAppliedBy()));
            rating = ratingRef.getScore();
            resultlist.add(new GenericPreference(userId, itemID, rating));
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("Returining :" + resultlist);
        }

        if (mahoutRatingScheme.equals(org.zaizi.mahout.config.RatingScheme.BOOLEAN_PREF))
            return new BooleanItemPreferenceArray(resultlist, false);
        else
            return new GenericItemPreferenceArray(resultlist);

    }

    public Float getPreferenceValue(long userID, long itemID) throws TasteException
    {

        logger.trace("getPreferenceValue {" + userID + "} itemId{" + itemID + "}");

        String username = ZaiziAlfrescoServiceUtil.getUsername(userID);

        SearchParameters searchParams = new SearchParameters();
        searchParams.setQuery(preferencesFromUserQuery);
        searchParams.addStore(getStoreRef());
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);

        ResultSet results = null;
        try
        {
            results = executeQuery(searchParams);

            if (results.length() > 0)
            {
                NodeRef documentRef = results.getRow(0).getNodeRef();
                Rating rating = getRatingValue(documentRef, username);
                if (logger.isTraceEnabled())
                {
                    logger.trace("Returining :" + rating.getScore());
                }
                if (rating != null)
                {
                    return rating.getScore();
                }
                else
                {
                    return 0.0f;
                }
            }
        }
        finally
        {
            if (results != null)
            {
                results.close();
            }
        }

        logger.trace("No preference");

        return null; // No preference
    }

    private Float getUserRating(NodeRef ratingRef)
    {
        logger.trace("getUserRating");

        RatingScheme ratingScheme = (RatingScheme) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(ratingRef,
                ContentModel.PROP_RATING_SCHEME);
        float max = ratingScheme.getMaxRating();
        float min = ratingScheme.getMinRating();

        if (min < 0)
        { // -5 to 5 ==> 0 to 10
            max = max - min;
            min = 0;
        }

        float score = (Float) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(ratingRef,
                ContentModel.PROP_RATING_SCORE);
        // 1 to 5 -> x * 6 / 10
        return score * (MAX_SCORE + 1) / max;
    }

    public Long getPreferenceTime(long userID, long itemID) throws TasteException
    {

        logger.trace("getPreferenceTime {" + userID + "} itemId{" + itemID + "}");

        NodeRef documentRef = ZaiziAlfrescoServiceUtil.getNodeRefByEntryId(itemID);
        String username = ZaiziAlfrescoServiceUtil.getUsername(userID);
        Rating ratingRef = getRatingValue(documentRef, username);
        Date datetime = ratingRef.getAppliedAt();

        if (logger.isTraceEnabled())
        {
            logger.trace("Returining :" + datetime);
        }
        return datetime.getTime();
    }

    /**
     * TODO return the value of the rating of user to a document. 3.4.0[EE] TODO DIFFERS FROM Alfresco-EE-3.4.3 and same
     * for Alfresco-EE-4.0
     * 
     * Alfresco is changing the implementation of services for ratings. The ratings are saved incorrectly, so the
     * document has no children's on their children association to the type (cm:rating).
     * <p/>
     * Aspect (cm:rateable) has association (cm:ratings) of type (cm:rating)
     * 
     * @param targetNode document rated
     * @param username user who has rated the document
     * @return rating of user over the document or null if does not exists
     */
    private Rating getRatingValue(NodeRef targetNode, String username)
    {

        final QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, username + "__"
                + LIKES_RATING_SCHEME);
        List<ChildAssociationRef> myRatingChildren = ZaiziAlfrescoServiceUtil.getNodeService().getChildAssocs(
                targetNode, ContentModel.ASSOC_RATINGS, assocQName);
        if (myRatingChildren.isEmpty())
        {
            logger.trace("No children available");
            return null;
        }
        else
        {
            // There are previous ratings by this user.
            if (myRatingChildren.size() > 1 && logger.isTraceEnabled())
            {
                logger.trace("Warn : More than 1 children returned");
            }

            NodeRef myPreviousRatingsNode = myRatingChildren.get(0).getChildRef();
            return convertNodeRefToRating(username, myPreviousRatingsNode);
        }

    }

    /**
     * TODO return the ratings done to a document. 3.4.0 TODO DIFFERS FROM Alfresco-EE-3.4.3 and same for
     * Alfresco-EE-4.0 Alfresco is changing services
     * 
     * @param targetNode Current document
     * @return list of ratings of current document
     * @see this.getRatingValue()
     */
    private List<Rating> getRatings(NodeRef targetNode)
    {
        List<Rating> result = new ArrayList<Rating>();

        List<ChildAssociationRef> ratingChildrens = ZaiziAlfrescoServiceUtil.getNodeService().getChildAssocs(
                targetNode, ContentModel.ASSOC_RATINGS, RegexQNamePattern.MATCH_ALL);
        if (ratingChildrens.isEmpty())
        {
            logger.trace("No children available");
        }
        else
        {

            for (ChildAssociationRef childrenAssociation : ratingChildrens)
            {
                result.add(convertNodeRefToRating(childrenAssociation.getQName().getLocalName(),
                        childrenAssociation.getChildRef()));
            }
        }

        return result;
    }

    public int getNumItems() throws TasteException
    {

        logger.trace("getNumItems");

        SearchParameters searchParams = new SearchParameters();

        searchParams.setQuery(allItemIdsQuery);
        searchParams.addStore(getStoreRef());
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);

        ResultSet results = null;
        try
        {
            results = executeQuery(searchParams);
            if (logger.isTraceEnabled())
            {
                logger.trace("Returining :" + results.length());
            }
            return results.length();
        }
        finally
        {
            if (results != null)
            {
                results.close();
            }
        }

    }

    public int getNumUsers() throws TasteException
    {

        logger.trace("getNumUsers");

        int numPeople = ZaiziAlfrescoServiceUtil.getPersonService().getAllPeople().size();

        if (logger.isTraceEnabled())
        {
            logger.trace("Returining :" + numPeople);
        }

        return numPeople;
    }

    public int getNumUsersWithPreferenceFor(long... itemIDs) throws TasteException
    {

        logger.warn("getNumUsersWithPreferenceFor - WARN (Not implemented)");

        return 0;
    }

    public void setPreference(long userID, long itemID, float value) throws TasteException
    {

        logger.trace("setPreference");

    }

    public void removePreference(long userID, long itemID) throws TasteException
    {

        logger.trace("removePreference");

    }

    public boolean hasPreferenceValues()
    {

        logger.trace("hasPreferenceValues");

        return true;
    }

    public float getMaxPreference()
    {
        return 5.0F;
    }

    public float getMinPreference()
    {
        return 1.0F;
    }

    public void refresh(Collection<Refreshable> alreadyRefreshed)
    {

        logger.trace("refresh");

    }

    private StoreRef getStoreRef()
    {
        return ZaiziAlfrescoServiceUtil.getStoreRef();
    }

}
