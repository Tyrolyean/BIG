package server_outer_part;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import serializers.ItemSerializer;



public class inventory {

	public static void save_Inventory(Player player){
		Inventory inventory=player.getInventory();

		
		
		if(Person_splitter.debug){
			Person_splitter.logger.log(Level.INFO, "Saving Inventory from "+player.getDisplayName()+" as ");
		}
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
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(ItemStack item:inventory.getContents()){
				try{
					if(ItemSerializer.serializeItem(item, false, 0)==null){
						writer.write("");
					}else{
						Gson gson =new Gson();
						writer.write(gson.toJson(ItemSerializer.serializeItem(item, false, 0)));

					}
				writer.newLine();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
			writer.flush();
			writer.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void load_Inventory(Player player){
		Inventory inventory=player.getInventory();
		ItemStack[] contents=new ItemStack[inventory.getContents().length];
		inventory.clear();
		File file = new File(System.getProperty("user.dir") + "/plugins/BIG/inventorys/"
				+ player.getLocation().getWorld().getName() + "/"
				+ player.getUniqueId().toString());
		try {
			JsonParser parser=new JsonParser();
			BufferedReader reader =new BufferedReader(new FileReader(file));
			for(int i=0;i<contents.length;i++){
				try{
				String line=reader.readLine();
				if(!line.equalsIgnoreCase(null)){
		            contents[i]=ItemSerializer.deserializeItem(parser.parse(line).getAsJsonObject());
				}
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		inventory.setContents(contents);
	}
	
	
}
