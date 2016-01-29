import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LocationMap extends JPanel implements Runnable {

	Thread thread = null;
	boolean loaded = false;
	double latitude = -1;
	double longitude = -1;
	BufferedImage map = null;

	public void paint(Graphics g) {
		if (loaded == true)
			g.drawImage(map, 0, 0, null);
		else
			g.drawString("Loading ...", 600, 300);
	}

	public void run() {
		try {
			
			// Get location from IP
			URL oracle;
			oracle = new URL("http://www.ipinfo.io/json");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					oracle.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("loc")) {
					
					// Split the line that contains latitude and longitude
					String[] parts = inputLine.replace("  \"loc\": \"", "")
							.replace("\",", "").split(",");

					// Convert the string to double
					latitude = Double.parseDouble(parts[0]);
					longitude = Double.parseDouble(parts[1]);

					// Location of RUET, Rajshahi
					// latitude = 24.3717701;
					// longitude = 88.6254557;

					// Generate URL using location info
					URL url = new URL(
							"http://maps.googleapis.com/maps/api/staticmap?center="
									+ latitude
									+ ","
									+ longitude
									+ "&zoom=14&scale=2&size=600x300&maptype=roadmap&format=png&visual_refresh=true");
					// Get map from that URL
					map = ImageIO.read(url);

					break;
				}
			}
			in.close();

			loaded = true;

		} catch (Exception e) {
		}
		
		// Draw map
		repaint();
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public static void main(String args[]) {
		JFrame window = new JFrame();
		window.setTitle("Location Map");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0, 0, 1200, 630);
		LocationMap locationMap = new LocationMap();
		window.getContentPane().add(locationMap);
		window.setVisible(true);
		locationMap.start();
	}
}