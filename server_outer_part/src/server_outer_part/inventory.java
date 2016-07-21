package server_outer_part;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import serializers.InventorySerializer;

public class inventory {

	public static void save_Inventory(Player player){
		Inventory inventory=player.getInventory();
		inventory.clear();
		JsonArray raw_json ;
		
		raw_json=InventorySerializer.serializeInventory(player.getInventory().getContents());
		
		String save_Inventory=raw_json.getAsString();
		if (Person_splitter.debug) {
			System.out.println("Setting file Inventory for Player " + player.getUniqueId().toString());
		}
		File file = new File(System.getProperty("user.dir") + "/plugins/BIG/inventorys/"
				+ player.getLocation().getWorld().getName() + "/"
				+ player.getUniqueId().toString());
		file.mkdirs();
		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(save_Inventory);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void load_Inventory(Player player){
		String inventory_string=new String();//Initializing for the win!
		Inventory inventory=player.getInventory();
		inventory.clear();
		File file = new File(System.getProperty("user.dir") + "/plugins/BIG/inventorys/"
				+ player.getLocation().getWorld().getName() + "/"
				+ player.getUniqueId().toString());
		try {
			BufferedReader reader =new BufferedReader(new FileReader(file));
			inventory_string=reader.readLine();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonArray json =new JsonParser().parse(inventory_string).getAsJsonArray();
		
		ItemStack[] content=InventorySerializer.deserializeInventory(json, 88); //I wasn't able to rewrite the Serializer so I did what the original Programmer said and 
		
		inventory.setContents(content);
	}
	
	
}
