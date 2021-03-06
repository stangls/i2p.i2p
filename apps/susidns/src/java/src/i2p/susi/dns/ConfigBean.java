/*
 * Created on Sep 02, 2005
 * 
 *  This file is part of susidns project, see http://susi.i2p/
 *  
 *  Copyright (C) 2005 <susi23@mail.i2p>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *  
 * $Revision: 1.3 $
 */

package i2p.susi.dns;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import net.i2p.I2PAppContext;
import net.i2p.data.DataHelper;
import net.i2p.util.OrderedProperties;

public class ConfigBean implements Serializable {
	
	/*
	 * as this is not provided as constant in addressbook, we define it here
	 */
 	public static final String addressbookPrefix =
 		(new File(I2PAppContext.getGlobalContext().getRouterDir(), "addressbook")).getAbsolutePath()
			+ File.separatorChar;
	public static final String configFileName = addressbookPrefix + "config.txt";
	
	private String action, config;
	private String serial, lastSerial;
	private boolean saved;
	
	public static String getConfigFileName() {
		return configFileName;
	}

	public String getfileName() {
		return getConfigFileName();
	}

	public boolean isSaved() {
		return saved;
	}

	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getConfig()
	{
		if( config != null )
			return config;
		
		reload();
		
		return config;
	}
	
	private void reload()
	{
		File file = new File( configFileName );
		if( file != null && file.isFile() ) {
			StringBuilder buf = new StringBuilder();
			try {
				// use loadProps to trim
				Properties props = new OrderedProperties();
				DataHelper.loadProps(props, file);
				for (Map.Entry<Object, Object> e : props.entrySet()) {
					buf.append((String) e.getKey()).append('=')
					   .append((String) e.getValue()).append('\n');
				}
				config = buf.toString();
				saved = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void save()
	{
		File file = new File( configFileName );
		try {
			// use loadProps to trim, use storeProps to sort and get line endings right
			Properties props = new OrderedProperties();
			DataHelper.loadProps(props, new ByteArrayInputStream(config.getBytes("UTF-8")));
			DataHelper.storeProps(props, file);
			saved = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setConfig(String config) {
		// will come from form with \r\n line endings
		this.config = config;
		this.saved = false;
	}

	public String getMessages() {
		String message = "";
		if( action != null ) {
                        if (I2PAppContext.getGlobalContext().getBooleanProperty(BaseBean.PROP_PW_ENABLE) ||
			    (serial != null && serial.equals(lastSerial))) {
				if(action.equals(_("Save"))) {
					save();
					message = _("Configuration saved.");
				} else if (action.equals(_("Reload"))) {
					reload();
					message = _("Configuration reloaded.");
				}
			}			
			else {
				message = _("Invalid form submission, probably because you used the \"back\" or \"reload\" button on your browser. Please resubmit.")
                                          + ' ' +
                                          _("If the problem persists, verify that you have cookies enabled in your browser.");
			}
		}
		if( message.length() > 0 )
			message = "<p class=\"messages\">" + message + "</p>";
		return message;
	}

	public String getSerial()
	{
		lastSerial = "" + Math.random();
		action = null;
		return lastSerial;
	}

	public void setSerial(String serial ) {
		this.serial = serial;
	}

	/** translate */
	private static String _(String s) {
		return Messages.getString(s);
	}
}
