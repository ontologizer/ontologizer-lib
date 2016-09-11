package ontologizer.association;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import ontologizer.types.ByteString;
import sonumina.collections.ObjectIntHashMap;
import sonumina.collections.ObjectIntHashMap.ObjectIntProcedure;

public class AnnotationContext
{
	/** The symbols */
	private ByteString[] symbols;

	/** And the corresponding object ids */
	private ByteString[] objectIds;

	/** Maps object symbols to item indices within the items list */
	private ObjectIntHashMap<ByteString> objectSymbolMap;

	/** Maps object ids to item indices within the items list */
	private ObjectIntHashMap<ByteString> objectIdMap;

	/** Maps synonyms to item indices within the items list */
	private ObjectIntHashMap<ByteString> synonymMap;

	public AnnotationContext(List<ByteString> symbols, List<ByteString> objectIds, ObjectIntHashMap<ByteString> objectSymbolMap, ObjectIntHashMap<ByteString> objectIdMap, ObjectIntHashMap<ByteString> synonymMap)
	{
		if (symbols.size() != objectIds.size()) throw new IllegalArgumentException("Symbols and object ids size must match");

		this.symbols = new ByteString[symbols.size()];
		symbols.toArray(this.symbols);

		this.objectIds = new ByteString[objectIds.size()];
		objectIds.toArray(this.objectIds);

		this.objectSymbolMap = objectSymbolMap;
		this.objectIdMap = objectIdMap;
		this.synonymMap = synonymMap;
	}

	/**
	 * Creates a mapping from a list and two other maps.
	 *
	 * @param symbols
	 * @param synonym2Item
	 * @param objectId2Item
	 */
	public AnnotationContext(List<ByteString> symbols, HashMap<ByteString, ByteString> synonym2Item, HashMap<ByteString, ByteString> objectId2Item)
	{
		objectSymbolMap = new ObjectIntHashMap<ByteString>();
		Set<ByteString> allSymbols = new HashSet<ByteString>(symbols);

		if (synonym2Item != null)
		{
			for (ByteString otherSymbol : synonym2Item.values())
				allSymbols.add(otherSymbol);
		}

		if (objectId2Item != null)
		{
			for (ByteString otherSymbol : objectId2Item.values())
				allSymbols.add(otherSymbol);
		}

		this.symbols = new ByteString[allSymbols.size()];
		this.objectIds = new ByteString[allSymbols.size()];
		this.synonymMap = new ObjectIntHashMap<ByteString>(synonym2Item.size());
		this.objectIdMap = new ObjectIntHashMap<ByteString>(objectId2Item.size());
		int i = 0;
		for (ByteString symbol : allSymbols)
		{
			objectSymbolMap.put(symbol, i);
			this.symbols[i] = symbol;
			i++;
		}

		if (synonym2Item != null)
		{
			for (Entry<ByteString, ByteString> e : synonym2Item.entrySet())
			{
				ByteString synonym = e.getKey();
				ByteString symbol = e.getValue();

				synonymMap.put(synonym, objectSymbolMap.get(symbol));
			}
		}

		if (objectId2Item != null)
		{
			for (Entry<ByteString, ByteString> e : objectId2Item.entrySet())
			{
				ByteString objectId = e.getKey();
				ByteString symbol = e.getValue();

				objectIdMap.put(objectId, objectSymbolMap.get(symbol));
			}
		}
	}

	/**
	 * Construct and return the mapping from synonyms to symbols.
	 *
	 * @return the constructed map.
	 */
	public HashMap<ByteString, ByteString> getSynonym2Symbol()
	{
		final HashMap<ByteString, ByteString> synonym2symbol = new HashMap<ByteString, ByteString>(synonymMap.size());
		synonymMap.forEachKeyValue(new ObjectIntProcedure<ByteString>()
		{
			@Override
			public void keyValue(ByteString key, int value)
			{
				synonym2symbol.put(key, symbols[value]);
			}
		});
		return synonym2symbol;
	}

	/**
	 * Construct and return the mapping from object ids to symbols.
	 *
	 * @return the constructed map.
	 */
	public HashMap<ByteString, ByteString> getDbObjectID2Symbol()
	{
		final HashMap<ByteString, ByteString> dbObjectID2gene = new HashMap<ByteString, ByteString>(synonymMap.size());
		objectIdMap.forEachKeyValue(new ObjectIntProcedure<ByteString>()
		{
			@Override
			public void keyValue(ByteString key, int value)
			{
				dbObjectID2gene.put(key, symbols[value]);
			}
		});
		return dbObjectID2gene;
	}

}
