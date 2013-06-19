/*********************************************************************
 *
 *      Copyright (C) 2002 Andrew Khan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 ***************************************************************************/

package de.enerko.reports2.engine;


/**
 * A helper to transform between excel cell references and
 * sheet:column:row notation
 * Because this function will be called when generating a string
 * representation of a formula, the cell reference will merely
 * be appened to the string buffer instead of returning a full
 * blooded string, for performance reasons
 */
public final class CellReferenceHelper
{
	/**
	 * The character which indicates whether a reference is fixed
	 */
	private static final char fixedInd='$';

	/**
	 * The character which indicates the sheet name terminator
	 */
	private static final char sheetInd = '!';

	/**
	 * Gets the columnn number of the string cell reference
	 *
	 * @param s the string to parse
	 * @return the column portion of the cell reference
	 */
	public static int getColumn(String s)
	{
		int colnum = 0;
		int numindex = getNumberIndex(s);

		String s2 = s.toUpperCase();

		int startPos = s.lastIndexOf(sheetInd) + 1;
		if (s.charAt(startPos) == fixedInd)
		{
			startPos++;
		}

		int endPos = numindex;
		if (s.charAt(numindex - 1) == fixedInd)
		{
			endPos--;
		}

		for (int i = startPos; i < endPos ; i++)
		{

			if (i != startPos)
			{
				colnum = (colnum+1) * 26;
			}
			colnum += (int) s2.charAt(i) - (int) 'A';
		}

		return colnum;
	}

	/**
	 * Gets the row number of the cell reference
	 */
	public static int getRow(String s)
	{
		try
		{
			return (Integer.parseInt(s.substring(getNumberIndex(s))) - 1);
		}
		catch (NumberFormatException e)
		{      
			return 0xffff;
		}
	}

	/**
	 * Finds the position where the first number occurs in the string
	 */
	private static int getNumberIndex(String s)
	{
		// Find the position of the first number
		boolean numberFound = false;
		int pos = s.lastIndexOf(sheetInd) + 1;
		char c = '\0';

		while (!numberFound && pos < s.length() )
		{
			c = s.charAt(pos);

			if (c >= '0' && c <= '9')
			{
				numberFound = true;
			}
			else
			{
				pos++;
			}
		}

		return pos;
	}
}