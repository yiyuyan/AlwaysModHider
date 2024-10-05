package cn.ksmcbrigade.amh.client;

import cn.ksmcbrigade.amh.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PreMod implements PreLaunchEntrypoint {

    public static final File config = new File("config/amh/amh-config.json");

    public static final File uninstall = new File("config/amh/amh-black-config.json");

    public static final File laterHide = new File("config/amh/amh-later-config.json");

    public static ArrayList<EntrypointContainer<ClientModInitializer>> hiddenClient = new ArrayList<>();
    public static ArrayList<EntrypointContainer<ModInitializer>> hiddenMain = new ArrayList<>();

    @Override
    public void onPreLaunch() {
        //hide self
        try {
            Utils.uninstall("amh");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("Can't hide self.");
        }

        //config
        new File("config").mkdirs();
        new File("config/amh").mkdirs();

        //common hide
        if(!config.exists()){
            try {
                Files.writeString(config.toPath(),"[\"meteor-client\",\"cheatutils\"]");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //uninstall
        if(!uninstall.exists()){
            try {
                Files.writeString(uninstall.toPath(),"[\"cheatdetector\"]");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //later hide
        if(!laterHide.exists()){
            try {
                Files.writeString(laterHide.toPath(),"[\"wurst\"]");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //hide
        try {
            JsonArray array = JsonParser.parseString(Files.readString(config.toPath())).getAsJsonArray();
            for(JsonElement Key:array){
                String key = Key.getAsString();
                EntrypointContainer<ClientModInitializer> client = Utils.getEntrypoint(key.toLowerCase());
                EntrypointContainer<ModInitializer> main = Utils.getEntrypointMain(key.toLowerCase());
                if(client!=null){
                    hiddenClient.add(client);
                }
                if(main!=null){
                    hiddenMain.add(main);
                }
                boolean h = Utils.uninstall(key.toLowerCase());
                if(h){
                    System.out.println("Hided a mod: "+key.toLowerCase());
                }
                else{
                    System.out.println("Can't hide the mod: "+key.toLowerCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //uninstall
        try {
            JsonArray array = JsonParser.parseString(Files.readString(uninstall.toPath())).getAsJsonArray();
            for(JsonElement Key:array){
                String key = Key.getAsString();
                boolean h = Utils.uninstall(key.toLowerCase());
                if(h){
                    System.out.println("Uninstalled a mod: "+key.toLowerCase());
                }
                else{
                    System.out.println("Can't uninstall the mod: "+key.toLowerCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //main
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("hideMod").then(argument("id", StringArgumentType.string()).executes(context -> {
            try {
                int r = 0;
                boolean ret = Utils.uninstall(StringArgumentType.getString(context,"id").toLowerCase());
                if(!ret){
                    r = 1;
                    context.getSource().sendMessage(Text.of("Can't hide the mod."));
                }
                else{
                    context.getSource().sendMessage(Text.of("Done."));
                }
                return r;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                context.getSource().sendMessage(Text.of("ERROR:" + e.getMessage()));
                e.printStackTrace();
                return 1;
            }
        }))));
    }
}
