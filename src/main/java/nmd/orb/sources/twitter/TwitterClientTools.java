package nmd.orb.sources.twitter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nmd.orb.sources.twitter.entities.AccessToken;
import nmd.orb.sources.twitter.entities.Tweet;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static nmd.orb.util.Assert.assertNotNull;
import static nmd.orb.util.Assert.assertStringIsValid;
import static nmd.orb.util.CloseableTools.close;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 28.02.14
 */
public class TwitterClientTools {

    private static final String AUTHENTICATION_URL = "https://api.twitter.com/oauth2/token";
    private static final String TIMELINE_API_URL_TEMPLATE = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=%s&count=%d";

    private static final String UTF_8 = "UTF-8";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String BASIC = "Basic ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8 = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String CREDENTIALS_REQUEST_BODY = "grant_type=client_credentials";
    private static final String TWITTER_COM = "twitter.com";
    private static final String MOBILE_TWITTER_COM = "mobile.twitter.com";
    private static final Pattern TWITTER_USER_NAME_PATTERN = Pattern.compile("https?://(mobile\\.)?twitter.com/(#!/)?([^/]*)", Pattern.CASE_INSENSITIVE);

    private static final Gson GSON = new Gson();

    private static final Type TWEET_LIST_TYPE = new TypeToken<ArrayList<Tweet>>() {
    }.getType();

    public static List<Tweet> fetchTweets(final AccessToken accessToken, final String screenName, final int count) throws IOException {
        final URL url = new URL(format(TIMELINE_API_URL_TEMPLATE, screenName, count));

        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(GET);
        connection.setRequestProperty(AUTHORIZATION, BEARER + accessToken.getAccess_token());

        final String content = readAllFromConnection(connection);

        return GSON.fromJson(content, TWEET_LIST_TYPE);
    }

    public static AccessToken getAccessToken(final String apiKey, String apiSecret) throws IOException {
        assertStringIsValid(apiKey);
        assertStringIsValid(apiSecret);

        OutputStream outputStream = null;

        try {
            final String apiKeyEncoded = URLEncoder.encode(apiKey, UTF_8);
            final String apiSecretEncoded = URLEncoder.encode(apiSecret, UTF_8);
            final String bearerTokenCredential = apiKeyEncoded + ":" + apiSecretEncoded;
            final String bearerTokenCredentialBase64 = Base64.encodeBase64String(bearerTokenCredential.getBytes()).replaceAll("\n", "");

            final URL url = new URL(AUTHENTICATION_URL);

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod(POST);
            connection.setRequestProperty(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED_CHARSET_UTF_8);
            connection.setRequestProperty(AUTHORIZATION, BASIC + bearerTokenCredentialBase64);

            connection.setRequestProperty("Content-Length", String.valueOf(CREDENTIALS_REQUEST_BODY.length()));

            outputStream = connection.getOutputStream();
            outputStream.write(CREDENTIALS_REQUEST_BODY.getBytes(UTF_8));

            final String content = readAllFromConnection(connection);

            return GSON.fromJson(content, AccessToken.class);
        } finally {
            close(outputStream);
        }
    }

    public static String readAllFromConnection(final HttpURLConnection connection) throws IOException {
        assertNotNull(connection);

        final InputStream inputStream = connection.getInputStream();

        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            inputStreamReader = new InputStreamReader(inputStream, UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);

            final StringBuilder result = new StringBuilder();

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } finally {
            close(bufferedReader);
            close(inputStreamReader);
            close(inputStream);
        }
    }

    public static boolean isItTwitterUrl(final String url) {

        try {

            if (url == null || url.isEmpty()) {
                return false;
            }

            final URI uri = new URI(url);
            final String host = uri.getHost();

            return TWITTER_COM.equalsIgnoreCase(host) || MOBILE_TWITTER_COM.equalsIgnoreCase(host);
        } catch (URISyntaxException exception) {
            return false;
        }
    }

    public static String getTwitterUserName(final String url) {

        if (!isItTwitterUrl(url)) {
            return null;
        }

        final Matcher matcher = TWITTER_USER_NAME_PATTERN.matcher(url);

        return matcher.find() ? matcher.group(3) : null;
    }

    private TwitterClientTools() {
        // empty
    }

}
