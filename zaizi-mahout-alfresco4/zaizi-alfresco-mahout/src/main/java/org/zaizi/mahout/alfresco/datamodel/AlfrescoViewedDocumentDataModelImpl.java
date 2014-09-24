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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveArrayIterator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.AbstractDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public class AlfrescoViewedDocumentDataModelImpl extends AbstractDataModel implements AlfrescoViewDataModel
{
    private static Logger logger = Logger.getLogger(AlfrescoViewedDocumentDataModelImpl.class);

    private FastByIDMap<PreferenceArray> preferenceFromUsers = new FastByIDMap<PreferenceArray>();
    private FastByIDMap<PreferenceArray> preferenceForItems = new FastByIDMap<PreferenceArray>();
    private final FastByIDMap<FastByIDMap<Long>> timestamps = null;

    private static final int MAX_VIEWED = 10;
    private static final float MAX_SCORE = 5;

    public void refresh(Collection<Refreshable> alreadyRefreshed)
    {
        logger.trace("refresh");
        // Our DataModel is always Refreshed
    }

    public synchronized LongPrimitiveIterator getUserIDs() throws TasteException
    {
        long[] userIds = new long[preferenceFromUsers.size()];
        int i = 0;
        LongPrimitiveIterator it = preferenceFromUsers.keySetIterator();
        while (it.hasNext())
        {
            userIds[i++] = it.next();
        }
        Arrays.sort(userIds);
        return new LongPrimitiveArrayIterator(userIds);
    }

    public synchronized PreferenceArray getPreferencesFromUser(long userID) throws TasteException
    {
        PreferenceArray prefs = preferenceFromUsers.get(userID);
        if (prefs == null)
        {
            return new GenericUserPreferenceArray(0);
        }
        return prefs;
    }

    public synchronized FastIDSet getItemIDsFromUser(long userID) throws TasteException
    {
        PreferenceArray prefs = getPreferencesFromUser(userID);
        if(prefs == null)
        	return new FastIDSet(0);
        
        int size = prefs.length();
        FastIDSet result = new FastIDSet(size);
        for (int i = 0; i < size; i++)
        {
            result.add(prefs.getItemID(i));
        }
        return result;
    }

    public synchronized LongPrimitiveIterator getItemIDs() throws TasteException
    {
        long[] itemIds = new long[preferenceForItems.size()];
        int i = 0;
        LongPrimitiveIterator it = preferenceForItems.keySetIterator();
        while (it.hasNext())
        {
            itemIds[i++] = it.next();
        }
        Arrays.sort(itemIds);
        return new LongPrimitiveArrayIterator(itemIds);
    }

    public synchronized PreferenceArray getPreferencesForItem(long itemID) throws TasteException
    {
        PreferenceArray prefs = preferenceForItems.get(itemID);
        if (prefs == null)
        {
            return new GenericItemPreferenceArray(0);
        }
        return prefs;
    }

    public synchronized Float getPreferenceValue(long userID, long itemID) throws TasteException
    {
        PreferenceArray prefs = getPreferencesFromUser(userID);
        if(prefs == null)
        	return null;
        
        int size = prefs.length();
        for (int i = 0; i < size; i++)
        {
            if (prefs.getItemID(i) == itemID)
            {
                return prefs.getValue(i);
            }
        }
        return null;
    }

    public synchronized Long getPreferenceTime(long userID, long itemID) throws TasteException
    {
        if (timestamps == null)
        {
            return null;
        }
        FastByIDMap<Long> itemTimestamps = timestamps.get(userID);
        if (itemTimestamps == null)
        {
            throw new NoSuchUserException(userID);
        }
        return itemTimestamps.get(itemID);
    }

    public synchronized int getNumItems() throws TasteException
    {
        return preferenceForItems.size();
    }

    public synchronized int getNumUsers() throws TasteException
    {
        return preferenceFromUsers.size();
    }

    public synchronized int getNumUsersWithPreferenceFor(long... itemIDs) throws TasteException
    {
        logger.warn("getNumUsersWithPreferenceFor - WARN (Not implemented)");
        return 0;
    }

    public synchronized void setPreference(long userID, long itemID, float value) throws TasteException
    {
        Preference newPref = new GenericPreference(userID, itemID, normalizeScore((int) value));

        // PreferenceFromUsers
        PreferenceArray prefs = preferenceFromUsers.get(userID);
        if (prefs == null)
        {
            PreferenceArray newPrefArray = new GenericUserPreferenceArray(1);
            newPrefArray.set(0, newPref);
            preferenceFromUsers.put(userID, newPrefArray);
        }
        else
        {
            boolean finded = false;
            int i = 0;
            while (i < prefs.length() && !finded)
                if (prefs.getItemID(i) == itemID)
                    finded = true;
                else
                    i++;

            if (finded)
            {
                prefs.set(i, newPref);
            }
            else
            {
                PreferenceArray newPrefArray = new GenericUserPreferenceArray(prefs.length() + 1);
                for (i = 0; i < prefs.length(); i++)
                    newPrefArray.set(i, prefs.get(i));
                newPrefArray.set(i, newPref);
                preferenceFromUsers.put(userID, newPrefArray);
            }
        }

        // Preference From Items
        prefs = preferenceForItems.get(itemID);
        if (prefs == null)
        {
            PreferenceArray newPrefArray = new GenericItemPreferenceArray(1);
            newPrefArray.set(0, newPref);
            preferenceForItems.put(itemID, newPrefArray);
        }
        else
        {
            boolean finded = false;
            int i = 0;
            while (i < prefs.length() && !finded)
                if (prefs.getItemID(i) == itemID)
                    finded = true;
                else
                    i++;

            if (finded)
            {
                prefs.set(i, newPref);
            }
            else
            {
                PreferenceArray newPrefArray = new GenericItemPreferenceArray(prefs.length() + 1);
                for (i = 0; i < prefs.length(); i++)
                    newPrefArray.set(i, prefs.get(i));
                newPrefArray.set(i, newPref);
                preferenceForItems.put(itemID, newPrefArray);
            }
        }
    }

    public synchronized void setPreferenceData(Map<Long, List<UserViewedCount>> preferences)
    {
        if (preferences != null && preferences.size() > 0)
        {

            if (preferenceForItems.size() > 0)
                preferenceForItems = new FastByIDMap<PreferenceArray>();

            if (preferenceFromUsers.size() > 0)
                preferenceFromUsers = new FastByIDMap<PreferenceArray>();

            Map<Long, List<Preference>> prefUsers = new HashMap<Long, List<Preference>>();

            Iterator<Long> it = preferences.keySet().iterator();
            while (it.hasNext())
            {
                Long nextItemId = it.next();
                List<UserViewedCount> prefsForItem = preferences.get(nextItemId);

                if(prefsForItem.size() > 0){
                    PreferenceArray nextArray = new GenericItemPreferenceArray(prefsForItem.size());
                    for (int i = 0; i < prefsForItem.size(); i++)
                    {
                        long userId = prefsForItem.get(i).getUserId();
                        float value = normalizeScore(prefsForItem.get(i).getNumViewed());
                        Preference nextPreference = new GenericPreference(userId, nextItemId, value);

                        nextArray.set(i, nextPreference);
                        List<Preference> upList = prefUsers.get(userId);
                        if (upList == null)
                        {
                            upList = new ArrayList<Preference>();
                            upList.add(nextPreference);
                            prefUsers.put(userId, upList);
                        }
                        else
                            upList.add(nextPreference);

                    }


                    preferenceForItems.put(nextItemId, nextArray);
                }
            }

            Iterator<Entry<Long, List<Preference>>> userIt = prefUsers.entrySet().iterator();
            while (userIt.hasNext())
            {
                Entry<Long, List<Preference>> nextEntry = userIt.next();
                List<Preference> nextList = nextEntry.getValue();
                PreferenceArray nextArray = new GenericUserPreferenceArray(nextList.size());
                for (int i = 0; i < nextList.size(); i++)
                    nextArray.set(i, nextList.get(i));

                preferenceFromUsers.put(nextEntry.getKey(), nextArray);
            }

        }
    }

    public synchronized void removePreference(long userID, long itemID) throws TasteException
    {
        logger.trace("removePreference method has no sense in this Data Model");
    }

    public boolean hasPreferenceValues()
    {
        return !(preferenceFromUsers.size() == 0);
    }

    public static float normalizeScore(int numViewed)
    {
        return numViewed >= MAX_VIEWED ? MAX_SCORE : (MAX_SCORE * numViewed) / MAX_VIEWED;
    }

}
