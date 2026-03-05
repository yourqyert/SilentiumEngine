package net.silentium.engine.api.skin.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.silentium.engine.api.skin.Skin;
import net.silentium.engine.api.skin.SkinType;
import net.silentium.engine.api.skin.exeptions.SkinRequestException;
import net.silentium.engine.api.skin.response.SkinProperty;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class MineskinSkinService extends SkinService {

    private static final String MINESKIN_GENERATE_URL = "https://api.mineskin.org/generate/url";

    public MineskinSkinService() {
        super(null, null);
    }

    @Override
    public SkinType getSkinType() {
        return SkinType.CUSTOM;
    }

    @Override
    public Skin getSkinByName(String imageUrl) throws SkinRequestException {
        try {
            HttpURLConnection connection = getHttpURLConnection(imageUrl);

            Scanner scanner = new Scanner(connection.getInputStream()).useDelimiter("\\A");
            String responseJson = scanner.hasNext() ? scanner.next() : "";

            JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();
            JsonObject data = response.getAsJsonObject("data");
            JsonObject texture = data.getAsJsonObject("texture");

            String value = texture.get("value").getAsString();
            String signature = texture.get("signature").getAsString();

            SkinProperty property = new SkinProperty("textures", value, signature);
            return property.toSkin("Mineskin", getSkinType());

        } catch (Exception e) {
            throw new SkinRequestException("Не удалось загрузить скин через Mineskin API", e);
        }
    }

    private static @NotNull HttpURLConnection getHttpURLConnection(String imageUrl) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("url", imageUrl);

        HttpURLConnection connection = (HttpURLConnection) new URL(MINESKIN_GENERATE_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "SilentiumEngine");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }
}
