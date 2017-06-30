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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.gson.JsonObject;

import server_outer_part.Person_splitter;

public class ItemSerializer {

    protected ItemSerializer() {}

    public static JsonObject serializeInventoryItem(ItemStack item, int index) {
        return serializeItem(item, true, index);
    }

    public static JsonObject serializeItem(ItemStack item, boolean useIndex, int index) {
        JsonObject values = new JsonObject();
        if (item == null)
            return null;

        /*
         * Check to see if the item is a skull with a null owner.
         * This is because some people are getting skulls with null owners, which causes Spigot to throw an error
         * when it tries to serialize the item. If this ever gets fixed in Spigot, this will be removed.
         */
        if (item.getType() == Material.SKULL_ITEM) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta.hasOwner() && (meta.getOwner() == null || meta.getOwner().isEmpty())) {
                item.setItemMeta(Person_splitter.server.getItemFactory().getItemMeta(Material.SKULL_ITEM));
            }
        }

        if (useIndex)
            values.addProperty("index", index);

        ByteArrayOutputStream outputStream;
        BukkitObjectOutputStream dataObject;
        try {
            outputStream = new ByteArrayOutputStream();
            dataObject = new BukkitObjectOutputStream(outputStream);
            dataObject.writeObject(item);
            dataObject.close();

            values.addProperty("item", Base64Coder.encodeLines(outputStream.toByteArray()));
        } catch (IOException ex) {
        	Person_splitter.logger.severe("Error saving an item:");
        	Person_splitter.logger.severe("Item: " + item.getType().toString());
        	Person_splitter.logger.severe("Reason: " + ex.getMessage());
            return null;
        }

        return values;
    }

    public static ItemStack deserializeItem(JsonObject data) {
        try (
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data.get("item").getAsString()));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack) dataInput.readObject();
        } catch (IOException | ClassNotFoundException ex) {
        	Person_splitter.logger.severe("Error loading an item:" + ex.getMessage());
            return new ItemStack(Material.AIR);
        }
    }

    
}