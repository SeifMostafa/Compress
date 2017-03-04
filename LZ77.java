package Multimedia;

import java.util.ArrayList;

import org.omg.PortableInterceptor.INACTIVE;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.org.glassfish.external.probe.provider.PluginPoint;

public class LZ77 {
	private String string;
	private ArrayList<String> dict = new ArrayList<String>();

	public LZ77() {
		super();
		this.string = "";
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public void process() {
		int i = 0, j = 1, LENGTH = 0, POINTER = 0, check = 0;
		String Substring = "", FORSEARCHING = "";
		while ((i + j) <= this.string.length()) {
			Substring = this.string.substring(i, i + j);
			FORSEARCHING = this.string.substring(0, i + j - 1);
			check = FORSEARCHING.lastIndexOf(Substring);
			if ((i + j) == this.string.length()) {
				if (check == -1) {
					dict.add("<" + POINTER + "," + LENGTH + "," + Substring.charAt(Substring.length() - 1) + ">");
					return;
				} else {
					j++;
					LENGTH++;
					POINTER = i - check;
					dict.add("<" + POINTER + "," + LENGTH + "," + "NULL>");
					return;
				}

			} else {

				if (check == -1) {
					dict.add("<" + POINTER + "," + LENGTH + "," + Substring.charAt(Substring.length() - 1) + ">");
					j = 1;
					LENGTH = 0;
					POINTER = 0;
					i += Substring.length();
				} else {
					j++;
					LENGTH++;
					POINTER = i - check;
				}
			}
		}
	}

	public void de_process() {
		this.string = "";
		String temp = "";
		int pointer = 0, len = 0, c = 0, POINTER = 0, LENGTH = 0;
		char CHAR = 0;
		for (int i = 0; i < this.dict.size(); i++) {
			// getting length , pointer and new char.
			for (int j = 1; j < this.dict.get(i).length(); j++) {
				if (c == 0 && this.dict.get(i).charAt(j) == ',') {
					pointer = j;
					c++;
				} else if (c == 1 && this.dict.get(i).charAt(j) == ',') {
					len = j;
					c++;
				}
			}
			temp = this.dict.get(i).substring(1, pointer);
			POINTER = Integer.parseInt(temp);
			temp = this.dict.get(i).substring(pointer + 1, len);
			LENGTH = Integer.parseInt(temp);
			temp = this.dict.get(i).substring((len + 1), this.dict.get(i).length());
			if (temp != "NULL") {
				CHAR = temp.charAt(0);
			}
			if (POINTER > 0) {
				int p = (this.string.length() - POINTER);
				while (LENGTH > 0) {
					this.string += this.string.charAt(p);
					p++;
					LENGTH--;

				}
			} else {
				this.string += CHAR;
			}
			temp = "";
		}
	}

	public String show_tags() {
		String Tags = "";
		for (int i = 0; i < dict.size(); i++) {
			Tags += dict.get(i);
			Tags += '\n';
		}
		return Tags;
	}
}
