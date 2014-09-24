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


import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveArrayIterator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.zaizi.mahout.alfresco.ZaiziAlfrescoServiceUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 31/08/2011
 * Time: 08:55
 * To change this template use File | Settings | File Templates.
 */
public class MetaversantDataModelImpl implements DataModel {

    private static Logger logger = Logger.getLogger(MetaversantDataModelImpl.class);

    private static final String preferencesFromUserQuery = "TYPE:mr\\:rating AND @mr\\:rater:";

    private static final String allItemIdsQuery = "ASPECT:mr\\:rateable AND TYPE:cm\\:content";


    private ResultSet executeQuery(SearchParameters params) throws TasteException {
        try {
            return ZaiziAlfrescoServiceUtil.getSearchService().query(params);
        } catch (AlfrescoRuntimeException are) {
            throw new TasteException("Error executing query :" + are, are);
        }
    }

    private List<NodeRef> getRatings(NodeRef nodeRef) {
        logger.trace("getRatings(nodeRef)" + nodeRef.getId());


        ArrayList<NodeRef> returnList = new ArrayList<NodeRef>();
        List<ChildAssociationRef> children = ZaiziAlfrescoServiceUtil.getNodeService().getChildAssocs(nodeRef, RatingsModel.ASSN_MR_RATINGS,
                new RegexQNamePattern(RatingsModel.NAMESPACE_METAVERSANT_RATINGS_CONTENT_MODEL,
                        "rating.*"));
        for (ChildAssociationRef child : children) {
            returnList.add(child.getChildRef());
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Returning : " + returnList);
        }
        return returnList;
    }

    public LongPrimitiveIterator getUserIDs() throws TasteException {

        logger.trace("getUserIDs");


        Set<NodeRef> persons = ZaiziAlfrescoServiceUtil.getPersonService().getAllPeople();


        long[] userIds = new long[persons.size()];
        int i = 0;
        for (NodeRef user : persons) {
            userIds[i] = ZaiziAlfrescoServiceUtil.getNodeId(user);
            i++;
        }


        if (logger.isTraceEnabled()) {
            logger.trace("Returning : " + toList(userIds));
        }


        return new LongPrimitiveArrayIterator(userIds);
    }

    public PreferenceArray getPreferencesFromUser(long userID) throws TasteException {

        logger.trace("getPreferencesFromUser {" + userID + "}");

        String username = ZaiziAlfrescoServiceUtil.getUsername(userID);

        SearchParameters searchParams = new SearchParameters();
        searchParams.addStore(getStoreRef());
        searchParams.setQuery(preferencesFromUserQuery + "\"" + username + "\"");
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);
        //logger.trace(searchParams.getQuery());

        ResultSet results = null;
        List<GenericPreference> resultlist;

        try {
            NodeRef currentNodeRef;
            int rating;
            long itemId;

            results = executeQuery(searchParams);
            resultlist = new ArrayList<GenericPreference>(results.length());

            for (ResultSetRow row : results) {
                currentNodeRef = row.getNodeRef();
                rating = (Integer) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(currentNodeRef, RatingsModel.PROP_RATING);
                itemId = ZaiziAlfrescoServiceUtil.getNodeId(
                        getParent(currentNodeRef));
                resultlist.add(new GenericPreference(userID, itemId, Integer.valueOf(rating).floatValue()));
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }


        if (logger.isTraceEnabled()) {
            logger.trace("Returning : " + resultlist);
        }

        return new GenericUserPreferenceArray(resultlist);
    }

    private NodeRef getParent(NodeRef currentNodeRef) {
        return ZaiziAlfrescoServiceUtil.getNodeService().getPrimaryParent(currentNodeRef).getParentRef();
    }

    public FastIDSet getItemIDsFromUser(long userID) throws TasteException {

        logger.trace("getItemIDsFromUser { " + userID + "}");

        SearchParameters searchParams = new SearchParameters();

        final String username = ZaiziAlfrescoServiceUtil.getUsername(userID);

        searchParams.setQuery(preferencesFromUserQuery + "\"" + username + "\"");
        searchParams.addStore(getStoreRef());
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);
        //logger.trace(searchParams.getQuery());

        ResultSet results = null;
        long[] resultKeys;
        try {
            NodeRef currentNodeRef;
            long itemId;

            results = executeQuery(searchParams);
            resultKeys = new long[results.length()];
            int i = 0;
            for (ResultSetRow row : results) {
                currentNodeRef = row.getNodeRef();
                itemId = ZaiziAlfrescoServiceUtil.getNodeId(getParent(currentNodeRef));
                resultKeys[i] = itemId;
                i++;
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }


        if (logger.isTraceEnabled()) {
            logger.trace("Returning : " + toList(resultKeys));
        }

        return new FastIDSet(resultKeys);
    }

    private String toList(long[] resultKeys) {
        StringBuilder buffer = new StringBuilder("[");
        for (long l : resultKeys) {
            buffer.append(String.valueOf(l)).append(",");
        }
        buffer.append("]");
        return buffer.toString();
    }

    public LongPrimitiveIterator getItemIDs() throws TasteException {

        logger.trace("getItemIDs");

        SearchParameters searchParams = new SearchParameters();

        searchParams.setQuery(allItemIdsQuery);
        searchParams.addStore(getStoreRef());
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);
        //logger.trace(searchParams.getQuery());

        ResultSet results = null;
        long[] resultKeys;
        try {
            NodeRef currentNodeRef;
            long itemId;

            results = executeQuery(searchParams);
            resultKeys = new long[results.length()];
            int i = 0;
            for (ResultSetRow row : results) {
                currentNodeRef = row.getNodeRef();
                itemId = ZaiziAlfrescoServiceUtil.getNodeId(currentNodeRef);
                resultKeys[i] = itemId;
                i++;
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Returning : " + toList(resultKeys));
        }

        return new LongPrimitiveArrayIterator(resultKeys);
    }

    public PreferenceArray getPreferencesForItem(long itemID) throws TasteException {

        logger.trace("getPreferencesForItem { " + itemID + "}");


        List<GenericPreference> resultlist;

        NodeRef currentNodeRef;
        int rating;
        long userId;

        currentNodeRef = ZaiziAlfrescoServiceUtil.getNodeRefByEntryId(itemID);
        List<NodeRef> ratings = getRatings(currentNodeRef);
        resultlist = new ArrayList<GenericPreference>(ratings.size());
        for (NodeRef ratingRef : ratings) {
            userId = ZaiziAlfrescoServiceUtil.getNodeId(
                    ZaiziAlfrescoServiceUtil.getPersonService().getPerson((String) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(ratingRef, RatingsModel.PROP_RATER)));
            rating = (Integer) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(ratingRef, RatingsModel.PROP_RATING);
            resultlist.add(new GenericPreference(userId, itemID, Integer.valueOf(rating).floatValue()));
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Returning :" + resultlist);
        }

        return new GenericItemPreferenceArray(resultlist);
    }

    public Float getPreferenceValue(long userID, long itemID) throws TasteException {

        logger.trace("getPreferenceValue {" + userID + "} itemID{" + itemID + "}");

        String username = ZaiziAlfrescoServiceUtil.getUsername(userID);
        NodeRef itemNodeRef = ZaiziAlfrescoServiceUtil.getNodeRefByEntryId(itemID);

        ResultSet results = null;
        try {
            NodeRef currentNodeRef = getRatingNodeRef(itemNodeRef, username);
            if (currentNodeRef != null) {
                float result = Integer.valueOf(getUserRating(currentNodeRef, username)).floatValue();
                if (logger.isTraceEnabled()) {
                    logger.trace("Returning " + result);
                }
                return result;
            } else {
                return null;
            }
        } finally {
            if (results != null) {
                results.close();
            }
        }

    }


    protected NodeRef getRatingNodeRef(NodeRef nodeRef, String user) throws TasteException {
        String queryString = "PARENT:\"" + nodeRef.toString() + "\" AND @mr\\:rater:\"" + user + "\"";

        SearchParameters searchParams = new SearchParameters();
        searchParams.setQuery(queryString);
        searchParams.addStore(nodeRef.getStoreRef());
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);

        ResultSet results = executeQuery(searchParams);
        try {
            List<NodeRef> resultList = results.getNodeRefs();

            if (resultList == null || resultList.isEmpty()) {
                logger.trace("No ratings found for this node for user: " + user);
                return null;
            } else {
                return resultList.get(resultList.size() - 1);
            }

        } finally {
            if (results != null) {
                results.close();
            }
        }
    }

    private int getUserRating(NodeRef nodeRef, String user) {

        logger.trace("getUserRating");

        int rating = 0;

        if (user == null || user.equals("")) {
            logger.warn("User name was not passed in");
            return rating;
        }

        Integer ratingProp = (Integer) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(nodeRef, RatingsModel.PROP_RATING);

        if (ratingProp != null) {
            rating = ratingProp;
        }

        return rating;
    }


    public Long getPreferenceTime(long userID, long itemID) throws TasteException {
        logger.trace("getPreferenceTime");
        return null;
    }

    public int getNumItems() throws TasteException {
        logger.trace("getNumItems");

        SearchParameters searchParams = new SearchParameters();

        searchParams.setQuery(allItemIdsQuery);
        searchParams.addStore(getStoreRef());
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);
        //logger.trace(searchParams.getQuery());
        ResultSet results = null;
        try {
            results = executeQuery(searchParams);
            if (logger.isTraceEnabled()) {
                logger.trace("Returning : " + results.length());
            }
            return results.length();
        } finally {
            if (results != null) {
                results.close();
            }
        }

    }

    public int getNumUsers() throws TasteException {
        logger.trace("getNumUsers");
        return ZaiziAlfrescoServiceUtil.getPersonService().getAllPeople().size();
    }

    public int getNumUsersWithPreferenceFor(long... itemIDs) throws TasteException {
        logger.trace("getNumUsersWithPreferenceFor");
        return 0;
    }

    public void setPreference(long userID, long itemID, float value) throws TasteException {
        logger.trace("setPreference");

    }

    public void removePreference(long userID, long itemID) throws TasteException {
        logger.trace("removePreference");
    }

    public boolean hasPreferenceValues() {
        logger.trace("hasPreferenceValues");
        return true;
    }

    public float getMaxPreference() {
        return 5.0F;
    }

    public float getMinPreference() {
        return 1.0F;
    }

    public void refresh(Collection<Refreshable> alreadyRefreshed) {
        logger.trace("refresh");
    }

    public StoreRef getStoreRef() {
        return ZaiziAlfrescoServiceUtil.getStoreRef();
    }
}


