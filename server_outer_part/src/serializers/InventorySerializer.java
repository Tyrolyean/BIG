/*
 * Copyright (C) 2014-2016  EbonJaguar
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package serializers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class InventorySerializer {

    protected InventorySerializer() {}

    /**
     * Serialize a PlayerInventory. This will save the armor contents of the inventory as well
     *
     * @param player The player to serialize
     * @return A JsonObject representing the serialized Inventory.
     */


    /**
     * Serialize an ItemStack array.
     *
     * @param contents The items in the inventory
     * @return A JsonArray representing the serialized ItemStack array
     */
    public static JsonArray serializeInventory(ItemStack[] contents) {
        JsonArray inventory = new JsonArray();

        for (int i = 0; i < contents.length; i++) {
            JsonObject values = ItemSerializer.serializeInventoryItem(contents[i], i);
            if (values != null)
                inventory.add(values);
        }

        return inventory;
    }

    /**
     * Sets the Inventory using an ItemStack array constructed from a JsonObject.
     *
     * @param player The InventoryHolder to which the Inventory will be set
     * @param inv    The reference JsonArray
     * @param format Data format being used; 0 is old, 1 is new
     */
    public static void setInventory(Player player, JsonObject inv, int format) {
        PlayerInventory inventory = player.getInventory();
        
        ItemStack[] armor = deserializeInventory(inv.getAsJsonArray("armor"), 4);
        ItemStack[] inventoryContents = deserializeInventory(inv.getAsJsonArray("inventory"), inventory.getSize());

        inventory.clear();
        if (armor != null) {
        	inventory.setArmorContents(armor);
        }
        
        if (inventoryContents != null) {
        	inventory.setContents(inventoryContents);
        }
       
    }


	/**
     * Gets an ItemStack array from a JsonObject.
     *
     * @param inv  The JsonObject to get from
     * @param size The expected size of the inventory, can be greater than expected
     * @return An ItemStack array constructed from the given JsonArray
     */
    public static ItemStack[] deserializeInventory(JsonArray inv, int size) {
    	// Be tolerant if the expected JsonArray tag is missing
    	if (inv == null) {
    		return null;
    	}
    	
        ItemStack[] contents = new ItemStack[size];
        for (int i = 0; i < inv.size(); i++) {
        	// We don't want to risk failing to deserialize a players inventory. Try your best
        	// to deserialize as much as possible.
        	try {
	            JsonObject item = inv.get(i).getAsJsonObject();
	            int index = item.get("index").getAsInt();
	            
	            ItemStack is;
	                is = ItemSerializer.deserializeItem(item);

	
	            contents[index] = is;
        	}
        	catch (Exception e) {
        		System.out.println(e);
        	}
        }

        return contents;
    }
}