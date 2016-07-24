package server_outer_part;

import java.io.BufferedReader;
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


public class inventory {

	public static void save_Inventory(Player player){
		Inventory inventory=player.getInventory();
		Gson gson =new Gson();

		
		
		if(Person_splitter.debug){
			Person_splitter.logger.log(Level.INFO, "Saving Inventory from "+player.getDisplayName()+" as "+gson.toJson(inventory.getContents()));
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
			FileWriter writer = new FileWriter(file);
			gson.toJson(inventory, writer);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void load_Inventory(Player player){
		String inventory_string=new String();//Initializing for the win!
		Inventory inventory=player.getInventory();
		Gson gson =new Gson();
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
		ItemStack[] content=	gson.fromJson(inventory_string, ItemStack[].class);	
		
		inventory.setContents(content);
	}
	
	
}
