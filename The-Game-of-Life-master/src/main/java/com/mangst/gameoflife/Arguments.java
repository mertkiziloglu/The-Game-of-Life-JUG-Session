package com.mangst.gameoflife;

import java.util.HashMap;
import java.util.Map;


public class Arguments {

	private Map<String, String> args = new HashMap<String, String>();


	public Arguments(String args[]) {
		for (String arg : args) {
			//ignore arguments that doesn't start with "-" (or "--")
			if (!arg.startsWith("-")) {
				continue;
			}
			
			//remove dashes
			boolean longArg;
			if (arg.startsWith("--")){
				longArg = true;
				arg = arg.substring(2);
			} else {
				longArg = false;
				arg = arg.substring(1);
			}

			//get key and value
			String key, value;
			int equals = arg.indexOf('=');
			if (equals >= 0) {
				key = arg.substring(0, equals);
				if (equals < arg.length() - 1) {
					value = arg.substring(equals + 1);
				} else {
					value = "";
				}
			} else {
				key = arg;
				value = null;
			}
			
			if (longArg){
				this.args.put(key, value);
			} else {
				//flags can be grouped (example: "-abc" is the same as "-a -b -c")
				for (int i = 0; i < key.length(); i++){
					this.args.put(key.charAt(i) + "", value);
				}
			}
		}
	}


	public boolean exists(String shortArg, String longArg) {
		return args.containsKey(shortArg) || args.containsKey(longArg);
	}


	public String value(String shortArg, String longArg) {
		return value(shortArg, longArg, null);
	}


	public String value(String shortArg, String longArg, String defaultValue) {
		String value = args.get(shortArg);
		if (value == null) {
			value = args.get(longArg);
		}

		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public Integer valueInt(String shortArg, String longArg) {
		return valueInt(shortArg, longArg, null);
	}


	public Integer valueInt(String shortArg, String longArg, Integer defaultValue) {
		String value = value(shortArg, longArg);

		if (value == null) {
			return defaultValue;
		} else if (value.isEmpty()) {
			return 0;
		}
		return Integer.valueOf(value);
	}


	public Double valueDouble(String shortArg, String longArg) {
		return valueDouble(shortArg, longArg, null);
	}

	public Double valueDouble(String shortArg, String longArg, Double defaultValue) {
		String value = value(shortArg, longArg);

		if (value == null) {
			return defaultValue;
		} else if (value.isEmpty()) {
			return 0.0;
		}
		return Double.valueOf(value);
	}
}
