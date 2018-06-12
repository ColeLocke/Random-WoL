
//TODO: Robes maybe

import java.io.*;
import org.json.*;
import org.apache.commons.io.*;
import java.net.*;

public class randomWizardRun {
	static String filePath = "http://www.tomeofknowledge.info/data/db.json";
	static String[] arcanaType = { "Basic", "Dash", "Standard", "Signature" };

	public static String elementDetermination(int ran) {
		// depending on the random number input, an element will be chosen
		String element = "";

		if (ran >= 1 && ran <= 3) {
			element = "Air";
		}
		if (ran >= 4 && ran <= 6) {
			element = "Fire";
		}
		if (ran >= 7 && ran <= 9) {
			element = "Water";
		}
		if (ran >= 10 && ran <= 12) {
			element = "Earth";
		}
		if (ran >= 13 && ran <= 15) {
			element = "Lightning";
		}
		if (ran == 16) {
			element = "Chaos";
			// there are 8 Chaos arcana but 24 for each of the other elements
			// thus the range of numbers is also adjusted to 1/3 for chaos
		}

		return element;
	}

	public static String[] spellDetermination(InputStream file, JSONObject obj, String slot, String chosenElement,
			int range) throws JSONException {
		String[] arcana = new String[2]; // Array to save name and number of chosen arcana
		for (int i = 1, counter = 0; i <= 128; i++) {
			// check if the element & slot type (input params) match the element & slot type
			// of the current (i-th) spell and count
			// then check if the counter equals the specified random arcana number

			if (slot.equals("Standard")) {
				// exception for standard arcana, since signature arcana can also be used as
				// standard arcana
				if (chosenElement.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("type"))
						&& (slot.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("slot")) || obj
								.getJSONObject("spells").getJSONObject("" + i).getString("slot").equals("Signature"))) {
					counter++;
				}
				if (chosenElement.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("type"))
						&& (slot.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("slot")) || obj
								.getJSONObject("spells").getJSONObject("" + i).getString("slot").equals("Signature"))
						&& counter == range) {
					arcana[0] = obj.getJSONObject("spells").getJSONObject("" + i).getString("name");
					arcana[1] = "" + i;
					break;
				}
			}

			if (slot.equals("Signature") && chosenElement.equals("Chaos")) {
				// exception for chaos arcana
				// all standard chaos arcana can also be used as signature
				if (chosenElement.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("type"))
						&& (slot.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("slot")) || obj
								.getJSONObject("spells").getJSONObject("" + i).getString("slot").equals("Standard"))) {
					counter++;
				}
				if (chosenElement.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("type"))
						&& (slot.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("slot")) || obj
								.getJSONObject("spells").getJSONObject("" + i).getString("slot").equals("Standard"))
						&& counter == range) {
					arcana[0] = obj.getJSONObject("spells").getJSONObject("" + i).getString("name");
					arcana[1] = "" + i;
					break;
				}
			}

			if (chosenElement.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("type"))
					&& slot.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("slot"))) {
				counter++;
			}
			if (chosenElement.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("type"))
					&& slot.equals(obj.getJSONObject("spells").getJSONObject("" + i).getString("slot"))
					&& counter == range) {
				arcana[0] = obj.getJSONObject("spells").getJSONObject("" + i).getString("name");
				arcana[1] = "" + i;
				break;
			}
		}
		return arcana;
	}

	public static void main(String[] args) throws IOException, JSONException, MalformedURLException {
		// the database file
		InputStream file = new URL(filePath).openStream();
		// create a JSON object which takes the file as an input. Later on search the
		// file for various objects
		JSONObject obj = new JSONObject(IOUtils.toString(file));

		// String variables which change in each step to the corresponding spell that
		// has been chosen
		String chosenSpellName = "";
		String chosenElement = "";
		int chosenSpellNumber = 0;
		String temp = "";

		// generate a lot of random numbers :D
		int randomElement = 0;
		int relicRandom = (int) (Math.random() * 177 + 1);
		int basicRandomArcana = (int) (Math.random() * 2 + 1); // for each type two basic
		int dashRandomArcana = (int) (Math.random() * 2 + 1); // and two dash arcana exist
		int standardRandomArcana = (int) (Math.random() * 20 + 1); // actually 16 standard arcana but...
		int signatureRandomArcana = (int) (Math.random() * 4 + 1); // the 4 signature arcana can also be used as
																	// standard
		int chaosStandardRandomArcana = (int) (Math.random() * 6 + 1); // different for chaos arcana
		int chaosSignatureRandomArcana = (int) (Math.random() * 6 + 1);

		// save random numbers in array for convenience
		int[] randomArcanaArray = { basicRandomArcana, dashRandomArcana, standardRandomArcana, signatureRandomArcana };
		int[] chaosArcanaArray = { 1, 1, chaosStandardRandomArcana, chaosSignatureRandomArcana };

		// let the RNG begin!!!

		// the relic
		// pretty straight forward; generate random number, look it up in database (JSON
		// file), fetch name, use it as output
		while (obj.getJSONObject("relics").getJSONObject("" + relicRandom).getString("pool").equals("")) {
			// certain items are only available in the trials (e.g. cursed items,...)
			relicRandom = (int) (Math.random() * 177 + 1);
		}
		System.out.println("Relic: \t\t" + obj.getJSONObject("relics").getJSONObject("" + relicRandom).getString("name")
				+ " (" + obj.getJSONObject("relics").getJSONObject("" + relicRandom).getString("type") + ")");

		// the arcana
		for (int i = 0; i < arcanaType.length; i++) {
			temp = chosenSpellName; // only relevant for signature arcana
			while (temp == chosenSpellName) {// avoids having same arcana as standard and signature

				randomElement = (int) (Math.random() * 16 + 1); // first choose the element of the arcana
				chosenElement = elementDetermination(randomElement); // save chosen element in separate String to avoid
																		// conflict with chaos type

				if (!chosenElement.equals("Chaos")) {
					// choose the actual spell and save name and number
					chosenSpellName = spellDetermination(file, obj, arcanaType[i], chosenElement,
							randomArcanaArray[i])[0];
					chosenSpellNumber = Integer.parseInt(
							spellDetermination(file, obj, arcanaType[i], chosenElement, randomArcanaArray[i])[1]);
				} else {
					// if it's a chaos spell, use the chaosSpellNumber Array instead
					chosenSpellName = spellDetermination(file, obj, arcanaType[i], chosenElement,
							chaosArcanaArray[i])[0];
					chosenSpellNumber = Integer.parseInt(
							spellDetermination(file, obj, arcanaType[i], chosenElement, chaosArcanaArray[i])[1]);
				}
			}
			System.out.println(arcanaType[i] + ": \t\t" + chosenSpellName + " ("
					+ obj.getJSONObject("spells").getJSONObject("" + chosenSpellNumber).getString("type") + ")");
		}
	}
}