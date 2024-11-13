import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
// this is just a test to confirm if git pull works
// this is another test to confirm wheather the commit works in real time
// this is another comment to check whather I can edit and upload changes
public class FootballPredictorGUI extends JFrame {
    private JTextField team1Field, team2Field, locationField;
    private JButton predictButton, resetButton;
    private JLabel vsLabel, resultLabel;
    private DeviceStatusGUI deviceStatusGUI;

    public FootballPredictorGUI() {
        setTitle("Medusa 'V_1.6'");
        setSize(550, 240);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        deviceStatusGUI = new DeviceStatusGUI(); // Initialize DeviceStatusGUI

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        team1Field = new JTextField(15);
        team2Field = new JTextField(15);
        locationField = new JTextField(15);
        vsLabel = new JLabel("vs");

        inputPanel.add(new JLabel("Team 1:"));
        inputPanel.add(team1Field);
        inputPanel.add(vsLabel);
        inputPanel.add(new JLabel("Team 2:"));
        inputPanel.add(team2Field);
        inputPanel.add(new JLabel("Location:"));
        inputPanel.add(locationField);

        predictButton = new JButton("Predict");
        resetButton = new JButton("Reset");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(predictButton);
        buttonPanel.add(resetButton);

        resultLabel = new JLabel("Prediction will appear here", SwingConstants.CENTER);
        resultLabel.setPreferredSize(new Dimension(350, 50));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resultLabel, BorderLayout.NORTH); // Add resultLabel at the top
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(deviceStatusGUI, BorderLayout.NORTH); // Add DeviceStatusGUI at the top
        add(inputPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        predictButton.addActionListener(e -> predictMatch());

        resetButton.addActionListener(e -> resetFields());
    }

    private void predictMatch() {
        String team1 = team1Field.getText();
        String team2 = team2Field.getText();
        String location = locationField.getText();

        try {
            // Collect predictions from multiple APIs
            JsonObject prediction1 = getPredictionFromAPI1(team1, team2, location);
            JsonObject prediction2 = getPredictionFromAPI2(team1, team2, location);
            JsonObject prediction3 = getPredictionFromAPI3(team1, team2, location);
            JsonObject prediction4 = getPredictionFromAPI4(team1, team2, location);

            // Aggregate the predictions
            String finalPrediction = aggregatePredictions(prediction1, prediction2, prediction3, prediction4);

            // Display the final prediction
            resultLabel.setText(finalPrediction);
        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Error retrieving predictions");
        }
    }

    private JsonObject getPredictionFromAPI1(String team1, String team2, String location) throws Exception {
        String url = "https://api-football-v1.p.rapidapi.com/v2/predictions?team1=" + team1 + "&team2=" + team2 + "&location=" + location;
        return sendRequest(url, "YOUR_API_KEY_1");
    }

    private JsonObject getPredictionFromAPI2(String team1, String team2, String location) throws Exception {
        String url = "https://football-api.com/api/predictions?team1=" + team1 + "&team2=" + team2 + "&location=" + location;
        return sendRequest(url, "YOUR_API_KEY_2");
    }

    private JsonObject getPredictionFromAPI3(String team1, String team2, String location) throws Exception {
        String url = "https://footballdata.org/api/predictions?team1=" + team1 + "&team2=" + team2 + "&location=" + location;
        return sendRequest(url, "YOUR_API_KEY_3");
    }

    private JsonObject getPredictionFromAPI4(String team1, String team2, String location) throws Exception {
        String url = "https://api.sportmonks.com/v2.0/predictions?team1=" + team1 + "&team2=" + team2 + "&location=" + location;
        return sendRequest(url, "YOUR_API_KEY_4");
    }

    private JsonObject sendRequest(String url, String apiKey) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "application/json");
        request.addHeader("x-api-key", apiKey); // Replace with your API key

        HttpResponse response = client.execute(request);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        client.close();

        JsonParser parser = new JsonParser();
        return parser.parse(jsonResponse).getAsJsonObject();
    }

    private String aggregatePredictions(JsonObject... predictions) {
        int teamAWins = 0;
        int teamBWins = 0;
        int draws = 0;

        for (JsonObject prediction : predictions) {
            String predictedOutcome = prediction.get("predictedOutcome").getAsString();
            switch (predictedOutcome) {
                case "Team A":
                    teamAWins++;
                    break;
                case "Team B":
                    teamBWins++;
                    break;
                case "Draw":
                    draws++;
                    break;
            }
        }

        if (teamAWins > teamBWins && teamAWins > draws) {
            return "Team A wins";
        } else if (teamBWins > teamAWins && teamBWins > draws) {
            return "Team B wins";
        } else {
            return "Draw";
        }
    }

    private void resetFields() {
        team1Field.setText("");
        team2Field.setText("");
        locationField.setText("");
        resultLabel.setText("Prediction will appear here");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FootballPredictorGUI gui = new FootballPredictorGUI();
            gui.setVisible(true);
        });
    }

    // Inner class for DeviceStatusGUI
    private class DeviceStatusGUI extends JPanel {
        private JLabel statusLabel;

        public DeviceStatusGUI() {
            statusLabel = new JLabel("Checking status...", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
            setLayout(new BorderLayout());
            add(statusLabel, BorderLayout.NORTH); // Position label at the top
            checkDeviceStatus();
        }

        private void checkDeviceStatus() {
            Thread statusThread = new Thread(() -> {
                while (true) {
                    try {
                        InetAddress device = InetAddress.getByName("google.com");
                        boolean online = device.isReachable(5000); // Timeout in milliseconds
                        if (online) {
                            updateStatusLabel("Medusa is Live", Color.BLUE);
                        } else {
                            updateStatusLabel("You are offline", Color.RED);
                        }
                        Thread.sleep(5000); // Check status every 5 seconds
                    } catch (UnknownHostException | InterruptedException e) {
                        e.printStackTrace();
                    } catch (java.io.IOException e) {
                        updateStatusLabel("Device is offline", Color.RED);
                    }
                }
            });
            statusThread.start();
        }

        private void updateStatusLabel(String text, Color color) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(text);
                statusLabel.setForeground(color);
            });
        }
    }
}
