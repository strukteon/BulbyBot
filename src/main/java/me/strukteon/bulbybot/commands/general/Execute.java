package me.strukteon.bulbybot.commands.general;
/*
    Created by nils on 29.08.2018 at 02:00.
    
    (c) nils 2018
*/

import me.strukteon.bettercommand.CommandEvent;
import me.strukteon.bettercommand.command.CommandInfo;
import me.strukteon.bettercommand.command.ExtendedCommand;
import me.strukteon.bettercommand.syntax.Syntax;
import me.strukteon.bettercommand.syntax.SyntaxBuilder;
import me.strukteon.bettercommand.syntax.SyntaxElementType;
import me.strukteon.bulbybot.utils.ChatTools;
import me.strukteon.bulbybot.utils.Settings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import okhttp3.*;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Execute implements ExtendedCommand {
    private static String langs = "ada, bash, bc, brainfuck, c99, c, clisp, clojure, cobol, coffeescript, cpp14, cpp, csharp, d, dart, elixir, factor, falcon, fantom, forth, fortran, freebasic, fsharp, gccasm, go, groovy, hack, haskell, icon, intercal, java, kotlin, lolcode, lua, mozart, nasm, nemerle, nim, nodejs, objc, ocaml, octave, pascal, perl, php, picolisp, pike, prolog, python2, python3, r, racket, rhino, ruby, rust, scala, scheme, smalltalk, spidermonkey, sql, swift, tcl, unlambda, vbn, verilog, whitespace, yabasic";
    private static List<String> languages = Arrays.asList(langs.split(", "));
    private static String[] versionCodes = "2 2 1 0 2 3 2 1 1 2 2 3 2 0 2 2 2 0 0 0 2 0 0 1 2 2 0 2 0 0 2 1 0 1 0 2 0 2 2 2 0 2 2 2 2 2 0 0 1 2 2 1 0 2 2 2 1 0 1 2 2 2 0 2 1 0 0".split(" ");
    private static URL API_URL;

    static {
        try {
            API_URL = new URL("https://api.jdoodle.com/v1/execute");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExecute(CommandEvent event, Syntax syntax, User author, MessageChannel channel) throws Exception {
        EmbedBuilder response = ChatTools.INFO(author);
        if (syntax.getExecutedBuilder() == 1){
            response.setDescription("Execute some code through the JDoodle API.\n\nAvailable Languages:\n ```" + langs + "```");
        } else {
            int codeIndex = languages.indexOf(syntax.getAsString("language"));
            if (codeIndex > -1){
                String code = syntax.getAsJoinedListString("code");
                String realCode = code;
                if (code.matches("`{3}.*\\s.+\\s`{3}"))
                    realCode = code.substring(0, code.length()-4).replaceFirst("`{3}.*\\s(?=.+)", "");
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                        new JSONObject()
                                .put("clientId", Settings.INSTANCE.jDoodleClientId)
                                .put("clientSecret", Settings.INSTANCE.jDoodleClientSecret)
                                .put("script", realCode)
                                .put("language", syntax.getAsString("language"))
                                .put("versionIndex", versionCodes[codeIndex]).toString());
                ResponseBody responseBody = client.newCall(new Request.Builder().post(requestBody).url(API_URL).build()).execute().body();
                JSONObject res = new JSONObject(responseBody.string());
                if (res.getInt("statusCode") == 200)
                    response.addField("Additional info:", String.format("CPU time: **%ss**\nMemory usage: **%sB**", res.get("cpuTime"), res.get("memory")), false)
                            .addField("Output:", "```" + res.getString("output") + "```", false);
                else
                    response.addField("Status code:", res.getInt("statusCode") + "", false)
                            .addField("Output:", "```" + res.getString("output") + "```", false);
                response.setFooter("Powered by JDoodle", author.getEffectiveAvatarUrl());
            } else {
                response.setDescription("This language is not available. Please choose one of this list:\n```" + langs + "```");
            }
        }
        channel.sendMessage(response.build()).queue();
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("exec")
                .setCooldown(30000)
                .setSyntaxBuilder(new SyntaxBuilder(
                        new SyntaxBuilder()
                            .addElement("language", SyntaxElementType.STRING)
                            .addElement("code", SyntaxElementType.STRING, true),
                        new SyntaxBuilder()
                ));
    }
}
