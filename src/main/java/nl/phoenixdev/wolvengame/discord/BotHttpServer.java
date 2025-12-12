package nl.phoenixdev.wolvengame.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BotHttpServer {
    private final VoiceManager voiceManager;
    private HttpServer server;

    public BotHttpServer(VoiceManager voiceManager) {
        this.voiceManager = voiceManager;
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/game/created", new GameCreatedHandler());
        server.createContext("/game/started", new GameStartedHandler());
        server.createContext("/game/ended", new GameEndedHandler());
        server.createContext("/voice/mute", new MuteHandler());
        server.createContext("/voice/muteall", new MuteAllHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("[WolvenGame Discord Bot] HTTP Server started on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private class GameCreatedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readBody(exchange.getRequestBody());
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                String host = json.get("host").getAsString();
                
                System.out.println("Game created by: " + host);
                voiceManager.autoJoinVoiceChannel();
                
                sendResponse(exchange, 200, "OK");
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    private class GameStartedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readBody(exchange.getRequestBody());
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                JsonArray players = json.getAsJsonArray("players");
                
                System.out.println("Game started with " + players.size() + " players");
                voiceManager.unmuteAllInChannel(); // Ensure everyone can speak at start
                
                sendResponse(exchange, 200, "OK");
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    private class GameEndedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readBody(exchange.getRequestBody());
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                String winner = json.get("winner").getAsString();
                
                System.out.println("Game ended. Winner: " + winner);
                voiceManager.unmuteAllInChannel();
                voiceManager.leaveVoiceChannel();
                
                sendResponse(exchange, 200, "OK");
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    private class MuteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readBody(exchange.getRequestBody());
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                String action = json.get("action").getAsString();
                String player = json.get("player").getAsString();
                
                if ("mute".equals(action)) {
                    voiceManager.mutePlayer(player);
                } else if ("unmute".equals(action)) {
                    voiceManager.unmutePlayer(player);
                }
                
                sendResponse(exchange, 200, "OK");
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    private class MuteAllHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readBody(exchange.getRequestBody());
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                String action = json.get("action").getAsString();
                
                if ("muteall".equals(action)) {
                    voiceManager.muteAllInChannel();
                } else if ("unmuteall".equals(action)) {
                    voiceManager.unmuteAllInChannel();
                }
                
                sendResponse(exchange, 200, "OK");
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    private String readBody(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
