package velho.controller;

import java.util.HashSet;
import java.util.Set;

import velho.model.Shelf;
import velho.model.ShelfLevel;
import velho.model.ShelfSlot;

/**
 * A controller for handling {@link Shelf}, {@link velho.model.ShelfLevel} and {@link velho.model.ShelfSlot} objects.
 *
 * @author Jose Uusitalo
 */
public abstract class ShelfController
{
	/**
	 * Gets a set of contextually invalid {@link Shelf} objects from the specified set of shelves.
	 *
	 * @param validDataSet a set of technically valid shelves
	 * @return a set of invalid shelves
	 */
	public static Set<Shelf> getInvalidShelves(final Set<Shelf> validDataSet)
	{
		final Set<Shelf> invalids = new HashSet<Shelf>();

		for (final Shelf shelf : validDataSet)
		{
			if (shelf.getLevelCount() < 1)
				invalids.add(shelf);
		}

		return invalids;
	}

	/**
	 * Gets a set of contextually invalid {@link ShelfLevel} objects from the specified set of shelf levels.
	 *
	 * @param validDataSet a set of technically valid shelf levels
	 * @return a set of invalid shelf levels
	 */
	public static Set<ShelfLevel> getInvalidShelfLevels(final Set<ShelfLevel> validDataSet)
	{
		final Set<ShelfLevel> invalids = new HashSet<ShelfLevel>();

		for (final ShelfLevel level : validDataSet)
		{
			if (level.getMaxShelfSlots() < 1 || level.getParentShelf() == null || level.getShelfPosition() < 1)
				invalids.add(level);
		}

		return invalids;
	}

	/**
	 * Gets a set of contextually invalid {@link ShelfSlot} objects from the specified set of shelf slots.
	 *
	 * @param validDataSet a set of technically valid shelf slots
	 * @return a set of invalid shelf slots
	 */
	public static Set<ShelfSlot> getInvalidShelfSlots(final Set<ShelfSlot> validDataSet)
	{
		final Set<ShelfSlot> invalids = new HashSet<ShelfSlot>();

		for (final ShelfSlot slot : validDataSet)
		{
			if (slot.getLevelPosition() < 1 || slot.getMaxProductBoxes() < 1)
				invalids.add(slot);
		}

		return invalids;
	}
}
