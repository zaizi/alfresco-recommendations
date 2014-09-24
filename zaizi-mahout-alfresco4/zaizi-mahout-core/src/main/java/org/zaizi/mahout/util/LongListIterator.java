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
package org.zaizi.mahout.util;

import com.google.common.base.Preconditions;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 31/08/2011
 * Time: 09:13
 * To change this template use File | Settings | File Templates.
 */
public class LongListIterator implements LongPrimitiveIterator {


    private final List<Long> results;
    private int position;
    private final int max;

    /**
     * <p>
     * Creates an  over an entire results.
     * </p>
     *
     * @param array results to iterate over
     */
    public LongListIterator(List<Long> array) {
        Preconditions.checkArgument(array != null, "results is null");
        this.results = array; // yeah, not going to copy the results here, for performance
        this.position = 0;
        this.max = array.size();
    }


    public boolean hasNext() {
        return position < max;
    }


    public Long next() {
        return nextLong();
    }


    public long nextLong() {
        if (position >= results.size()) {
            throw new NoSuchElementException();
        }
        return results.get(position++);
    }


    public long peek() {
        if (position >= results.size()) {
            throw new NoSuchElementException();
        }
        return results.get(position);
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void skip(int n) {
        if (n > 0) {
            position += n;
        }
    }

    public String toString() {
        return "LongListIterator";
    }

}

