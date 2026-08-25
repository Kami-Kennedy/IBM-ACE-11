package com.ubagroup.sochitel;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OperatorSync extends MbJavaComputeNode {

    // Adjust this to a directory the broker's runtime user can actually write to
    private static final String LOG_FILE = "/var/mqsi/system/SOCHITEL/OperatorSync_debug.log";

    @Override
    public void evaluate(MbMessageAssembly inAssembly) throws MbException {

        MbOutputTerminal out = getOutputTerminal("out");

        try {
            // ── 1. Read request details from Environment ──
            MbElement envRoot = inAssembly.getGlobalEnvironment().getRootElement();
            MbElement syncEnv = envRoot.getFirstElementByPath("Variable/OperatorSync");

            if (syncEnv == null) {
                logToFile("ERROR: Variables/OperatorSync not found in Environment. "
                        + "Compute5 may not have run, or Environment wasn't propagated correctly.", null);
                return; // nothing to propagate; stop here rather than NPE downstream
            }

            MbElement bodyElem = syncEnv.getFirstElementByPath("RequestBody");
            MbElement urlElem  = syncEnv.getFirstElementByPath("RequestURL");

            if (bodyElem == null || urlElem == null) {
                logToFile("ERROR: RequestBody or RequestURL missing under Variables/OperatorSync.", null);
                return;
            }

            String requestBody = (String) bodyElem.getValue();
            String requestURL  = (String) urlElem.getValue();

            logToFile("INFO: Attempting POST to " + requestURL + " | body length=" +
                    (requestBody != null ? requestBody.length() : 0), null);

            // ── 2. Make HTTP POST ──
            String responseBody = httpPost(requestURL, requestBody);

            logToFile("INFO: Received response, length=" +
                    (responseBody != null ? responseBody.length() : 0), null);

            // ── 3. Create new outgoing message ──
            MbMessage outMsg = new MbMessage(inAssembly.getMessage());
            MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMsg);

            // ── 4. Set response into Environment for ESQL to read ──
            MbElement outEnvRoot = outAssembly.getGlobalEnvironment().getRootElement();

            MbElement varsElem = outEnvRoot.getFirstElementByPath("Variables");
            if (varsElem == null) {
                varsElem = outEnvRoot.createElementAsLastChild(
                        MbElement.TYPE_NAME, "Variables", null);
            }

            MbElement oldResp = varsElem.getFirstElementByPath("OperatorSyncResponse");
            if (oldResp != null) {
                oldResp.delete();
            }

            MbElement syncResp = varsElem.createElementAsLastChild(
                    MbElement.TYPE_NAME, "OperatorSyncResponse", null);
            syncResp.createElementAsLastChild(
                    MbElement.TYPE_NAME, "body", responseBody);

            // ── 5. Replace message body with raw JSON response ──
            MbElement msgRoot = outMsg.getRootElement();

            MbElement existingJson = msgRoot.getFirstElementByPath("JSON");
            if (existingJson != null) {
                existingJson.delete();
            }

            MbElement jsonRoot = msgRoot.createElementAsLastChild(MbJSON.PARSER_NAME);
            MbElement dataElem = jsonRoot.createElementAsLastChild(
                    MbElement.TYPE_NAME, "Data", null);
            dataElem.createElementAsLastChild(
                    MbElement.TYPE_NAME, "rawResponse", responseBody);

            out.propagate(outAssembly);

        } catch (Exception e) {
            // ── Log full detail to file instead of routing to failure terminal ──
            logToFile("ERROR: OperatorSync failed", e);
            // No propagation on failure — this run is simply skipped.
            // The next Timeout Notification firing will retry.
        }
    }

    private String httpPost(String targetURL, String body) throws Exception {
        URL url = new URL(targetURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }

        if (status < 200 || status >= 300) {
            throw new Exception("HTTP Error " + status + ": " + response);
        }

        return response.toString();
    }

    /**
     * Appends a timestamped entry (and full stack trace, if a throwable is given)
     * to LOG_FILE. Swallows any logging failure itself so we never mask the
     * original error with a new one.
     */
    private void logToFile(String message, Throwable t) {
        try {
            Path path = Paths.get(LOG_FILE);
            Files.createDirectories(path.getParent());

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

            StringBuilder sb = new StringBuilder();
            sb.append("[").append(timestamp).append("] ").append(message).append(System.lineSeparator());

            if (t != null) {
                sb.append("Exception: ").append(t.toString()).append(System.lineSeparator());
                if (t.getCause() != null) {
                    sb.append("Cause: ").append(t.getCause().toString()).append(System.lineSeparator());
                }
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                sb.append(sw.toString());
            }
            sb.append("----------------------------------------").append(System.lineSeparator());

            Files.write(path, sb.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException logFailure) {
            // Last resort: at least surface it in the broker's own log
            logFailure.printStackTrace();
        }
    }
}