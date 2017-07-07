package ontologizer.association;

import java.util.ArrayList;
import java.util.List;

import ontologizer.types.ByteString;
import sonumina.collections.ObjectIntHashMap;

/**
 * Contains methods to build up an annotation map in an incremental manner.
 *
 * @author Sebastian Bauer
 */
public class AnnotationMapBuilder
{
	/** Unique list of items added so far */
	private List<ByteString> items = new ArrayList<ByteString>();

	/** And the corresponding object ids */
	private List<ByteString> objectIds = new ArrayList<ByteString>();

	/** Maps object symbols to item indices within the items list */
	private ObjectIntHashMap<ByteString> objectSymbolMap = new ObjectIntHashMap<ByteString>();

	/** Maps object ids to item indices within the items list */
	private ObjectIntHashMap<ByteString> objectIdMap = new ObjectIntHashMap<ByteString>();

	/** Maps synonyms to item indices within the items list */
	private ObjectIntHashMap<ByteString> synonymMap = new ObjectIntHashMap<ByteString>();

	private int dbObjectWarnings;
	private int symbolWarnings;

	public static interface WarningCallback
	{
		public void warning(String warning);
	}

	private WarningCallback warningCallback;

	public AnnotationMapBuilder()
	{
		this(null);
	}

	public AnnotationMapBuilder(WarningCallback warningCallback)
	{
		this.warningCallback = warningCallback;
	}

	/**
	 * Add a new association to the map.
	 *
	 * @param assoc the association to add. Synonyms are ignored.
	 * @param synonyms array of synonymes.
	 * @return this for composing further calls.
	 */
	public AnnotationMapBuilder add(Association assoc, ByteString [] synonyms, int lineno)
	{
		ByteString objectSymbol = assoc.getObjectSymbol();

		/* New code */
		int potentialObjectIndex = items.size();
		int objectIndex = objectSymbolMap.getIfAbsentPut(objectSymbol, potentialObjectIndex);
		if (objectIndex == potentialObjectIndex)
		{
			/* Object symbol was not seen before */
			items.add(objectSymbol);
			objectIds.add(assoc.getDB_Object());
		} else
		{
			/* Object symbol was seen before */
			if (!assoc.getDB_Object().equals(objectIds.get(objectIndex)))
			{
				/* Record this as a synonym now */
				synonymMap.put(assoc.getDB_Object(), objectIndex);

				/* Warn about that the same symbol is used with at least two object ids */
				dbObjectWarnings++;
				if (dbObjectWarnings < 1000 && warningCallback != null)
				{
					warningCallback.warning("Line " + lineno + ": Expected that symbol \"" + assoc.getObjectSymbol() + "\" maps to \"" + objectIds.get(objectIndex) + "\" but it maps to \"" + assoc.getDB_Object() + "\"");
				}
			}
		}

		/* Get how the object id is mapped to our id space */
		int objectIdIndex = objectIdMap.getIfAbsentPut(assoc.getDB_Object(), objectIndex);
		if (objectIdIndex != objectIndex)
		{
			/* The same object id is is used for two object symbols, warn about it */
			symbolWarnings++;
			if (symbolWarnings < 1000)
			{
				warningCallback.warning("Line " + lineno + ": Expected that dbObject \"" + assoc.getDB_Object() + "\" maps to symbol \"" + items.get(objectIdIndex) + "\" but it maps to \"" + assoc.getObjectSymbol() + "\"");
			}
		}

		if (synonyms != null)
		{
			for (ByteString synonym : synonyms)
				synonymMap.put(synonym, objectIndex);
		}
		return this;
	}

	public int getNumberOfSymbolWarnings()
	{
		return symbolWarnings;
	}

	public int getNumberOfDbObjectWarnings()
	{
		return dbObjectWarnings;
	}

	/**
	 * @return the annotation map.
	 */
	public AnnotationContext build()
	{
		return new AnnotationContext(items, objectIds, objectSymbolMap, objectIdMap, synonymMap);
	}
}
